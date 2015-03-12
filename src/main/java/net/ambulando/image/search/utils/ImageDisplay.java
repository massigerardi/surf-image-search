/**
 * 
 */
package net.ambulando.image.search.utils;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Massimiliano Gerardi
 * Mar 12, 2015
 */
public class ImageDisplay extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3270259836156774703L;

	public ImageDisplay(BufferedImage image) {
		this.setSize(650, 650);
		this.setLocation(100, 100);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel jLabel = new JLabel(new ImageIcon(image));
        JPanel jPanel = new JPanel();
        jPanel.add(jLabel);
        jPanel.setLocation(25, 25);
        this.add(jPanel);
	}

	public void display() {
		setVisible(true);
	}
	
}
