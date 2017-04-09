package com.example.netty.core.j8583;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.TimeZone;

import com.solab.iso8583.CustomField;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.util.HexCodec;

public class IsoValue<T> extends com.solab.iso8583.IsoValue<T> {

	public IsoValue(IsoType t, T value) {
		super(t, value);
	}

	public IsoValue(IsoType t, T value, CustomField<T> custom) {
		super(t, value, custom);
	}
	
	public IsoValue(IsoType t, T val, int len) {
		super(t, val, len);
	}

	public IsoValue(IsoType t, T val, int len, CustomField<T> custom) {
		super(t, val, len, custom);
	}
	
	@Override
	public String toString() {
		T value = getValue();
		IsoType type = getType();
		int length = getLength();
		
		CustomField<T> encoder = getEncoder();
		TimeZone tz = getTimeZone();
		
		if (value == null) {
			return "ISOValue<null>";
		}
		if (type == IsoType.NUMERIC || type == IsoType.AMOUNT) {
			if (type == IsoType.AMOUNT) {
				if (value instanceof BigDecimal) {
					return type.format((BigDecimal) value, 12);
				} else {
					return type.format(value.toString(), 12);
				}
            } else if (value instanceof BigInteger) {
                return type.format(encoder == null ? value.toString() : encoder.encodeField(value), length);
			} else if (value instanceof Number) {
				return encoder == null ? type.format(((Number)value).longValue(), length) : type.format(encoder.encodeField(value), length);
			} else {
				return type.format(encoder == null ? value.toString() : encoder.encodeField(value), length);
			}
		} else if (type == IsoType.ALPHA) {
			return type.format(encoder == null ? value.toString() : encoder.encodeField(value), length);
		} else if (type == IsoType.LLVAR || type == IsoType.LLLVAR || type == IsoType.LLLLVAR) {
			return encoder == null ? value.toString() : encoder.encodeField(value);
		} else if (value instanceof Date) {
			return type.format((Date)value, tz);
		} else if (type == IsoType.BINARY) {
			if (value instanceof byte[]) {
                final byte[] _v = (byte[])value;
				return type.format(encoder == null ? HexCodec.hexEncode(_v, 0, _v.length) : encoder.encodeField(value), length * 2);
			} else {
				return type.format(encoder == null ? value.toString() : encoder.encodeField(value), length * 2);
			}
		} else if (type == IsoType.LLBIN || type == IsoType.LLLBIN || type == IsoType.LLLLBIN) {
			if (value instanceof byte[]) {
                final byte[] _v = (byte[])value;
				return encoder == null ? HexCodec.hexEncode(_v, 0, _v.length) : encoder.encodeField(value);
			} else {
				final String _s = encoder == null ? value.toString() : encoder.encodeField(value);
				return (_s.length() % 2 == 1) ? String.format("0%s", _s) : _s;
			}
		}
		return encoder == null ? value.toString() : encoder.encodeField(value);
	}
}
