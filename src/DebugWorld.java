import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class DebugWorld extends JFrame implements KeyListener{
	private CanvasPanel canvas;
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
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_SPACE){
			WorldBuilder.buildWorld();
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}