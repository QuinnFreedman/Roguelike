import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JFrame implements KeyListener
{
	private static final long serialVersionUID = 1L;
	
	public static int seed;
	public static Random generator;
	
	public static Party player;
	
	public static JFrame window;
	
	public static JPanel parent;
	
	public static JPanel roguelike;	
	public static Combat combat;
	public static Menu menu;
	public static JPanel inv;
	public static JPanel cut_scene;
	
	public static double scale = 2;
	public static boolean isLoading = true;
	
	public static Font font = new Font("Courier New", Font.PLAIN, 12);
	
	public static String ROGUELIKE = "ROGUELIKE";
	public static String COMBAT = "COMBAT";
	public static String MENU = "MENU";
	public static String INV = "INV";
	public static String CUT_SCENE = "CUT_SCENE";
	
	public static String currentlyDisplayed = MENU;
	
	public static Map<String, BufferedImage> images = new HashMap<>();
	
	public static Debug debug;
	
	public static void main(String[] args)
	{
		
		try {
			GraphicsEnvironment ge = 
		    	GraphicsEnvironment.getLocalGraphicsEnvironment();
			Font newFont = Font.createFont(Font.TRUETYPE_FONT, new File("assets/font/pixelated.ttf"));
			ge.registerFont(newFont);
			font = newFont.deriveFont(14f);
		} catch (Exception e) {
		     //Handle exception
			e.printStackTrace();
		}
		new Main();
		
		Console.benchmark();
		
		init();
		
		isLoading = false;
	}
	
	//**********GUI Constructor***********
	public Main()
	{
		window = new JFrame();
		//window.setSize(555*scale,325*scale);//440,320
		//window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setTitle("Roguelike");
		window.addKeyListener(this);
		window.setFocusable(true);
		window.setFocusTraversalKeysEnabled(false);
		
		parent = new JPanel(new CardLayout());
		parent.setPreferredSize(new Dimension((int) (scale*555),(int) (325*scale)));
		window.add(parent);
		window.pack();
		window.setLocationRelativeTo(null);
		
		combat = new Combat();
		parent.add(combat, COMBAT);
		
		menu = new Menu();
		parent.add(menu, MENU);
		
		
		roguelike = new JPanel();
		new Roguelike();
		
		inv = new JPanel();
		parent.add(inv, INV);
		new Inventory();
		
		cut_scene = new CutScene.cutSceneDisplayPanel();
		parent.add(cut_scene, CUT_SCENE);
		
		window.setVisible(true);
		
	}
	
	public static void display(String j){
		Console.log("display("+j+")");
		CardLayout cl = (CardLayout)(parent.getLayout());
		currentlyDisplayed = j;
		cl.show(parent,j);
		parent.repaint();
		parent.revalidate();
	}
	
	public static void init()
	{
		
		Roguelike.init((int) Math.floor(Math.random()*2147483647));
		
	}
	
	public static Image loadImage(String srcIndex){
		if(Main.images.get(srcIndex) == null){
			Console.log("Loading "+srcIndex+".png");
			BufferedImage src = null;
			try {
				src = ImageIO.read(Main.class.getResource(srcIndex+".png"));
			} catch (IOException e) {
				Console.log(srcIndex+".png not found!");
				e.printStackTrace();
			}
			Main.images.put(srcIndex,src);
			return src;
		}else{
			return images.get(srcIndex);
		}
	}
	
	public static Image loadGif(final String url) {
	    try {
	    	Console.log("Toolkit Loading "+url);
	    	final Toolkit tk = Toolkit.getDefaultToolkit();
	        final Image img = tk.createImage(new File("assets/"+url).getAbsolutePath());
	        tk.prepareImage(img, -1, -1, null);
	        return img;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(isLoading)
			return;
		int c = e.getKeyCode ();
		if(c==192) {               
	 		new Input();
        } else if(c==KeyEvent.VK_TAB) {
        	if(currentlyDisplayed != COMBAT){
        		Combat.init(new Party());
        	}else{
        		display(Main.ROGUELIKE);
        	}
        }else if(c==KeyEvent.VK_ESCAPE){
        	Console.log(currentlyDisplayed);
        	if(currentlyDisplayed != INV){
        		display(INV);
        	}else{
        		display(Main.ROGUELIKE);
        	}
        }else if(currentlyDisplayed == ROGUELIKE){
			Roguelike.handleKeyInput(c);
		}else if(currentlyDisplayed == COMBAT){
			Combat.handleKeyInput(c);
		}else if(currentlyDisplayed == MENU){
			menu.handleKeyInput(c);
		}else if(currentlyDisplayed == INV){
			Inventory.handleKeyInput(c);
		}
     	
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}
}