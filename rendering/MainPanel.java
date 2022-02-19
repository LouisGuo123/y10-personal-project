package louis_guo.rendering;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JFormattedTextField;

import louis_guo.network.Network;
import louis_guo.Helper;

public class MainPanel extends JPanel {
	private static final long serialVersionUID = -6829867209149395940L;
	
	public JLabel image_text;
	public ImagePanel image_panel;
	
	public JLabel output_text;
	public OutputPanel output_panel;
	
	public JTabbedPane options_pane;
	
	public JPanel database_panel;
	public JButton roll_random;
	public JButton roll_random_correct;
	public JButton roll_random_wrong;
	public JLabel label_text;
	public JComboBox<String> selected_label;
	public JLabel goto_text;
	public NumberFormat goto_format;
	public JFormattedTextField goto_field;
	public JButton goto_button;
	public JButton get_index;
	
	public JPanel drawing_panel;
	public JButton clear_image;
	public JLabel weight_text;
	public JLabel size_text;
	public JComboBox<Double> selected_weight;
	public JComboBox<String> selected_size;
	
	public JButton help_button;
	public JFrame help_frame;
	public JDialog help_dialog;
	
	public JTextPane help_text;
	public JScrollPane help_scroll;
	
	public double[][] images;
	public double[][] labels;
	public Network network;
	
	public double[] image;
	public double[] label;
	public double[] output;
	
	public Map<Integer, Map<Integer, List<Integer>>> image_map = new HashMap<>();
	
	public Random rand = new Random(ThreadLocalRandom.current().nextLong());
	
	public GridBagLayout layout = new GridBagLayout();
	public GridBagConstraints constraints = new GridBagConstraints();
	
	public CardLayout options_layout = new CardLayout();
	
	int result_index;
	
	public MainPanel(double[][] images, double[][] labels, Network network) {
		setLayout(layout);
		
		this.images = images;
		this.labels = labels;
		this.network = network;
		
		image = new double[28*28];
		label = new double[10];
		output = new double[10];
		
		initMap();
		
		image_text = new JLabel("Network Input Image");
		image_text.setForeground(Color.WHITE);
		image_text.setFont(new Font("Arial", Font.PLAIN, 20));
		
		image_panel = new ImagePanel(image, this);
		image_panel.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				int currX = e.getX();
				int currY = e.getY();
				
				for(int i = 0; i < 28; i++) {
					for(int j = 0; j < 28; j++) {
						int circX = image_panel.margin_x + (image_panel.size / 2) + (image_panel.size + image_panel.padding) * j;
						int circY = image_panel.margin_y + (image_panel.size / 2) + (image_panel.size + image_panel.padding) * i;
						
						if(Math.abs(currX - currY - circX + circY) + Math.abs(currX + currY - circX - circY) <= image_panel.size) {
							setImageIndex(i * 28 + j);
						}
					}
				}
				
				image_panel.prevX = currX;
				image_panel.prevY = currY;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				image_panel.prevX = -1;
				image_panel.prevY = -1;
			}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
		});
		image_panel.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int currX = e.getX();
				int currY = e.getY();
				
				int currY_c = -currY;
				int prevY_c = -image_panel.prevY;
				
				double slope = (double)(currY_c - prevY_c) / (double)(currX - image_panel.prevX);
				double slope_p = -1/slope;
				
				for(int i = 0; i < 28; i++) {
					for(int j = 0; j < 28; j++) {
						int circX = image_panel.margin_x + (image_panel.size / 2) + (image_panel.size + image_panel.padding) * j;
						int circY = image_panel.margin_y + (image_panel.size / 2) + (image_panel.size + image_panel.padding) * i;
						
						int circY_c = -circY;
						
						double newX;
						double newY;
						
						if(currX - image_panel.prevX == 0 && currY_c - prevY_c == 0) {
							newX = currX;
							newY = currY;
						}
						else if(currX - image_panel.prevX == 0) {
							newX = currX;
							newY = Math.max(Math.min(circY, Math.max(currY, image_panel.prevY)), Math.min(currY, image_panel.prevY));
						}
						else if(currY_c - prevY_c == 0) {
							newX = Math.max(Math.min(circX, Math.max(currX, image_panel.prevX)), Math.min(currX, image_panel.prevX));;
							newY = currY;
						}
						else {
							newX = (double)(slope * currX - currY_c - slope_p * circX + circY_c) / (slope - slope_p);
							newY = -(slope * newX - slope * currX + currY_c);
							
							newX = Helper.clamp(currX, image_panel.prevX, newX);
							newY = Helper.clamp(currY, image_panel.prevY, newY);
						}
						
						if(Math.abs(newX - newY - circX + circY) + Math.abs(newX + newY - circX - circY) <= image_panel.size) {
							setImageIndex(i * 28 + j);
						}
					}
				}
				
				image_panel.prevX = currX;
				image_panel.prevY = currY;
			}
			
			@Override
			public void mouseMoved(MouseEvent e) {}
		});
		
		output_text = new JLabel("Network Output");
		output_text.setForeground(Color.WHITE);
		output_text.setFont(new Font("Arial", Font.PLAIN, 20));
		
		output_panel = new OutputPanel(label, output, false);
		
		options_pane = new JTabbedPane();
		
		database_panel = new JPanel(layout);
		database_panel.setBackground(Color.BLACK);
		
		roll_random = new JButton("Roll Random Image");
		roll_random.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				randomImage();
			}
		});
		roll_random.setToolTipText("Set the Input Image to a Random Image from the Dataset");
		
		roll_random_correct = new JButton("Roll Random Correct Image");
		roll_random_correct.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				randomCorrectImage();
			}
		});
		roll_random_correct.setToolTipText("Set the Input Image to a Random Image from the Dataset that the AI Identified Correctly");
		
		roll_random_wrong = new JButton("Roll Random Incorrect Image");
		roll_random_wrong.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				randomWrongImage();
			}
		});
		roll_random_wrong.setToolTipText("Set the Input Image to a Random Image from the Dataset that the AI Identified Incorrectly");
		
		label_text = new JLabel("Number Filter");
		label_text.setForeground(Color.WHITE);
		
		selected_label = new JComboBox<String>(new String[] {"None", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});
		selected_label.setToolTipText("Limit the Random Images to a Specific Number");
		
		goto_text = new JLabel("Image ID");
		goto_text.setForeground(Color.WHITE);
		
		goto_format = NumberFormat.getIntegerInstance();
		
		goto_field = new JFormattedTextField(goto_format);
		goto_field.setColumns(5);
		goto_field.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		goto_field.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				validateGoto();
			}

			@Override
			public void focusLost(FocusEvent e) {}
		});
		goto_field.setText("0");
		goto_field.setToolTipText("Go to a Specific Image in the Testing Database");
		
		goto_button = new JButton("Go to Specific Image ID");
		goto_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gotoImage();
			}
		});
		goto_button.setToolTipText("Go to a Specific Image in the Testing Database");
		
		get_index = new JButton("Get Current Image ID");
		get_index.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getImageIndex();
			}
		});
		get_index.setToolTipText("Get the Current Image ID");
		
		drawing_panel = new JPanel(layout);
		drawing_panel.setBackground(Color.BLACK);
		
		clear_image = new JButton("Clear Image");
		clear_image.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearImage();
			}
		});
		clear_image.setToolTipText("Make the Input Image Blank (All Value 0.0)");
		
		weight_text = new JLabel("Brush Weight");
		weight_text.setForeground(Color.WHITE);
		
		selected_weight = new JComboBox<Double>(new Double[] {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0});
		selected_weight.setSelectedIndex(10);
		selected_weight.setToolTipText("Set the Brush Painting Value");
		
		size_text = new JLabel("Brush Size");
		size_text.setForeground(Color.WHITE);
		
		selected_size = new JComboBox<String>(new String[] {"Small", "Large"});
		selected_size.setSelectedIndex(1);
		selected_size.setToolTipText("Set the Brush Size");
		
		help_button = new JButton("Um... What?");
		help_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				help_dialog.setVisible(true);
			}
		});
		
		help_frame = new JFrame();
		
		help_dialog = new JDialog(help_frame);
		help_dialog.setVisible(false);
		help_dialog.setSize(new Dimension(500, 500));
		help_dialog.setBackground(Color.BLACK);
		help_dialog.setLocationRelativeTo(null);
		
		help_text = new JTextPane();
		help_text.setEditable(false);
		
		help_scroll = new JScrollPane(help_text);
		
		randomImage();
		
		setBackground(Color.BLACK);
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.CENTER;
		constraints.insets = new Insets(20, 0, 0, 0);
		add(image_text, constraints);
		
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.insets = new Insets(0, 0, 0, 0);
		add(image_panel, constraints);
		
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.insets = new Insets(10, 0, 5, 0);
		add(output_text, constraints);
		
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.insets = new Insets(0, 0, 20, 0);
		add(output_panel, constraints);
		
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridheight = 2;
		constraints.insets = new Insets(0, 0, 0, 20);
		add(options_pane, constraints);
		
		options_pane.addTab("Database Navigation", database_panel);
		options_pane.addTab("Drawing Options", drawing_panel);
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridheight = 1;
		constraints.insets = new Insets(10, 0, 0, 0);
		database_panel.add(roll_random, constraints);
		
		constraints.gridy = 1;
		database_panel.add(roll_random_correct, constraints);
		
		constraints.gridy = 2;
		database_panel.add(roll_random_wrong, constraints);
		
		constraints.gridy = 3;
		database_panel.add(label_text, constraints);
		
		constraints.gridy = 4;
		constraints.insets = new Insets(0, 0, 0, 0);
		database_panel.add(selected_label, constraints);
		
		constraints.gridy = 5;
		constraints.insets = new Insets(10, 0, 0, 0);
		database_panel.add(goto_text, constraints);
		
		constraints.gridy = 6;
		constraints.insets = new Insets(0, 0, 0, 0);
		database_panel.add(goto_field, constraints);
		
		constraints.gridy = 7;
		database_panel.add(goto_button, constraints);
		
		constraints.gridy = 8;
		database_panel.add(get_index, constraints);
		
		constraints.gridy = 0;
		constraints.insets = new Insets(10, 0, 0, 0);
		drawing_panel.add(clear_image, constraints);
		
		constraints.gridy = 1;
		drawing_panel.add(weight_text, constraints);
		
		constraints.gridy = 2;
		constraints.insets = new Insets(0, 0, 0, 0);
		drawing_panel.add(selected_weight, constraints);
		
		constraints.gridy = 3;
		constraints.insets = new Insets(10, 0, 0, 0);
		drawing_panel.add(size_text, constraints);
		
		constraints.gridy = 4;
		constraints.insets = new Insets(0, 0, 0, 0);
		drawing_panel.add(selected_size, constraints);
		
//		constraints.gridx = 1;
//		constraints.gridy = 2;
//		constraints.gridheight = 2;
//		add(help_button, constraints);
		
		help_dialog.add(help_scroll);
	}
	
	public void initMap() {
		for(Integer i = 0; i < 10; i++) {
			Map<Integer, List<Integer>> temp_map = new HashMap<>();
			
			for(int j = 0; j < 3; j++) {
				temp_map.put(j, new ArrayList<>());
			}
			
			image_map.put(i, temp_map);
		}
		
		for(int i = 0; i < images.length; i++) {
			double[] temp_image = new double[images[i].length];
			double[] temp_label = new double[labels[i].length];
			Helper.setArray(temp_image, images[i]);
			Helper.setArray(temp_label, labels[i]);
			network.feedforward(temp_image);
			double[] temp_output = network.getOutput();
			
			double max_label_value = temp_label[0];
			int max_label_index = 0;
			
			double max_output_value = temp_output[0];
			int max_output_index = 0;
			
			for(int j = 1; j < 10; j++) {
				if(temp_label[j] > max_label_value) {
					max_label_value = temp_label[j];
					max_label_index = j;
				}
				if(temp_output[j] > max_output_value) {
					max_output_value = temp_output[j];
					max_output_index = j;
				}
			}
			
			image_map.get(max_label_index).get(0).add(i);
			
			if(max_label_index == max_output_index) {
				image_map.get(max_label_index).get(1).add(i);
			}
			else {
				image_map.get(max_label_index).get(2).add(i);
			}
		}
	}
	
	public void randomImage() {
		Integer index = getLabel();
		
		if(index == null) {
			index = rand.nextInt(10);
		}
		
		List<Integer> possible_values = image_map.get(index).get(0);
		int i = possible_values.get(rand.nextInt(possible_values.size()));
		
		Helper.setArray(image, images[i]);
		Helper.setArray(label, labels[i]);
		network.feedforward(image);
		Helper.setArray(output, network.getOutput());
		
		image_panel.repaint();
		output_panel.repaint();
		
		result_index = i;
		
		output_panel.draw_mode = false;
	}
	
	public void randomCorrectImage() {
		Integer index = getLabel();
		
		if(index == null) {
			index = rand.nextInt(10);
		}
		
		List<Integer> possible_values = image_map.get(index).get(1);
		int i = possible_values.get(rand.nextInt(possible_values.size()));
		
		Helper.setArray(image, images[i]);
		Helper.setArray(label, labels[i]);
		network.feedforward(image);
		Helper.setArray(output, network.getOutput());
		
		image_panel.repaint();
		output_panel.repaint();
		
		result_index = i;
		
		output_panel.draw_mode = false;
	}
	
	public void randomWrongImage() {
		Integer index = getLabel();
		
		if(index == null) {
			index = rand.nextInt(10);
		}
		
		List<Integer> possible_values = image_map.get(index).get(2);
		int i = possible_values.get(rand.nextInt(possible_values.size()));
		
		Helper.setArray(image, images[i]);
		Helper.setArray(label, labels[i]);
		network.feedforward(image);
		Helper.setArray(output, network.getOutput());
		
		image_panel.repaint();
		output_panel.repaint();
		
		result_index = i;
		
		output_panel.draw_mode = false;
	}
	
	public void validateGoto() {
		String formatted_string = goto_field.getText().replaceAll(",", "");
		if(formatted_string.equals("")) {
			goto_field.setText("0");
		}
		else {
			int n = Integer.parseInt(formatted_string);
			
			if(n < 0) {
				goto_field.setText("0");
			}
			if(n >= images.length) {
				goto_field.setText(Integer.toString(images.length - 1));
			}
		}
	}
	
	public void gotoImage() {
		String formatted_string = goto_field.getText().replaceAll(",", "");
		if(!formatted_string.equals("")) {
			int index = Integer.parseInt(formatted_string);
			
			if(index >= 0 && index < images.length) {
				Helper.setArray(image, images[index]);
				Helper.setArray(label, labels[index]);
				network.feedforward(image);
				Helper.setArray(output, network.getOutput());
				
				image_panel.repaint();
				output_panel.repaint();
				
				result_index = index;
				
				output_panel.draw_mode = false;
			}
		}
	}
	
	public void getImageIndex() {
		if(!output_panel.draw_mode) {
			goto_field.setText(Integer.toString(result_index));
		}
	}
	
	public Integer getLabel() {
		String label_string = (String)selected_label.getSelectedItem();
		if(label_string.equals("None")) {
			return null;
		}
		
		return Integer.parseInt(label_string);
	}
	
	public void setImageIndex(int index) {
		setImageIndexFromValue(index, (Double)selected_weight.getSelectedItem(), false);
		
		if(((String)selected_size.getSelectedItem()).equals("Large")) {
			if(index / 28 > 0) {
				setImageIndexFromValue(index - 28, (Double)selected_weight.getSelectedItem(), false);
			}
			if(index / 28 < 27) {
				setImageIndexFromValue(index + 28, (Double)selected_weight.getSelectedItem(), false);
			}
			
			if(index % 28 > 0) {
				setImageIndexFromValue(index - 1, (Double)selected_weight.getSelectedItem(), false);
			}
			if(index % 28 < 27) {
				setImageIndexFromValue(index + 1, (Double)selected_weight.getSelectedItem(), false);
			}
		}
		
		network.feedforward(image);
		Helper.setArray(output, network.getOutput());
		image_panel.repaint();
		output_panel.repaint();
		
		output_panel.draw_mode = true;
	}
	
	public void setImageIndexFromValue(int index, double value, boolean doUpdate) {
		image[index] = value;
		
		if(doUpdate) {
			network.feedforward(image);
			Helper.setArray(output, network.getOutput());
			image_panel.repaint();
			output_panel.repaint();
			
			output_panel.draw_mode = true;
		}
	}
	
	public void clearImage() {
		Helper.setArray(image, new double[image.length]);
		
		network.feedforward(image);
		Helper.setArray(output, network.getOutput());
		image_panel.repaint();
		output_panel.repaint();
		
		output_panel.draw_mode = true;
	}
}
