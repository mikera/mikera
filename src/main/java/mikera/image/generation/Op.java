package mikera.image.generation;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;

public class Op {

	public static BufferedImage add(BufferedImage ba, double fa, BufferedImage bb, double fb) {
		int h=ba.getHeight();
		int w=ba.getWidth();
		BufferedImage result=Generator.newImage(w,h);
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				int ca=ba.getRGB(x, y);
				int cb=bb.getRGB(x, y);
				double r=(Colours.getRed(ca)*fa+Colours.getRed(cb)*fb);
				double g=(Colours.getGreen(ca)*fa+Colours.getGreen(cb)*fb);
				double b=(Colours.getBlue(ca)*fa+Colours.getBlue(cb)*fb);
				double a=(Colours.getAlpha(ca)*fa+Colours.getAlpha(cb)*fb);
				
				result.setRGB(x, y, Colours.getARGBClamped(r,g,b,a));
			}
		}
		return result;
	}
	
	public static BufferedImage multiply(BufferedImage ba, BufferedImage bb) {
		int h=ba.getHeight();
		int w=ba.getWidth();
		BufferedImage result=Generator.newImage(w,h);
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				int ca=ba.getRGB(x, y);
				int cb=bb.getRGB(x, y);
				int r=(Colours.getRed(ca)*Colours.getRed(cb))/255;
				int g=(Colours.getGreen(ca)*Colours.getGreen(cb))/255;
				int b=(Colours.getBlue(ca)*Colours.getBlue(cb))/255;
				int a=(Colours.getAlpha(ca)*Colours.getAlpha(cb))/255;
				
				result.setRGB(x, y, Colours.getARGBClamped(r,g,b,a));
			}
		}
		return result;
	}
	
	public static BufferedImage multiply(BufferedImage ba, float rf, float gf, float bf, float af) {
		int h=ba.getHeight();
		int w=ba.getWidth();
		BufferedImage result=Generator.newImage(w,h);
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				int ca=ba.getRGB(x, y);
				int r=(int)(Colours.getRed(ca)*rf);
				int g=(int)(Colours.getGreen(ca)*gf);
				int b=(int)(Colours.getBlue(ca)*bf);
				int a=(int)(Colours.getAlpha(ca)*af);		
				result.setRGB(x, y, Colours.getARGBClamped(r,g,b,a));
			}
		}
		return result;
	}

	public static BufferedImage applyWithWrap(BufferedImage b, BufferedImageOp op) {
		BufferedImage c=Generator.createTiledImage(b, 3, 3);
		c=op.filter(c, null);
		return Generator.unTileImage(c, 3, 3);
	}
	
	public static BufferedImage apply(BufferedImage b, BufferedImageOp op) {
		return op.filter(b, null);
	}

	public static BufferedImage apply(BufferedImage b, ImageFilter filter) {
		return Op.createImage(b.getWidth(), b.getHeight(),new FilteredImageSource(b.getSource(),filter));
	}

	public static BufferedImage merge(BufferedImage b1, BufferedImage b2, double proportion2) {
		int w=b1.getWidth();
		int h=b1.getHeight();
		BufferedImage result=Generator.newImage(w,h);
		Graphics2D g=result.createGraphics();
		g.drawImage(b1,0,0,null);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float)proportion2));
		g.drawImage(b2,0,0,w,h,null);
		return result;
	}
	
	public static BufferedImage turbulence(BufferedImage source, float freq, float scale) {
		int w=source.getWidth();
		int h=source.getHeight();
		BufferedImage result=Generator.newImage(w,h);
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				float sx=((float)x)/w;
				sx+=scale/freq*Generator.perlin.tileableNoise2(x*freq/w, y*freq/h, freq, freq);
				float sy=((float)y)/h;
				sy+=scale/freq*Generator.perlin.tileableNoise2(x*freq/w, y*freq/h, freq, freq);
				sx-=Math.floor(sx);
				sy-=Math.floor(sy);
				result.setRGB(x, y, source.getRGB((int)(sx*w),(int)(sy*h)));
			}
		}
		return result;
	}

	public static BufferedImage resize(BufferedImage source, float factor) {
		return resize(source,(int)(source.getWidth()*factor),(int)(source.getHeight()*factor));
	}
	
	public static BufferedImage resize(BufferedImage source, int w, int h) {
		BufferedImage b=Generator.newImage(w,h);
		Graphics2D g=b.createGraphics();
		//g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		RenderingHints rh = g.getRenderingHints(); 
		rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.setRenderingHints(rh);
		
		AffineTransform at = new AffineTransform();
		at.scale(((float)w)/source.getWidth(), ((float)h)/source.getHeight());
		g.drawRenderedImage(source, at);
		return b;
	}

	public static BufferedImage createImage(int w, int h, ImageProducer ip) {
		Image img=Toolkit.getDefaultToolkit().createImage(ip);
		BufferedImage result=Generator.newImage(w,h);
		Graphics2D gr=result.createGraphics();
		//gr.setComposite(AlphaComposite.Src);
		gr.drawImage(img, 0,0,w,h, null);
		return result;
	}

	public static BufferedImage blurImage(BufferedImage bi) {
		BufferedImageOp op = ImageFilters.blurOperation;
		return op.filter(bi, null);
	}

	public static BufferedImage copy(BufferedImage b) {
		BufferedImage result=Generator.newImage(b.getWidth(),b.getHeight());
		Graphics2D gr=result.createGraphics();
		gr.drawImage(b,0,0,null);
		return result;
	}
	
	public static BufferedImage overlay(BufferedImage top, BufferedImage bottom) {
		BufferedImage result=copy(bottom);
		Graphics2D gr=result.createGraphics();
		gr.drawImage(top,0,0,null);
		return result;
	}

}
