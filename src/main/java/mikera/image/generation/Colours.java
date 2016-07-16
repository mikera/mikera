package mikera.image.generation;

import java.awt.Color;

import mikera.math.Vector;
import mikera.util.Maths;

/**
 * Class providing static methods for manipulation of colour values
 * 
 * @author Mike
 *
 */
public class Colours {
	public static int RGB_MASK=   0x00FFFFFF;
	public static int RED_MASK=   0x00FF0000;
	public static int GREEN_MASK= 0x0000FF00;
	public static int BLUE_MASK=  0x000000FF;
	public static int ALPHA_MASK= 0xFF000000;
	public static final int BYTE_MASK=255;
	
	private static final int MAX_BYTE=255;
	private static final float INVERSE_FLOAT_FACTOR=1.0f/MAX_BYTE;
	
	/**
	 * Gets the red value of an ARGB colour int
	 * 
	 * @param argb
	 * @return
	 */
	public static int getRed(int argb) {
		return ((argb>>16)&255);
	}
	
	/**
	 * Gets the green value of an ARGB colour int
	 * 
	 * @param argb
	 * @return
	 */
	public static int getGreen(int argb) {
		return ((argb>>8)&255);
	}
	
	/**
	 * Gets the blue value of an ARGB colour int
	 * 
	 * @param argb
	 * @return
	 */
	public static int getBlue(int argb) {
		return ((argb)&255);
	}
	
	/**
	 * Gets the alpha value of an ARGB colour int
	 * 
	 * @param argb
	 * @return
	 */
	public static int getAlpha(int argb) {
		return ((argb>>24)&255);
	}
	
	/**
	 * Gets the grayscale equivalent of an ARGB int
	 * 
	 * Sets alpha to 255 (fully opaque)
	 * 
	 * @param argb
	 * @return
	 */
	public static int toGreyScale(int argb) {
    	int lum=getLuminance(argb);
    	return (argb&ALPHA_MASK)|(0x010101*lum);
	}

	/**
	 * Gets the luminance of an ARGB int
	 * 
	 * @param argb
	 * @return
	 */
	public static int getLuminance(int argb) {
    	int lum= (   77  * ((argb>>16)&255) 
    		       + 150 * ((argb>>8)&255) 
    		       + 29  * ((argb)&255))>>8;
    	return lum;
	}
	
	/**
	 * Converts double component values to an ARGB int
	 * 
	 * @param r Red value on 0..1 scale
	 * @param g Green value on 0..1 scale
	 * @param b Blue value on 0..1 scale
	 * @param a Alpha value on 0..1 scale
	 * @return
	 */
	public static int getARGBClamped(double r, double g, double b, double a) {
    	int ri=Maths.clampToInteger(r*MAX_BYTE, 0, MAX_BYTE);
    	int gi=Maths.clampToInteger(g*MAX_BYTE, 0, MAX_BYTE);
    	int bi=Maths.clampToInteger(b*MAX_BYTE, 0, MAX_BYTE);
    	int ai=Maths.clampToInteger(a*MAX_BYTE, 0, MAX_BYTE);
		return getARGBQuick(ri,gi,bi,ai);
	}
	
	/**
	 * Converts float component values to an ARGB int
	 * 
	 * @param r Red value on 0..1 scale
	 * @param g Green value on 0..1 scale
	 * @param b Blue value on 0..1 scale
	 * @param a Alpha value on 0..1 scale
	 * @return
	 */	
	public static int getARGBClamped(float r, float g, float b, float a) {
    	int ri=Maths.clampToInteger(r*MAX_BYTE, 0, MAX_BYTE);
    	int gi=Maths.clampToInteger(g*MAX_BYTE, 0, MAX_BYTE);
    	int bi=Maths.clampToInteger(b*MAX_BYTE, 0, MAX_BYTE);
    	int ai=Maths.clampToInteger(a*MAX_BYTE, 0, MAX_BYTE);
		return getARGBQuick(ri,gi,bi,ai);
	}
	
	/**
	 * Converts float component values to an ARGB int
	 * 
	 * @param r Red value on 0..1 scale
	 * @param g Green value on 0..1 scale
	 * @param b Blue value on 0..1 scale
	 * @return
	 */	
	public static int getRGBClamped(float r, float g, float b) {
    	int ri=Maths.clampToInteger(r*MAX_BYTE, 0, MAX_BYTE);
    	int gi=Maths.clampToInteger(g*MAX_BYTE, 0, MAX_BYTE);
    	int bi=Maths.clampToInteger(b*MAX_BYTE, 0, MAX_BYTE);
		return getRGBQuick(ri,gi,bi);
	}
	
	/**
	 * Converts double component values to an ARGB int
	 * 
	 * @param r Red value on 0..1 scale
	 * @param g Green value on 0..1 scale
	 * @param b Blue value on 0..1 scale
	 * @return
	 */	
	public static int getRGBClamped(double r, double g, double b) {
    	int ri=Maths.clampToInteger(r*MAX_BYTE, 0, MAX_BYTE);
    	int gi=Maths.clampToInteger(g*MAX_BYTE, 0, MAX_BYTE);
    	int bi=Maths.clampToInteger(b*MAX_BYTE, 0, MAX_BYTE);
		return getRGBQuick(ri,gi,bi);
	}
	
	public static int getARGBClamped3(Vector v) {
		int result=ALPHA_MASK;
		float r=v.data[0];
    	result+=Maths.clampToInteger(r*MAX_BYTE, 0, MAX_BYTE)<<16;
		float g=v.data[1];
		result+=Maths.clampToInteger(g*MAX_BYTE, 0, MAX_BYTE)<<8;
		float b=v.data[2];
		result+=Maths.clampToInteger(b*MAX_BYTE, 0, MAX_BYTE);
		return result;
	}
	
	public static int getARGBClamped4(Vector v) {
		int result=0x00000000;
		float r=v.data[0];
    	result+=Maths.clampToInteger(r*MAX_BYTE, 0, MAX_BYTE)<<16;
		float g=v.data[1];
		result+=Maths.clampToInteger(g*MAX_BYTE, 0, MAX_BYTE)<<8;
		float b=v.data[2];
		result+=Maths.clampToInteger(b*MAX_BYTE, 0, MAX_BYTE);
		float a=v.data[3];
		result+=Maths.clampToInteger(a*MAX_BYTE, 0, MAX_BYTE)<<24;
		return result;
	}
	
	public static int getARGBClamped(int r, int g, int b, int a) {
    	int ri=Maths.bound(0, r, MAX_BYTE);
    	int gi=Maths.bound(0, g, MAX_BYTE);
    	int bi=Maths.bound(0, b, MAX_BYTE);
    	int ai=Maths.bound(0, a, MAX_BYTE);
		return getARGBQuick(ri,gi,bi,ai);
	}
	
	public static int getARGBClamped(long r, long g, long b, long a) {
    	int ri=(int)Maths.bound(0, r, MAX_BYTE);
    	int gi=(int)Maths.bound(0, g, MAX_BYTE);
    	int bi=(int)Maths.bound(0, b, MAX_BYTE);
    	int ai=(int)Maths.bound(0, a, MAX_BYTE);
		return getARGBQuick(ri,gi,bi,ai);
	}
	
	/**
	 * Converts an ARGB value to a array of floats
	 * 
	 * @param col
	 * @param offset
	 * @param argb
	 */
	public static void toFloat4(float[] col, int offset, int argb) {
		col[offset]=getRed(argb)*INVERSE_FLOAT_FACTOR;
		col[offset+1]=getGreen(argb)*INVERSE_FLOAT_FACTOR;
		col[offset+2]=getBlue(argb)*INVERSE_FLOAT_FACTOR;
		col[offset+3]=getAlpha(argb)*INVERSE_FLOAT_FACTOR;
	}
	
	
	/**
	 * Converts an array of floats to an ARGB value
	 * 
	 * @param col
	 * @param offset
	 * @return
	 */
	public static int fromFloat4(float[] col, int offset) {
		return getARGBClamped(col[offset],col[offset+1],col[offset+2],col[offset+3]);
	}
	
	public static int fromDouble3(double[] col, int offset) {
		return getRGBClamped(col[offset],col[offset+1],col[offset+2]);
	}
	
	public static int fromFloat3(float[] col, int offset) {
		return getRGBClamped(col[offset],col[offset+1],col[offset+2]);
	}
	
	public static int fromVector4(Vector col) {
		return fromFloat4(col.data,0);
	}
	
	public static int fromVector3(Vector col) {
		return fromFloat3(col.data,0);
	}
	
	public static int getARGB(int r, int g, int b, int a) {
		return getARGBQuick(r&255,g&255,b&255,a&255);
	}
	
	public static int getARGB(int r, int g, int b) {
		return getARGBQuick(r&255,g&255,b&255,255);
	}
	
	public static int getARGB(int rgb, int alpha) {
		return (rgb&RGB_MASK)|((alpha&BYTE_MASK)<<24);
	}
	
	public static int getRGB(Vector v3) {
		return v3.toRGBColour();
	}
	
	public static int getARGB(Vector v4) {
		return v4.toARGBColour();
	}
	
	static int getARGBQuick(int r, int g, int b, int a) {
		return (a<<24)|(r<<16)|(g<<8)|b;
	} 
	
	static int getRGBQuick(int r, int g, int b) {
		return ALPHA_MASK|(r<<16)|(g<<8)|b;
	} 
	
	public Color getRGBColor(int rgb) {
		int r=getRed(rgb);
		int g=getGreen(rgb);
		int b=getBlue(rgb);
		return new Color(r,g,b);
	}
	
	public static Color getColor(int argb) {
		int r=getRed(argb);
		int g=getGreen(argb);
		int b=getBlue(argb);
		int a=getAlpha(argb);
		return new Color(r,g,b,a);
	}

	public static Color getColor(int r, int g, int b) {
		return new Color(r,g,b);
	}

	public static int toGreyScale(float f) {
		return 0xFF000000|(0x10101*floatToByte(f));
	}
	
	public static int toGreyScale(double d) {
		return 0xFF000000|(0x10101*doubleToByte(d));
	}
	
	public static int toGreen(float f) {
		return 0xFF000000|(0x000100*floatToByte(f));
	}

	private static int floatToByte(float f) {
		return Maths.bound(0, (int)(f*255),255);
	}
	
	private static int doubleToByte(double d) {
		return Maths.bound(0, (int)(d*255),255);
	}


}
