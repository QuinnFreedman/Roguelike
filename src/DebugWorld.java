import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class DebugWorld extends JFrame implements KeyListener{
	private CanvasPanel canvas;
	protected ArrayList<Room> cities = new ArrayList<Room>();
	private Rectangle worldBounds;
	protected int[][] world;
	protected String message;
	protected float scaleF = 0.5f;
	public DebugWorld(){
		super();
		
		JPanel holder = new JPanel();
		holder.setLayout(new BorderLayout());
		this.getContentPane().add(holder);
		
		canvas = new CanvasPanel();
		holder.add(canvas, BorderLayout.CENTER);
		
		JPanel control = new JPanel();
		control.setLayout(new BorderLayout());
		
		JPanel top = new JPanel();
		JPanel bottom = new JPanel();
		JPanel scale = new JPanel();
		
		JLabel levelsLabel = new JLabel("levels");
		top.add(levelsLabel);
		JTextField levels = new JTextField(".22, .32, .5, .8");
		top.add(levels);
		
		JLabel shapeLabel = new JLabel("shape");
		bottom.add(shapeLabel);
		JTextField shape = new JTextField("1.2");
		bottom.add(shape);
		
		JLabel scaleLabel = new JLabel("scale");
		scale.add(scaleLabel);
		JTextField scaleField = new JTextField("0.5");
		scale.add(scaleField);
		

		control.add(top, BorderLayout.NORTH);
		control.add(bottom, BorderLayout.CENTER);
		control.add(scale, BorderLayout.SOUTH);
		
		JButton generate = new JButton("generate");
		generate.addActionListener(e -> {
			try{
				WorldBuilder.shape = Float.parseFloat(shape.getText());
				String[] str = levels.getText().split(", ");
				float[] l = new float[4];
				for(int i = 0; i < 4; i++){
					l[i] = Float.parseFloat(str[i]);
				}
				
				WorldBuilder.levels = l;
				
				scaleF = Float.parseFloat(scaleField.getText());
				
				WorldBuilder.buildWorld();
			}catch(Exception e1){
				
			}
		});
		bottom.add(generate);
		
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
			((Graphics2D) g).scale(scaleF,scaleF);
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
						case 4:
							image.setRGB(x, y, Color.WHITE.hashCode());
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