import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Menu extends JPanel{
	
	private static int currentPos;
	private static String CLASS = "CLASS";
	private static String RACE = "RACE";
	private static String currentMenu;

	private static String clas;
	private static String race;
	
	static String output = "";
	static String output2 = "";
	static String image = null;
	
	private static Image backgroundImage;
	
	private static void getMenuText() {
		output = "";
		output2 = "";
		if(currentMenu == RACE){
			output += "Choose Race\n";
			for(int i = 0; i < Charecter.races.length; i++){
				output += ((currentPos == i) ? "* " : "  ")
						+ Charecter.races[i]
						+((currentPos == i) ? " *\n" : "\n");
			}
			image = "characterTiles/"+Charecter.races[currentPos]+"_Adventurer";
		}else if(currentMenu == CLASS){
			output += "Choose Class\n";
			for(int i = 0; i < Charecter.classes.length; i++){
				output += ((currentPos == i) ? "* " : "  ")
						+ Charecter.classes[i]
						+((currentPos == i) ? " *\n" : "\n");
			}
			output2 += Charecter.parseClas(Charecter.classes[currentPos]).getInfo();//TODO, make parse return static, not instantiated
			image = "characterTiles/"+race+"_"+Charecter.parseClas(Charecter.classes[currentPos]).getName();
		}
	}
	
	public void handleKeyInput(int c) {
		if (c==KeyEvent.VK_UP) {
			if(currentMenu == CLASS){
				currentPos = (currentPos == 0) ? Charecter.classes.length - 1 : currentPos-1;
			}else if(currentMenu == RACE){
				currentPos = (currentPos == 0) ? Charecter.races.length - 1 : currentPos-1;
			}
			getMenuText();
			this.repaint();
		} else if(c==KeyEvent.VK_DOWN) {
			if(currentMenu == CLASS){
				currentPos++;
				currentPos = Math.abs(currentPos) % Charecter.classes.length;
			}else if(currentMenu == RACE){
				currentPos++;
				currentPos = Math.abs(currentPos) % Charecter.races.length;
			}
    		getMenuText();
    		this.repaint();
     	} else if(c==KeyEvent.VK_LEFT) {
     		
   	 	} else if(c==KeyEvent.VK_RIGHT) {
   	 		
   	 	} else if(c==KeyEvent.VK_SPACE) {
        	
        }else if(c==KeyEvent.VK_ENTER) {
        	if(currentMenu == RACE){
        		race = Charecter.races[currentPos];
        		currentMenu = CLASS;
        		currentPos = 0;
        		getMenuText();
        		this.repaint();
        	}else if(currentMenu == CLASS && !Main.isLoading){
        		clas = Charecter.classes[currentPos];
        		((Player) Main.player).addCharecter(new Charecter(1, Main.player, race, clas));
        		((Player) Main.player).addCharecter(new Charecter(1, Main.player));
        		Main.player.setupSrcImage();

        		Inventory.setup();
        		
    			Roguelike.parties[Main.player.getYpos()][Main.player.getXpos()] = Main.player.getSrcIndex();
        		Roguelike.render();
        		Main.display(Main.ROGUELIKE);
    		}
        	
        }
		
	}
	Menu(){
		this.setBackground(Color.black);
		backgroundImage = Main.loadGif("Background_Default");
		//backgroundImage = Main.loadImage("Background");
		setupMenu();
	}
	public static void setupMenu(){
		currentPos = 0;
		currentMenu = RACE;
		getMenuText();
	}

	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		((Graphics2D) g).scale(Main.scale, Main.scale);
		g.drawImage(backgroundImage, 0, 0, 555, 325, this);
		g.setFont(Main.font);
		//int offset = Main.parent.getWidth()/2;
		g.setColor(Color.black);
		Utility.drawString(g, output, 8, 4);//(int) Math.max(10,offset/Main.scale-100), 10);
		g.drawImage(Main.loadImage(image), 340, 60, 64, 64, this);
		
		Utility.drawString(g, output2, 100, 4, 200);
	}

}