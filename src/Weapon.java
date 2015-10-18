public class Weapon extends Item{
	public String id;
	public int accuracyModifier;//percent
	public int[] damageModifier;//[0]+[1]d[2]
	public int[] throwDamageModifier;//[0]+[1]d[2]
	public int[] magicDamageModifier;//[0]+[1]d[2]
	public int range;//0 = mele, 1 = back row to front, 2 = front to back, back to front, 3 = ranged
	public int size;//1 = small, 2 = med, 3 = large
	public static String[] sizes = new String[]{"small","medium","large"};
	public int getDamage(){
		int damage = damageModifier[0];
		for(int i = 0; i < damageModifier[1]; i++){
			damage += (int) Math.ceil(Math.random()*damageModifier[2]);
		}
		return damage;
	}
	public int getThrowDamage(){
		int damage = throwDamageModifier[0];
		for(int i = 0; i < throwDamageModifier[1]; i++){
			damage += (int) Math.ceil(Math.random()*throwDamageModifier[2]);
		}
		return damage;
	}
	public int getMagicDamage(){
		int damage = magicDamageModifier[0];
		for(int i = 0; i < magicDamageModifier[1]; i++){
			damage += (int) Math.ceil(Math.random()*magicDamageModifier[2]);
		}
		return damage;
	}
	
	static class Fists extends Weapon{
		Fists(){
			this.id = "Fists";
			this.accuracyModifier = 90;
			this.damageModifier = new int[]{0,1,4};
			this.throwDamageModifier = new int[]{0,0,0};
			this.magicDamageModifier = new int[]{0,0,0};
			this.range = 0;
			this.size = 0;
		}
		Fists(String id){
			this();
			this.id = id;
		}
	}
	
	static class WoodenSword extends Weapon{
		WoodenSword(){
			this.id = "Wooden Sword";
			this.accuracyModifier = 90;
			this.damageModifier = new int[]{0,1,4};
			this.throwDamageModifier = new int[]{-3,1,4};
			this.magicDamageModifier = new int[]{0,0,0};
			this.range = 0;
			this.size = 2;
		}
	}
	
	static class WoodenStaff extends Weapon{
		WoodenStaff(){
			this.id = "Wooden Staff";
			this.accuracyModifier = 90;
			this.damageModifier = new int[]{-1,1,4};
			this.throwDamageModifier = new int[]{-3,1,4};
			this.magicDamageModifier = new int[]{2,0,0};
			this.range = 0;
			this.size = 2;
		}
	}
}