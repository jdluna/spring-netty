package com.example.netty.core.j8583.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.example.netty.core.j8583.parser.vo.FieldInfo;
import com.example.netty.core.j8583.parser.vo.MessageInfo;
import com.solab.iso8583.CustomField;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.codecs.CompositeField;

public class ConfigParser {

	private final static Logger logger = LoggerFactory.getLogger(ConfigParser.class);
	
	public static <T extends IsoMessage> void configureFromClasspathConfig(MessageFactory<T> mfact, String path) throws IOException {
        try (InputStream ins = mfact.getClass().getClassLoader().getResourceAsStream(path)) {
            if (ins != null) {
                logger.debug("ISO8583 Parsing config from classpath file {}", path);
                parse(mfact, new InputSource(ins));
            } else {
                logger.warn("ISO8583 File not found in classpath: {}", path);
            }
        }
	}
	
	public static <T extends IsoMessage> void configureFromUrl(MessageFactory<T> mfact, URL url) throws IOException {
		try (InputStream stream = url.openStream()) {
			parse(mfact, new InputSource(stream));
		}
	}
	
	protected static <T extends IsoMessage> void parse(MessageFactory<T> mfact, InputSource source) throws IOException {
		final DocumentBuilderFactory docfact = DocumentBuilderFactory.newInstance();
		DocumentBuilder docb = null;
		Document doc = null;
		try {
			docb = docfact.newDocumentBuilder();
			docb.setEntityResolver(new EntityResolver() {
				@Override
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					if (systemId.contains("j8583.dtd")) {
						URL dtd = getClass().getResource("j8583.dtd");
						if (dtd == null) {
							logger.warn("Cannot find j8583.dtd in classpath. j8583 config files will not be validated.");
						} else {
							return new InputSource(dtd.toString());
						}
					}
					return null;
				}
			});
			doc = docb.parse(source);
		} catch (ParserConfigurationException | SAXException ex) {
			logger.error("ISO8583 Cannot parse XML configuration", ex);
			return;
		}
		final Element root = doc.getDocumentElement();

		parseTemplate(root.getElementsByTagName("message"), mfact);
	}
	
	protected static <T extends IsoMessage> void parseTemplate(final NodeList nodes, final MessageFactory<T> mfact) throws IOException {
        ArrayList<Element> subs = null;
        for (int i = 0; i < nodes.getLength(); i++) {
            Element elem = (Element)nodes.item(i);
            int type = parseType(elem.getAttribute("type"));
            if (type == -1) {
                throw new IOException("Invalid ISO8583 type for template: " + elem.getAttribute("type"));
            }
            if (elem.getAttribute("extends") != null && !elem.getAttribute("extends").isEmpty()) {
                if (subs == null) {
                    subs = new ArrayList<>(nodes.getLength()-i);
                }
                subs.add(elem);
                continue;
            }
            @SuppressWarnings("unchecked")
            T m = (T)new IsoMessage();
            m.setType(type);
            m.setCharacterEncoding(mfact.getCharacterEncoding());
            NodeList fields = elem.getElementsByTagName("field");
            for (int j = 0; j < fields.getLength(); j++) {
                Element f = (Element)fields.item(j);
                if (f.getParentNode()==elem) {
                    final int num = Integer.parseInt(f.getAttribute("num"));
                    IsoValue<?> v = getTemplateField(f, mfact, true);
                    if (v != null) {
                        v.setCharacterEncoding(mfact.getCharacterEncoding());
                    }
                    m.setField(num, v);
                }
            }
            mfact.addMessageTemplate(m);
            
            // register custom templates
            
            Class<?> clazz = null;
            try {
				clazz = Class.forName(elem.getAttribute("class"));
			} catch (ClassNotFoundException e) {
				logger.error("Class is undefined -> {}", elem.getAttribute("class"));
			}
            
            MessageInfo msgInfo = new MessageInfo();
            msgInfo.setType(type);
            msgInfo.setClazz(clazz);
            
            for (int j = 0; j < fields.getLength(); j++) {
                Element f = (Element) fields.item(j);
                if (f.getParentNode() == elem) {
                    Integer num = Integer.parseInt(f.getAttribute("num"));
                    String attr = f.getAttribute("attr");
                    IsoType type_ = IsoType.valueOf(f.getAttribute("type"));
                    
                    Class<?> converterClass = null;
                    try {
                    	converterClass = Class.forName(elem.getAttribute("converter"));
        			} catch (ClassNotFoundException e) {
        				logger.error("Class is undefined -> {}", elem.getAttribute("converter"));
        			}
                    
                    FieldInfo fieldInfo = new FieldInfo();
                    fieldInfo.setNum(num);
                    fieldInfo.setType(type_);
                    
                    fieldInfo.setAttr(attr);
                    fieldInfo.setConverterClass(converterClass);
                    
                    if (type_ == IsoType.ALPHA || type_ == IsoType.NUMERIC) {
	                    Integer length = Integer.parseInt(f.getAttribute("length"));
	                    fieldInfo.setLength(length);
                    }
                    
                    msgInfo.getFields().put(num, fieldInfo);
                }
            }
            
            com.example.netty.core.j8583.MessageFactory mf = (com.example.netty.core.j8583.MessageFactory) mfact;
            
            mf.getEncodeMap().put(clazz, msgInfo);
            
            mf.getDecodeMap().put(m.getType(), msgInfo);
        }
        
        if (subs != null) {
            for (Element elem : subs) {
                int type = parseType(elem.getAttribute("type"));
                int ref = parseType(elem.getAttribute("extends"));
                if (ref == -1) {
                    throw new IllegalArgumentException("Message template "
                            + elem.getAttribute("type") + " extends invalid template "
                            + elem.getAttribute("extends"));
                }
                IsoMessage tref = mfact.getMessageTemplate(ref);
                if (tref == null) {
                    throw new IllegalArgumentException("Message template "
                            + elem.getAttribute("type") + " extends nonexistent template "
                            + elem.getAttribute("extends"));
                }
                @SuppressWarnings("unchecked")
                T m = (T)new IsoMessage();
                m.setType(type);
                m.setCharacterEncoding(mfact.getCharacterEncoding());
                for (int i = 2; i < 128; i++) {
                    if (tref.hasField(i)) {
                        m.setField(i, tref.getField(i).clone());
                    }
                }
                NodeList fields = elem.getElementsByTagName("field");
                for (int j = 0; j < fields.getLength(); j++) {
                    Element f = (Element)fields.item(j);
                    int num = Integer.parseInt(f.getAttribute("num"));
                    if (f.getParentNode()==elem) {
                        IsoValue<?> v = getTemplateField(f, mfact, true);
                        if (v != null) {
                            v.setCharacterEncoding(mfact.getCharacterEncoding());
                        }
                        m.setField(num, v);
                    }
                }
                mfact.addMessageTemplate(m);
            }
        }
    }
	
	protected static <M extends IsoMessage> IsoValue<?> getTemplateField(Element f, MessageFactory<M> mfact, boolean toplevel) {
        final int num = Integer.parseInt(f.getAttribute("num"));
        final String typedef = f.getAttribute("type");
        if ("exclude".equals(typedef)) {
            return null;
        }
        int length = 0;
        if (f.getAttribute("length").length() > 0) {
            length = Integer.parseInt(f.getAttribute("length"));
        }
        final IsoType itype = IsoType.valueOf(typedef);
        final NodeList subs = f.getElementsByTagName("field");
        if (subs != null && subs.getLength() > 0) {
            //Composite field
            final CompositeField cf = new CompositeField();
            for (int j = 0; j < subs.getLength(); j++) {
                Element sub = (Element)subs.item(j);
                if (sub.getParentNode()==f) {
                    IsoValue<?> sv = getTemplateField(sub, mfact, false);
                    if (sv != null) {
                        sv.setCharacterEncoding(mfact.getCharacterEncoding());
                        cf.addValue(sv);
                    }
                }
            }
            return itype.needsLength() ? new IsoValue<>(itype, cf, length, cf) :
                    new IsoValue<>(itype, cf, cf);
        }
        final String v;
        if (f.getChildNodes().getLength() == 0) {
            v = "";
        } else {
            v = f.getChildNodes().item(0).getNodeValue();
        }
        final CustomField<Object> cf = toplevel ? mfact.getCustomField(num) : null;
        if (cf == null) {
            return itype.needsLength() ? new IsoValue<>(itype, v, length) : new IsoValue<>(itype, v);
        }
        return itype.needsLength() ? new IsoValue<>(itype, cf.decodeField(v), length, cf) :
                new IsoValue<>(itype, cf.decodeField(v), cf);
    }
	
	protected static int parseType(String type) throws IOException {
		if (type.length() % 2 == 1) {
			type = "0" + type;
		}
		if (type.length() != 4) {
			return -1;
		}
		return ((type.charAt(0) - 48) << 12) | ((type.charAt(1) - 48) << 8)
			| ((type.charAt(2) - 48) << 4) | (type.charAt(3) - 48);
	}
}
