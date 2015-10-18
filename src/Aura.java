import java.awt.Image;
import java.awt.image.BufferedImage;

public class Aura{
	public TriggerType trigger;//upkeep,tick,eot,attack,defend,onDealDamage,onRecieveDamage,trigger(called manually)
	public String remove;
	protected Charecter parent;
	protected Item parentItem;
	protected boolean debuff;
	protected int duration;
	protected int level;
	protected String icon = null;
	public static enum TriggerType{
		UPKEEP,
		TICK,
		EOT,
		ATTACK,
		DEFEND,
		onDealDamage,
		onRecieveDamage,
		TRIGGER
	}
	protected void remove(){
		parent.aurasRemoveTemp.add(this);
		//parent.auras.remove(this);
	}
	
	public void effect(Charecter target){
		
	}
	
	public int effect(int damage, Charecter source, String type){//for on deal/take damage/att
		return -1;
	}
	
	public Image getIcon() {
		return Main.loadImage("auraIcons\\"+this.icon);
	}
	
	public void effect() {
		
	}
	
	Aura(Charecter parent, int level){
		this.parent = parent;
		this.level = level;
	}
	Aura(Item parentItem, int level){
		this.parentItem = parentItem;
		this.level = level;
	}
	
	static class AuraTrigger extends Aura{//triggers all auras of the chosen class attached to a target
		private Aura auraToTrigger;
		private Charecter targetParent;
		AuraTrigger(Charecter parent, Charecter targetParent, Aura auraToTrigger, String trigger) {
			super(parent, 0);
			this.trigger = TriggerType.TRIGGER;
			this.auraToTrigger = auraToTrigger;
		}
		
		@Override
		public void effect(){
			for(Aura aura : targetParent.auras){
				if(aura.getClass() == auraToTrigger.getClass()){
					aura.effect();
				}
			}
		}
		
	}
	static class AuraDeleter extends Aura{//Deletes specific aura
		private Aura auraToTrigger;
		AuraDeleter(Charecter parent, Aura auraToTrigger, String trigger) {//TODO, remove parent field - unused
			super(parent, 0);
			this.trigger = TriggerType.TRIGGER;
			this.auraToTrigger = auraToTrigger;
		}
		AuraDeleter(Charecter parent, Aura auraToTrigger, String trigger, int durration) {//TODO, remove parent field - unused
			super(parent, 0);
			this.trigger = TriggerType.TRIGGER;
			this.auraToTrigger = auraToTrigger;
			this.duration = durration;
		}
		
		@Override
		public void effect(){
			if(this.duration > 0){
				duration--;
			}
			if(this.duration == 0){
				auraToTrigger.remove();
			}
		}
		
	}
	static class Retreating extends Aura {
		
		@Override
		public void effect() {
			Combat.remove(this.parent);
		}
		
		Retreating(Charecter parent, int level) {
			super(parent, level);
			this.trigger = TriggerType.UPKEEP;
		}
	}
	static class Poison extends Aura{
		int[] damage = new int[]{1,2,3,4};
		int[] time = new int[]{1,2,3,4};
		
		@Override
		public void effect(){
			parent.damage(damage[level],this.parent,"TRUE",
					"%PARENT is poisoned for %DAMAGE damage");
			this.duration--;
			if(duration <= 0){
				this.remove();
			}
		}
		
		Poison(Charecter parent, int level) {
			super(parent, level);
			this.debuff = true;
			this.duration = time[level];
			this.trigger = TriggerType.TICK;
			this.icon = "poison";
		}
	}
	static class StandBehindMeAura extends Aura{
		double[] damageReduction = new double[]{0.9,0.75,0.65,0.5};
		Charecter target;//protector
		@Override
		public int effect(int damage, Charecter source, String type){
			this.target.damage((int) Math.round(damage*damageReduction[this.level]), source, type);
			return 0;
		}
		
		StandBehindMeAura(Charecter parent, Charecter target, int level) {
			super(parent, level);
			this.debuff = false;
			this.trigger = TriggerType.DEFEND;
			target.auras.add(new AuraDeleter(target, this, "UPKEEP"));
		}

	}
	static class Shield extends Aura{
		private int health;
		
		public int effect(int damage, Charecter source, String type){
			if(damage <= health){
				health -= damage;
				return 0;
			}else{
				int d = damage-health;
				health = 0;
				return d;
			}
		}
		
		Shield(Charecter target, Charecter parent, int level, int health){
			super(parent, level);
			this.trigger = TriggerType.DEFEND;
			this.debuff = false;
			this.health = health;
			target.auras.add(new AuraDeleter(target, this, "UPKEEP", 2));
		}
	}
	static class PoisonWeapon extends Aura{
		int[] damage = new int[]{1,2,3,4};
		
		@Override
		public void effect(){
			
		}
		
		public PoisonWeapon(Item parentItem, int level) {
			super(parentItem, level);
			this.debuff = false;
			this.trigger = TriggerType.onDealDamage;
			
		}

	}
}