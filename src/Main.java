import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Main extends JFrame implements KeyListener
{
	private static final long serialVersionUID = 1L;
	
	public static int seed;
	public static Random generator;
	
	public static Party player;
	
	public static JFrame window;
	
	public static JPanel parent;
	
	public static JPanel roguelike;	
	public static JPanel combat;
	public static JPanel menu;
	public static JPanel inv;
	
	public static Font font = new Font("Courier New", Font.PLAIN, 12);
	
	public static String ROGUELIKE = "ROGUELIKE";	
	public static String COMBAT = "COMBAT";	
	public static String MENU = "MENU";	
	public static String INV = "INV";
	
	public static String currentlyDisplayed = MENU;
	
	public static Map<String, BufferedImage> images = new HashMap<>();
	
	public static Debug debug;
	
	public static void main(String[] args)
	{
		debug = new Debug();

		Debug.startTime = System.nanoTime();
		Debug.lastTime = Debug.startTime;
		
		new Main();
		
		Console.benchmark();
		
		init();
		
	}
	
	//**********GUI Constructor***********
	public Main()
	{
		window = new JFrame();
		window.setSize(555,325);//440,320
		window.setLocationRelativeTo(null);
		//window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setTitle("Roguelike");
		window.addKeyListener(this);
		window.setFocusable(true);
		window.setFocusTraversalKeysEnabled(false);
		
		parent = new JPanel(new CardLayout());
		window.add(parent);
		
		combat = new JPanel();
		parent.add(combat, COMBAT);
		new Combat();
		
		menu = new JPanel();
		parent.add(menu, MENU);
		new Menu();
		
		roguelike = new JPanel();
		new Roguelike();
		
		inv = new JPanel();
		parent.add(inv, INV);
		new Inventory();
		
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
	
	public static String loadImage(String srcIndex){
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
			return srcIndex;
		}
		return null;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
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
			Menu.handleKeyInput(c);
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