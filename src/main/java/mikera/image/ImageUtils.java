package mikera.image;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

import mikera.math.Function;
import mikera.math.Vector;
import mikera.util.Rand;
import mikera.util.Resource;


public class ImageUtils {

	/*
		Standard way to load images:
		myClass.getClass().getClassLoader().getResourceAsStream("resources/somefile.png")
		Note: class must be in jar file to get resources from same jar
	 */
	
	public static BufferedImage getImage(String filename) {
		URL imageURL = Resource.getResource(filename);
		if (imageURL != null) {
			return getImage(imageURL);
		}
		throw new Error("Image URL not found");
	}

	public static BufferedImage getImage(URL imageURL) {
		BufferedImage image;
		try {
			image = ImageIO.read(imageURL);
		} catch (IOException e) {
			throw new Error("Image read failed", e);
		}

		return image;
	}
	
	public static int randColour() {
		return 0xFF000000+0x10000*Rand.r(256)+0x100*Rand.r(256)+Rand.r(256);
	}
	
	@SuppressWarnings("serial")
	public static Frame display(final Image image) {
		JFrame f=new JFrame("Image popup");
		JComponent c=new JComponent() {
			public void paint(Graphics g) {
				g.drawImage(image,0,0,null);
			}
		};
		c.setMinimumSize(new Dimension(image.getWidth(null),image.getHeight(null)));
		f.setMinimumSize(new Dimension(image.getWidth(null)+50,image.getHeight(null)+50));
		f.add(c);
		f.setVisible(true);
		f.pack();
		
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		return f;
	}
	
	private static final HashMap<String,JFrame> imageFrames=new HashMap<String,JFrame>();
	private static final HashMap<String,Image> images=new HashMap<String,Image>();
	
	@SuppressWarnings("serial")
	public static Frame display(final String s, Image image) {
		images.put(s,image);
		JFrame f=imageFrames.get(s);
		
		if (f==null) {
			f=new JFrame(s);
			imageFrames.put(s,f);
			JComponent c=new JComponent() {
				public void paint(Graphics g) {
					g.drawImage(images.get(s),0,0,null);
				}
			};
			c.setMinimumSize(new Dimension(image.getWidth(null),image.getHeight(null)));
			f.setMinimumSize(new Dimension(image.getWidth(null)+50,image.getHeight(null)+50));
			f.add(c);
			f.setVisible(true);
			f.pack();
			
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		} else {
			f.setVisible(true);
			f.repaint();
		}
		
		return f;
	}
	
	public static Frame displayAndExit(Image image) {
		final Frame frame=display(image);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.dispose();
			}
		});
		return frame;
	}
	
	public static Frame displayAndExit(Function<Vector,Vector> f) {
		BufferedImage image=Generator.createFunctionGradient(512,512,f, Gradient.createRainbowGradient(256));
		
		final Frame frame=display(image);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.dispose();
			}
		});
		return frame;
	}
	
	public static void main (String[] args) {
		BufferedImage img = Generator.createChecker(256, 256, 4, 0xFFFF0000, 0xFF000000);
		display(img);
		
	}

}
