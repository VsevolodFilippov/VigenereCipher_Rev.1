package cryptography;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel{
	private Image img;
	
	public ImagePanel(Image img) {
		this.img=img;
	}
	
	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}
}
