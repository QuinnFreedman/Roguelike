import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Item{
	public String name;
	public int lvl;
	public ArrayList<Aura> auras;
	private String srcIndex;
	public String getSrcIndex(){
		return this.srcIndex;
	}
	public BufferedImage getIcon(){
		return Main.images.get(srcIndex);
	}
	public Weapon getWeapon(){
		Console.log("get weapon: this.class = "+getClass().getSuperclass().getName());
		if(this.getClass().getSuperclass().getName() == "Weapon"){
			return (Weapon) this;
		}
		return new Weapon.Fists(this.getClass().getSimpleName());
	}
	private void setUpIcon(){
		srcIndex = this.getClass().getSimpleName();
		Main.loadImage(srcIndex);
	}
	public void onEquip(Charecter c){
		
	}
	public void onUnEquip(Charecter c){
		
	}
}