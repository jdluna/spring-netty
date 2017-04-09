package com.example.netty.core.j8583;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import com.example.netty.core.j8583.parser.ConfigParser;
import com.example.netty.core.j8583.parser.vo.FieldInfo;
import com.example.netty.core.j8583.parser.vo.MessageInfo;
import com.solab.iso8583.CustomField;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;

public class MessageFactory extends com.solab.iso8583.MessageFactory<IsoMessage> {

	private final static Logger logger = LoggerFactory.getLogger(MessageFactory.class);

	private Map<Class<?>, MessageInfo> encodeMap = new HashMap<Class<?>, MessageInfo>();

	private Map<Integer, MessageInfo> decodeMap = new HashMap<Integer, MessageInfo>();

	public void setConfigResource(Resource resource) throws IOException {
		Assert.notNull(resource, "Resource is undefined");
		ConfigParser.configureFromUrl(this, resource.getURL());

		logger.debug("Register configuration {} to message factory", resource.getURL().getPath());
	}

	public void setConfigResources(List<? extends Resource> resources) throws IOException {
		Assert.notEmpty(resources, "Resources is empty");
		for (Resource resource : resources) {
			setConfigResource(resource);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IsoMessage encode(Iso8583 pojo) throws Exception {
		IsoMessage isoMessage = null;
		
		MessageInfo messageInfo = encodeMap.get(pojo.getClass());
		
		if (messageInfo != null) {
			isoMessage = getMessageTemplate(messageInfo.getType());
			
			Map<Integer, FieldInfo> fieldsMap = messageInfo.getFields();
			Set<Entry<Integer, FieldInfo>> fieldSet = fieldsMap.entrySet();
			
			for (Entry<Integer, FieldInfo> field : fieldSet) {
				FieldInfo fieldInfo = field.getValue();
				
				IsoValue isoValue = createIsoValue(field.getValue(), pojo);
				
				isoMessage.putAt(fieldInfo.getNum(), isoValue);
			}
		}
			
		return isoMessage;
	}

	public <T> T decode(IsoMessage isoMessage, Class<T> clazz) throws Exception {
		T pojo = null;
		
		try {
			MessageInfo messageInfo = decodeMap.get(isoMessage.getType());
			
			if (messageInfo != null) {
				pojo = clazz.newInstance();
				
				Map<Integer, FieldInfo> fieldsMap = messageInfo.getFields();
				Set<Entry<Integer, FieldInfo>> fieldSet = fieldsMap.entrySet();
				
				for (Entry<Integer, FieldInfo> field : fieldSet) {
					FieldInfo fieldInfo = field.getValue();
					
					IsoType isoType = fieldInfo.getType();
					
					String fieldName = fieldInfo.getAttr();
					Class<?> fieldClass = PropertyUtils.getPropertyType(pojo, fieldName);
					
					if (!validateFieldType(isoType, fieldClass)) {
						throw new Exception(String.format("Field %s not compatibale with %s -> %s", fieldName, isoType, pojo.getClass().getSimpleName()));
					}
					
					IsoValue<Object> fieldValue = isoMessage.getAt(fieldInfo.getNum());
					
					BeanUtils.setProperty(pojo, fieldName, fieldValue.getValue());
				}	
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return pojo;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IsoValue createIsoValue(FieldInfo fieldInfo, Iso8583 pojo) throws Exception {
		IsoValue isoValue = null;
		
		try {
			String fieldName = fieldInfo.getAttr();
			IsoType isoType = fieldInfo.getType();
			Class<?> fieldClass = PropertyUtils.getPropertyType(pojo, fieldName);
			
			if (!validateFieldType(isoType, fieldClass)) {
				throw new Exception(String.format("Field %s not compatibale with %s -> %s", fieldName, isoType, pojo.getClass().getSimpleName()));
			}
			
			Object fieldValue = PropertyUtils.getProperty(pojo, fieldName);
				
			Class<?> converterClass = fieldInfo.getConverterClass();
			
			if (isoType == IsoType.ALPHA || isoType == IsoType.NUMERIC) {
				if (converterClass == null) {
					isoValue = new IsoValue(isoType, fieldValue, fieldInfo.getLength());
					
				} else {
					Object converter = converterClass.newInstance();
					if (converter instanceof CustomField) {
						isoValue = new IsoValue(isoType, fieldValue, fieldInfo.getLength(), (CustomField) converter);
					}
				}
			} else {
				if (converterClass == null) {
					isoValue = new IsoValue(isoType, fieldValue);
					
				} else {
					Object converter = converterClass.newInstance();
					if (converter instanceof CustomField) {
						isoValue = new IsoValue(isoType, fieldValue, (CustomField) converter);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return isoValue;
	}
	
	public boolean validateFieldType(IsoType isoType, Class<?> clazz) {
		boolean valid = true;
		
		switch (isoType) {
		case ALPHA:
		case LLVAR:
		case LLLVAR:
		case LLLLVAR:
			valid = clazz.isAssignableFrom(String.class) 
					|| clazz.isAssignableFrom(Integer.class)
					|| clazz.isAssignableFrom(Long.class); 
			break;
		case AMOUNT:
		case NUMERIC:
			valid = clazz.isAssignableFrom(Float.class)
					|| clazz.isAssignableFrom(Double.class)
					|| clazz.isAssignableFrom(BigDecimal.class)
					|| clazz.isAssignableFrom(Integer.class)
					|| clazz.isAssignableFrom(Long.class);
			break;
		case DATE4:
		case DATE10:
		case DATE12:
			valid = clazz.isAssignableFrom(Date.class);
			break;
		default:
			break;
		}
		
		return valid;
	}

	public Map<Class<?>, MessageInfo> getEncodeMap() {
		return encodeMap;
	}

	public void setEncodeMap(Map<Class<?>, MessageInfo> encodeMap) {
		this.encodeMap = encodeMap;
	}

	public Map<Integer, MessageInfo> getDecodeMap() {
		return decodeMap;
	}

	public void setDecodeMap(Map<Integer, MessageInfo> decodeMap) {
		this.decodeMap = decodeMap;
	}
}
