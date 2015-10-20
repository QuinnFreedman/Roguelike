import java.awt.Polygon;
import java.util.ArrayList;

public abstract class WorldBuilder {
	
	private static final int WORLD_WIDTH = 1000;
	private static final int WORLD_HEIGHT = 1000;
	
	public static int[][] world = new int[WORLD_HEIGHT][WORLD_WIDTH];
	public static int[][] dynamicTiles = new int[WORLD_HEIGHT][WORLD_WIDTH];
	
	private static DebugWorld debug = new DebugWorld();
	
	public static void buildWorld(){
		//DRAW OUTLINE
		int samples = 100;
		
		ArrayList<java.awt.Point> points = new ArrayList<java.awt.Point>();
		
		int[] top = stackedSinWave((samples-1) * 4, 30);
		
		float r = Math.min(WORLD_HEIGHT, WORLD_WIDTH)/2;
		
		float step = (float) (2*Math.PI / samples);
		
		for(int i = 1; i < samples; i++){
			int xpos = (int) (Math.cos(step * i)*(r + 20*Math.sin(4 * step * i - 1.5) + top[i]) + WORLD_WIDTH/2);
			int ypos = (int) (Math.sin(step * i)*(r+ 20*Math.sin(4 * step * i - 1.5) + top[i]) + WORLD_HEIGHT/2);
			points.add(new java.awt.Point(xpos, ypos));
		}
		
		debug.setOutline(makePolygon(points));
		
		//SET TILES
		
		for(int y = 0; y < WORLD_HEIGHT; y++){
			for(int x = 0; x < WORLD_WIDTH; x++){
				//TODO
			}
		}
	}
	static Polygon makePolygon(ArrayList<java.awt.Point> points){
		int len = points.size();
		int[] outlineX = new int[len];
		int[] outlineY = new int[len];
		for(int i = 0; i < len; i++){
			outlineX[i] = points.get(i).x;
			outlineY[i] = points.get(i).y;
		}
		
		Polygon outline = new Polygon(outlineX,outlineY,len);
		
		return outline;
	}
	private static int[] stackedSinWave(int samples, float depth){
		int[] output = new int[samples];
		double step = 2*Math.PI/samples;
		double offset1 = Math.random()*2*Math.PI;
		double offset2 = Math.random()*2*Math.PI;
		double offset3 = Math.random()*2*Math.PI;
		for(int i = 0; i < samples; i++){
			double x = i * step;
			output[i] = (int) (
					 (Math.sin(4*x+offset1) * 2*depth)
					+(Math.sin(4*2*x+offset2) * depth)
					+(Math.sin(4*5*x+offset3) * depth)
					+(Math.sin(4*8*x+offset3) * depth)
					- 3*depth);
		}
		return output;
	}
}