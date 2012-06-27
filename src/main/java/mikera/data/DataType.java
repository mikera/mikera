package mikera.data;

public enum DataType {
	STRING(0),
	LONG(8),
	INTEGER(4),
	SHORT(2),
	CHAR(2),
	BYTE(1),
	BOOLEAN(1),
	FLOAT(4),
	DOUBLE(8),
	OBJECT(0); 
	
	private int length;
	
	private DataType(int byteLength) {
		length=byteLength;
	}
	
	public int getPrimitiveLength() {
		return length;
	}
	
	public DataType of(Object o) {
		if (o instanceof Number) {
			if (o instanceof Integer) return INTEGER;
			if (o instanceof Long) return LONG;
			if (o instanceof Float) return FLOAT;
			if (o instanceof Double) return DOUBLE;
			if (o instanceof Byte) return BYTE;
			if (o instanceof Short) return SHORT;
		} else {
			if (o instanceof String) return STRING;
			if (o instanceof Character) return CHAR;
			if (o instanceof Boolean) return BOOLEAN;
		}
		return OBJECT;
	}
}
