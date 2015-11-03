import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class Inventory{
	private static TabsFrame tabs;
	private static JPanel mainContainer;
	private static JPanel sidebar;
	private static InventoryDisplay inventoryDisplay;
	private static Combat.DetailedInfo info;
	private static InvPanel charInv;
	private static int currentChar = 0;
	private static String focussedComponent = "PARTY_INV"; //PARTY_INV, CHAR_INV, CHAR_SELECT
	private static Point focussedNode = new Point(0,0);
	private static InvPanel.icon[][] InvIcons;
	
	Inventory(){
		//Main.inv.setBackground(Color.black);
		Main.inv.setLayout(new BorderLayout());
		tabs = new TabsFrame();
		Main.inv.add(tabs, BorderLayout.PAGE_START);
		mainContainer = new JPanel();
		mainContainer.setLayout(new FlowLayout(FlowLayout.LEFT));
		mainContainer.setBackground(Color.black);
		mainContainer.setBorder(BorderFactory.createLineBorder(Color.gray,2));
		//TODO sev vgap hgap for mainContainer
		Main.inv.add(mainContainer, BorderLayout.CENTER);
		sidebar = new JPanel();
		sidebar.setBorder(BorderFactory.createLineBorder(Color.white));
		sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.PAGE_AXIS));
		mainContainer.add(sidebar);
		inventoryDisplay = new InventoryDisplay();
		inventoryDisplay.setBorder(BorderFactory.createLineBorder(Color.white));
		mainContainer.add(inventoryDisplay);
		//info = new Main.combat.DetailedInfo();
		//sidebar.add(info);
		charInv = new InvPanel();
		sidebar.add(charInv);
	}
	
	public static void setup(){
		InvIcons = new InvPanel.icon[Main.player.getInventory().length][Main.player.getInventory()[0].length];//TODO
		for(int i = 0; i < Main.player.allies.size(); i++){
			Component[] components = tabs.getComponents();
			if(i < components.length){
				((TabsFrame.tab) components[i]).charecter = Main.player.allies.get(i);
			}else{
				tabs.add(new TabsFrame.tab(Main.player.allies.get(i)));
			}
			
		}
		inventoryDisplay.setup();
		//setupChar();
	}
	
	private static void setupChar(){
		if(Main.player.allies.size() > 0){
			info.setChar(Main.player.allies.get(currentChar));
			charInv.setItemSet(Main.player.allies.get(currentChar).items);
		}
	}
	
	private static class TabsFrame extends JPanel{
		private static final long serialVersionUID = 1L;
		List<tab> chars;
		private static int vgap = 5;
		TabsFrame(){
			this.setBackground(Color.black);
			this.setBorder(BorderFactory.createLineBorder(Color.gray,2));
			this.setLayout(new FlowLayout(FlowLayout.LEFT,5,vgap));
		}
		@Override
		public Dimension getPreferredSize(){
			return new Dimension(400,tab.getStaticSize().height+vgap);
		}
		
		private static class tab extends JPanel{
			private static final long serialVersionUID = 1L;
			private static Dimension size = new Dimension(80,50);
			private static int radius = 20;
			private static float lineWidth = 2;
			public boolean selected = false;
			private Charecter charecter;
			tab(){
				
			}
			tab(Charecter c){
				this.charecter = c;
			}
			@Override
			public void paintComponent(Graphics g){
				((Graphics2D) g).setStroke(new BasicStroke(lineWidth));
				((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                        RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(Color.GRAY);
				g.drawRoundRect(0, 0, size.width, size.height,radius,radius);
				if(!selected){
					g.drawLine(0, getStaticSize().height-1, size.width-1, getStaticSize().height-1);
				}
				g.drawImage(this.charecter.getIcon(), 10, 10, 32, 32, this);
			}
			@Override
			public Dimension getPreferredSize(){
				return getStaticSize();
			}
			private static Dimension getStaticSize(){
				return new Dimension(size.width-((int) Math.floor(lineWidth/2)),size.height+((int) lineWidth)-(radius/2));
			}
		}
	}
	private static class InvPanel extends JPanel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ItemSet itm;
		int selected;
		public void setItemSet(ItemSet itm){
			this.itm = itm;
		}
		InvPanel(){
			this.setLayout(null);
			this.setBackground(Color.green);
			Insets insets = this.getInsets();
			icon head = new icon();
			icon top = new icon();
			icon legs = new icon();
			icon feet = new icon();
			icon hands = new icon();
			this.add(head);
			this.add(top);
			this.add(legs);
			Dimension size = head.getPreferredSize();
			head.setBounds(25 + insets.left, 5 + insets.top,
			             size.width, size.height);
			top.setBounds(25 + insets.left, 30 + insets.top,
		             size.width, size.height);
			legs.setBounds(25 + insets.left, 55 + insets.top,
		             size.width, size.height);
		}
		InvPanel(ItemSet inv){
			this();
			setItemSet(inv);
		}
		@Override
		public Dimension getPreferredSize(){
			return new Dimension(0,90);
		}
		private static class icon extends JPanel{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private String src = null;
			private static String borderDefault = "/ItemTiles/ItemBorder20";
			private static int size;
			public boolean isSelected = false;
			public boolean isHover = false;
			@Override
			public void paintComponent(Graphics g){
				super.paintComponent(g);
				if(isSelected){
					g.drawImage(Main.loadImage(borderDefault), 0, 0, this);//TODO
				}else{
					g.drawImage(Main.loadImage(borderDefault), 0, 0, this);
				}
				if(src != null){
					g.drawImage(Main.loadImage(src), 0, 0, 20, 20, this);
				}
				if(isHover){
					g.setColor(Color.green);
					g.drawRect(0, 0, 20, 20);//TODO
				}
			}
			icon(Item item){
				this();
				if(item == null || item.getIcon() == null){
					
				}else{
					this.src = item.getSrcIndex();
				}
			}
			icon(){
				//this.setBorder(BorderFactory.createEmptyBorder());
				this.setBackground(Color.black);
			}
			@Override
			public Dimension getPreferredSize(){
				return new Dimension(20,20);
			}
		}
	}
	private static class InventoryDisplay extends JPanel{//TODO
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public Point selected;
		public Item[][] ownerInventory;
		private int width;
		private int height;

		public void setup(){
			ownerInventory = Main.player.getInventory();
			width = ownerInventory.length;
			height = ownerInventory[0].length;
			this.setLayout(new GridLayout(height, width));
			((GridLayout) this.getLayout()).setVgap(0);
			((GridLayout) this.getLayout()).setHgap(0);
			
			for(int y = 0; y < height; y++){
				for(int x = 0; x < width; x++){
					InvIcons[x][y] = new InvPanel.icon(ownerInventory[x][y]);
					this.add(InvIcons[x][y]);
				}
			}
			Console.log(width,height);
			repaint();
		}
		InventoryDisplay(){
		}
		@Override
		public Dimension getPreferredSize(){
			return new Dimension(width*21,height*21);
		}
	}
	public static void handleKeyInput(int c) {
		if(c==KeyEvent.VK_1){
			currentChar = 0;
			setupChar();
		}else if(c==KeyEvent.VK_2 && 2 <= Main.player.allies.size()){
			currentChar = 1;
			setupChar();
	    }else if(c==KeyEvent.VK_3 && 3 <= Main.player.allies.size()){
	    	currentChar = 2;
	    	setupChar();
	    }else if(c==KeyEvent.VK_4 && 4 <= Main.player.allies.size()){
	    	currentChar = 3;
	    	setupChar();
	    }else if(c==KeyEvent.VK_LEFT){
	    	if(focussedComponent == "PARTY_INV"){
	    		if(focussedNode.x > 0){
		    		InvIcons[focussedNode.x][focussedNode.y].isHover = false;
		    		InvIcons[focussedNode.x][focussedNode.y].repaint();
		    		focussedNode.x--;
		    		InvIcons[focussedNode.x][focussedNode.y].isHover = true;
		    		InvIcons[focussedNode.x][focussedNode.y].repaint();
	    		}
	    	}
	    }else if(c==KeyEvent.VK_RIGHT){
	    	if(focussedComponent == "PARTY_INV"){
	    		if(focussedNode.x+1 < InvIcons.length){
		    		InvIcons[focussedNode.x][focussedNode.y].isHover = false;
		    		InvIcons[focussedNode.x][focussedNode.y].repaint();
		    		focussedNode.x++;
		    		InvIcons[focussedNode.x][focussedNode.y].isHover = true;
		    		InvIcons[focussedNode.x][focussedNode.y].repaint();
	    		}
	    	}
	    }else if(c==KeyEvent.VK_UP){
	    	if(focussedComponent == "PARTY_INV"){
	    		if(focussedNode.y > 0){
		    		InvIcons[focussedNode.x][focussedNode.y].isHover = false;
		    		InvIcons[focussedNode.x][focussedNode.y].repaint();
		    		focussedNode.y--;
		    		InvIcons[focussedNode.x][focussedNode.y].isHover = true;
		    		InvIcons[focussedNode.x][focussedNode.y].repaint();
	    		}
	    	}
	    }else if(c==KeyEvent.VK_DOWN){
	    	if(focussedComponent == "PARTY_INV"){
	    		if(focussedNode.y+1 < InvIcons[0].length){
		    		InvIcons[focussedNode.x][focussedNode.y].isHover = false;
		    		InvIcons[focussedNode.x][focussedNode.y].repaint();
		    		focussedNode.y++;
		    		InvIcons[focussedNode.x][focussedNode.y].isHover = true;
		    		InvIcons[focussedNode.x][focussedNode.y].repaint();
	    		}
	    	}
	    }
	}
}