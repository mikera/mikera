package mikera.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import mikera.math.Function;
import mikera.math.Functions;
import mikera.math.PerlinNoise;
import mikera.math.PerlinScalar;
import mikera.math.VF;
import mikera.math.Vector;
import mikera.math.VectorFunction;
import mikera.util.Maths;
import mikera.util.Rand;

public class Generator {
	public static BufferedImage createFromArray(float[] arr, int offset, int w, int h) {
		BufferedImage bi=newImage(w,h);
		for (int y=0; y<h ; y++) {
			for (int x=0; x<w; x++) {
				bi.setRGB(x, y, Colours.toGreyScale(arr[offset++]));
			}		
		}
		return bi;
	}
	
	public static BufferedImage createFromArray(double[] arr, int offset, int w, int h) {
		BufferedImage bi=newImage(w,h);
		for (int y=0; y<h ; y++) {
			for (int x=0; x<w; x++) {
				bi.setRGB(x, y, Colours.toGreyScale(arr[offset++]));
			}		
		}
		return bi;
	}
	
	
	public static BufferedImage createTiledImage(BufferedImage bi, int tw, int th) {
		int h=bi.getHeight();
		int w=bi.getWidth();
		BufferedImage result=newImage(w*tw,h*th);
		Graphics2D gr=result.createGraphics();
		for (int y=0; y<th; y++) {
			for (int x=0; x<tw; x++) {
				gr.drawImage(bi,x*w,y*h,w,h,null);
			}
		}
		return result;
	}
	
	public static BufferedImage unTileImage(BufferedImage bi, int tw, int th) {
		int h=bi.getHeight()/tw;
		int w=bi.getWidth()/th;
		BufferedImage result=newImage(w,h);
		Graphics2D gr=result.createGraphics();
		gr.drawImage(bi,-(tw-1)*w/2,-(th-1)*h/2,null);
		return result;
	}
	
	public static BufferedImage createSolidImage(int w, int h,int c) {
		BufferedImage result=newImage(w,h);
		Graphics2D g=result.createGraphics();
		g.setColor(new Color(c));
		g.fillRect(0, 0, w, h);
		return result;
	}
	
	public static BufferedImage newImage(int w, int h) {
		BufferedImage result=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		return result;
	}
	
	static PerlinNoise perlin=new PerlinNoise();
	
	public static BufferedImage createPerlinNoise(int w, int h, double s) {
		BufferedImage b=newImage(w,h);
		float sx=(float)s/w;
		float sy=(float)s/h;
		
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				//double nv=perlin.noise3(x*sx, y*sy,0);
				//double nv=perlin.tileableNoise3(x*sx, y*sy, 0 , w*sx, h*sy ,256);
				double nv=PerlinScalar.pnoise(x*sx, y*sy, 0 , w*sx, h*sy ,256);
				//int a=Maths.clampToInteger((128+128*1.9*nv), 0, 255);
				int a=Maths.clampToInteger(255*nv, 0, 255);
				b.setRGB(x, y, Colours.ALPHA_MASK+0x010101*a);
			}
		}
		return b;
	}
	
	public static BufferedImage createFunction4(int w, int h, Function<Vector,Vector> f) {
		BufferedImage b=newImage(w,h);
		Vector input=new Vector(2);
		Vector output=new Vector(4);
		
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				input.data[0]=x/(float)w;
				input.data[1]=y/(float)h;
				f.calculate(input, output);
				int rgba=Colours.getARGBClamped4(output);
				b.setRGB(x, y, rgba);
			}
		}
		return b;
	}
	
	public static BufferedImage createFunctionGradient(int w, int h, Function<Vector,Vector> f, Gradient grad) {
		BufferedImage b=newImage(w,h);
		Vector input=new Vector(2);
		Vector output=new Vector(20);
		
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				input.data[0]=x/(float)w;
				input.data[1]=y/(float)h;
				f.calculate(input, output);
				int max=grad.size()-1;
				int i=Maths.clampToInteger(output.data[0]*max, 0, max);
				int rgba=grad.get(i);
				b.setRGB(x, y, rgba);
			}
		}
		return b;
	}
	
	public static BufferedImage createChecker(int w, int h, int tiles, int c1, int c2) {
		BufferedImage b=newImage(w,h);
		
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				int i=x*tiles/w+y*tiles/h;
				b.setRGB(x, y, ((i&1)==0)?c1:c2);
			}
		}
		return b;
	}
	
	public static BufferedImage createGradientImage(Gradient gradient) {
		int size=gradient.size();
		BufferedImage b=newImage(size,size);
		
		for (int y=0; y<size; y++) {
			for (int x=0; x<size; x++) {
				b.setRGB(x, y, gradient.get(x));
			}
		}
		return b;
	}
	
	public static BufferedImage createGradientCircle(Gradient gradient, int r) {
		int size=gradient.size();
		BufferedImage b=newImage(r*2,r*2);
		
		for (int y=-r; y<r; y++) {
			for (int x=-r; x<r; x++) {
				int i=(int)(Maths.sqrt(x*x+y*y)*size/r);
				if (i<size) {	
					b.setRGB(r+x, r+y, gradient.get(i));
				}
			}
		}
		return b;
	}
	

	public static BufferedImage createWhiteNoise(int w, int h) {
		BufferedImage b=newImage(w,h);
		int s=1;
		
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				s=Rand.xorShift32(s);
				//int a=Rand.r(256);
				int a =s>>>24;
				b.setRGB(x, y, 0xFF000000+0x010101*a);
			}
		}
		return b;
	}
	
	public static BufferedImage createConvolvedNoise(int w, int h) {
		BufferedImage b=newImage(w,h);
		
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				double cx=Math.sin(x/20+10*Math.sin(y/33.0));
				double cy=Math.sin(y/30+15*Math.sin(x/22.0));
				cx=Math.sin(cy+Math.sin((cx+x/23.0)));
				cy=Math.sin(cx+Math.sin((cy-y/13.0)));

				int a=(int)(128+127*Math.sin(cx)*Math.cos(cy));
				b.setRGB(x, y, 0xFF000000+0x010101*a);
			}
		}
		return b;
	}

	/**
	 * Test function
	 */
	public static void main(String[] args) {
		// background
		BufferedImage bg=createChecker(1024,1024,64,0xFFFFFFFF,0xFFE0B0D0);	
		
		VectorFunction f = Functions.createLandscapeFunction(7);
		
		Gradient grad=Gradient.createLandscapeGradient();
		//BufferedImage e=createFunction4(512,512,f);	
		BufferedImage e=createFunctionGradient(512,512,f, grad);
		
		grad=Gradient.createMonoGradient();
		f=VF.max(f,0.5);
		
		Vector v=new Vector(-0.001,-0.001);
		f=VF.madd(f, VF.offset(f, v), -1);
		f=VF.multiply(f, 100);
		f=VF.add(f, 0.5);
		BufferedImage ee=createFunctionGradient(512,512,f, grad);
		
		e=Op.multiply(e, ee);
		e=Op.multiply(e, 2, 2, 2, 1);
		
		Graphics2D gr=bg.createGraphics();
		gr.drawImage(e, 0, 0, null);
		ImageUtils.displayAndExit(bg);
		System.err.println("Done image generation");
	}

}
