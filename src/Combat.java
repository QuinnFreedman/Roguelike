import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JPanel;
//TODO make ability.isAble(charecter) check (for has weapon, etc), also check resources
public class Combat extends JPanel{
	//public static JPanel charecterInfo;
	private static DetailedInfo about;
	/*public static JPanel arena;
	private static JPanel combatMenu;
	private static JLabel abilityMenu;
	private static JLabel weaponMenu;
	private static JLabel targetMenu;
	private static JLabel doneMenu;*/
	//labels
	//private static String ABILITY = "ABILITY";
	//private static String WEAPON = "WEAPON";
	//private static String TARGET = "TARGET";
	//private static String DONE = "DONE";
	
	//public static JLabel sidebar;
	private static String menuText = "";
	private static CharecterIcon[][] enemyIcons = new CharecterIcon[2][2];
	private static CharecterIcon[][] alliedIcons = new CharecterIcon[2][2];;
	private static Charecter active;
	private static List<Charecter> orderedList = new ArrayList<Charecter>();
	private static Party opponent;
	private static String log;
	public static ArrayList<Charecter> getOpponents(){
		return opponent.allies;
	}
	//combat
	private static List<Charecter> targets;
	private static Charecter target = null;
	private static Ability ability;
	private static Weapon weapon;
	private static MenuState currentMenu;
	private static int currentTarget;
	private static int currentAbility;
	private static int currentWeapon;
	Combat(){
		Console.log("new Combat");
		//this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBackground(Color.BLACK);
		currentMenu = MenuState.ABILITY;
		//charecterInfo = new JPanel();
		//charecterInfo.setBackground(Color.BLACK);
		//charecterInfo.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		//charecterInfo.setPreferredSize(new Dimension(150,283));
		//Main.combat.add(charecterInfo);
		//arena = new JPanel();
		//arena.setLayout(new GridLayout(4,2));
		//((GridLayout) arena.getLayout()).setVgap(5);
		//((GridLayout) arena.getLayout()).setHgap(5);
		//arena.setBackground(Color.black);
		//Main.combat.add(arena);
		about = new DetailedInfo();
		/*charecterInfo.add(about);
		combatMenu = new JPanel();
		combatMenu.setBackground(Color.BLACK);
		combatMenu.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		combatMenu.setLayout(new CardLayout());
		charecterInfo.add(combatMenu);
		abilityMenu = new JLabel();
		weaponMenu = new JLabel();
		targetMenu = new JLabel();
		doneMenu = new JLabel();
		combatMenu.add(abilityMenu,ABILITY);
		combatMenu.add(weaponMenu,WEAPON);
		combatMenu.add(targetMenu, TARGET);
		combatMenu.add(doneMenu, DONE);
		currentMenu = ABILITY;
		sidebar = new JLabel();
		sidebar.setPreferredSize(new Dimension(180,283));
		sidebar.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		sidebar.setVerticalAlignment(JLabel.BOTTOM);
		Main.combat.add(sidebar);*/
		for(int i = 0; i < 4; i++){
			int y = (i < 2) ? 0 : 1;
			int x = i%2;
			enemyIcons[x][y] = new CharecterIcon(null);
			//arena.add(enemyIcons[x][y]);
		}
		for(int i = 3; i >= 0; i--){
			int y = (i < 2) ? 0 : 1;
			int x = (i+1)%2;
			alliedIcons[x][y] = new CharecterIcon(null);
			//arena.add(alliedIcons[x][y]);
		}
	}
	private enum MenuState{
		ABILITY,WEAPON,TARGET,DONE
	}
	public static void init(Party opponent) {
		about.setChar(Main.player.allies.get(0));
		Combat.opponent = opponent;
		log = "";
		Main.display(Main.COMBAT);
		for(Charecter charecter : Main.player.allies){
			select(charecter, false);
		}
		orderedList.clear();
		orderedList.addAll(Main.player.allies);
		orderedList.addAll(opponent.allies);
		Collections.sort(orderedList,new CustomComparator());
		active = orderedList.get(0);
		activate(active, true);
		for(int i = 0; i < 4; i++){
			int y = (i < 2) ? 0 : 1;
			int x = i%2;
			enemyIcons[x][y].setChar(getCharAtPos(opponent.allies,new Point(x,y)));
		}
		for(int i = 3; i >= 0; i--){
			int y = (i < 2) ? 0 : 1;
			int x = (i+1)%2;
			alliedIcons[x][y].setChar(getCharAtPos(Main.player.allies, new Point(x,y)));
		}
		Main.combat.setup();
		takeTurn();
	}
	private static Charecter getCharAtPos(ArrayList<Charecter> L, Point p){
		for(Charecter charecter : L){
			if(charecter.position.x == p.x && charecter.position.y == p.y){
				if(charecter.currentHealth > 0 && orderedList.contains(charecter)){
					return charecter;
				}else{
					return null;
				}
			}
		}
		return null;
	}
	private void setup(){
		for(int x = 0; x < 2; x++){
			for(int y = 0; y < 2; y++){
				if(enemyIcons[x][y] != null)
					enemyIcons[x][y].update();
				if(alliedIcons[x][y] != null)
					alliedIcons[x][y].update();
			}
		}
		
		String output = "";
		if(active != null && active.parent == Main.player){
			if(currentMenu == MenuState.ABILITY){
				output += "Choose Ability\n";
				for(int i = 0; i < active.abilities.size(); i++){
					if(currentAbility == i){
						output += "*"+active.abilities.get(i).name+"* \n";
					}else{
						output += " "+active.abilities.get(i).name+" \n";
					}
				}
				output += ((currentAbility == active.abilities.size()) ? "*" : " ")
						+ "Wait"
						+ ((currentAbility == active.abilities.size()) ? "*" : " ")
						+"\n";
				output += ((currentAbility == active.abilities.size()+1) ? "*" : " ")
						+ "Retreat"
						+ ((currentAbility == active.abilities.size()+1) ? "*" : " ");
			}else if(currentMenu == MenuState.WEAPON){
				output += "Choose Weapon\n";
				if(active.items.PRIMAIRY.getWeapon() != null){
					if(currentWeapon == 0){
						output += "*"+active.items.PRIMAIRY.getWeapon().id+"* ";
					}else{
						output += " "+active.items.PRIMAIRY.getWeapon().id+" ";
					}
				}
				if(active.items.SECONDAIRY != null && active.items.SECONDAIRY.getWeapon() != null){
					if(currentWeapon == 1){
						output += "*"+active.items.SECONDAIRY.getWeapon().id+"* ";
					}else{
						output += " "+active.items.SECONDAIRY.getWeapon().id+" ";
					}
				}
			}
		}else if(currentMenu == MenuState.TARGET){
			output += "Choose Target\n";
		}else{
			currentMenu = MenuState.DONE;
		}
		menuText = output;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		((Graphics2D) g).scale(Main.scale, Main.scale);
	//left sidebar
		g.setColor(Color.white);
		g.setFont(Main.font);
		g.drawRect(5, 5, 140, 270);
		about.paint(g,5,5);
		Utility.drawString(g, menuText, 10, 190);
	//center
		g.drawRect(150, 5, 220, 270);
		for(int x = 0; x < 2; x++){
			for(int y = 0; y < 2; y++){
				g.drawRect(150+x*100, 5+y*100, CharecterIcon.getSize().width, CharecterIcon.getSize().height);
				if(enemyIcons[x][y] != null){
					enemyIcons[x][y].paint(g, 150+x*CharecterIcon.getSize().width, 5+y*CharecterIcon.getSize().height);
				}
				g.drawRect(150+x*100, 205+y*100, CharecterIcon.getSize().width, CharecterIcon.getSize().height);
				if(alliedIcons[x][y] != null){
					alliedIcons[x][y].paint(g, 150+x*CharecterIcon.getSize().width, 5+2*CharecterIcon.getSize().height+y*CharecterIcon.getSize().height);
				}
			}
		}
	//right sidebar
		g.drawRect(375, 5, 180, 250);
		Utility.drawString(g, log, 380, 5, 170, 250);
	}
	
	private static void takeTurn(){
		Console.log(1,"takeTurn");
		ability = null;
		activate(active, true);
		about.setChar(active);
		//UPKEEP
		for(Charecter charecter : orderedList){
			charecter.currentHealth += charecter.healthRegen;
			charecter.currentMana += charecter.manaRegen;
			charecter.currentEnergy += charecter.energyRegen;
		}
		//All aura effects
		Iterator<Charecter> charItr = orderedList.iterator();
		while(charItr.hasNext()) {
	    	Charecter charecter = charItr.next();
	    	Iterator<Aura> auraItr = charecter.auras.iterator();
	    	while(auraItr.hasNext()) {
		    	Aura aura = auraItr.next();//TODO concurent mod except
				if(aura.trigger == Aura.TriggerType.TICK){
					aura.effect();
				}
			}
		}
		Iterator<Aura> auraItr = active.auras.iterator();
    	while(auraItr.hasNext()) {
	    	Aura aura = auraItr.next();
			if(aura.trigger == Aura.TriggerType.UPKEEP){
				aura.effect();
			}
		}
		cleanup();
		//COMBAT
		if(orderedList.contains(active) && active.stunned == false){
			if(active.parent != Main.player){//NPC
				Console.log(1,"NPC "+active.race+" "+active.clas.getName());
				int abilityIndex = 0;
				boolean cont;
				do{
					Console.log(1,"Trying ability "+abilityIndex);
					cont = false;
					if(abilityIndex < active.abilities.size()){
						ability = active.abilities.get(abilityIndex);
						weapon = active.items.PRIMAIRY.getWeapon();
						targets = getInRangeCharecters(active, ability, ability.getRange(weapon));
						if(targets == null || targets.size() == 0 || ability.getEnergyCost() > active.currentEnergy || ability.getManaCost() > active.currentMana){
							abilityIndex++;
							cont = true;
							Console.log("can't cast");
						}
					}else{
						log("WAIT");
					}
					Console.log("continue = "+Boolean.toString(cont));
					Console.log(-1,"");
				}while(cont);
				Console.log(-1,"");
				if(targets != null && targets.size() != 0 && abilityIndex < active.abilities.size()){
					int get = (int) Math.floor(Math.random()*targets.size());
					target = targets.get(get);
					takeTurn2();
					return;
				}else{
					wait(active);
				}
			}else{//Player
				currentAbility = 0;
				currentMenu = MenuState.ABILITY;
				print("Choose Ability");
			}
		}else{
			if(active.stunned){
				active.stunned = false;
				//log(active.race+" "+active.clas.getName()+" is stunned");
			}
			takeTurn3();
			return;
		}
	}
	private static void takeTurn2(){
		Console.log("takeTurn2");
		if(ability != null){
			log(active.race+" "+active.clas.getName()+" uses "+ability.name+" with his "+weapon.id+" on "+target.race+" "+target.clas.getName());
			boolean b = ability.effect(new Charecter[]{target}, (Weapon) active.items.PRIMAIRY);
			if(!b){
				log("The "+target.clas.getName()+" dodges the ability");
			}
			if(ability.getEnergyCost() != -1)
				active.currentEnergy -= ability.getEnergyCost();
			if(ability.getManaCost() != -1)
				active.currentMana -= ability.getManaCost();
		}
		takeTurn3();
	}
	private static void takeTurn3(){
		Console.log("takeTurn3");
		//EOT
		for(Aura aura : active.auras){
			if(aura.trigger == Aura.TriggerType.EOT){
				aura.effect();
			}
		}
		cleanup();
		Console.log(-1,"");
		
		boolean allDead = true;
		boolean opponentsDead = true;
		
		for(Charecter charecter : opponent.allies){
			if(charecter.currentHealth > 0){
				allDead = false;
				break;
			}
		}
		
		for(Charecter charecter : Main.player.allies){
			if(charecter.currentHealth > 0){
				opponentsDead = false;
				break;
			}
		}
		
		//active.selected = '_';
		if(active.parent == Main.player){
			alliedIcons[active.position.x][active.position.y].setActive(false);
		}else{
			enemyIcons[active.position.x][active.position.y].setActive(false);
		}
		if(allDead || opponentsDead){
			log("all dead");
			currentMenu = MenuState.DONE;
		}else{
			active = orderedList.get((getCharecterPos(active, orderedList)+1)%orderedList.size());
			takeTurn();
		}
		Main.combat.setup();
	}
	private static void cleanup(){		
		ListIterator<Charecter> itr = orderedList.listIterator();
	  	while(itr.hasNext()) {
	  		Charecter element = itr.next();
	  		if(element.currentHealth <= 0){
	  			log("The "+element.clas.getName()+" dies");
	  			if(element.parent != Main.player){
	  				for(Charecter charecter : Main.player.allies){
	  					charecter.exp += element.lvl*2;
	  					if(charecter.exp > charecter.levelUpPoints*40){
	  						charecter.exp %= 40;
	  						charecter.lvl += 1;
	  						charecter.levelUpPoints += 3;
	  					}
	  				}
	  			}
	  			itr.remove();
	  		}
	  	}
	}
	public static void remove(Charecter charecter){
		log(charecter.race+" "+charecter.clas.getName()+" is left the battle");
		orderedList.remove(charecter);
	}
	private static int getCharecterPos(Charecter charecter, List<Charecter> list) {
	    for(int i = 0; i < list.size(); ++i) {
	        if(charecter == list.get(i)) return i;
	    }

	    return -1;
	}
	private static List<Charecter> getInRangeCharecters(Charecter caster, Ability a, int range){
		List<Charecter> inRangeCharecters = new ArrayList<Charecter>();
		ArrayList<Charecter> target = (caster.parent == Main.player) ? opponent.allies : Main.player.allies;
		if(a.targetType == "ALLY" && caster.parent == Main.player){
			inRangeCharecters = Main.player.allies;
		}else if(a.targetType == "ENEMY"){
			int position = caster.position.y;
			boolean enemyFrontLineOpen = (getCharAtPos(target,new Point(0,1)) == null && getCharAtPos(target,new Point(1,1)) == null);
			//if no one in front line, position = 1;
			if(getCharAtPos(caster.parent.allies,new Point(0,1)) == null && getCharAtPos(caster.parent.allies,new Point(1,1)) == null)
				position = 1;
			if(position == 0){
				//range: 0 = mele, 1 = back row to front, 2 = front to back, back to front, 3 = ranged
				if(range == 0){
					return null;
				}
				if(range > 0){
					for(int x = 0; x < 2; x++){
						if(getCharAtPos(target,new Point(x,1)) != null){
							inRangeCharecters.add(getCharAtPos(target, new Point(x,1)));
						}
					}
				}
				if(range == 3 || ((range == 1 || range == 2) && enemyFrontLineOpen)){
					for(int x = 0; x < 2; x++){
						if(getCharAtPos(target,new Point(x,0)) != null){
							inRangeCharecters.add(getCharAtPos(target, new Point(x,0)));
						}
					}
				}
			}else if(position == 1){
				//if range == 0 || 1
				for(int x = 0; x < 2; x++){
					if(getCharAtPos(target,new Point(x,1)) != null){
						inRangeCharecters.add(getCharAtPos(target, new Point(x,1)));
					}
				}
				if(range == 2 || range == 3 || ((range == 0 || range == 1) && enemyFrontLineOpen)){
					for(int x = 0; x < 2; x++){
						if(getCharAtPos(target,new Point(x,0)) != null){
							inRangeCharecters.add(getCharAtPos(target, new Point(x,0)));
						}
					}
				}
				
			}
		}
		
		return inRangeCharecters;
	}
	private static void retreat(Charecter charecter){
		log("The "+charecter.race+" "+charecter.clas.name+" is retreating");
		charecter.auras.add(new Aura.Retreating(charecter, 0));
	}
	private static void print(String s){
		Console.log(s);
		log += s+"\n";
	}
	static void log(String s){
		print(s+"\n");
	}
	private static void select(Charecter c, boolean b){
		if(c != null){
			if(c.parent == Main.player){
				alliedIcons[c.position.x][c.position.y].setSelected(b);
			}else{
				enemyIcons[c.position.x][c.position.y].setSelected(b);
			}
			if(!b){
				activate(c,false);
			}
		}
	}
	private static void activate(Charecter c, boolean b){
		if(c != null){
			if(c.parent == Main.player){
				alliedIcons[c.position.x][c.position.y].setActive(b);
			}else{
				enemyIcons[c.position.x][c.position.y].setActive(b);
			}
		}
	}
	public static void handleKeyInput(int c) {
		if(currentMenu == MenuState.ABILITY){
			if(c==KeyEvent.VK_LEFT) {
				if(currentAbility > 0)
					currentAbility--;
			} else if(c==KeyEvent.VK_RIGHT) {               
				if(currentAbility+1 < active.abilities.size()+2)
					currentAbility++;
			}else if(c == KeyEvent.VK_ENTER){
				if(currentAbility < active.abilities.size()){
					ability = active.abilities.get(currentAbility);
					if(ability.getEnergyCost() != -1 && active.currentEnergy-ability.getEnergyCost() < 0){
						log("Not enougth energy to cast "+ability.name);
					}else if(ability.getManaCost() != -1 && active.currentMana-ability.getManaCost() < 0){
						log("Not enougth mana to cast "+ability.name);
					}else{
						if(ability.usesWeapon == true){
							currentMenu = MenuState.WEAPON;
							print("Choose Weapon");
						}else{
							weapon = null;
							if(ability.targetType != "SELF"){
								currentMenu = MenuState.TARGET;
								print("Choose Target");
							}else{
								target = active;
								takeTurn2();
							}
						}
					}
				}else if(currentAbility == active.abilities.size()){
					wait(active);
				}else if(currentAbility == active.abilities.size()+1){
					retreat(active);
					takeTurn3();
				}
			}
			Main.combat.setup();
		}else if(currentMenu == MenuState.WEAPON){
			if(c==KeyEvent.VK_LEFT) {
				if(currentWeapon > 0)
					currentWeapon--;
			} else if(c==KeyEvent.VK_RIGHT) {
				if(currentWeapon < ((active.items.PRIMAIRY.getClass().getSuperclass().getName() == "WEAPON" && active.items.SECONDAIRY.getClass().getSuperclass().getName() == "WEAPON") ? 1 : 0))
					currentWeapon++;
			}else if(c == KeyEvent.VK_ENTER){
				weapon = currentWeapon == 0 ? active.items.PRIMAIRY.getWeapon() : active.items.SECONDAIRY.getWeapon();
				targets = getInRangeCharecters(active, ability, ability.getRange(weapon));
				if(targets != null){
					target = targets.get(0);
					select(target,true);
					print("Choose Target");
					currentMenu = MenuState.TARGET;
				}else{
					log("no in-range targets for "+ability.name+" with "+weapon.id);
					print("Choose Ability");
					currentMenu = MenuState.ABILITY;
				}
			}
			Main.combat.setup();
		}else if(currentMenu == MenuState.TARGET){
			if(c==KeyEvent.VK_LEFT) {
				currentTarget--;
			} else if(c==KeyEvent.VK_RIGHT) {                
				currentTarget++;
			}
			currentTarget = Math.abs(currentTarget%targets.size());
			for(Charecter charecter : targets){
				select(charecter,false);
			}
			select(targets.get(currentTarget),true);
			if(c == KeyEvent.VK_ENTER){
				target = targets.get(currentTarget);
				select(target,false);
				takeTurn2();
			}
			Main.combat.setup();
		}else if(currentMenu == MenuState.DONE){
			Main.display(Main.ROGUELIKE);
		}
	}
	private static void wait(Charecter charecter) {
		log("The "+charecter.race+" "+charecter.clas.getName()+" bides his time");
		takeTurn3();
	}
	private static class CharecterIcon{
		Charecter c;
		private BufferedImage image;
		private int MaxHealth;
		private int currentHealth;
		private int MaxEnergy;
		private int currentEnergy;
		private int MaxMana;
		private int currentMana;
		private boolean selected;
		private boolean active;
		
		CharecterIcon(Charecter c){
			if(c != null){
				this.c = c;
				this.MaxHealth = c.health;
				this.currentHealth = c.currentHealth;
				this.MaxEnergy = c.energy;
				this.currentEnergy = c.currentEnergy;
				this.MaxMana = c.mana;
				this.currentHealth = c.currentMana;
				this.image = c.getIcon();
			}
			//setBorder(BorderFactory.createLineBorder(Color.darkGray));
			//setBackground(Color.BLACK);
		}
		public void setChar(Charecter c){
			this.c = c;
			if(c != null){
				this.MaxHealth = c.health;
				this.currentHealth = c.currentHealth;
				this.MaxEnergy = c.energy;
				this.currentEnergy = c.currentEnergy;
				this.MaxMana = c.mana;
				this.currentHealth = c.currentMana;
				this.image = c.getIcon();
			}
			Main.combat.repaint();
		}
		public void update(){
			if(c != null){
				this.currentHealth = c.currentHealth;
				this.currentMana = c.currentMana;
				this.currentEnergy = c.currentEnergy;
				if(c.currentHealth <= 0)
					this.c = null;
				Main.combat.repaint();
			}
		}
		public void setSelected(boolean b){
			this.selected = b;
			Main.combat.repaint();
			/*if(b){
				this.setBorder(BorderFactory.createLineBorder(Color.WHITE));
			}else{
				this.setBorder(BorderFactory.createLineBorder(Color.darkGray));
			}*/
		}
		public void setActive(boolean b){
			this.active = b;
			Main.combat.repaint();
			/*if(b){
				this.setBorder(BorderFactory.createLineBorder(Color.WHITE));
			}else{
				this.setBorder(BorderFactory.createLineBorder(Color.darkGray));
			}*/
		}
		public void paint(Graphics g, int x, int y){
			if(this.c != null){
				g.drawImage(image, x, y, 64, 64, Main.combat);
				g.setColor(new Color(0x880000));
				g.drawRect(x+64, y, 9, 63);
				g.fillRect(x+64, y, 9, (int) 64*this.currentHealth/this.MaxHealth);
				g.setColor(new Color(0xFDD017));
				g.drawRect(x+74, y, 9, 63);
				g.fillRect(x+74, y, 9, (int) 64*this.currentEnergy/this.MaxEnergy);
				g.setColor(Color.blue);
				g.drawRect(x+84, y, 9, 63);
				g.fillRect(x+84, y, 9, (int) 64*this.currentMana/this.MaxMana);
				g.setFont(Main.font);
				g.setColor(new Color(0x999999));
				/*((Graphics2D) g).setRenderingHint(
				        RenderingHints.KEY_TEXT_ANTIALIASING,
				        RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);*/
				g.drawString("(lvl "+Integer.toString(c.lvl)+")", x, y+60);
				if(selected){
					((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));	
					g.fillRect(x, y, getSize().width, getSize().height);
					((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
				}
				
			}
		}
		public static Dimension getSize() {
	        return new Dimension(94,64);
	    }
	}
	public class DetailedInfo{
		private Charecter c;
		private String label2;
		private String label1;
		//private JPanel auras;
		public void setChar(Charecter c){
			if(this.c != c){
				this.c = c;
				if(c == null){
					label2 = "";
					label1 = "";
				}else{
					label1 =
							((c.name != null) ? c.name : "")+"\n"
							+ c.race+" "+c.clas.getName()+"\n"
							+ "Level "+c.lvl;
					label2 =
							"Health: "+c.currentHealth+"/"+c.health+" (+"+c.healthRegen+")\n"
							+ ((c.mana != -1) ? "Mana: "+c.currentMana+"/"+c.mana+" (+"+c.manaRegen+")\n" : "")
							+ "Energy: "+c.currentEnergy+"/"+c.energy+" (+"+c.manaRegen+")\n"
							+ "Armor: "+c.armor+"\n"
							+ "Magic Resitance: "+c.magicResist+"\n"
							+ "Damage: "+c.damage+"\n"
							+ ((c.magicDamage != 0) ? "Magic Damage : "+c.magicDamage : "\n \n");
				}
				
			}
		}
		public void paint(Graphics g, int x, int y){
			Utility.drawString(g, this.label1, x+37, y, 98);
			g.drawImage(c.getIcon(), x+5, y+5, Main.combat);
			for(int i = 0; i < c.auras.size(); i++){
				g.drawImage(c.auras.get(i).getIcon(), x+5+37*i, y+45, 32, 32, Main.combat);
			}
			Utility.drawString(g, this.label2, x+5, y+61, 130);
		}
		DetailedInfo(){
			
		}
		DetailedInfo(Charecter charecter){
			this();
			setChar(charecter);
		}
	}
}
class CustomComparator implements Comparator<Charecter> {
    @Override
    public int compare(Charecter a, Charecter b) {
        return (Integer.compare(a.initiative, b.initiative) != 0 ? Integer.compare(a.initiative, b.initiative)*-1 : (int) Math.round(Math.random()-0.5));
    }
}