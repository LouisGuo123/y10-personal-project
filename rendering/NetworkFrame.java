package louis_guo.rendering;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;

import louis_guo.network.Network;
import louis_guo.rendering.MainPanel;

public class NetworkFrame extends JFrame {
	
	public MainPanel panel;
	
	public double[][] images;
	public double[][] labels;
	
	public Network network;
	
	public NetworkFrame(double[][] images, double[][] labels, Network network) {
		this.images = images;
		this.labels = labels;
		this.network = network;
		
		panel = new MainPanel(this.images, this.labels, this.network);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(772, 772);
		setLocationRelativeTo(null);
		
		setBackground(Color.BLACK);
		
		add(panel, BorderLayout.CENTER);
		pack();
		
		setVisible(true);
	}
}
