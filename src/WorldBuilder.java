import java.util.ArrayList;

public abstract class WorldBuilder {
	
	private static final int WORLD_WIDTH = 1000;
	private static final int WORLD_HEIGHT = 1000;
	
	public static int[][] world = new int[WORLD_WIDTH][WORLD_HEIGHT];
	public static int[][] dynamicTiles = new int[WORLD_WIDTH][WORLD_HEIGHT];
	
	private static DebugWorld debug = new DebugWorld();
	
	public static void buildWorld(){
		
		final int xStep = (int) WORLD_WIDTH/10;
		final int yStep = (int) WORLD_HEIGHT/10;
		
		ArrayList<java.awt.Point> points = new ArrayList<java.awt.Point>();
		
		for(int i = 1; i < 10; i++){
			int xpos = i * xStep;
			int ypos = (int) (Math.random()*yStep);
			
			points.add(new java.awt.Point(xpos, ypos));
		}
		
		for(int i = 1; i < 10; i++){
			int ypos = i * xStep;
			int xpos = WORLD_WIDTH - (int) (Math.random()*yStep);
			
			points.add(new java.awt.Point(xpos, ypos));
		}
		
		for(int i = 10; i > 1; i--){
			int xpos = i * xStep;
			int ypos = WORLD_HEIGHT - (int) (Math.random()*yStep);
			
			points.add(new java.awt.Point(xpos, ypos));
		}
		
		for(int i = 10; i > 1; i--){
			int ypos = i * xStep;
			int xpos = (int) (Math.random()*yStep);
			
			points.add(new java.awt.Point(xpos, ypos));
		}
		
		debug.setOutline(points);
		
	}
}