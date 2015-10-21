import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class DebugWorld extends JFrame implements KeyListener{
	private CanvasPanel canvas;
	protected static ArrayList<Room> cities = new ArrayList<Room>();
	private static Rectangle worldBounds;
	protected static int[][] world;
	public DebugWorld(){
		super();
		
		canvas = new CanvasPanel();
		this.getContentPane().add(canvas);

		this.setSize(1000, 600);
		this.setVisible(true);
		this.addKeyListener(this);
	}
	
	void setOutline(Polygon outline){
		canvas.outline = outline;
		canvas.repaint();
	}
	
	private static class CanvasPanel extends JPanel{
		private Polygon outline;
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			((Graphics2D) g).scale(.5,.5);
			if(outline != null)
				g.drawPolygon(outline);
			if(world != null){
				/*
				for(int y = 0; y < world.length; y++){
					for(int x = 0; x < world[0].length; x++){
						if(world[y][x] == 0)
							g.setColor(Color.BLUE);
						else if(world[y][x] == 1)
							g.setColor(Color.GREEN);
						g.fillRect(x, y, x+1, y+1);
					}
				}
				g.setColor(Color.BLACK);*/
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