import java.util.ArrayList;
import java.util.List;

public abstract class RiverBuilder{
	
	public static ArrayList<Node> getPath(double[][] elevation, Point startPoint, Point endPoint){
		List<Node> openList = new ArrayList<Node>();
		List<Node> closedList = new ArrayList<Node>();
		ArrayList<Node> path = new ArrayList<Node>();
		
		path.clear();
		
		Node[][] nodes = new Node[elevation.length][elevation[0].length];
		for(int y = 0; y < elevation.length; y++){
			for(int x = 0; x < elevation[0].length; x++){
				if(elevation[y][x] == 0){
					nodes[y][x] = new Node(x,y,true);
				}else{
					nodes[y][x] = new Node(x,y,false);
				}
			}
		}
		
		// A* 
		
		Console.log(1,"START A-STAR");
		
		openList.clear();
		closedList.clear();
		nodes[startPoint.y][startPoint.x].h = distanceBetween(nodes[startPoint.y][startPoint.x],nodes[endPoint.y][endPoint.x]);
		closedList.add(nodes[startPoint.y][startPoint.x]);
		nodes[startPoint.y][startPoint.x].gf = 0;
	
		int g = 0;
		boolean done = false;
		int itt = 0;
		do{
			for(int h = 0; h < 4; h++){
				int localX = closedList.get(g).x;
				int localY = closedList.get(g).y;
			
				if(h == 0){
					localY++;
				}else if(h == 1){
					localX++;
				}else if(h == 2){
					localY--;
				}else if(h == 3){
					localX--;
				}
				//if h exists
				if(localX >= 0 && localY >= 0 && localY < nodes.length && localX < nodes[0].length){
					
					float cost = costAtoB(closedList.get(g), nodes[localY][localX], elevation);
					//Console.log(nodes[localY][localX]);
					//if h is not on open or closed list or is impassable
					if(!openList.contains(nodes[localY][localX]) && !closedList.contains(nodes[localY][localX])){
						
						nodes[localY][localX].parent = closedList.get(g); // parent current node to new node
						nodes[localY][localX].gf = closedList.get(g).gf + cost; // set move cost
						nodes[localY][localX].h = distanceBetween(nodes[localY][localX],nodes[endPoint.y][endPoint.x]);
						nodes[localY][localX].ff = nodes[localY][localX].gf + nodes[localY][localX].h;
						//Roguelike.level.debugBoard[localY][localX] = (nodes[localY][localX].h+"").charAt((nodes[localY][localX].h+"").length()-1;
						//(nodes[localY][localX].h+"").charAt((nodes[localY][localX].h+"").length()-1
						
						openList.add(nodes[localY][localX]); // add new node to open list
						
						
						
					}else if(closedList.contains(nodes[localY][localX])){
						if(closedList.get(g).gf + cost < nodes[localY][localX].gf){
							nodes[localY][localX].parent = closedList.get(g);
							nodes[localY][localX].gf = closedList.get(g).gf + cost;
							nodes[localY][localX].ff = nodes[localY][localX].gf + nodes[localY][localX].h;
						}
					}else if(localY == endPoint.y && localX == endPoint.x || elevation[localY][localX] < 0.2){
						nodes[endPoint.y][endPoint.x].parent = closedList.get(g);
						
						done = true;
					}else if(itt == 399){
						Console.log("ERROR: PATH NOT FOUND: itt max");
						
					}
				}
			}
			
			//loop through openList - find lowest f
			Node lowestF = null;
			
			for(Node test : openList){
				if(lowestF == null || test.ff <= lowestF.ff){
					lowestF = test;
				}
			}
			
			
			if(!closedList.contains(lowestF)){
				closedList.add(lowestF); //move next node to be checked to closed list
			}
			if(openList.contains(lowestF)){
				openList.remove(lowestF); //and remove from open list
			}

			//loop do again
			g++;
			itt++;
			
		}while(done == false);
		Console.log(-1,"While loop itteration "+itt);
		
		//Trace back
		//if(done == true){
			Node currentTile = nodes[endPoint.y][endPoint.x].parent;
			if(currentTile != null){
				while(currentTile.parent != null){
					path.add(currentTile);
					elevation[currentTile.y][currentTile.x] = -1;
					currentTile = currentTile.parent;
				}
			}else{
				Console.log("ERROR: 206");
			}
		//}
		Console.log(-1," ");
		System.err.println("path = "+path.toString());
		return path;
	}

	private static int distanceBetween(Point a, Point b){
		int d = Math.abs(a.x-b.x)+Math.abs(a.y-b.y);
		return d;
	}
	
	private static float costAtoB(Node a, Node b, double[][] elevation){
		return 6 + (float) ((elevation[b.y][b.x] - elevation[a.y][a.x])*1000);
	}
	
}