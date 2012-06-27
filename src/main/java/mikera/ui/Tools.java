package mikera.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;


public class Tools {
	public static Dimension getScreenSize() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		return dim;
	}

	public static JFrame showComponent(Component c) {
		JFrame f=new JFrame("View Component");
		f.getContentPane().add(c);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		return f;
	}	
	
	public static JFrame showFillingComponent(Component c) {
		JFrame f=new JFrame("View Component");
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(c);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		return f;
	}	
}
