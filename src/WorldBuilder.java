import java.awt.Polygon;
import java.awt.Rectangle;
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
			int xpos = (int) (Math.cos(step * i)*(r + 40*Math.sin(4 * step * i - 1.5) + top[i]) + WORLD_WIDTH/2);
			int ypos = (int) (Math.sin(step * i)*(r + 40*Math.sin(4 * step * i - 1.5) + top[i]) + WORLD_HEIGHT/2);
			points.add(new java.awt.Point(xpos, ypos));
		}
		
		Polygon outline = makePolygon(points);
		debug.setOutline(outline);
		
		//SET TILES
		
		for(int y = 0; y < WORLD_HEIGHT; y++){
			for(int x = 0; x < WORLD_WIDTH; x++){
				if(outline.contains(x, y))
					world[y][x] = 1;
				else
					world[y][x] = 0;
			}
		}
		
		debug.setWorld(world);
		
		//SET BOUNDS
		
		Rectangle bounds = new Rectangle(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
		
		while(!outline.contains(bounds)){
			bounds.x += 10;
			bounds.y += 10;
			bounds.width -= 20;
			bounds.height -= 20;
		}
		
		while(outline.contains(bounds)){
			bounds.x -= 10;
			bounds.width += 10;
		}
		bounds.x += 10;
		bounds.width -= 10;
		
		while(outline.contains(bounds)){
			bounds.y -= 10;
			bounds.height += 10;
		}
		bounds.y += 10;
		bounds.height -= 10;
		
		while(outline.contains(bounds)){
			bounds.height += 10;
		}
		bounds.height -= 10;
		
		while(outline.contains(bounds)){
			bounds.width += 10;
		}
		bounds.width -= 10;
		
		debug.setWorldBounds(bounds);
		
		//Cities
		
		ArrayList<Room> cities = new ArrayList<Room>();
		while(cities.size() < 3){
			while(cities.size() < 3){
				int x = (int) (Math.random()*(bounds.width - City.cityPadding.width));
				int y = (int) (Math.random()*(bounds.height - City.cityPadding.height));
				cities.add(new Room(City.cityPadding.width, City.cityPadding.height, x, y));
			}
			
			for(Room city : cities){
				Console.log(city.toString());
			}
			
			DungeonBuilder.collideRooms(cities, bounds.width, bounds.height);
			
			for(Room city : cities){
				city.xpos += bounds.x;
				city.ypos += bounds.y;
			}
			debug.setCities(cities);
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