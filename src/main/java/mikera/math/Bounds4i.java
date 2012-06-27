package mikera.math;

public class Bounds4i {
	public int xmin=Integer.MAX_VALUE;
	public int xmax=Integer.MIN_VALUE;
	public int ymin=Integer.MAX_VALUE;
	public int ymax=Integer.MIN_VALUE;
	
	public int getWidth() {
		return (xmin<Integer.MAX_VALUE)?xmax-xmin+1:0;
	}
	
	public int getHeight() {
		return (ymin<Integer.MAX_VALUE)?ymax-ymin+1:0;
	}

}
