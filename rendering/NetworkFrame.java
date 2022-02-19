package louis_guo.rendering;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import louis_guo.network.Network;

public class NetworkFrame extends JFrame {
	private static final long serialVersionUID = -8988922372699935477L;

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
		setIconImage((new ImageIcon(NetworkFrame.class.getResource("/louis_guo/assets/network-icon.png"))).getImage());
		
		add(panel, BorderLayout.CENTER);
		pack();
		
		setVisible(true);
	}
}
