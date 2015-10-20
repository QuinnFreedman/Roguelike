import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class DebugWorld extends JFrame{
	private CanvasPanel canvas;
	public DebugWorld(){
		super();
		
		canvas = new CanvasPanel();
		this.getContentPane().add(canvas);
		
		this.setVisible(true);
		this.setSize(400, 400);
	}
	
	void setOutline(ArrayList<java.awt.Point> points){
		int len = points.size();
		canvas.outlineCount = len;
		canvas.outlineX = new int[len];
		canvas.outlineY = new int[len];
		for(int i = 0; i < len; i++){
			canvas.outlineX[i] = points.get(i).x;
			canvas.outlineY[i] = points.get(i).y;
		}
		canvas.repaint();
	}
	
	private static class CanvasPanel extends JPanel{
		private int[] outlineX;
		private int[] outlineY;
		private int outlineCount = 0;
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			((Graphics2D) g).scale(.5,.5);
			if(outlineCount != 0)
				g.drawPolygon(outlineX, outlineY, outlineCount);
		}
	}
}