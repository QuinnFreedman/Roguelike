import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Player extends Party
{
	public Player(int x, int y) {
		super(x, y, 0);
		this.lvl = 1;
		
	}
	public void addCharecter(Charecter charecter){
		if(this.allies.size() == 0){
			charecter.position.x = (int) Math.round(Math.random());
			charecter.position.y = 1;
		}else if(this.allies.size() < 4){
			boolean retry = false;
			int r = (int) Math.round(Math.random());
			charecter.position.y = Utility.percentChance(charecter.clas.percentFront) ? 1 : 0;
			do{
				if(!spotTaken(r,charecter.position.y)){
					charecter.position.x = r;
				}else if(!spotTaken((r == 0) ? 1 : 0,charecter.position.y)){
					charecter.position.x = (r == 0) ? 1 : 0;
				}else{
					charecter.position.y = (charecter.position.y == 0) ? 1 : 0;
					retry = true;
				}
			}while(retry);
		}
		
		this.allies.add(charecter);
	}
	
}