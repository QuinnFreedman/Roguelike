import java.util.ArrayList;

public abstract class DungeonBuilder{
	
	public static void collideRooms(ArrayList<Room> rooms, int width, int height){
		int[][] overlapWeights = new int[height][width];
		ArrayList<int[]> vectors = new ArrayList<int[]>();
		
		Console.log(1,"Move Rooms");
		int itt = 0;
		do{
			setWeights(overlapWeights, rooms);
			Main.debug.setWeights(overlapWeights);
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
							overlap[((y < halfHeight) ? 0 : 1)][((x < halfWidth) ? 0 : 1)] += overlapWeights[y+rooms.get(r).ypos][x+rooms.get(r).xpos] - 1;
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
				else if(rooms.get(r).xpos + rooms.get(r).w > Roguelike.level.size.width)
					rooms.get(r).xpos = Roguelike.level.size.width - rooms.get(r).w;
				
				rooms.get(r).ypos += vectors.get(r)[1];
				if(rooms.get(r).ypos < 0)
					rooms.get(r).ypos = 0;
				else if(rooms.get(r).ypos + rooms.get(r).h > Roguelike.level.size.height)
					rooms.get(r).ypos = Roguelike.level.size.height - rooms.get(r).h;
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
					if(overlapWeights[y+rooms.get(r).ypos][x+rooms.get(r).xpos] > 1){
						Console.log(-1,"removing room "+r);
						rooms.remove(r);
						r--;
						setWeights(overlapWeights, rooms);
						Main.debug.DrawRooms(new ArrayList<Integer>());
						break rooms;
					}
				}
			}
			rooms.get(r).construct();
			Console.log(-1,"");
			}
		}
	}
	
	private static void setWeights(int[][] overlapWeight, ArrayList<Room> rooms){
		for(int y = 0; y < overlapWeight.length; y++){
			for(int x = 0; x < overlapWeight[0].length; x++){
				overlapWeight[y][x] = 0;				
			}
		}
		for(int r = 0; r < rooms.size(); r++){
			for(int x = -1; x <= rooms.get(r).w; x++){
				if(x+rooms.get(r).xpos >= 0 && x+rooms.get(r).xpos < overlapWeight[0].length){
					for(int y = -1; y <= rooms.get(r).h; y++){
						if(y+rooms.get(r).ypos >= 0 && y+rooms.get(r).ypos < overlapWeight.length){
							overlapWeight[y+rooms.get(r).ypos][x+rooms.get(r).xpos] += 1;
						}
					}
				}
			}
		}
	}
}