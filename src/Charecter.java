import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Charecter{
	public static String[] classes = {"ASSASIN","MAGE", "KNIGHT"};
	public static String[] races = {"HUMAN","ELF"};
	public int health;
	public int currentHealth;
	public int mana;
	public int currentMana;
	public int energy;
	public int currentEnergy;
	public int damage;
	public int magicDamage;
	public int armor;
	public int magicResist;
	public int dodge;
	public int accuracy;
	public int healthRegen = 0;
	public int manaRegen = 0;
	public int energyRegen = 0;
	public int initiative;
	public Point position = new Point();
	public String race;
	public String name;
	public clas clas;
	public int lvl;
	public boolean stunned = false;
	private String srcIndex;
	public List<Ability> abilities;
	public ItemSet items = new ItemSet();//equipped
	public List<Aura> auras = new ArrayList<Aura>();
	public Party parent;
	public int levelUpPoints = 0;
	public int exp = 0;
	public String getSrcIndex(){
		return this.srcIndex;
	}
	public BufferedImage getIcon(){
		return Main.images.get(srcIndex);
	}
	public void levelUpAgility(){
		this.accuracy = this.clas.levelUpAccuracy(this.accuracy, this.lvl);
		this.initiative = this.clas.levelUpInitiative(this.initiative, this.lvl);
		this.dodge = this.clas.levelUpDodge(this.dodge, this.lvl);
	}
	public void levelUpStrength(){
		this.damage = this.clas.levelUpDamage(this.damage, this.lvl);
		this.energy = this.clas.levelUpEnergy(this.energy, this.lvl);
	}
	public void levelUpFortitude(){
		this.health = this.clas.levelUpHealth(this.health, this.lvl);
		this.healthRegen = this.clas.levelUpHealthRegen(this.healthRegen, this.lvl);
		this.energyRegen = this.clas.levelUpEnergyRegen(this.energyRegen, this.lvl);
	}
	public void levelUpIntelect(){
		this.magicDamage = this.clas.levelUpMagicDamage(this.magicDamage, this.lvl);
		this.mana = this.clas.levelUpMana(this.mana, this.lvl);
		this.manaRegen = this.clas.levelUpManaRegen(this.manaRegen, this.lvl);
	}
	
	public void damage(int damage, Charecter source, String type){
		for(Aura aura : source.auras){
			if(aura.trigger == Aura.TriggerType.ATTACK){
				aura.effect();
				aura.effect(damage,source,type);
			}
		}
		for(Aura aura : this.auras){
			if(aura.trigger == Aura.TriggerType.DEFEND){
				aura.effect();
				aura.effect(damage,source,type);
			}
		}
		int damageTaken;
		Combat.log("it does "+damage+" damage");
		if(type == "PHYSICAL"){
			damageTaken = damage * (100/(100+this.armor));
			Combat.log((Math.abs(damage-damageTaken))+" damage is prevented by the "+this.clas.getName()+"'s armor");
		}else if(type == "MAGIC"){
			damageTaken = damage * (100/(100+this.magicResist));
			Combat.log((Math.abs(damage-damageTaken))+" damage is prevented by the "+this.race+"'s magic resistance");
		}else{
			damageTaken = damage;
		}
		for(Aura aura : source.auras){
			if(aura.trigger == Aura.TriggerType.onDealDamage){
				aura.effect();
				int d = aura.effect(damageTaken,source,type);
				damageTaken = (d != -1) ? d : damageTaken;
			}
		}
		for(Aura aura : this.auras){
			if(aura.trigger == Aura.TriggerType.onRecieveDamage){
				aura.effect();
				int d = aura.effect(damageTaken,source,type);
				damageTaken = (d != -1) ? d : damageTaken;
			}
		}
		damageTaken = (damageTaken < 0) ? 0 : damageTaken;
		this.currentHealth -= damageTaken;
	}
	protected void setupSrcImage() {
		this.srcIndex = "/characterTiles/"+this.race+"_"+this.clas.getName();
		Main.loadImage(srcIndex);
	}
	public static clas parseClas(String clasID){
		if(clasID == "ASSASIN") return new Assassin();
		else if(clasID == "MAGE") return new Mage();
		else if(clasID == "KNIGHT") return new Knight();
		
		return null;
	}
	Charecter(int lvl, Party parent){
		this(lvl,
				parent,
				races[(int) Math.floor(Math.random()*races.length)],
				classes[(int) Math.floor(Math.random()*classes.length)]
			);
	}
	Charecter(int lvl, Party parent, String race, String clas){
		this.lvl = lvl;
		this.parent = parent;
		this.clas = parseClas(clas);
		this.race = race;
		this.health = this.clas.getHealth();
		this.mana = this.clas.getMana();
		this.energy = this.clas.getEnergy();
		this.damage = this.clas.getDamage();
		this.restore();
		this.magicDamage = this.clas.getMagicDamage();
		this.armor = this.clas.getArmor();
		this.magicResist = this.clas.getMagicResist();
		this.dodge = this.clas.getDodge();
		this.accuracy = this.clas.getAccuracy();
		this.initiative = this.clas.getinitiative();
		this.abilities = new ArrayList<Ability>();
		abilities.add(new Ability.BasicAttack(this,0));
		abilities.add(new Ability.Throw(this,0));
		abilities.add(new Ability.PoisonStrike(this, 0));
		
		setupSrcImage();
		
		this.items.PRIMAIRY = new Weapon.WoodenSword();
	}
	public void restore(){
		this.currentHealth = this.health;
		this.currentMana = this.mana;
		this.currentEnergy = this.energy;
	}
}
class ItemSet{
	public Item HEAD = null;
	public Item TOP = null;
	public Item LEGS = null;
	public Item HANDS = null;
	public Item CAPE = null;
	public Item NECK = null;
	public Item FEET = null;
	public Item[] RINGS = new Item[4];
	public Item PRIMAIRY = null;//WEAPON
	public Item SECONDAIRY = null;//SHEILD/SECONDAIRY WEAOPN
}


//****************************CLASSES******************************//TODO
abstract class clas{
	protected int health;
	protected int mana;
	protected int energy;
	protected int damage;
	protected int throwDamage;
	protected int magicDamage;
	protected int armor;
	protected int magicResist;
	protected int dodge;
	protected int accuracy;
	protected int initiative;
	protected int healthRegen;
	protected int energyRegen;
	protected int manaRegen;
	protected String name;
	protected int percentFront; //percent chance to be in the front row
	protected String description;
	
	public String getInfo(){
		String output = description;
		//TODO return starting stat values
		return output;
	}
	public int getHealth(){
		return health;
	};
	public int getMana(){
		return mana;
	};
	public int getEnergy(){
		return energy;
	};
	public int getDamage(){
		return damage;
	};
	public int getThrowDamage(){
		return throwDamage;
	};
	public int getMagicDamage(){
		return magicDamage;
	};
	public int getArmor(){
		return armor;
	};
	public int getMagicResist(){
		return magicResist;
	};
	public int getDodge(){
		return dodge;
	};
	public int getAccuracy(){
		return accuracy;
	};
	public int getinitiative(){
		return initiative;
	};
	public int getHealthRegen(){
		return healthRegen;
	};
	public int getEnergyRegen(){
		return energyRegen;
	};
	public int getManaRegen(){
		return manaRegen;
	};
	public String getName() {
		return name;
	};
	public int getPercentFront(){
		return percentFront;
	}
	public abstract int levelUpHealthRegen(int healthRegen, int level);
	public abstract int levelUpManaRegen(int manaRegen, int level);
	public abstract int levelUpEnergyRegen(int energyRegen, int level);
	public abstract int levelUpHealth(int health, int level);
	public abstract int levelUpMana(int mana, int level);
	public abstract int levelUpEnergy(int energy, int level);
	public abstract int levelUpDamage(int damage, int level);
	public abstract int levelUpMagicDamage(int magicdamage, int level);
	public abstract int levelUpArmor(int armor, int level);
	public abstract int levelUpMR(int magicResist, int level);
	public abstract int levelUpDodge(int dodge, int level);
	public abstract int levelUpAccuracy(int accuracy, int level);
	public abstract int levelUpInitiative(int initiative, int level);
}
class Assassin extends clas{
	public int levelUpHealth(int health, int level){
		return health+5;
	}
	public int levelUpMana(int mana, int level){
		return mana+5;
	}
	public int levelUpEnergy(int energy, int level){
		return energy+5;
	}
	public int levelUpDamage(int damage, int level){
		return damage+5;
	}
	public int levelUpMagicDamage(int magicDamage, int level){
		return magicDamage+5;
	}
	public int levelUpArmor(int armor, int level){
		return armor+5;
	}
	public int levelUpMR(int magicResist, int level){
		return magicResist+5;
	}
	public int levelUpDodge(int dodge, int level){
		return dodge+5;
	}
	public int levelUpAccuracy(int accuracy, int level){
		return accuracy+5;
	}
	public int levelUpInitiative(int initiative, int level){
		return initiative+5;
	}
	public int levelUpHealthRegen(int healthRegen, int level) {
		return healthRegen+0;
	}
	public int levelUpManaRegen(int manaRegen, int level) {
		return manaRegen+0;
	}
	public int levelUpEnergyRegen(int energyRegen, int level) {
		return energyRegen+0;
	}
	Assassin(){
		this.health = 20;
		this.mana = -1;
		this.energy = 25;
		this.damage = 7;
		this.magicDamage = 0;
		this.armor = 0;
		this.magicResist = 0;
		this.dodge = 20;
		this.accuracy = 90;
		this.initiative = 6;
		this.name = "Assassin";
		this.percentFront = 80;
		this.description = "A fast unit with high damage and agility but low health";
	}
}
class Mage extends clas{
	public int levelUpHealth(int health, int level){
		return health+5;
	}
	public int levelUpMana(int mana, int level){
		return mana+5;
	}
	public int levelUpEnergy(int energy, int level){
		return energy+5;
	}
	public int levelUpDamage(int damage, int level){
		return damage+5;
	}
	public int levelUpMagicDamage(int magicdamage, int level){
		return magicDamage+5;
	}
	public int levelUpArmor(int armor, int level){
		return armor+5;
	}
	public int levelUpMR(int magicResist, int level){
		return magicResist+5;
	}
	public int levelUpDodge(int dodge, int level){
		return dodge+5;
	}
	public int levelUpAccuracy(int accuracy, int level){
		return accuracy+5;
	}
	public int levelUpInitiative(int initiative, int level){
		return initiative+5;
	}
	public int levelUpHealthRegen(int healthRegen, int level) {
		return healthRegen+0;
	}
	public int levelUpManaRegen(int manaRegen, int level) {
		return manaRegen+0;
	}
	public int levelUpEnergyRegen(int energyRegen, int level) {
		return energyRegen+0;
	}
	Mage(){
		this.health = 20;
		this.mana = 20;
		this.energy = 10;
		this.damage = 2;
		this.magicDamage = 10;
		this.armor = 0;
		this.magicResist = 0;
		this.dodge = 5;
		this.accuracy = 90;
		this.initiative = 5;
		this.name = "Mage";
		this.percentFront = 30;
		this.description = "A magical unit high magic damage but low health";
	}
}
class Knight extends clas{
	public int levelUpHealth(int health, int level){
		return health+5;
	}
	public int levelUpMana(int mana, int level){
		return mana+5;
	}
	public int levelUpEnergy(int energy, int level){
		return energy+5;
	}
	public int levelUpDamage(int damage, int level){
		return damage+5;
	}
	public int levelUpMagicDamage(int magicdamage, int level){
		return magicDamage+5;
	}
	public int levelUpArmor(int armor, int level){
		return armor+5;
	}
	public int levelUpMR(int magicResist, int level){
		return magicResist+5;
	}
	public int levelUpDodge(int dodge, int level){
		return dodge+5;
	}
	public int levelUpAccuracy(int accuracy, int level){
		return accuracy+5;
	}
	public int levelUpInitiative(int initiative, int level){
		return initiative+5;
	}
	public int levelUpHealthRegen(int healthRegen, int level) {
		return healthRegen+0;
	}
	public int levelUpManaRegen(int manaRegen, int level) {
		return manaRegen+0;
	}
	public int levelUpEnergyRegen(int energyRegen, int level) {
		return energyRegen+0;
	}
	Knight(){
		this.health = 40;
		this.mana = -1;
		this.energy = 20;
		this.damage = 4;
		this.magicDamage = 0;
		this.armor = 0;
		this.magicResist = 0;
		this.dodge = 20;
		this.accuracy = 80;
		this.initiative = 2;
		this.name = "Knight";
		this.percentFront = 100;
		this.description = "A slow unit with a lot of health";
	}
}
//TODO, organize stats into tree or array -> choose one at random each level up 