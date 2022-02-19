package louis_guo.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import javax.swing.JPanel;

import louis_guo.Helper;

public class OutputPanel extends JPanel {
	private static final long serialVersionUID = -5771185086380692789L;
	
	public int margin_x = 50;
	public int margin_y = 20;
	public int padding_x = 10;
	public int padding_y = 10;
	public int size_x = 30;
	public int size_y = 90;
	public int font_size = 20;
	
	public double[] label;
	public double[] output;
	
	public boolean draw_mode;
	
	public OutputPanel(double[] label, double[] output, boolean draw_mode) {
		setPreferredSize(new Dimension((2 * margin_x) + (10 * size_x) + (9 * padding_x), (2 * margin_y) + size_y + font_size + padding_y));
		
		setBackground(Color.BLACK);
		
		this.label = label;
		this.output = output;
		this.draw_mode = draw_mode;
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		
		Map<Double, Integer> value_map = new HashMap<>();
		
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setStroke(new BasicStroke(2));
		
		g2D.setFont(new Font("Arial", Font.PLAIN, font_size));
		g2D.setColor(Color.WHITE);
		
		g2D.drawRect(1, 1, (2 * margin_x) + (10 * size_x) + (9 * padding_x) - 2, (2 * margin_y) + size_y + font_size + padding_y - 2);
		
		double[] display_label = new double[10];
		Helper.setArray(display_label, output);
		
		double max = 0.0;
		
		for(double n : display_label) {
			if(n > max) {
				max = n;
			}
		}
		
		for(int i = 0; i < display_label.length; i++) {
			value_map.put(display_label[i] / max, i);
		}
		
		Arrays.sort(display_label);
		
		for(int i = 0; i < display_label.length; i++) {
			display_label[i] /= max;
		}
		
		double max_value = 0.0;
		int max_i = 0;
		
		if(!draw_mode) {
			for(int i = 0; i < label.length; i++) {
				if(label[i] >= max_value) {
					max_value = label[i];
					max_i = i;
				}
			}
		}
		
		for(int i = 0; i < display_label.length; i++) {
			if(!draw_mode && i == 0) {
				g2D.setColor(Color.RED);
			}
			else {
				g2D.setColor(Color.WHITE);
			}
			
			if(!draw_mode && value_map.get(display_label[display_label.length - 1 - i]) == max_i) {
				g2D.setColor(Color.GREEN);
			}
			
			g2D.fillRect(margin_x + (size_x + padding_x) * i, margin_y + font_size + padding_y, size_x, (int)Math.round(size_y * display_label[display_label.length - 1 - i]));
			
			if(i == 0) {
				g2D.setFont(new Font("Arial", Font.BOLD, 20));
			}
			g2D.drawString(Integer.toString(value_map.get(display_label[display_label.length - 1 - i])), margin_x + (font_size / 2) + (size_x + padding_x) * i, margin_y + font_size);
			g2D.setFont(new Font("Arial", Font.PLAIN, 20));
		}
	}
}
