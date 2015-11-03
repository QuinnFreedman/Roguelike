import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

public abstract class CutScene implements ICutScene{
	@SuppressWarnings("serial")
	public static class cutSceneDisplayPanel extends JPanel {
		private static Image sceneImage;
		static float black;
		static void setImage(Image image){
			sceneImage = image;
		}
		public static void setBlack(float b){
			black = b;
		}
		
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if(sceneImage != null){
				g.drawImage(sceneImage, 0, 0, (int) (555*Main.scale), 
						(int) (325*Main.scale), this);
			}
			Graphics2D g2 = (Graphics2D) g;
			g.setColor(new Color(0f, 0f, 0f, black));
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
	}
	
	static void showCutScene(CutScene scene){
		Roguelike.paused = true;
		int fadeTime = 900;
		
		Image startImage = createImage(Main.roguelike);
		cutSceneDisplayPanel.setImage(startImage);
		Main.display(Main.CUT_SCENE);
		fadeToBlack(fadeTime, true);
		Thread loader = new Thread(() -> {
			for(String s : scene.getImageStrings()){
				scene.images.add(Main.loadGif(s));
			}
		});
		loader.start();
		Thread t1 = new Thread(() -> {
			try {
				Thread.sleep(fadeTime+10);
			} catch (Exception e1) {}
			for(Image image : scene.images){
				cutSceneDisplayPanel.setImage(image);
				fadeToBlack(fadeTime, false);
				try {
					Thread.sleep(2600);
				} catch (InterruptedException e){}
				fadeToBlack(fadeTime, true);
			}
			try {
				Thread.sleep(fadeTime+10);
			} catch (Exception e) {}
			cutSceneDisplayPanel.setImage(startImage);
			fadeToBlack(fadeTime, false);
			try {
				Thread.sleep(fadeTime+10);
			} catch (Exception e) {}
			Main.display(Main.ROGUELIKE);
			Roguelike.paused = false;
		});
		t1.start();
		
	}
	
	//CUTSCENES

	public ArrayList<Image> images = new ArrayList<Image>();
	
	static class DRAGON_CUTSCENE_1 extends CutScene{
		
		@Override
		public String[] getImageStrings() {
			return new String[]{"Background.png"};
		}
		
	}
	
	
	//UTILITIES
	
	private static BufferedImage createImage(JPanel panel){
		int w = panel.getWidth();
		int h = panel.getHeight();
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		panel.print(g);
		return bi;
	}
	
	private static Thread fadeToBlack(int time, final boolean fade_to_black){
		int numberOfFrames = (int) (time/33f);
		float blackStep = 1f/numberOfFrames;
		
		Thread t1 = new Thread(() -> {
			float black = fade_to_black ? 0 : 1;
			while((black < 1 && fade_to_black) || (black > 0 && !fade_to_black)){
				black += blackStep*(fade_to_black ? 1 : -1);
				black = Math.max(Math.min(1, black), 0);
				cutSceneDisplayPanel.setBlack(black);
				Main.cut_scene.repaint();
				try {
					Thread.sleep(33);
				} catch (InterruptedException e) {}
			}
		});
		t1.start();
		return t1;
	}
}
interface ICutScene{
	public String[] getImageStrings();
}