package mikera.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.io.Serializable;

import mikera.annotations.Mutable;
import mikera.math.Vector;
import mikera.math.VectorFunction;
import mikera.util.Maths;

/**
 * Represents an arbitrary sized gradient of RGBA colours
 * 
 * @author Mike
 *
 */
@Mutable
public final class Gradient implements Cloneable, Serializable {
	private static final long serialVersionUID = -1324286985827948661L;

	private static final int DEFAULT_GRADIENT_SIZE=256;
	
	public final int[] data;
	
	/**
	 * Creates a new gradient array of default size (256)
	 * @return
	 */
	public static int[] create() {
		return new int[DEFAULT_GRADIENT_SIZE];
	}
	
	public Gradient() {
		this(DEFAULT_GRADIENT_SIZE);
	}
	
	public Gradient(int size) {
		data=new int[size];
	}
	
	public Gradient(int[] grad) {
		data=grad;
	}
	
	public static int[] createDataArray(int size) {
		return new int[size];
	}
	
	public int size() {
		return data.length;
	}
	
	public int get(int i) {
		return data[i];
	}
	
	public int getScaled(double pos) {
		int length=data.length;
		int p=(int)Math.floor(pos*length);
		int bounded=Maths.middle(0, p, length-1);
		return data[bounded];
	}
	
	public static Gradient createInvertedMonoGradient() {
		int[] gr=create();
		for (int i=0; i<DEFAULT_GRADIENT_SIZE; i++) {
			int c=(255-i)*0x010101;
			gr[i]=c|Colours.ALPHA_MASK;
		}	
		return new Gradient(gr);
	}
	
	/**
	 * Fills a gradient from a line in an image
	 * taking the first 256 pixels in the given line
	 * @param grad
	 * @param b
	 * @param line
	 */
	public void fillFromImage(BufferedImage b, int line) {
		for (int i=0; i<size(); i++) {
			data[i]=b.getRGB(i, line);
		}
	}
	
	public void fillFromFunction(VectorFunction vf) {
		Vector col=new Vector(4);
		Vector pos=new Vector(1);
		col.data[3]=1.0f; // default to full alpha
		
		int s=size();
		for (int i=0; i<s; i++) {
			col.data[0]=((float)i)/s;
			vf.calculate(pos, col);
			data[i]=Colours.fromVector4(col);
		}
	}

	/**
	 * Creates a rainbow coloured gradient
	 * @return
	 */
	public static Gradient createRainbowGradient(int size) {
		int[] gr=create();
		for (int i=0; i<size; i++) {
			float h=((float)i)/size;
			gr[i]=Color.HSBtoRGB(h, 1, 1)|Colours.ALPHA_MASK;
		}
		
		return new Gradient(gr);
	}
	
	/**
	 * Creates a gradient suitable for colouring a generated
	 * fractal landscape, with sea level at midpoint
	 * @return
	 */
	public static Gradient createLandscapeGradient() {
		int[] gr=createDataArray(256);
		fillLinearGradient(gr, 0, 0xFF000000, 110, 0xFF0000FF);
		fillLinearGradient(gr, 111, 0xFF0000FF, 127, 0xFF0080FF);
		fillLinearGradient(gr, 128, 0xFFFFFF00, 130, 0xFFFFFF00);
		fillLinearGradient(gr, 131, 0xFF00FF00, 160, 0xFF006000);
		fillLinearGradient(gr, 161, 0xFF006000, 170, 0xFF808080);
		fillLinearGradient(gr, 171, 0xFF808080, 190, 0xFF707070);
		fillLinearGradient(gr, 191, 0xFFFFFFFF, 255, 0xFFB0FFFF);
		return new Gradient(gr);
	}
	
	/**
	 * Creates a monochrome gradient from black to white
	 * @return
	 */
	public static Gradient createMonoGradient() {
		int[] gr=create();
		fillLinearGradient(gr, 0, 0xFF000000, 255, 0xFFFFFFFF);
		return new Gradient(gr);
	}
	
	/**
	 * Reverses a gradient
	 * @param grad
	 */
	public static void reverseGradient(int[] grad) {
		int s=grad.length;
		for (int i=0; i<s/2; i++) {
			int t=grad[i];
			grad[i]=grad[s-1-i];
			grad[s-1-i]=t;
		}
	}
	
	public static void fillLinearGradient(int[] grad, int p1, int c1, int p2, int c2) {
		grad[p2]=c2;
		int d=Maths.sign(p2-p1);
		if (d==0) return;
		
		final float[] fs=new float[12];
		Colours.toFloat4(fs, 0, c1); // c1
		Colours.toFloat4(fs, 4, c2); // c2
		Colours.toFloat4(fs, 8, c1); // current
		float df=1.0f/(p2-p1);
		for (int i=p1; i!=p2; i+=d) {
			grad[i]=Colours.fromFloat4(fs, 8);
			fs[8] +=df*(fs[4]-fs[0]);
			fs[9] +=df*(fs[5]-fs[1]);
			fs[10]+=df*(fs[6]-fs[2]);
			fs[11]+=df*(fs[7]-fs[3]);
		}
	}
	
	static class RGBComponentGradientFilter extends RGBImageFilter {
		public int[] gradient;
	
		public RGBComponentGradientFilter(int[] g) {
			gradient=g;
		}
		
		@Override
		public int filterRGB(int x, int y, int argb) {
	    	int r=Colours.getRed(gradient[Colours.getRed(argb)]);
	    	int g=Colours.getGreen(gradient[Colours.getGreen(argb)]);
	    	int b=Colours.getBlue(gradient[Colours.getBlue(argb)]);
	    	int a=Colours.getAlpha(argb);
	
	    	return Colours.getARGBQuick(r, g, b,a);
	    }
	}
	
	static class IntensityRGBGradientFilter extends RGBImageFilter {
		public int[] gradient;
	
		public IntensityRGBGradientFilter(int[] g) {
			gradient=g;
		}
		
		@Override
		public int filterRGB(int x, int y, int argb) {
	    	int lum=Colours.getLuminance(argb);
	    	int rgb=gradient[lum]&Colours.RGB_MASK;
	    	return Colours.getARGB(rgb, Colours.getAlpha(argb));
	    }
	}
	
	public static BufferedImage applyToRGBComponents(BufferedImage b, int[] g) {
		ImageFilter filter=new RGBComponentGradientFilter(g);
		BufferedImage result=Op.apply(b, filter);
		return result;
	}
	
	public static BufferedImage applyToIntensity(BufferedImage b, int[] g) {
		ImageFilter filter=new IntensityRGBGradientFilter(g);
		BufferedImage result=Op.apply(b, filter);
		return result;
	}
	
	public Gradient clone() {
		return new Gradient(data.clone());
	}
}
