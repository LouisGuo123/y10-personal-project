package louis_guo.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {
	private static final long serialVersionUID = -5445695756091975798L;
	
	public int margin_x = 20;
	public int margin_y = 5;
	public int padding = 0;
	public int size = 20;
	
	public MainPanel main_panel;
	
	public double[] image;
	
	public boolean doRender = true;
	
	public int prevX = -1;
	public int prevY = -1;
	
	public ImagePanel(double[] image, MainPanel main_panel) {
		setPreferredSize(new Dimension((2 * margin_x) + (28 * size) + (27 * padding), (2 * margin_y) + (28 * size) + (27 * padding)));
		
		setBackground(Color.BLACK);
		
		this.image = image;
		
		this.main_panel = main_panel;
	}
	
	@Override
	public void paint(Graphics g) {
		if(!doRender) {
			return;
		}
		
		Graphics2D g2D = (Graphics2D)g;
		
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setStroke(new BasicStroke(2));
		
		g2D.setColor(Color.WHITE);
		g2D.drawRect(margin_x, margin_y, (28 * size) + (27 * padding), (28 * size) + (27 * padding));
		
		for(int i = 0; i < 28; i++) {
			for(int j = 0; j < 28; j++) {
				g2D.setColor(new Color(1f, 1f, 1f, (float)image[i * 28 + j]));
				g2D.fillRect(margin_x + (size + padding) * j, margin_y + (size + padding) * i, size, size);
			}
		}
	}
}
