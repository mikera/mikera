package mikera.sound;

import java.net.URL;
import java.util.HashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import mikera.util.Resource;

public class SoundEngine {

	private static HashMap<String,Sample> samples=new HashMap<>();
	
	public static final AudioFormat STEREO_FORMAT=new AudioFormat(
			AudioFormat.Encoding.PCM_SIGNED,
			AudioSystem.NOT_SPECIFIED, // no specific sample rate
			16, // 16=buit encoding
			2, // 2 channel stereo
			2,
			AudioSystem.NOT_SPECIFIED, // no specific sample rate
			true // big endian
			);
	
	
	public static Sample getSound (String url) {
		Sample c=samples.get(url);
		return c;
	}
	
	public static Sample sampleFromClip(Clip c) {
		Sample s=new Sample();
		s.clip=c;
		return s;
		
	}
	
	public static Sample loadSample(String url) {
		Sample c=getSound(url);
		if (c!=null) return c;
		
	    Clip clip = loadClip(url);
	    Sample sample=sampleFromClip(clip);

	    samples.put(url, sample);
	    return sample;
	}
	
	private static Clip loadClip(String url) {
		try {
			URL soundURL = Resource.getResource(url);	
			if (soundURL==null) throw new Error("File not found: "+url);
			AudioInputStream stream = AudioSystem.getAudioInputStream(soundURL);
			
	        AudioFormat format = stream.getFormat();

	        DataLine.Info info = new DataLine.Info(
	        		Clip.class, 
	        		format, 
	        		((int)stream.getFrameLength()*format.getFrameSize()));
	        
	        Clip clip = (Clip) AudioSystem.getLine(info);
	        clip.open(stream);
	        
	        return clip;
		} catch (Exception e) {	
			throw new Error(e);
		} 
	}
	
	public static void main(String[] args) {
		Clip c=loadClip("mikera/sound/Thud.wav");
		try {
			c.start();
			Thread.sleep(1000);
			c.start();
			Thread.sleep(c.getMicrosecondLength()/1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
