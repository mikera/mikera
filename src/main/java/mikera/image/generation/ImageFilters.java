package mikera.image.generation;

import java.awt.Color;
import java.awt.image.BufferedImageFilter;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RGBImageFilter;

import mikera.util.Maths;

public class ImageFilters {

	public static class TransparencyFilter extends RGBImageFilter {
	    private int factor=256; // 0 = transparent, 255= fully opaque
	    
	    public TransparencyFilter(int alpha) {
	    	factor=Maths.bound(0,(alpha*256)/255, 256);
	    }
	    
	    public TransparencyFilter(double alpha) {
	    	factor=Maths.clampToInteger(alpha*256, 0, 256);
	    }
	    
	    @Override
		public int filterRGB(int x, int y, int argb) {
			int alpha=Colours.getAlpha(argb);
			int newAlpha=(alpha*factor)>>8;
	    	return (argb & 0x00ffffff)
				| (newAlpha<<24);
	    }
	}

	public static class GreyFilter extends RGBImageFilter {
		public int filterRGB(int x, int y, int argb) {
	    	return Colours.toGreyScale(argb);
	    }
	}
	
	public static class RGBtoHSBFilter extends RGBImageFilter {
		private float[] hsbvals=new float[4];
		public int filterRGB(int x, int y, int argb) {
	    	int r=Colours.getRed(argb);
	    	int g=Colours.getGreen(argb);
	    	int b=Colours.getBlue(argb);
	    	int a=Colours.getAlpha(argb);
	    	Color.RGBtoHSB(r, g, b, hsbvals);
	    	int hu=(int)(hsbvals[0]*255);
	    	int st=(int)(hsbvals[1]*255);
	    	int br=(int)(hsbvals[2]*255);
			return Colours.getARGB(hu,st,br,a);
	    }
	}
	
	public static class HSBtoRGBFilter extends RGBImageFilter {
		public int filterRGB(int x, int y, int argb) {
	    	float hu=Colours.getRed(argb)/255.0f;
	    	float st=Colours.getGreen(argb)/255.0f;
	    	float br=Colours.getBlue(argb)/255.0f;
	    	int a=Colours.getAlpha(argb);
	    	int rgb=Color.HSBtoRGB(hu, st, br);
			return Colours.getARGB(rgb,a);
	    }
	}
	
	
	public static BufferedImageOp blurOperation = new ConvolveOp(
			new Kernel(3, 3,
			        new float[] {
			            1/9f, 1/9f, 1/9f,
			            1/9f, 1/9f, 1/9f,
			            1/9f, 1/9f, 1/9f})
			,ConvolveOp.EDGE_NO_OP,null
			        		
	);
	
	public static BufferedImageOp embossOperation = new ConvolveOp(
			new Kernel(3, 3,
			        new float[] {
			             1.0f,  0.5f,  0.0f,
			             0.5f,  1.0f, -0.5f,
			             0.0f, -0.5f, -1.0f})
			,ConvolveOp.EDGE_NO_OP,null
			        		
	);
	
	public static class BlurFilter extends BufferedImageFilter {
		public BlurFilter() {
			super(blurOperation);
			// TODO Auto-generated constructor stub
		}
	}

	public static class MultiplyFilter extends RGBImageFilter {
		public float r_factor=1.0f;
		public float g_factor=1.0f;
		public float b_factor=1.0f;
		public float a_factor=1.0f;
	
		public MultiplyFilter() {}
		
		public MultiplyFilter(double r, double g, double b) {
			r_factor=(float) r;
			g_factor=(float) g;
			b_factor=(float) b;
		}
		
		public MultiplyFilter(double r, double g, double b, double a) {
			this(r,g,b);
			a_factor=(float)a;
		}
		
		public int filterRGB(int x, int y, int rgb) {
	    	int r=Colours.getRed(rgb);
	    	int g=Colours.getGreen(rgb);
	    	int b=Colours.getBlue(rgb);
	    	int a=Colours.getAlpha(rgb);
	    	r=Maths.clampToInteger(r*r_factor, 0, 255);
	    	g=Maths.clampToInteger(g*g_factor, 0, 255);
	    	b=Maths.clampToInteger(b*b_factor, 0, 255);
	    	a=Maths.clampToInteger(a*a_factor, 0, 255);
	
	    	return ((a<<24)|(r<<16)|(g<<8)|b);
	    }
	}

}
