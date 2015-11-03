import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Party
{
	private int xpos;
	private int ypos;
	private int health = 1;
	private char id;
	public int lvl;
	private String srcIndex;
	ArrayList<Charecter> allies = new ArrayList<Charecter>();
	private Item[][] inventory = new Item[13][7];
	
	public char getId(){
		return id;
	}
	public int getXpos(){
		return xpos;
	}
	public int getYpos(){
		return ypos;
	}
	public int getHealth(){
		return health;
	}
	public void setXpos(int a){
		xpos = a;
	}
	public void setYpos(int a){
		ypos = a;
	};
	public void setHealth(int a){
		health = a;
	};
	public void setupSrcImage() {
		int i = 0;
		while(this.allies.get(i).currentHealth <= 0 && i+1 < this.allies.size()){
			i++;
		}
		this.srcIndex = this.allies.get(i).getSrcIndex();
	}
	//DEBUG TOOL
	void setSrcIndex(String src){
		this.srcIndex = src;
	}
	
	public BufferedImage getIcon(){
		return Main.images.get(this.srcIndex);
	}

	public String getSrcIndex() {
		return srcIndex;
	}
	
	public boolean invAdd(Item itm){
		for(int x = 0; x < inventory.length; x++){
			for(int y = 0; y < inventory[0].length; y++){
				if(inventory[x][y] != null){
					inventory[x][y] = itm;
					return true;
				}
			}
		}
		return false;
	}
	public boolean invRemove(int x, int y){
		if(x < inventory.length && y < inventory[0].length && inventory[x][y] != null){
			inventory[x][y] = null;
			return true;
		}
		return false;
	}
	public Item invGet(int x, int y){
		if(x < inventory.length && y < inventory[0].length){
			return inventory[x][y];
		}
		return null;
	}
	public Item[][] getInventory(){
		return inventory;
	}
	
	private char randomChar(){
		int i = (int) Math.floor(Math.random()*26) + 65;
		return (char) i;
	}

	Party(char id, int xpos, int ypos, int lvl)
	{
		this(lvl);
		this.id = id;
		this.xpos = xpos;
		this.ypos = ypos;
	}
	Party(int xpos, int ypos, int lvl)
	{
		this(lvl);
		this.xpos = xpos;
		this.ypos = ypos;
	}
	Party(char id, int xpos, int ypos)
	{
		this(Main.player.lvl);
		this.id = id;
		this.xpos = xpos;
		this.ypos = ypos;
	}
	Party(int xpos, int ypos)
	{
		this(Main.player.lvl);
		this.xpos = xpos;
		this.ypos = ypos;
	}
	Party(char id)
	{
		this(Main.player.lvl);
		this.id = id;
	}
	Party(int lvl)
	{
		Console.log(1, "new Party");
		this.lvl = lvl;
		int random;
		if(this.lvl == 0){
			return;
		}else{
			random = (int) Math.ceil(Math.random()*2*lvl/4.5);
			if(random > 4) random = 4;
			else if(random < 1) random = 1;
			random = 2;
			Console.log("this.lvl = "+this.lvl);
			Console.log(1,"number of allies = "+random);
			for(int i = 0; i < random; i++){
				int randomLevel = (int) Math.ceil(((Math.random()*4)-2)+lvl);
				if(randomLevel < 1) randomLevel = 1;
				Charecter ally = new Charecter(randomLevel, this);
				Console.log(1,"ally = new Charecter("+randomLevel+","+this+")");
				if(random == 1){
					ally.position.y = 1;
					ally.position.x = (int) Math.round(Math.random());
				}else if(random == 2){
						
					ally.position.y = Utility.percentChance(ally.clas.percentFront) ? 1 : 0;
					ally.position.x = (int) Math.round(Math.random());
					
					if(spotTaken(ally.position.x,ally.position.y)){
						ally.position.x = (ally.position.x == 0) ? 1 : 0;
					}
				}
				Console.log("ally.position = "+ally.position);
				Console.log("ally.level = "+ally.lvl);
				ally.levelUpPoints = 0;//3*ally.lvl;
				while(ally.levelUpPoints > 0){
					ally.levelUpPoints--;
					int tree = (int) Math.floor(Math.random()*3);
					if(tree > 2) tree = 2;
					if(tree == 0){
						ally.levelUpAgility();
					}else if(tree == 1){
						ally.levelUpStrength();
					}else if(tree == 2){
						ally.levelUpIntelect();
					}
				}
				ally.restore();
				Console.log(-1,"");
				this.allies.add(ally);
			}
			Console.log(-2,"");
			this.id = randomChar();
			this.xpos = (int) Math.floor(Math.random()*10);
			this.ypos = (int) Math.floor(Math.random()*10);
			
			setupSrcImage();
		}
	}
	protected boolean spotTaken(int x, int y){
		for(Charecter ally1 : allies){
			if(ally1.position.y == y && ally1.position.x == x){
				return true;
			}
		}
		return false;
	}
	
	public Party() {
		this(Main.player.lvl);
	}
}