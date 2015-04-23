import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Room
{
	public int h;
	public int w;
	public int xpos;
	public int ypos;
	public int doors;
	List<Point> roomWalls;
	List<Point> roomDoors;
	
	private static int randomBtwnBiased(int min, int max)
	{
		return (int) (Main.generator.nextDouble() * (max - min + 1)) + min;
	}
	public static int randomBtwn(int min, int max)
	{
		//inclusive
		return (int) Math.floor((Main.generator.nextDouble() * (max - min + 1)) + min);
	}
	private void setWalls(){
		roomWalls = new ArrayList<Point>();
		roomDoors = new ArrayList<Point>();
		
		for(int i = 0; i<this.w; i++){
			roomWalls.add(new Point(this.xpos+i, this.ypos));
			roomWalls.add(new Point(this.xpos+i, this.ypos+this.h-1));
		}
		for(int i = 2; i<this.h; i++){
			roomWalls.add(new Point(this.xpos, this.ypos+i-1));
			roomWalls.add(new Point(this.xpos+this.w - 1, this.ypos+i-1));
		}
	}
	private void setDoors(){
		boolean corner;
		Console.log("this.w, h = " + this.w + ", " + this.h);
		Console.log("this.xpos, ypos = " + this.xpos + ", " + this.ypos);
		for(int e=1; e<=doors; e++){
			Console.log(1, "New Door (e = "+e+")");
			do{	
				corner = false;			
				int k = randomBtwn(0, roomWalls.size()-1);
				Console.log("k = "+k);
				Console.log("roomWalls.get(k) = {"+roomWalls.get(k).x+","+roomWalls.get(k).y+"}");
				
				//check for corners
				if((roomWalls.get(k).y == this.h + this.ypos - 1 && roomWalls.get(k).x == this.w + this.xpos - 1) || 
					(roomWalls.get(k).y == this.ypos && roomWalls.get(k).x == this.w + this.xpos - 1) ||
					(roomWalls.get(k).y == this.ypos && roomWalls.get(k).x == this.xpos) ||
					(roomWalls.get(k).y == this.h + this.ypos - 1 && roomWalls.get(k).x == this.xpos)
				){
					corner = true;
					Console.log("!!corner = true!!");
				}else if(roomWalls.get(k).y == Roguelike.board.length - 1 ||
						roomWalls.get(k).x == Roguelike.board[0].length - 1 ||
						roomWalls.get(k).y == 0 ||
						roomWalls.get(k).x == 0
				){
					corner = true;
					Console.log();
					Console.log("!!wall = true!!");
				}else{
					//roomWalls.get(k).x = -1;
					roomDoors.add(roomWalls.get(k));
					roomWalls.remove(k);
				}
				
			}while(corner == true);
			
			Console.log(-1, "");
		}
		
	}
	Room(){
		Console.log("New Room");
		h = randomBtwn(4, 7);
		w = randomBtwn(4, 7);
		xpos = randomBtwn(0, Roguelike.map.width-this.w);
		ypos = randomBtwn(0, Roguelike.map.height-this.h);
		//construct();
	}
	Room(int h, int w, int x, int y){
		Console.log("New Room");
		this.h = h;
		this.w = w;
		this.xpos = x;
		this.ypos = y;
		construct();
	}
	public void construct(){
		Console.log(1,"Construct Room");
		doors = randomBtwnBiased(1,3);
		Console.log("Number of doors = "+doors);
		setWalls();
		
		String logOutput = "";
		for(int i = 0; i < roomWalls.size(); i++){
			logOutput += "{"+roomWalls.get(i).x+","+roomWalls.get(i).y+"} ";
		}
		Console.log(logOutput);
			
		setDoors();
		Console.log(-1, "Room Done");
	}
}
//TODO for each room, on creation, check if overlaps any others.
//for each side w/ overlap, if no overlap on opposite side - move 1 away from that side
//if overlap on all sides, delete room.