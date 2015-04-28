import java.awt.AlphaComposite;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class Roguelike{
	public static Dimension map = new Dimension(30,20);
	public static Dimension tileSize = new Dimension(32,32);
	
	public static viewPort viewPort = new viewPort(new Dimension(6,4),new Point());
	
	public static char[][] board = new char[map.height][map.width];
	public static int[][] walls = new int[map.height][map.width];
	public static int[][] dynamicElements = new int[map.height][map.width];
	public static String[][] parties = new String[map.height][map.width];
	public static boolean[][] visibleArea;
	
	private static int[][] overlapWeight = new int[map.height][map.width];
	
	public static char[][] debugBoard = new char[map.height][map.width];
	
	private static char[] textures = new char[]{'.','%','-','+'};
	
	private static List<Party> monsters = new ArrayList<Party>();
	
	public static List<Room> rooms = new ArrayList<Room>();
	
	private static int numberOfPartys = 0;
	
	private static tilePanel mainBoard;
	
	//private static JLabel sidebar;
	
	public static boolean noClip = false;
	
	Roguelike(){
		Main.roguelike.setOpaque(true);
		Main.roguelike.setVisible(true);
		Main.roguelike.setBackground(Color.BLACK);
		Main.roguelike.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		if(mainBoard == null){
			mainBoard = new tilePanel();
			Main.roguelike.add(mainBoard);
		}
		/*if(sidebar == null){
			sidebar = new JLabel();
			sidebar.setBackground(Color.BLACK);
			sidebar.setText("ERROR");
		//	sidebar.setBorder(BorderFactory.createLineBorder(Color.WHITE));
			Main.roguelike.add(sidebar);
		}*/
		Main.parent.add(Main.roguelike, Main.ROGUELIKE);
		CardLayout cl = (CardLayout)(Main.parent.getLayout());
		cl.next(Main.parent);
		Main.parent.repaint();
		Main.parent.revalidate();
		
	}
	
	public static void init(int s){
		Debug.startTime = System.nanoTime();
		Debug.lastTime = Debug.startTime;
		
		Main.debug.clear();
		Menu.setupMenu();
		Main.display(Main.MENU);
		
		Console.log("***********init()***********");
		
		Main.seed = s;
		Main.generator = new Random(s);
		Console.log("seed = "+s);
				
		clearWalls();
		Console.benchmark();
		setWalls();
		Console.benchmark();
		setPaths();
		Console.benchmark();
		clear();
		visibleArea = new boolean[map.height][map.width];
		
		int random = (int) Math.floor(Math.random()*rooms.size());
		Room room = rooms.get(random);
		Main.player = new Player(Utility.randomBetween(room.xpos,room.xpos+room.w-1),Utility.randomBetween(room.ypos,room.ypos+room.h-1));
		
		monsters.clear();
		rooms.clear();
		
		board[Main.player.getYpos()][Main.player.getXpos()] = Main.player.getId();
		parties[Main.player.getYpos()][Main.player.getXpos()] = Main.player.getSrcIndex();
		
		for(int i = 0; i < numberOfPartys; i++){
			
			int localxpos;
			int localypos;			
			
			do{
				localypos = (int) Math.floor(Main.generator.nextDouble()*board.length);
				localxpos = (int) Math.floor(Main.generator.nextDouble()*board[0].length);
				
			}while(isClear(localypos,localxpos) == false);
			
			monsters.add(new Party('X',localxpos,localypos));
			board[monsters.get(i).getYpos()][monsters.get(i).getXpos()] = monsters.get(i).getId();
		}
	
		
		
		//*********GAMELOOP 1**********
		
		
		
		for(int i = 0; i < monsters.size(); i++){
	
			board[monsters.get(i).getYpos()][monsters.get(i).getXpos()] = monsters.get(i).getId();

		}
		
		//**Debug**
		horizontalLine();
		System.out.print("   x = ");
		for(int x = 0; x < board[0].length; x++){
			System.out.print(""+Utility.expand(x,2)+"|");
		}
		Console.log();
		for(int y = 0; y < board.length; y++)
		{
			System.out.print("y = "+Utility.expand(y,2)+")");
			for(int x = 0; x < board[0].length; x++){
				if(debugBoard[y][x] != 'k'){
					System.out.print("["+debugBoard[y][x]+"]");
				}else{
					System.out.print("["+board[y][x]+"]");
				}
				
			}
			
			Console.log();
		}
		horizontalLine();
		
		Console.benchmark();
		//render
		render();
		Console.benchmark();

		Main.debug.finish();
		
		Console.log(0,"init - done");
	}
	
	static void gameloop()//TODO make private
	{
		movePartys();
		
		clear();
		
		board[Main.player.getYpos()][Main.player.getXpos()] = Main.player.getId();
		parties[Main.player.getYpos()][Main.player.getXpos()] = Main.player.getSrcIndex(); 
		
		for(int i = 0; i < monsters.size(); i++){
			board[monsters.get(i).getYpos()][monsters.get(i).getXpos()] = monsters.get(i).getId();			
		}
		
		render();
	}
	public static void move(Party m, int dir){
		if(dir == 1 && isClear(m.getYpos()+1, m.getXpos())){
			
			m.setYpos(m.getYpos()+1);
		
		}else if(dir == 2 && isClear(m.getYpos(), m.getXpos()+1)){
		
			m.setXpos(m.getXpos()+1);
		
		}else if(dir == 3 && isClear(m.getYpos()-1, m.getXpos())){
		
			m.setYpos(m.getYpos()-1);
	
		}else if(dir == 4 && isClear(m.getYpos(), m.getXpos()-1)){
			
			m.setXpos(m.getXpos()-1);
		
		}	
	}
	
	private static void movePartys(){
		for(int i = 0; i < monsters.size(); i++){
			int dir = (int) Math.ceil(Main.generator.nextDouble()*4);
			move(monsters.get(i), dir);
					
		}
	}
	
	private static boolean isClear(int y, int x){
		if(x < 0 || y < 0 || x >= board[0].length || y >= board.length){
			return false;
		}else{
			if(noClip){
				return true;
			}
			if(Main.player.getXpos() == x && Main.player.getYpos() == y){
				return false;
			}
			if(walls[y][x] == 1){
				return false;
			}
			for(int i = 0; i < monsters.size(); i++){
				if(monsters.get(i).getXpos() == x && monsters.get(i).getYpos() == y){
					return false;
				}
			}
		}
		
		return true;
	}

	private static void clear(){
		
		for(int y = 0; y < board.length; y++)
		{
			for(int x = 0; x < board[0].length; x++){
				dynamicElements[y][x] = 0;
				parties[y][x] = null;
				if(walls[y][x] < textures.length){
					board[y][x] = textures[walls[y][x]];
				}else{
					board[y][x] = Integer.toString(walls[y][x]).charAt(0);
				}
			}
		}		
	}
	private static void clearWalls(){
		
		for(int y = 0; y < walls.length; y++){
			for(int x = 0; x < walls[0].length; x++){
				walls[y][x] = 0;
				debugBoard[y][x] = 'k';
				
			}
		}
		
	}
	
	private static void setWalls() {
		Console.log(1, "setWalls()");
		rooms.clear();
		Main.debug.DrawRooms(new ArrayList<Integer>());
		for(int r = 0; r < 9; r++){ //r = room number
			Console.log(1,"room "+r);
			rooms.add(new Room());
			/*int collideN = 0;
			int collideS = 0;
			int collideE = 0;
			int collideW = 0;
			boolean Continue;
			boolean Randomize;
			int itt = 0;
			rooms.add(new Room());
			Main.debug.DrawRooms(null);
			int x = 0;
			int y = 0;
			boolean hasMovedNorth = false;
			boolean hasMovedSouth = false;
			boolean hasMovedEast = false;
			boolean hasMovedWest = false;
			Console.log(1,"");
			do{
				collideS = 0;
				collideN = 0;
				collideE = 0;
				collideW = 0;
				Continue = false;
				Randomize = false;
				Console.log("collide");
				Console.log("itt = "+itt);
				Console.log("x = "+rooms.get(r).xpos+", y = "+rooms.get(r).ypos+", w = "+rooms.get(r).w+", h ="+rooms.get(r).h);
				Main.debug.DrawRooms(new ArrayList<Integer>());
				for(y = rooms.get(r).ypos - 1; y < rooms.get(r).ypos + rooms.get(r).h + 1; y++){	
					for(x = rooms.get(r).xpos - 1; x < rooms.get(r).xpos + rooms.get(r).w + 1; x++){
						
						if(y < walls.length && y >= 0 && x < walls[y].length && x >= 0){
							if(walls[y][x] == 1 || walls[y][x] == 2){
								if(y < rooms.get(r).ypos + (rooms.get(r).h + 1)/2){
									//Console.log("walls["+y+"]["+x+"] = "+Integer.toString(walls[y][x])+": collideN");
									collideN++;
								
								}else{
									//Console.log("walls["+y+"]["+x+"] = "+Integer.toString(walls[y][x])+": collideS");
									collideS++;
									
								}
								if(x < rooms.get(r).xpos + (rooms.get(r).w + 1)/2){
									//Console.log("walls["+y+"]["+x+"] = "+Integer.toString(walls[y][x])+": collideW");
									collideW++;
									
								}else{
									//Console.log("walls["+y+"]["+x+"] = "+Integer.toString(walls[y][x])+": collideE");
									collideE++;
								}
							}
						}else{
							//Console.log("x or y margin off the map ("+x+", "+y+")");
							//ignore margins when at edge of map
						}
					}
					
				}
				
				Console.log("collideN = "+collideN+"; collideS = "+collideS+"; collideE = "+collideE+"; collideW = "+collideW);
				
				int max = Utility.findMaxValue(new int[]{collideN, collideS, collideE, collideW});
				
				if(max == 0){
					//done
					Console.log("room clear");
					break;
				}else if(max == collideN /&& collideS == 0/ && !hasMovedNorth){
					Console.log("Move South");
					if(!(rooms.get(r).ypos+rooms.get(r).h+1 >= walls.length)){
				 		rooms.get(r).ypos++;
						hasMovedSouth = true;
						Continue = true;
					}else{
						Console.log("can't move south - off map");
						Randomize = true;
					}
				}else if(max == collideS && !hasMovedSouth){
					Console.log("Move North");
					if(!(rooms.get(r).ypos <= 0)){
						rooms.get(r).ypos--;
						hasMovedNorth = true;
						Continue = true;
					}else{
						Console.log("can't move north - off map");
						Randomize = true;
					}
					
				}else if(max == collideE && !hasMovedEast){
					Console.log("Move West");
					if(!(rooms.get(r).xpos <= 0)){
						rooms.get(r).xpos--;
						hasMovedWest = true;
						Continue = true;
					}else{
						Console.log("can't move west - off map");
						Randomize = true;
					}
					
				}else if(max == collideW && !hasMovedWest){
					Console.log("Move East");
					if(!(rooms.get(r).xpos+rooms.get(r).w+1 >= walls[0].length)){
						rooms.get(r).xpos++;
						hasMovedEast = true;
						Continue = true;
					}else{
						Console.log("can't move east - off map");
						Randomize = true;
					}
				}else{
					Console.log("multiple overlap - randomize");
					Randomize = true;
				}
			
				if(Randomize == true){
					if(itt < 5){
						Console.log("Randomize room");
						rooms.set(r, new Room());
						hasMovedNorth = false;
						hasMovedSouth = false;
						hasMovedEast = false;
						hasMovedWest = false;
						Continue = true;
					}else{
						rooms.remove(rooms.size()-1);
						break;
					}
				}
				
			}while(Continue == true);
			*/
			Main.debug.setWeights(overlapWeight);
			Main.debug.DrawRooms(new ArrayList<Integer>());
			Console.log(-1,"");
		}
		
		
		ArrayList<int[]> vectors = new ArrayList<int[]>();
		
		Console.log(1,"Move Rooms");
		int itt = 0;
		do{
			setWeights();
			Main.debug.setWeights(overlapWeight);
			Console.log(1,"itt "+itt);
			for(int r = 0; r < rooms.size(); r++) {
				int[][] overlap = new int[2][2];
					//[[NW,NE],
					// [SW,SE]]
				int vectorNE;
				int vectorSE;
				int vectorE;
				int vectorS;
				float halfWidth = rooms.get(r).w >> 1;
				float halfHeight = rooms.get(r).h >> 1;
				for(int x = 0; x < rooms.get(r).w; x++){
					for(int y = 0; y < rooms.get(r).h; y++){
						if((float)x != halfWidth && (float)y != halfHeight)
							overlap[((y < halfHeight) ? 0 : 1)][((x < halfWidth) ? 0 : 1)] += overlapWeight[y+rooms.get(r).ypos][x+rooms.get(r).xpos] - 1;
					}
				}
				vectorNE = (overlap[1][0] == overlap[0][1]) ? 0 : (int) (Math.max(Math.round(Math.sqrt((float) overlap[1][0]) - Math.sqrt((float) overlap[0][1])), ((overlap[1][0] - overlap[0][1] > 0) ? 1 : -1)));
				vectorSE = (overlap[0][0] == overlap[1][1]) ? 0 : (int) (Math.max(Math.round(Math.sqrt((float) overlap[0][0]) - Math.sqrt((float) overlap[1][1])), ((overlap[0][0] - overlap[1][1] > 0) ? 1 : -1)));
					Console.log(1,"Vectors for rooms.get(r)@("+rooms.get(r).xpos+","+rooms.get(r).ypos+")");
					Console.log("["+Integer.toString(overlap[0][0])+","+Integer.toString(overlap[0][1])+"]");
					Console.log("["+Integer.toString(overlap[1][0])+","+Integer.toString(overlap[1][1])+"]");
					Console.log("vectorNE = "+vectorNE);
					Console.log("vectorSE = "+vectorSE);
				vectorE = (int) Math.round(0.7f*(vectorSE + vectorNE));
				vectorS = (int) Math.round(0.7f*(vectorSE - vectorNE));
					Console.log("vectorE = "+vectorE);
					Console.log("vectorS = "+vectorS);
					Console.log(-1,"");
				if(r >= vectors.size()){
					vectors.add(new int[]{vectorE,vectorS});
				}else{
					vectors.set(r, new int[]{vectorE,vectorS});
				}
				Main.debug.DrawVector(new Point((int) Math.round((rooms.get(r).xpos + (float) rooms.get(r).w/2)*Debug.scale), (int) Math.round((rooms.get(r).ypos + (float) rooms.get(r).h/2)*Debug.scale)), vectorE*Debug.scale, 0);
				Main.debug.DrawVector(new Point((int) Math.round((rooms.get(r).xpos + (float) rooms.get(r).w/2)*Debug.scale), (int) Math.round((rooms.get(r).ypos + (float) rooms.get(r).h/2)*Debug.scale)), vectorS*Debug.scale, 1.5);
				Main.debug.DrawVector(new Point((int) Math.round((rooms.get(r).xpos + (float) rooms.get(r).w/2)*Debug.scale), (int) Math.round((rooms.get(r).ypos + (float) rooms.get(r).h/2)*Debug.scale)), vectorNE*Debug.scale, 0.25);
				Main.debug.DrawVector(new Point((int) Math.round((rooms.get(r).xpos + (float) rooms.get(r).w/2)*Debug.scale), (int) Math.round((rooms.get(r).ypos + (float) rooms.get(r).h/2)*Debug.scale)), vectorSE*Debug.scale, 1.75);
			}
			Main.debug.update();
			Main.debug.clearVectors();
			for(int r = 0; r < rooms.size(); r++){
				rooms.get(r).xpos += vectors.get(r)[0];
				if(rooms.get(r).xpos < 0)
					rooms.get(r).xpos = 0;
				else if(rooms.get(r).xpos + rooms.get(r).w > Roguelike.map.width)
					rooms.get(r).xpos = Roguelike.map.width - rooms.get(r).w;
				
				rooms.get(r).ypos += vectors.get(r)[1];
				if(rooms.get(r).ypos < 0)
					rooms.get(r).ypos = 0;
				else if(rooms.get(r).ypos + rooms.get(r).h > Roguelike.map.height)
					rooms.get(r).ypos = Roguelike.map.height - rooms.get(r).h;
			}
			Console.log(-1,"");
			itt++;
		}while(itt < 10);
		Console.log(-1,"");
		
		Main.debug.DrawRooms(new ArrayList<Integer>());
		
		for (int r = 0; r < rooms.size(); r++) {
			rooms:{
			Console.log(1,"room "+r+" ("+rooms.get(r).xpos+","+rooms.get(r).ypos+")");
			for(int x = 0; x < rooms.get(r).w; x++){
				for(int y = 0; y < rooms.get(r).h; y++){
					if(overlapWeight[y+rooms.get(r).ypos][x+rooms.get(r).xpos] > 1){
						Console.log(-1,"removing room "+r);
						rooms.remove(r);
						r--;
						setWeights();
						Main.debug.DrawRooms(new ArrayList<Integer>());
						break rooms;
					}
				}
			}
			rooms.get(r).construct();
			Console.log(-1,"");
			}
		}
		
		for(int r = 0; r < rooms.size(); r++){
			for(int i = 0; i < rooms.get(r).roomWalls.size(); i++){
				if(rooms.get(r).roomWalls.get(i).x >= 0){
					walls[rooms.get(r).roomWalls.get(i).y][rooms.get(r).roomWalls.get(i).x] = 1;
				}
			}
			for(int y1 = rooms.get(r).ypos; y1 < rooms.get(r).ypos + rooms.get(r).h; y1++){	
				for(int x1 = rooms.get(r).xpos; x1 < rooms.get(r).xpos + rooms.get(r).w; x1++){
					if(walls[y1][x1] == 0){
						walls[y1][x1] = 2;
					}
				}
				
			}
		}
		Console.log(-1, "");
	}
	private static void setWeights(){
		for(int y = 0; y < walls.length; y++){
			for(int x = 0; x < walls[0].length; x++){
				overlapWeight[y][x] = 0;				
			}
		}
		for(int r = 0; r < rooms.size(); r++){
			for(int x = -1; x <= rooms.get(r).w; x++){
				if(x+rooms.get(r).xpos >= 0 && x+rooms.get(r).xpos < map.width){
					for(int y = -1; y <= rooms.get(r).h; y++){
						if(y+rooms.get(r).ypos >= 0 && y+rooms.get(r).ypos < map.height){
							overlapWeight[y+rooms.get(r).ypos][x+rooms.get(r).xpos] += 1;
						}
					}
				}
			}
		}
	}
	
	//TODO fix doors on top of doors ( x < 0 )
	private static void setPaths(){
		List<Point> doors = new ArrayList<Point>();
		for(int i = 0; i < rooms.size(); i++){
			for(int e = 0; e < rooms.get(i).roomDoors.size(); e++){
				doors.add(rooms.get(i).roomDoors.get(e));
			}
		}
		Pathing.setPaths(walls, doors);		
	}

	public static class viewPort{
		public Point position;
		public Dimension size;
		
		viewPort(){
			size = new Dimension();
			position = new Point();
		}
		viewPort(Dimension d, Point p){
			size = d;
			position = p;
		}
	}
	
	public static void render(){
		
		if(Main.player.getXpos()-viewPort.size.width >= 0 && (viewPort.size.width)+1+Main.player.getXpos() < map.width){
			viewPort.position.x = Main.player.getXpos()-viewPort.size.width;
		}else if(!(Main.player.getXpos()-viewPort.size.width >= 0) && !((viewPort.size.width)+1+Main.player.getXpos() < map.width)){
			Console.log("view port too large");
			viewPort.size.width = map.width;
		}else if (Main.player.getXpos()-viewPort.size.width < 0){
			viewPort.position.x = 0;
		}else if((viewPort.size.width)+1+Main.player.getXpos() >= map.width){
			viewPort.position.x = map.width-((viewPort.size.width*2)+1);
		}
		
		if(Main.player.getYpos()-viewPort.size.height >= 0 && (viewPort.size.height)+1+Main.player.getYpos() < map.height){
			viewPort.position.y = Main.player.getYpos()-viewPort.size.height;
		}else if(!(Main.player.getYpos()-viewPort.size.height >= 0) && !((viewPort.size.height)+1+Main.player.getYpos() < map.height)){
			Console.log("view port too large");
			viewPort.size.height = map.height;
		}else if (Main.player.getYpos()-viewPort.size.height < 0){
			viewPort.position.y = 0;
		}else if((viewPort.size.height)+1+Main.player.getYpos() >= map.height){
			viewPort.position.y = map.height-((viewPort.size.height*2)+1);
		}
		
		mainBoard.repaint();
	}

	private static void horizontalLine(){
		for(int i = 1; i <= 30; i++){
			System.out.print("-");
		}
		System.out.println();
			
	}

	public static void handleKeyInput(int c) {
		if (c==KeyEvent.VK_UP) {
     		move(Main.player, 3);
     		gameloop();
     	} else if(c==KeyEvent.VK_DOWN) {
     		move(Main.player, 1);
     		gameloop();
   	 	} else if(c==KeyEvent.VK_LEFT) {
   	 		move(Main.player, 4);
   	 		gameloop();
   	 	} else if(c==KeyEvent.VK_RIGHT) {                
   	 		move(Main.player, 2);
   	 		gameloop();
        } else if(c==KeyEvent.VK_SPACE) {
        	Main.isLoading = true;
        	Main.init();
        	Main.isLoading = false;
        } else if(c==KeyEvent.VK_S) {                
        	gameloop();
	    } else if(c==KeyEvent.VK_ENTER) {                
	    	Main.window.dispatchEvent(new WindowEvent(Main.window, WindowEvent.WINDOW_CLOSING));
        } else if(c==KeyEvent.VK_1){
        	noClip  = noClip ? false : true;
        }
		
	}
}

class tilePanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private static BufferedImage[] images = new BufferedImage[4];
	private static BufferedImage[] images2 = new BufferedImage[1];
	private static BufferedImage fog;
	
	@Override
	protected void paintComponent(Graphics g) {
		((Graphics2D) g).scale(Main.scale,Main.scale);
        super.paintComponent(g);
        if(!(Main.player != null && Roguelike.viewPort != null)){
        	return;
        }
        for(int y = 0; y < (Roguelike.viewPort.size.height*2)+1; y++){
        	for(int x =  0; x < (Roguelike.viewPort.size.width*2)+1; x++){
        		boolean isFog = false;
				float opacity = (float) Math.sqrt(Math.pow(
						Main.player.getXpos()
						-(x+Roguelike.viewPort.position.x),2)
						+Math.pow(Main.player.getYpos()
								-(y+Roguelike.viewPort.position.y),2))
								/4;
        		if(opacity > 1)
					opacity = 1;
				opacity = opacity*-1+1;
				if(opacity > 0){
					Roguelike.visibleArea[y+Roguelike.viewPort.position.y][x+Roguelike.viewPort.position.x] = true;
				}
				
				if(Roguelike.visibleArea[y+Roguelike.viewPort.position.y][x+Roguelike.viewPort.position.x] && opacity == 0){
					isFog = true;
				}
				
				opacity = (float) Math.max(opacity, ((Roguelike.visibleArea[y+Roguelike.viewPort.position.y][x+Roguelike.viewPort.position.x]) ? 0.18 : 0));
				
				((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));	
				
				g.drawImage(images[Roguelike.walls[y+Roguelike.viewPort.position.y][x+Roguelike.viewPort.position.x]],
						x*Roguelike.tileSize.width, 
						y*Roguelike.tileSize.height,
						Roguelike.tileSize.width,
						Roguelike.tileSize.height,
						this);

				if(isFog){
					((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
					g.drawImage(fog,
							x*Roguelike.tileSize.width, 
							y*Roguelike.tileSize.height,
							Roguelike.tileSize.width,
							Roguelike.tileSize.height,
							this);
				}else{
					if(Roguelike.dynamicElements[y+Roguelike.viewPort.position.y][x+Roguelike.viewPort.position.x] != 0){
						g.drawImage(images2[Roguelike.dynamicElements[y+Roguelike.viewPort.position.y][x+Roguelike.viewPort.position.x]-1],
								x*Roguelike.tileSize.width, 
								y*Roguelike.tileSize.height,
								Roguelike.tileSize.width,
								Roguelike.tileSize.height,
								this);
					}
					if(Roguelike.parties[y+Roguelike.viewPort.position.y][x+Roguelike.viewPort.position.x] != null){
						g.drawImage(Main.images.get(Roguelike.parties[y+Roguelike.viewPort.position.y][x+Roguelike.viewPort.position.x]),
								x*Roguelike.tileSize.width, 
								y*Roguelike.tileSize.height,
								Roguelike.tileSize.width,
								Roguelike.tileSize.height,
								this);
					}
				}
				/*
				((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));	
				
				g.setColor(Color.WHITE);
				g.drawString(Utility.crop(opacity,3),
						x*Roguelike.tileSize.width, 
						(y+1)*Roguelike.tileSize.height);
				g.drawString(Boolean.toString(Roguelike.visibleArea[y+Roguelike.viewPort.position.y][x+Roguelike.viewPort.position.x]),
						x*Roguelike.tileSize.width, 
						y*Roguelike.tileSize.height+10);*/
				/*
				g.setColor(Color.WHITE);
				g.drawString(""+Roguelike.walls[y+Roguelike.viewPort.position.y][x+Roguelike.viewPort.position.x],
						x*Roguelike.tileSize.width, 
						(y+1)*Roguelike.tileSize.height);
				*/
			}
			
		}
    }
	
	tilePanel(){
		this.setBackground(Color.BLACK);
		for(int i = 0; i < images.length; i++){
			if(images[i] == null){
				try {
					Console.log("Loading /floorTiles/"+Integer.toString(i)+".png");
					images[i] = ImageIO.read(getClass().getResource("/floorTiles/"+Integer.toString(i)+".png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try{
			fog = ImageIO.read(getClass().getResource("/floorTiles/fog.png"));
		}catch (IOException e){
			e.printStackTrace();
		}
		/*for(int i = 0; i < images2.length; i++){
			if(images2[i] == null){
				try {
					Console.log("Loading /dynamicTiles/"+Integer.toString(i+1)+".png");
					images2[i] = ImageIO.read(getClass().getResource("/dynamicTiles/"+Integer.toString(i+1)+".png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}*/
		
	}
	
	public Dimension getPreferredSize() {
        return new Dimension(
        		(int) (((Roguelike.viewPort.size.width*2)+1)*Roguelike.tileSize.width*Main.scale),
        		(int) (((Roguelike.viewPort.size.height*2)+1)*Roguelike.tileSize.width*Main.scale)
        	);
    }
}