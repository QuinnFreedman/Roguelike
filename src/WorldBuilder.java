import java.util.ArrayList;

public abstract class WorldBuilder {
	
	private static final int WORLD_WIDTH = 1000;
	private static final int WORLD_HEIGHT = 1000;
	
	public static int[][] world = new int[WORLD_WIDTH][WORLD_HEIGHT];
	public static int[][] dynamicTiles = new int[WORLD_WIDTH][WORLD_HEIGHT];
	
	private static DebugWorld debug = new DebugWorld();
	
	public static void buildWorld(){

		int samples = 30;
		final int xStep = (int) WORLD_WIDTH/samples;
		final int yStep = (int) WORLD_HEIGHT/samples;
		
		ArrayList<java.awt.Point> points = new ArrayList<java.awt.Point>();
		
		int[] top = stackedSignWave(samples-1, 30);
		
		for(int i = 1; i < samples; i++){
			int xpos = i * xStep;
			//int ypos = (int) (Math.random()*yStep);
			int ypos = top[i-1];
			
			points.add(new java.awt.Point(xpos, ypos));
		}
		
		for(int i = 1; i < samples; i++){
			int ypos = i * xStep;
			int xpos = WORLD_WIDTH - (int) (Math.random()*yStep);
			
			points.add(new java.awt.Point(xpos, ypos));
		}
		
		for(int i = samples; i > 1; i--){
			int xpos = i * xStep;
			int ypos = WORLD_HEIGHT - (int) (Math.random()*yStep);
			
			points.add(new java.awt.Point(xpos, ypos));
		}
		
		for(int i = samples; i > 1; i--){
			int ypos = i * xStep;
			int xpos = (int) (Math.random()*yStep);
			
			points.add(new java.awt.Point(xpos, ypos));
		}
		
		debug.setOutline(points);
		
	}
	
	private static int[] stackedSignWave(int samples, float depth){
		int[] output = new int[samples];
		double step = 2*Math.PI/samples;
		for(int i = 0; i < samples; i++){
			double x = i * step;
			double offset1 = Math.random();
			double offset2 = Math.random();
			double offset3 = Math.random();
			output[i] = (int) (
					 (Math.sin(x+offset1) * 2*depth)
					+(Math.sin(2*x+offset2) * depth)
					//+(Math.sin(3*x+offset3) * depth)
					+ 2*depth);
		}
		return output;
	}
}