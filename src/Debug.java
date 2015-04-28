import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Debug extends JFrame implements KeyListener{
	
	public static List<BufferedImage> images = new ArrayList<BufferedImage>();
	public static int index;
	private JFrame window;
	private JPanel parent;
	private debugPanel draw;
	private JPanel Display;
	public static long startTime;
	public static long lastTime;
	private boolean debugOn = true;
	
	public static int scale = 16;

	Debug(){
		if(debugOn){
			SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                createAndShowGUI();
	            }
	        });
		}
	}
	private void createAndShowGUI() {
		window = new JFrame();
		window.setSize((int) (600*Main.scale),(int) (400*Main.scale));
		window.addKeyListener(this);
		//window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(EXIT_ON_CLOSE);
		window.setTitle("Roguelike");
		window.setVisible(true);
		window.setFocusable(true);
		window.setFocusTraversalKeysEnabled(false);
		
		parent = new JPanel();
		window.add(parent);
		
		draw = new debugPanel();
		draw.setSize((int) (Roguelike.map.width*scale+20*Main.scale),(int) (Roguelike.map.height*scale+20*Main.scale));
		draw.setVisible(false);
		parent.add(draw);
		
		Display = new debugDisplay();
		Display.setBorder(BorderFactory.createLineBorder(Color.black));
		parent.add(Display);
		window.pack();
	}
	public void update(){
		if(debugOn){
			draw.repaint();
			images.add(createImage(draw));
		}
	}
	public void clear(){
		if(debugOn == false){
			return;
		}
		if(draw.closedList != null && draw.openList != null){
			draw.closedList.clear();
			draw.openList.clear();
		}
		draw.lines.clear();
		draw.connectedRooms.clear();
		draw.vectors.clear();
		draw.weights = new int[Roguelike.map.height][Roguelike.map.width];
		for(int[] array : draw.weights){
			for(int i = 0; i < array.length; i++){
				array[i] = 0;
			}
		}
		draw.done = false;
		index = 0;
		images.clear();
		Pathing.paths.clear();
		draw.repaint();
	}
	public BufferedImage createImage(JPanel panel){
		int w = panel.getWidth();
		int h = panel.getHeight();
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		panel.print(g);
		return bi;
	}
	
	public void DrawRooms(List<Integer> connectedRooms){
		if(connectedRooms != null && debugOn){
			draw.connectedRooms = connectedRooms;
			update();
		}
	}
	public void setWeights(int[][] overlapWeight){
		if(overlapWeight != null && debugOn){
			draw.weights = overlapWeight;
		}
	}
	public void DrawLine(Point p1, Point p2){
		if(debugOn){
			draw.lines.add(new Point[]{p1,p2});
		}
	}
	public void DrawVector(Point p1, double magnitude, double angle){
		if(debugOn){
			Point p2 = new Point();
			p2.x = p1.x + (int) Math.round(Math.cos(angle*Math.PI)*magnitude);
			p2.y = p1.y - (int) Math.round(Math.sin(angle*Math.PI)*magnitude);
			draw.vectors.add(new Point[]{p1,p2});
		}
	}
	public void clearVectors(){
		if(debugOn){
			draw.vectors.clear();
		}
	}
	public void paintLists(List<Node> openList, List<Node> closedList) {
		if(debugOn){
			draw.openList = openList;
			draw.closedList = closedList;
			update();
		}
	}
	public void finish(){
		draw.done = true;
		update();
		draw.done = false;
		end();
		Console.log(images.get(0).toString());
	}
	private void end(){
		index = images.size()-1;
		Display.repaint();
	}
	@Override
	public void keyPressed(KeyEvent e){
		if(Main.isLoading)
			return;
		int c = e.getKeyCode ();
     	if(c==KeyEvent.VK_LEFT) {
   	 		if(index > 1){
   	 			index--;
   	 			Display.repaint();
   	 		}
   	 	}else if(c==KeyEvent.VK_RIGHT){                
   	 		if(index < images.size()-1){
	 			index++;
	 			Display.repaint();
	 		}else{
	 			index = 1;
	 			Display.repaint();
	 		}
        }else if(c==KeyEvent.VK_SPACE){                
        	Main.isLoading = true;
        	Main.init();
	   	 	if(index < images.size()-1){
	 			index++;
	 			Display.repaint();
	 		}
        	Main.isLoading = false;
        }else if(c == KeyEvent.VK_A){
        	end();
        }else if(c == KeyEvent.VK_END){
        	end();
        }else if(c == KeyEvent.VK_HOME){
        	index = 1;
    		Display.repaint();
        }
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}
}

class debugPanel extends JPanel{
	public List<Node> closedList;
	public List<Node> openList;
	private static final long serialVersionUID = 1L;
	private int scale;
	public List<Point[]> lines = new ArrayList<Point[]>();
	public List<Point[]> vectors = new ArrayList<Point[]>();
	public List<Integer> connectedRooms = new ArrayList<Integer>();
	public int[][] weights;
	public boolean done;
	
	@Override
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //((Graphics2D) g).scale(Main.scale,Main.scale);
        g.setColor(Color.lightGray);
        for(int x = 0; x < Roguelike.map.width; x++){
        	g.drawLine(x*scale, 0, x*scale, Roguelike.map.height*scale);
        }
        for(int y = 0; y < Roguelike.map.height; y++){
        	g.drawLine(0, y*scale, Roguelike.map.width*scale, y*scale);
        }
        
        for(int i = 0; i < Roguelike.rooms.size(); i++){
        	Room r = Roguelike.rooms.get(i);
        	if(connectedRooms.contains(i)){
	        	g.setColor(Color.lightGray);
	        	g.fillRect(r.xpos*scale, r.ypos*scale, r.w*scale, r.h*scale);
        	}else{
        		g.setColor(Color.GRAY);
	        	g.fillRect(r.xpos*scale, r.ypos*scale, r.w*scale, r.h*scale);
        	}
        	g.setColor(Color.magenta);
        	for(Point[] points : vectors){
        		if(points[0] != points[1]){
        			g.drawLine(points[0].x, points[0].y, points[1].x, points[1].y);
        			g.fillRect(points[1].x-2, points[1].y-2, 5, 5);
        		}
        	}
        	g.setColor(Color.BLACK);
        	g.drawRect(r.xpos*scale, r.ypos*scale, r.w*scale, r.h*scale);
        	g.drawString("("+r.xpos+", "+r.ypos+")", r.xpos*scale, r.ypos*scale+10);
        	for(int y = 0; y < Roguelike.map.height; y++){
    			for(int x = 0; x < Roguelike.map.width; x++){
    				if(weights[y][x] == 0)
    					g.setColor(Color.lightGray);
    				else
    					g.setColor(Color.darkGray);
    				g.drawString(Integer.toString(weights[y][x]),x*scale,(y+1)*scale);    				
    			}
    		}
        }
        if(Pathing.paths != null){
        	g.setColor(Color.YELLOW);
        	for(ArrayList<Node> path : Pathing.paths){
        		for(Node node : path){
        			g.fillRect(node.x*scale, node.y*scale, scale, scale);
        		}
        	}
        }
        if(openList != null && closedList != null){
        	g.setColor(Color.RED);
	        for(Point node : openList){
	        	g.fillRect(node.x*scale,node.y*scale,scale,scale);
	        }
        	g.setColor(Color.GREEN);
	        for(Point node : closedList){
	        	g.fillRect(node.x*scale,node.y*scale,scale,scale);
	        }
        }
        for(Point[] p : this.lines){
        	g.setColor(Color.YELLOW);
        	g.drawLine(p[0].x*scale+(scale/2), p[0].y*scale+(scale/2), p[1].x*scale+(scale/2), p[1].y*scale+(scale/2));
        }
        if(done){
	        for(int y = 0; y < Roguelike.map.height; y++){
				for(int x = 0; x < Roguelike.map.width; x++){
					if(Roguelike.walls[y][x] == 0){
						g.setColor(Color.magenta);
					}else if(Roguelike.walls[y][x] == 1){
						g.setColor(Color.blue);
					}else if(Roguelike.walls[y][x] == 2 || Roguelike.walls[y][x] == 3){
						g.setColor(Color.gray);
					}else{
						g.setColor(Color.black);
					}
					g.fillRect(x*scale, y*scale, scale, scale);
				}
			}
        }
    }

	debugPanel(){
		this.scale = Debug.scale;
	}
	
	@Override
	public Dimension getPreferredSize() {
        return new Dimension(484,324);
    }
}
class debugDisplay extends JPanel{
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void paintComponent(Graphics g) {
		((Graphics2D) g).scale(Main.scale,Main.scale);
    	if(Debug.index > 0){
        	g.drawImage(Debug.images.get(Debug.index),0,0,this);
        	g.drawString("seed = "+Integer.toString(Main.seed), 1, 11);
        	g.drawString("frame "+Integer.toString(Debug.index)+"/"+Integer.toString(Debug.images.size()-1), 1, 22);
        }
    }
	
	debugDisplay(){
		
	}
	@Override
	public Dimension getPreferredSize() {
		return new Dimension((int) (Roguelike.map.width*Debug.scale*Main.scale),(int) (Roguelike.map.height*Debug.scale*Main.scale));
		//return new Dimension(484,324);
    }
}