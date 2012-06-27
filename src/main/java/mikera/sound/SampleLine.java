package mikera.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Control;
import javax.sound.sampled.Control.Type;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class SampleLine implements TargetDataLine {

	
	public void open(AudioFormat format) throws LineUnavailableException {
		// TODO Auto-generated method stub
		
	}

	public void open(AudioFormat format, int bufferSize)
			throws LineUnavailableException {
		// TODO Auto-generated method stub
		
	}

	public int read(byte[] b, int off, int len) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int available() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void drain() {
		// TODO Auto-generated method stub
		
	}

	public void flush() {
		// TODO Auto-generated method stub
		
	}

	public int getBufferSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public AudioFormat getFormat() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getFramePosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getLongFramePosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getMicrosecondPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	public void start() {
		// TODO Auto-generated method stub
		
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}

	public void addLineListener(LineListener listener) {
		// TODO Auto-generated method stub
		
	}

	public void close() {
		// TODO Auto-generated method stub
		
	}

	public Control getControl(Type control) {
		return null;
	}

	public Control[] getControls() {
		// TODO Auto-generated method stub
		return null;
	}

	public Line.Info getLineInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isControlSupported(Type control) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	public void open() throws LineUnavailableException {
		// TODO Auto-generated method stub
		
	}

	public void removeLineListener(LineListener listener) {
		// TODO Auto-generated method stub
		
	}


}
