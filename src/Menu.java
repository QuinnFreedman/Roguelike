import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Menu{
	
	private static JLabel menuText;
	private static CharecterInfoPanel sidebar;
	private static int currentPos;
	private static String CLASS = "CLASS";
	private static String RACE = "RACE";
	private static String currentMenu;

	private static String clas;
	private static String race;
	
	private static void printMenu() {
		String output = "<html><font face=\"Courier New\" color = \"#999999\">";
		String output2 = "";
		String image;
		if(currentMenu == RACE){
			output += "Choose Race<br>";
			for(int i = 0; i < Charecter.races.length; i++){
				output += ((currentPos == i) ? "* <u>" : "&nbsp;&nbsp;")
						+ Charecter.races[i]
						+((currentPos == i) ? "</u> *<br>" : "<br>");
			}
			image = Charecter.races[currentPos]+"_Adventurer";
			sidebar.setImage(image);
		}else if(currentMenu == CLASS){
			output += "Choose Class<br>";
			for(int i = 0; i < Charecter.classes.length; i++){
				output += ((currentPos == i) ? "* <u>" : "&nbsp;&nbsp;")
						+ Charecter.classes[i]
						+((currentPos == i) ? "</u> *<br>" : "<br>");
			}
			output2 += Charecter.parseClas(Charecter.classes[currentPos]).getInfo();//TODO, make parse return static, not instantiated
			image = race+"_"+Charecter.parseClas(Charecter.classes[currentPos]).getName();
			sidebar.setImage(image);
		}
		output += "</font></html>";
		menuText.setText(output);
		sidebar.setText(output2);
	}
	
	public static void handleKeyInput(int c) {
		if (c==KeyEvent.VK_UP) {
			if(currentMenu == CLASS){
				currentPos = (currentPos == 0) ? Charecter.classes.length - 1 : currentPos-1;
			}else if(currentMenu == RACE){
				currentPos = (currentPos == 0) ? Charecter.races.length - 1 : currentPos-1;
			}
			printMenu();
		} else if(c==KeyEvent.VK_DOWN) {
			if(currentMenu == CLASS){
				currentPos++;
				currentPos = Math.abs(currentPos) % Charecter.classes.length;
			}else if(currentMenu == RACE){
				currentPos++;
				currentPos = Math.abs(currentPos) % Charecter.races.length;
			}
			printMenu();
     	} else if(c==KeyEvent.VK_LEFT) {
     		
   	 	} else if(c==KeyEvent.VK_RIGHT) {
   	 		
   	 	} else if(c==KeyEvent.VK_SPACE) {
        	
        }else if(c==KeyEvent.VK_ENTER) {
        	if(currentMenu == RACE){
        		race = Charecter.races[currentPos];
        		currentMenu = CLASS;
        		currentPos = 0;
        		printMenu();
        	}else if(currentMenu == CLASS){
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
		Main.menu.setLayout(new FlowLayout());
		Main.menu.setBackground(Color.BLACK);
		Main.menu.setVisible(true);
		menuText = new JLabel();
		menuText.setPreferredSize(new Dimension(100,200));
		menuText.setVerticalAlignment(JLabel.TOP);
		Main.menu.add(menuText);
		sidebar = new CharecterInfoPanel();
		sidebar.setPreferredSize(new Dimension(200,200));
		Main.menu.add(sidebar);
		setupMenu();
	}

	public static void setupMenu() {
		currentPos = 0;
		currentMenu = RACE;
		printMenu();
	}
}
class CharecterInfoPanel extends JPanel{
	private String text;
	private String image;
	private JLabel label;
	CharecterInfoPanel(){
		this.setBackground(Color.BLACK);
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		label = new JLabel("<html><Style=\"color:white\">hiadsafsdfas");
		c.gridx = 0;
		c.gridy = 1;
		this.add(label);
	}
	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		if(image != null)
			g.drawImage(Main.images.get(image), 0, 0, 64, 64, this);
		g.setFont(Main.font);
		g.setColor(new Color(0x999999));
		((Graphics2D) g).setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		List<String> wrap = Utility.StringUtils.wrap(text, g.getFontMetrics(new Font("Courier New", Font.PLAIN, 12)), 200);
		for(int i = 0; i < wrap.size(); i++){
			g.drawString(wrap.get(i), 0, 70+(i*14));
		}
	}
	public void setText(String t){
		this.text = t;
		this.repaint();
	}
	public void setImage(String img){
		this.image = "/characterTiles/"+img;
		Main.loadImage(this.image);
		this.repaint();
	}
	public void setALL(String t,String img){
		this.image = img;
		this.text = t;
		this.repaint();
	}
}