import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class City{
	static final Dimension citySize = new Dimension(40,40);

	int majorRoads = 1;
	ArrayList<ArrayList<Integer>> roadsNorthSouth = new ArrayList<ArrayList<Integer>>(
			Arrays.asList(
					new ArrayList<Integer>(),
					new ArrayList<Integer>()));
	ArrayList<ArrayList<ArrayList<Integer>>> crossroads = new ArrayList<ArrayList<ArrayList<Integer>>>(
			Arrays.asList(
					new ArrayList<ArrayList<Integer>>(),
					new ArrayList<ArrayList<Integer>>()));
	int width;
	int height;
	
	public void buildCity(){
		for(int r = 0; r < 2; r++){
			ArrayList<Integer> road = roadsNorthSouth.get(r);
			//make north-south roads
			road.add((int) (Math.random()*5));
			while(road.get(road.size()-1) < width){
				road.add((int) (Math.random()*5 + 10));
				crossroads.get(r).add(new ArrayList<Integer>());
			}
			
			//make horizontal roads
			for(int i = 0; i < road.size(); i++){
				ArrayList<Integer> localCrossroads = new ArrayList<Integer>();
				while(localCrossroads.isEmpty() || 
						localCrossroads.get(localCrossroads.size()-1) < height/2){
					localCrossroads.add((int) (Math.random()*10 + 10));
				}
				crossroads.get(r).set(i, localCrossroads);
			}
		}
		
	}
}