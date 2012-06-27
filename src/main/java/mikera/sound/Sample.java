package mikera.sound;

import javax.sound.sampled.Clip;

public class Sample {

	public Clip clip;

	public void play() {
		clip.setFramePosition(0);
		clip.start();
	}
	


}
