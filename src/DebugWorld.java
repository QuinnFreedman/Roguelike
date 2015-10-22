import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class DebugWorld extends JFrame implements KeyListener{
	private CanvasPanel canvas;
	protected ArrayList<Room> cities = new ArrayList<Room>();
	private Rectangle worldBounds;
	protected int[][] world;
	protected String message;
	public DebugWorld(){
		super();
		
		JPanel holder = new JPanel();
		holder.setLayout(new BorderLayout());
		this.getContentPane().add(holder);
		
		canvas = new CanvasPanel();
		holder.add(canvas, BorderLayout.CENTER);
		
		JPanel control = new JPanel();
		JButton generate = new JButton("generate");
		generate.addActionListener(e -> {
			WorldBuilder.buildWorld();
		});
		control.add(generate);
		holder.add(control, BorderLayout.EAST);
		
		this.pack();
		this.setVisible(true);
		this.addKeyListener(this);
	}
	
	void setOutline(Polygon outline){
		canvas.outline = outline;
		canvas.repaint();
	}
	
	public void clear(){
		this.world = null;
		canvas.outline = null;
		this.worldBounds = null;
		this.cities = new ArrayList<Room>();
		this.message = null;
		canvas.repaint();
	}
	
	public void message(String string) {
		this.message = string;
		canvas.repaint();
		
	}
	
	private class CanvasPanel extends JPanel{
		private Polygon outline;
		private final Color DARK_GRREEN = new Color(0, 153, 0);
		private final Color BROWN = new Color(219, 184, 77);
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(500,500);
		}
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			((Graphics2D) g).scale(.5,.5);
			if(outline != null)
				g.drawPolygon(outline);

			if(message != null){
				//g.setFont(new Font(Font.MONOSPACED, 34, Font.PLAIN));
				g.drawString(message, 500, 500);
			}
			
			if(world != null){
				BufferedImage image = new BufferedImage(world[0].length, world.length, BufferedImage.TYPE_INT_RGB);
				
				for(int y = 0; y < world.length; y++){
					for(int x = 0; x < world[0].length; x++){
						switch(world[y][x]){
						case 0:
							image.setRGB(x, y, Color.BLUE.hashCode());
							break;
						case 1:
							image.setRGB(x, y, Color.GREEN.hashCode());
							break;
						case 2:
							image.setRGB(x, y, DARK_GRREEN.hashCode());
							break;
						case 3:
							image.setRGB(x, y, BROWN.hashCode());
							break;
						}
					}
				}
				g.drawImage(image, 0, 0, this);
			}
			if(worldBounds != null){
				g.drawRect(worldBounds.x, worldBounds.y, worldBounds.width, worldBounds.height);
			}
			
			if(cities != null){
				for(Room r : cities){
					g.drawRect(r.xpos, r.ypos, r.w, r.h);
				}
			}
		}
	}
	
	public void setWorld(int[][] array) {
		world = array;
		canvas.repaint();
		
	}

	public void setWorldBounds(Rectangle bounds) {
		worldBounds = bounds;
		canvas.repaint();
		
	}

	public void setCities(ArrayList<Room> list) {
		cities = list;
		canvas.repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_SPACE){
			WorldBuilder.buildWorld();
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

}