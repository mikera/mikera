package mikera.image.generation;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TextureFactory {
	public static BufferedImage createRockBumpMap() {
		BufferedImage b=Generator.createPerlinNoise(256,256,40);
		b=Op.applyWithWrap(b, ImageFilters.embossOperation);
		
		return b;
	}
	
	
	
	
	public static void main(String[] args) {
		// background
		BufferedImage bg=Generator.createSolidImage(1024,1024,0xFF000000);
		
		// image
		BufferedImage e=createRockBumpMap();
		
		e=Op.resize(e,256,256);
		e=Generator.createTiledImage(e, 2, 2);
		
		// finally render
		Graphics2D gr=bg.createGraphics();
		gr.drawImage(e, 0, 0, null);
		ImageUtils.displayAndExit(bg);
		System.err.println("Done texture factory");
	}
}
