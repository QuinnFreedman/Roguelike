import java.awt.AlphaComposite;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Roguelike{
	public static Dimension tileSize = new Dimension(32,32);
	
	public static viewPort viewPort = new viewPort(new Dimension(6,4),new Point());

	protected static Level level;
	
	private static char[] textures = new char[]{'.','%','-','+'};
	
	private static List<Party> monsters = new ArrayList<Party>();
	
	public static ArrayList<Room> rooms = new ArrayList<Room>();
	
	private static int numberOfPartys = 0;
	
	private static tilePanel mainBoard;
	
	//private static JLabel sidebar;
	
	public static boolean toggleFog = true;
	public static boolean toggleShadow = true;
	
	public static boolean noClip = false;
	
	static boolean paused = false;
	
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
		
		Menu.setupMenu();
		Main.display(Main.MENU);
		
		Console.log("***********init()***********");
		
		Main.seed = s;
		Main.generator = new Random(s);
		Console.log("seed = "+s);
		
		WorldBuilder.buildWorld();
		
		level = new Level(30,20);
		Main.debug = new Debug(level);
		
		Main.debug.clear();
				
		clearWalls();
		Console.benchmark();
		setWalls();
		Console.benchmark();
		setPaths();
		Console.benchmark();
		clear();
		
		int random = (int) Math.floor(Math.random()*rooms.size());
		Room room = rooms.get(random);
		Main.player = new Player(Utility.randomBetween(room.xpos,room.xpos+room.w-1),Utility.randomBetween(room.ypos,room.ypos+room.h-1));
		
		monsters.clear();
		rooms.clear();
		
		level.board[Main.player.getYpos()][Main.player.getXpos()] = Main.player.getId();
		level.parties[Main.player.getYpos()][Main.player.getXpos()] = Main.player.getSrcIndex();
		
		for(int i = 0; i < numberOfPartys; i++){
			
			int localxpos;
			int localypos;			
			
			do{
				localypos = (int) Math.floor(Main.generator.nextDouble()*level.board.length);
				localxpos = (int) Math.floor(Main.generator.nextDouble()*level.board[0].length);
				
			}while(isClear(localypos,localxpos) == false);
			
			monsters.add(new Party('X',localxpos,localypos));
			level.board[monsters.get(i).getYpos()][monsters.get(i).getXpos()] = monsters.get(i).getId();
		}
	
		
		
		//*********GAMELOOP 1**********
		
		
		
		for(int i = 0; i < monsters.size(); i++){
	
			level.board[monsters.get(i).getYpos()][monsters.get(i).getXpos()] = monsters.get(i).getId();

		}
		
		//**Debug**
		horizontalLine();
		System.out.print("   x = ");
		for(int x = 0; x < level.board[0].length; x++){
			System.out.print(""+Utility.expand(x,2)+"|");
		}
		Console.log();
		for(int y = 0; y < level.board.length; y++)
		{
			System.out.print("y = "+Utility.expand(y,2)+")");
			for(int x = 0; x < level.board[0].length; x++){
				if(level.debugBoard[y][x] != 'k'){
					System.out.print("["+level.debugBoard[y][x]+"]");
				}else{
					System.out.print("["+level.board[y][x]+"]");
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
		
		level.board[Main.player.getYpos()][Main.player.getXpos()] = Main.player.getId();
		level.parties[Main.player.getYpos()][Main.player.getXpos()] = Main.player.getSrcIndex(); 
		
		for(int i = 0; i < monsters.size(); i++){
			level.board[monsters.get(i).getYpos()][monsters.get(i).getXpos()] = monsters.get(i).getId();			
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
		if(x < 0 || y < 0 || x >= level.board[0].length || y >= level.board.length){
			return false;
		}else{
			if(noClip){
				return true;
			}
			if(Main.player.getXpos() == x && Main.player.getYpos() == y){
				return false;
			}
			if(level.walls[y][x] == 1){
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
		
		for(int y = 0; y < level.board.length; y++)
		{
			for(int x = 0; x < level.board[0].length; x++){
				level.dynamicElements[y][x] = 0;
				level.parties[y][x] = null;
				if(level.walls[y][x] < textures.length){
					level.board[y][x] = textures[level.walls[y][x]];
				}else{
					level.board[y][x] = Integer.toString(level.walls[y][x]).charAt(0);
				}
			}
		}		
	}
	private static void clearWalls(){
		
		for(int y = 0; y < level.walls.length; y++){
			for(int x = 0; x < level.walls[0].length; x++){
				level.walls[y][x] = 0;
				level.debugBoard[y][x] = 'k';
				
			}
		}
		
	}
	
	private static void setWalls() {
		Console.log(1, "setWalls()");
		rooms.clear();
		Main.debug.DrawRooms(new ArrayList<Integer>());
		for(int r = 0; r < 9; r++){ //r = room number
			Console.log(1,"room "+r);
			rooms.add(new Room(level));
			
			Main.debug.setWeights(null);
			Main.debug.DrawRooms(new ArrayList<Integer>());
			Console.log(-1,"");
		}
		
		
		DungeonBuilder.collideRooms(rooms, level.size.width, level.size.height);
		
		for(int r = 0; r < rooms.size(); r++){
			for(int i = 0; i < rooms.get(r).roomWalls.size(); i++){
				if(rooms.get(r).roomWalls.get(i).x >= 0){
					level.walls[rooms.get(r).roomWalls.get(i).y][rooms.get(r).roomWalls.get(i).x] = 1;
				}
			}
			for(int y1 = rooms.get(r).ypos; y1 < rooms.get(r).ypos + rooms.get(r).h; y1++){	
				for(int x1 = rooms.get(r).xpos; x1 < rooms.get(r).xpos + rooms.get(r).w; x1++){
					if(level.walls[y1][x1] == 0){
						level.walls[y1][x1] = 2;
					}
				}
				
			}
		}
		Console.log(-1, "");
	}
	
	
	private static void setPaths(){
		List<Point> doors = new ArrayList<Point>();
		for(int i = 0; i < rooms.size(); i++){
			for(int e = 0; e < rooms.get(i).roomDoors.size(); e++){
				doors.add(rooms.get(i).roomDoors.get(e));
			}
		}
		Pathing.setPaths(level.walls, doors);
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
		
		if(Main.player.getXpos()-viewPort.size.width >= 0 && (viewPort.size.width)+1+Main.player.getXpos() < level.size.width){
			viewPort.position.x = Main.player.getXpos()-viewPort.size.width;
		}else if(!(Main.player.getXpos()-viewPort.size.width >= 0) && !((viewPort.size.width)+1+Main.player.getXpos() < level.size.width)){
			Console.log("view port too large");
			viewPort.size.width = level.size.width;
		}else if (Main.player.getXpos()-viewPort.size.width < 0){
			viewPort.position.x = 0;
		}else if((viewPort.size.width)+1+Main.player.getXpos() >= level.size.width){
			viewPort.position.x = level.size.width-((viewPort.size.width*2)+1);
		}
		
		if(Main.player.getYpos()-viewPort.size.height >= 0 && (viewPort.size.height)+1+Main.player.getYpos() < level.size.height){
			viewPort.position.y = Main.player.getYpos()-viewPort.size.height;
		}else if(!(Main.player.getYpos()-viewPort.size.height >= 0) && !((viewPort.size.height)+1+Main.player.getYpos() < level.size.height)){
			Console.log("view port too large");
			viewPort.size.height = level.size.height;
		}else if (Main.player.getYpos()-viewPort.size.height < 0){
			viewPort.position.y = 0;
		}else if((viewPort.size.height)+1+Main.player.getYpos() >= level.size.height){
			viewPort.position.y = level.size.height-((viewPort.size.height*2)+1);
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
		if(paused)
			return;
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
        } else if(c==KeyEvent.VK_2){
        	CutScene.showCutScene(new CutScene.DRAGON_CUTSCENE_1());
        }
		
    	//*************DEBUG ONLY***************************
		else if(c==KeyEvent.VK_C){
        	JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File(System.getProperty("user.home")+"/workspace/Roguelike/assets"));
        	fc.setFileFilter(new FileNameExtensionFilter("gif","gif"));
        	fc.setFileFilter(new FileNameExtensionFilter("png","png"));
        	
        	int returnVal = fc.showOpenDialog(Main.window);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                Console.log("Opening: " + file.getAbsolutePath() + "...");
                
                String fileName = file.getAbsolutePath();
                String extension = "";
                int i = fileName.lastIndexOf('.');
                if (i > 0) {
                    extension = fileName.substring(i+1);
                }
                
                if(extension.equals("gif") || extension.equals("png")){
                	try {
            	    	//final Toolkit tk = Toolkit.getDefaultToolkit();
                		//final URL path = new URL("assets\\Background.png");
            	        //final Image img = tk.createImage(fileName);
            	        //tk.prepareImage(img, -1, -1, null);
            	        Main.images.put(fileName, ImageIO.read(file));
            	        Main.player.setSrcIndex(fileName);
            	        Main.roguelike.revalidate();
            	        Main.roguelike.repaint();
            	        Roguelike.mainBoard.repaint();
            	    } catch (Exception e) {
            	        e.printStackTrace();
            	    }
                }
            }
        }else if(c == KeyEvent.VK_F){
        	Roguelike.toggleFog = !Roguelike.toggleFog;
        	Roguelike.mainBoard.repaint();
        }
        else if(c == KeyEvent.VK_L){
        	System.out.println(toggleShadow);
        	Roguelike.toggleShadow = !Roguelike.toggleShadow;
        	Roguelike.mainBoard.repaint();
        }
		

        //***************DEBUG END*******************
		
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
					Roguelike.level.visibleArea[y+Roguelike.viewPort.position.y][x+Roguelike.viewPort.position.x] = true;
				}
				
				if(Roguelike.level.visibleArea[y+Roguelike.viewPort.position.y][x+Roguelike.viewPort.position.x] && opacity == 0){
					if(Roguelike.toggleFog)
						isFog = true;
				}
				
				opacity = (float) Math.max(opacity, ((Roguelike.level.visibleArea[y+Roguelike.viewPort.position.y][x+Roguelike.viewPort.position.x]) ? 0.18 : 0));
				
				if(Roguelike.toggleShadow)
					((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
				else
					((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
				
				g.drawImage(images[Roguelike.level.walls[y+Roguelike.viewPort.position.y][x+Roguelike.viewPort.position.x]],
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
					if(Roguelike.level.dynamicElements[y+Roguelike.viewPort.position.y][x+Roguelike.viewPort.position.x] != 0){
						g.drawImage(images2[Roguelike.level.dynamicElements[y+Roguelike.viewPort.position.y][x+Roguelike.viewPort.position.x]-1],
								x*Roguelike.tileSize.width, 
								y*Roguelike.tileSize.height,
								Roguelike.tileSize.width,
								Roguelike.tileSize.height,
								this);
					}
					if(Roguelike.level.parties[y+Roguelike.viewPort.position.y][x+Roguelike.viewPort.position.x] != null){
						g.drawImage(Main.images.get(Roguelike.level.parties[y+Roguelike.viewPort.position.y][x+Roguelike.viewPort.position.x]),
								x*Roguelike.tileSize.width, 
								y*Roguelike.tileSize.height,
								Roguelike.tileSize.width,
								Roguelike.tileSize.height,
								this);
					}
				}
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