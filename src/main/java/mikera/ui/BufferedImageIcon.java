package mikera.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.Icon;

public final class BufferedImageIcon implements Icon {
	private final BufferedImage image;
	private final int x;
	private final int y;
	private final int w;
	private final int h;
	private final int dw;
	private final int dh;

	public BufferedImageIcon(BufferedImage b) {
		this(b,0,0,b.getWidth(),b.getHeight());
	}
	
	public BufferedImageIcon(BufferedImage b,int sx, int sy, int sw, int sh) {
		image=b;
		x=sx;
		y=sy;
		w=sw;
		h=sh;
		dw=sw;
		dh=sh;
	}
	
	public BufferedImageIcon(BufferedImage b,int sx, int sy, int sw, int sh, int destw, int desth) {
		image=b;
		x=sx;
		y=sy;
		w=sw;
		h=sh;
		dw=destw;
		dh=desth;
	}
	
	public void paintIcon(Component c, Graphics g, int dx, int dy) {
		g.drawImage(image, dx, dy, dx+dw, dy+dh, x, y, x+w, y+h, null);
	}

	public int getIconWidth() {
		return dw;
	}

	public int getIconHeight() {
		return dh;
	}

}
