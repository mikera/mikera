package mikera.net;

import java.io.InputStream;

import mikera.data.Data;
import mikera.util.Maths;

public class DataInputStream extends InputStream {
	private final Data data;
	private int pos=0;
	
	public DataInputStream() {
		this(new Data());
	}
	
	public DataInputStream(Data d) {
		data=d;
	}	
	
	@Override
	public int read() {
		if (pos>=data.size()) return -1;
		return (255&data.getByte(pos++));
	}
	
	@Override
	public int read(byte[] buffer) {
		return read(buffer, 0, buffer.length);
	}
	
	@Override
	public int read(byte[] buffer, int offset, int length) {
		if (pos>=data.size()) return -1;
		int left=data.size()-pos;
		int readcount=Maths.min(length, left);
		data.copyTo(pos, buffer, offset, readcount);
		pos+=readcount;
		return readcount;
	}
	
	public Data getData() {
		return data;
	}
	


	public void setPosition(int pos) {
		this.pos = pos;
	}

	public int getPosition() {
		return pos;
	}
	
	public int getRemaining() {
		return data.size()-pos;
	}

}
