import java.awt.Rectangle;
import java.util.ArrayList;

public abstract class WorldBuilder {
	
	private static final int WORLD_WIDTH = 1000;
	private static final int WORLD_HEIGHT = 1000;
	
	public static int[][] world = new int[WORLD_HEIGHT][WORLD_WIDTH];
	public static int[][] dynamicTiles = new int[WORLD_HEIGHT][WORLD_WIDTH];
	
	private static DebugWorld debug = new DebugWorld();
	
	static float shape = 1.2f;
	
	static float[] levels = {.22f, .32f, .5f, .8f};
	
	private static double limit(double x){
		return -1/(x + 1) + 1;
	}
	private static void makeRiver(Point origin, double[][] elevation){
		Point active = origin;
		
		int itt = 0;
		while(itt < 500){
			itt++;
			int x = active.x;
			int y = active.y;
			Point[] neighbors = {new Point(y-1, x),
								 new Point(y, x+1),
								 new Point(y+1, x),
								 new Point(y, x-1),
								 
								 //new Point(y-2, x),
								 //new Point(y, x+2),
								 //new Point(y+2, x),
								 //new Point(y, x-2),
								 
								 //new Point(y-3, x),
								 //new Point(y, x+3),
								 //new Point(y+3, x),
								 //new Point(y, x-3)
								 };
			
			double min = 2;
			active = null;
			for(Point p : neighbors){
				double pVal = elevation[p.y][p.x];
				if(pVal == -1)
					continue;
				if(pVal < levels[0])
					return;
				else if(pVal < min){
					min = pVal;
					active = p;
				}
			}
			
			world[y][x] = -1;
			
			if(active == null)
				return;
		}
		
	}
	
	private static void fillRivers(double[][] elevation){
		for(int y = 1; y < WORLD_HEIGHT - 1; y++){
			for(int x = 1; x < WORLD_WIDTH - 1; x++){
				if(world[y-1][x] == -1 ||
						world[y][x+1] == -1 || 
						world[y+1][x] == -1 ||
						world[y][x-1] == -1
					){
					world[y][x] = 0;
				}
			}
		}
	}
	
	public static void buildWorld(){
		debug.clear();
		debug.message("Building World");
		SimplexNoise simplexNoise=new SimplexNoise(200,0.3,(int) (Math.random()*5000));

	    double[][] normalizer = new double[WORLD_HEIGHT][WORLD_WIDTH];
	    for(int y = 0; y < WORLD_HEIGHT; y++){
			for(int x = 0; x < WORLD_WIDTH; x++){
				normalizer[y][x] = limit(shape*Math.min(
						(1 - Math.abs(WORLD_WIDTH/2.0 - x)/(WORLD_WIDTH/2.0)),
		 				(1 - Math.abs(WORLD_HEIGHT/2.0 - y)/(WORLD_HEIGHT/2.0))
	 				));
				
				//assert(normalizer[y][x] > 0 && normalizer[y][x] < 1);
			}
		}
	    
	    //CALCULATE ELEVATION
	    double[][] elevation = new double[WORLD_HEIGHT][WORLD_WIDTH];
	    double[][] percipitation = new double[WORLD_HEIGHT][WORLD_WIDTH];
	    
	    for(int y = 0; y < WORLD_HEIGHT; y++){
			for(int x = 0; x < WORLD_WIDTH; x++){
				elevation[y][x] = 0.5*(1+simplexNoise.getNoise(x,y)) * normalizer[y][x];
				percipitation[y][x] = 0.5*(1+simplexNoise.getNoise(x,y));
	        }
	    }
	    
	    //SET BOUNDS
	    
	    boolean moveNorth = true;
	    boolean moveSouth = true;
	    boolean moveEast = true;
	    boolean moveWest = true;
	    Rectangle bounds = new Rectangle((int) (WORLD_WIDTH/2f) - 1, 
	    		(int) (WORLD_HEIGHT/2f) - 1, 2, 2);
	    while(moveNorth || moveSouth || moveEast || moveWest){

	    	boolean water;
	    	//NORTH
	    	if(moveNorth){
	    		water = false;
		    	for(int i = bounds.x; i < bounds.x + bounds.width; i++){
		    		if(i < 0 || i == WORLD_WIDTH || elevation[bounds.y][i] < getSeaLevel()){
		    			water = true;
		    			bounds.y += 10;
		    			bounds.height -= 10;
		    			break;
		    		}
		    	}
		    	if(water){
		    		moveNorth = false;
		    	}else{
	    			bounds.y -= 10;
	    			bounds.height += 10;
	    		}
	    	}
	    	
	    	//WEST
	    	if(moveWest){
		    	water = false;
		    	for(int i = bounds.y; i < bounds.y + bounds.height; i++){
		    		if(i < 0 || i == WORLD_HEIGHT || elevation[i][bounds.x] < getSeaLevel()){
		    			water = true;
		    			bounds.x += 10;
		    			bounds.width -= 10;
		    			break;
		    		}
		    	}
		    	if(water){
		    		moveWest = false;
		    	}else{
	    			bounds.x -= 10;
	    			bounds.width += 10;
	    		}
	    	}

	    	//SOUTH
	    	if(moveSouth){
	    		water = false;
		    	for(int i = bounds.x; i < bounds.x + bounds.width; i++){
		    		if(i < 0 || i == WORLD_WIDTH || 
		    				elevation[bounds.y+bounds.height][i] < getSeaLevel()){
		    			water = true;
		    			bounds.height -= 10;
		    			break;
		    		}
		    	}
		    	if(water){
		    		moveSouth = false;
		    	}else{
	    			bounds.height += 10;
	    		}
	    	}
	    	
	    	//EAST
	    	if(moveEast){
		    	water = false;
		    	for(int i = bounds.y; i < bounds.y + bounds.height; i++){
		    		if(i < 0 || i == WORLD_HEIGHT || 
		    				elevation[i][bounds.x+bounds.width] < getSeaLevel()){
		    			water = true;
		    			bounds.width -= 10;
		    			break;
		    		}
		    	}
		    	if(water){
		    		moveEast = false;
		    	}else{
	    			bounds.width += 10;
	    		}
	    	}

	    }
	    
	    //CITIES
	    
	    ArrayList<Room> cities = new ArrayList<Room>(3);
		while(cities.size() < 3){
			cities = new ArrayList<Room>(3);
			
			while(cities.size() < 3){
				int x = (int) (Math.random()*(bounds.width - City.citySize.width));
				int y = (int) (Math.random()*(bounds.height - City.citySize.height));
				cities.add(new Room(City.citySize.width, City.citySize.height, x, y));
			}
			
			DungeonBuilder.collideRooms(cities, bounds.width, bounds.height, 300);
			
			for(Room city : cities){
				city.xpos += bounds.x;
				city.ypos += bounds.y;
			}
			debug.setCities(cities);
		}
	    
	    Console.log("bounds = "+bounds);
	    
	    debug.setWorldBounds(bounds);
	    
	    
	    
	    //MAKE RIVERS
	    
	    int rivers = 0;
	    while(rivers < 4){
	    	rivers++;
	    	ArrayList<Node> river1 = null;
	    	do{
		    	Point startpoint = new Point((int) (WORLD_WIDTH/2f + (Math.random() - 0.5)*50), 
		    			(int) (WORLD_HEIGHT/2f + (Math.random() - 0.5)*50));
		    	
		    	Point endpoint = new Point(0,0);
		    	int i = (int) Math.floor(Math.random()*4);
				Console.log(""+i);
		    	if(i == 0){
		    		endpoint.y = 0;
		    		endpoint.x = (int) (Math.random()*WORLD_WIDTH);
		    	}else if(i == 1){
		    		endpoint.y = WORLD_HEIGHT - 1;
		    		endpoint.x = (int) (Math.random()*WORLD_WIDTH);
		    	}else if(i == 2){
		    		endpoint.y = (int) (Math.random()*WORLD_HEIGHT);
		    		endpoint.x = 0;
		    	}else if(i == 3){
		    		endpoint.y = (int) (Math.random()*WORLD_HEIGHT);
		    		endpoint.x = WORLD_WIDTH-1;
		    	}
	    	
			    river1 = RiverBuilder.getPath(elevation, 
			    		startpoint, endpoint, true);
	    	}while(river1 == null);
	    	
		    for(Point p : river1){
		    	elevation[p.y][p.x] = 0;
		    }
	    }
	    
	    
	    //FILL MAP
	    for(int y = 0; y < WORLD_HEIGHT; y++){
			for(int x = 0; x < WORLD_WIDTH; x++){
	            double result = elevation[y][x];
	            double ratio = limit(shape*1d);
	            if(result > levels[3]*ratio)
	            	world[y][x] = 4;
	            else if(result > levels[2]*ratio)
	            	world[y][x] = 3;
	            else if(result > levels[1]*ratio)
	            	world[y][x] = 2;
	            else if(result > levels[0]*ratio)
					world[y][x] = 1;
				else
					world[y][x] = 0;
	        }
	    }

	    //makeRiver(new Point((int) (Math.random()*WORLD_WIDTH), (int) (Math.random()*WORLD_HEIGHT)), elevation);
	    
//	    fillRivers(elevation);
//	    fillRivers(elevation);
//	    fillRivers(elevation);
	    
	    debug.message("Rendering World");
		debug.setWorld(world);
		
		/*
		//DRAW OUTLINE
		int samples = 500;
		
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
		
		ArrayList<Room> cities = new ArrayList<Room>(3);
		while(cities.size() < 3){
			cities = new ArrayList<Room>(3);
			
			while(cities.size() < 3){
				int x = (int) (Math.random()*(bounds.width - City.citySize.width));
				int y = (int) (Math.random()*(bounds.height - City.citySize.height));
				cities.add(new Room(City.citySize.width, City.citySize.height, x, y));
			}
			
			DungeonBuilder.collideRooms(cities, bounds.width, bounds.height, 300);
			
			for(Room city : cities){
				city.xpos += bounds.x;
				city.ypos += bounds.y;
			}
			debug.setCities(cities);
		}
		
		//Towns
		
		
	}
	private static Polygon makePolygon(ArrayList<java.awt.Point> points){
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
		double offset4 = Math.random()*2*Math.PI;
		double offset5 = Math.random()*2*Math.PI;
		for(int i = 0; i < samples; i++){
			double x = i * step;
			output[i] = (int) (
					 (Math.sin(4*x+offset1) * 2*depth)
					+(Math.sin(4*2*x+offset2) * depth)
					+(Math.sin(4*5*x+offset3) * depth)
					+(Math.sin(4*8*x+offset4) * depth)
					+(Math.sin(4*20*x+offset5) * depth/3)
					- 3*depth);
		}
		return output;
	*/
	}
	public static float getSeaLevel() {
		return (float) (levels[0]*limit(shape*1d));
	}
}