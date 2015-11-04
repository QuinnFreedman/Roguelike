import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class roomBuilder extends JFrame{
	private JPanel table;
	private static final JFileChooser fc = new JFileChooser();
	File dictionairy;
	HashMap<Integer, String> map = new HashMap<Integer, String>();
	private int active_image = 0;
	private int scale = 2;
	private int[][] data = new int[10][10];
	private int[][] dynamic = new int[10][10];
	JFrame THIS = this;
	private void resetData(){
		for(int y = 0; y < data.length; y++){
			for(int x = 0; x < data[0].length; x++){
				data[y][x] = 0;
				dynamic[y][x] = 0;
			}
		}
	}
	public roomBuilder() {
		resetData();
		//gui
		this.setSize(new Dimension(900, 800));
		JPanel container = new JPanel();
		this.getContentPane().add(container);
		
		container.setLayout(new BorderLayout());
		
		JPanel dictionairyViewer = new JPanel();
		container.add(dictionairyViewer, BorderLayout.EAST);
		dictionairyViewer.setLayout(new BorderLayout());
		
		table = new JPanel();
		dictionairyViewer.add(new JScrollPane(table), BorderLayout.CENTER);
		table.setLayout(new BoxLayout(table, BoxLayout.Y_AXIS));
		
		JPanel buttons = new JPanel();
		//buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
		dictionairyViewer.add(buttons, BorderLayout.AFTER_LAST_LINE);
		
		JButton addFloor = new JButton("Add Floor Tile");
		buttons.add(addFloor);
		addFloor.addActionListener(e -> {
			fc.setCurrentDirectory(new File("assets/floorTiles"));
	        if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
	        	if(!fc.getSelectedFile().getAbsolutePath().contains("assets")){
	        		error("all tiles should be in the assets folder");
	        		return;
	        	}
	        	int p = fc.getSelectedFile().getAbsolutePath().lastIndexOf('.');
	        	String extension = null;
	        	if (p > 0) {
	        		extension = fc.getSelectedFile().getAbsolutePath().substring(p+1);
	        	}
	        	if(!extension.equals("png")){
	        		error("only supports .png's right now");
	        		return;
	        	}
	        	int i = 1;
				while(map.containsKey(i)){
					i++;
				}
	        	File file = fc.getSelectedFile();
		        addRow(file.getPath().split("assets")[1], i, true);
	        } 
		});
		JButton addWall = new JButton("Add Wall Tile");
		buttons.add(addWall);
		addWall.addActionListener(e -> {
			fc.setCurrentDirectory(new File("assets/floorTiles"));
	        if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
	        	if(!fc.getSelectedFile().getAbsolutePath().contains("assets")){
	        		error("all tiles should be in the assets folder");
	        		return;
	        	}
	        	int p = fc.getSelectedFile().getAbsolutePath().lastIndexOf('.');
	        	String extension = null;
	        	if (p > 0) {
	        		extension = fc.getSelectedFile().getAbsolutePath().substring(p+1);
	        	}
	        	if(!extension.equals("png")){
	        		error("only supports .png's right now");
	        		return;
	        	}
	        	int i = -1;
				while(map.containsKey(i)){
					i--;
				}
	        	File file = fc.getSelectedFile();
		        addRow(file.getPath().split("assets")[1], i, true);
	        } 
		});
		
		//CANVAS
		JPanel canvasHolder = new JPanel();
		canvasHolder.setLayout(new BorderLayout());
		container.add(canvasHolder, BorderLayout.CENTER);
		
		JPanel top = new JPanel();
		canvasHolder.add(top, BorderLayout.NORTH);
		JTextField width = new JTextField("room width");
		JTextField height = new JTextField("room height");
		JButton newRoom = new JButton("New Room");
		top.add(width);
		top.add(height);
		top.add(newRoom);
		
		JPanel bottom = new JPanel();
		canvasHolder.add(bottom, BorderLayout.SOUTH);
		JTextField name = new JTextField("room name");
		JButton save = new JButton("Save Room");
		bottom.add(name);
		bottom.add(save);
		save.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				File file = new File("assets/rooms.txt");
				try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
					if(!file.exists()) {
				    	file.createNewFile();
					}
					
					out.println("/*"+name.getText()+"*/");
					
					out.print("{");
					out.println();
					out.print("{");
					for(int y = 0; y < data.length; y++){
						out.print("{");
						for(int x = 0; x < data[0].length; x++){
							out.print(String.valueOf(data[y][x]));
							if(x != data[0].length -1)
								out.print(",");
						}
						if(y != data.length -1){
							out.print("},");
							out.println();
						}
					}
					out.print("}");
					out.print("},");
					out.println();
					out.println();
					out.print("{");
					for(int y = 0; y < data.length; y++){
						out.print("{");
						for(int x = 0; x < data[0].length; x++){
							out.print(String.valueOf(dynamic[y][x]));
							if(x != data[0].length -1)
								out.print(",");
						}
						if(y != data.length -1){
							out.print("},");
							out.println();
						}
					}
					out.print("}");
					out.print("}");
					out.println();
					out.print("},");
					out.println();
					out.println();
					JOptionPane.showMessageDialog(THIS,
							"Room saved successfully.",
							"Saved",
					        JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException e1) {
					error(e1);
					e1.printStackTrace();
				}
				
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		
		JPanel canvas = new JPanel(){
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if(data.length == 0)
					return;
				if(data[0].length == 0)
					return;
				
				for(int y = 0; y < data.length; y++){
					g.drawLine(0, y*32*scale, data[0].length*32*scale, y*32*scale);
					for(int x = 0; x < data[0].length; x++){
						g.drawLine(x*32*scale, 0, x*32*scale, data.length*32*scale);
						
						try {
							String s = map.get(data[y][x]);
							if(s != null){
								URL resource = this.getClass().getResource(s);
								if(resource != null){
									BufferedImage src = ImageIO.read(resource);
									g.drawImage(src, x*32*scale, y*32*scale, 32*scale, 32*scale, this);
								}
							}
							
							s = map.get(dynamic[y][x]);
							if(s != null){
								URL resource = this.getClass().getResource(s);
								if(resource != null){
									BufferedImage src = ImageIO.read(resource);
									g.drawImage(src, x*32*scale, y*32*scale, 32*scale, 32*scale, this);
								}
							}
							
						} catch (IOException e) {
							error(e);
							e.printStackTrace();
						}
						
					}
				}
				g.drawLine(0, data.length*32*scale, data[0].length*32*scale, data.length*32*scale);
				g.drawLine(data[0].length*32*scale, 0, data[0].length*32*scale, data.length*32*scale);
			}
		};
		canvas.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {
				java.awt.Point p = e.getPoint();
				if(p.y < data.length*32*scale && p.x < data[0].length*32*scale){
					if(e.getButton() == MouseEvent.BUTTON1){
						data[(int) (p.y/(32f*scale))][(int) (p.x/(32f*scale))] = active_image;
					}else if(e.getButton() == MouseEvent.BUTTON3){
						dynamic[(int) (p.y/(32f*scale))][(int) (p.x/(32f*scale))] = active_image;
					}
					canvas.repaint();
				}
			}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		canvasHolder.add(canvas, BorderLayout.CENTER);
		
		newRoom.addActionListener(e -> {
			try{
				int w = Integer.parseInt(width.getText());
				int h = Integer.parseInt(height.getText());
				data = new int[h][w];
				dynamic = new int[h][w];
				canvas.repaint();
			}catch(Exception e1){
				error(e1);
			}
		});
		
		this.setVisible(true);
		
		JOptionPane.showMessageDialog(THIS,
				"Load and select tiles in the pane on the right.\n"
				+ "Left-click to place background (floor) tiles.\n"
				+ "Right-click to place foreground (dynamic) tiles.\n"
				+ "select the '0 | delete' tile to remove tiles.\n"
				+ "negate (blue) tiles are impassable (walls, tables).\n"
				+ "positive tiles can be walked on/though (doors, floor).\n"
				+ "Save room only once when you're done.",
				"Room Editor",
		        JOptionPane.INFORMATION_MESSAGE);
		
		//make file
		dictionairy = new File("assets/tile_table.txt");
		if(!dictionairy.exists()) {
		    try {
		    	dictionairy.createNewFile();
			} catch (IOException e) {
				error(e);
			}
		}
		
		//add rows
		
		addRow("delete", 0, false);
		
		try {
			FileReader fstream = new FileReader(dictionairy);
			BufferedReader dictionairy_in = new BufferedReader(fstream);
			String s = dictionairy_in.readLine();
			while(s != null){
				int i = Integer.parseInt(s.split(",")[0]);
				addRow(s.split(",")[1], i, false);
				
				map.put(i, s.split(",")[1]);
				s = dictionairy_in.readLine();
			}
			dictionairy_in.close();
		} catch (IOException e) {
			error(e);
			e.printStackTrace();
		}
		
	}
	
	private void addRow(String src_orig, int i, boolean write){
		String src = "assets"+src_orig;
		File file = new File(src);
		if(!file.exists() && i != 0){
			error("Can't find file "+file.getAbsolutePath());
		}
		ImageIcon addIcon = new ImageIcon(src);
		table.add(new Row(addIcon, i, i == 0 ? "delete" : file.getName()));
		table.revalidate();
		
		map.put(i, src_orig);
		
		if(write && i != 0){
			try {
				FileWriter fstream = new FileWriter(dictionairy);
				BufferedWriter dictionairy_out = new BufferedWriter(fstream);
				for(Entry<Integer, String> entry : map.entrySet()){
					dictionairy_out.write(entry.getKey()+","+entry.getValue());
					dictionairy_out.newLine();
				}
				dictionairy_out.close();
			} catch (IOException e) {
				error(e);
				e.printStackTrace();
			}
		}
	}
	
	private class Row extends JLabel{
		Row(ImageIcon ico, int i, String src){
			super(ico);
			this.setPreferredSize(new Dimension(200,32));
			if(i < 0)
				this.setForeground(Color.blue);
			this.setBackground(Color.LIGHT_GRAY);
			this.setText(" "+i+" | "+src);
			this.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent e) {
					active_image = i;
					for(Component c : table.getComponents()){
						((JComponent) c).setOpaque(false);
						c.repaint();
					}
					setOpaque(true);
					repaint();
					
				}
				
				@Override
				public void mousePressed(MouseEvent e) {}
				@Override
				public void mouseExited(MouseEvent e) {}
				@Override
				public void mouseEntered(MouseEvent e) {}
				@Override
				public void mouseClicked(MouseEvent e) {}
			});
		}	
	}
	private void error(Exception e) {
		JOptionPane.showMessageDialog(this,
				e.getMessage(),
				"Error",
		        JOptionPane.ERROR_MESSAGE);
	}
	
	private void error(String string) {
		JOptionPane.showMessageDialog(this,
				string,
				"Error",
		        JOptionPane.ERROR_MESSAGE);
	}
	
}