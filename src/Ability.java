class Ability{//TODO, on level up, each character receives 3 points. cost 2 to learn a spell, 1 to upgrade spell or stat
	protected int[] manaCost = new int[]{-1,-1,-1,-1};
	protected int[] energyCost = new int[]{-1,-1,-1,-1};
	public  int turnCost = 1;
	public String name;
	public static String description;
	protected Charecter parent;
	protected Charecter[] target;
	protected int level;
	protected int[] basePower;
	protected double[] powerRatio;
	protected double[] magicDamageRatio;
	public boolean usesWeapon = true;
	public String targetType = "ENEMY";//ENEMY, ALLY, or SELF
	public int getManaCost(){
		return this.manaCost[this.level];
	}
	public int getEnergyCost(){
		return this.energyCost[this.level];
	}
	public int getRange(Weapon weapon){
		return weapon.range;
	}
	
	public boolean effect(Charecter[] target, Weapon weapon){
		return false;
	}
	
	Ability(Charecter parent, int level){
		this.parent = parent;
		this.level = level;
	}

	protected int getDamage(Weapon weapon) {
		return 0;
	}

	protected boolean getHit(Charecter charecter, Weapon weapon) {
		return false;
	}


	static class BasicAttack extends Ability{
		
		@Override
		public boolean effect(Charecter[] target, Weapon weapon){
			if(getHit(target[0], weapon) == true){
				target[0].damage(getDamage(weapon), this.parent, "PHYSICAL");
				return true;
			}else{
				return false;
			}
		}
		@Override
		protected boolean getHit(Charecter target, Weapon weapon){
			return getHit(target, weapon, parent);
		}
		public static boolean getHit(Charecter target, Weapon weapon, Charecter caster){
			int roll = (int) Math.ceil(Math.random()*100);
			int accuracy = caster.accuracy * (weapon.accuracyModifier/100);
			int dodge = target.dodge;
			if(100 - (accuracy + roll) > dodge){
				return true;
			}else{
				return false;
			}
		}
		@Override
		protected int getDamage(Weapon weapon){
			int damage = parent.damage + weapon.getDamage();
			return damage;
		}
		protected static int getDamage(Weapon weapon, Charecter caster){
			int damage = caster.damage + weapon.getDamage();
			return damage;
		}
		
		BasicAttack(Charecter parent, int level){
			super(parent, level);
			name = "Basic Attack";
			energyCost = new int[]{5,5,5,5};
		}
	}
	
	static class Throw extends Ability{
		@Override
		public boolean effect(Charecter[] target, Weapon weapon){
			if(weapon.getClass().getSimpleName() != "Fists"){
				target[0].parent.invAdd(weapon);
				if(parent.items.PRIMAIRY.equals(weapon)) parent.items.PRIMAIRY = new Weapon.Fists();
				else if(parent.items.SECONDAIRY.equals(weapon)) parent.items.SECONDAIRY = new Weapon.Fists();
				
				if(getHit(target[0], weapon) == true){
					target[0].damage(getDamage(weapon), this.parent, "PHYSICAL");
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}
		@Override
		protected boolean getHit(Charecter target, Weapon weapon){
			return getHit(target, weapon, parent);
		}
		protected boolean getHit(Charecter target, Weapon weapon, Charecter caster){
			int roll = (int) Math.ceil(Math.random()*100);
			int accuracy = caster.accuracy-(10*(weapon.size-1));
			int dodge = target.dodge;
			if(100 - (accuracy + roll) > dodge){
				return true;
			}else{
				return false;
			}
		}
		@Override
		protected int getDamage(Weapon weapon){
			return (int) Math.ceil((parent.damage + weapon.getThrowDamage())*((1.25*parent.accuracy > 100) ? (1.25*parent.accuracy)/100 : 1));
		}
		@Override
		public int getRange(Weapon weapon){
			return 3;
		}
		Throw(Charecter parent, int level){
			super(parent, level);
			name = "Throw";
			energyCost = new int[]{10,10,10,10};
		}
	}
	//Assassin
	static class PoisonStrike extends Ability{
		@Override
		public boolean effect(Charecter[] target, Weapon weapon){
			if(BasicAttack.getHit(target[0], weapon, parent)){
				target[0].damage((int)Math.round(BasicAttack.getDamage(weapon,parent)*this.powerRatio[this.level]),this.parent, "PHYSICAL");
				target[0].auras.add(new Aura.Poison(target[0],this.level));
				return false;
			}else{
				return false;
			}
		}
		
		PoisonStrike(Charecter parent, int level){
			super(parent, level);
			name = "Poison Strike";
			energyCost = new int[]{10,10,10,10};
			powerRatio = new double[]{0.5,0.6,0.7,0.8};
		}
	}
	//Knight
	static class BruteForce extends Ability{
		@Override
		public boolean effect(Charecter[] target, Weapon weapon){
			Console.log(target[0]+" "+weapon+" "+parent);
			if(BasicAttack.getHit(target[0], weapon, parent)){
				target[0].damage(
						(int) Math.round(
								BasicAttack.getDamage(weapon,this.parent)*
								this.powerRatio[this.level]),
								parent, 
								"PHYSICAL");
				return false;
			}else{
				return false;
			}
		}
		BruteForce(Charecter parent, int level) {
			super(parent, level);
			this.name = "Brute Force";
			this.powerRatio = new double[]{1.75,2.1,2.5,3};
			this.energyCost = new int[]{20,20,20,20};
		}
		
	}
	static class StandBehindMe extends Ability{
		@Override
		public boolean effect(Charecter[] target, Weapon weapon){
			target[0].auras.add(new Aura.StandBehindMeAura(target[0],this.parent, this.level));
			return true;
		}
		StandBehindMe(Charecter parent, int level) {
			super(parent, level);
			this.name = "Stand Behind Me";
			this.energyCost = new int[]{20,20,20,20};
		}
	}
	static class Meditate extends Ability{
		private double[] regen = new double[]{5,10,17,30};
		@Override
		public boolean effect(Charecter[] target, Weapon weapon){
			parent.currentHealth += (10 + regen[this.level]);
			return true;
		}
		Meditate(Charecter parent, int level) {
			super(parent, level);
			this.name = "Meditate";
			this.energyCost = new int[]{0,0,0,0};
			this.targetType = "SELF";
			this.usesWeapon = false;
		}
	}
	//Mage
static class Zap extends Ability{
		
		@Override
		public boolean effect(Charecter[] target, Weapon weapon){
			if(getHit(target[0], weapon) == true){
				target[0].damage(getDamage(weapon), this.parent, "MAGIC");
				return true;
			}else{
				return false;
			}
		}
		@Override
		public boolean getHit(Charecter target, Weapon weapon){
			int roll = (int) Math.ceil(Math.random()*100);
			if(roll > 10){
				return true;
			}else{
				return false;
			}
		}
		@Override
		protected int getDamage(Weapon weapon){
			return getDamage(weapon, parent, powerRatio[this.level]);
		}
		public static int getDamage(Weapon weapon, Charecter caster, double ratio){
			return (int) Math.round(ratio * (caster.magicDamage + weapon.getMagicDamage()));
		}
		
		Zap(Charecter parent, int level){
			super(parent, level);
			name = "Zap";
			manaCost = new int[]{10,10,10,10};
			powerRatio = new double[]{0.8,1,1.2,1.5};
		}
	}
	static class FlashFreeze extends Ability{
		@Override
		public boolean effect(Charecter[] target, Weapon weapon){
			for(Charecter charecter : Combat.getOpponents()){
				charecter.damage(getDamage(weapon),parent,"MAGIC");
			}
			return true;
		}
		@Override
		protected int getDamage(Weapon weapon){
			return basePower[this.level] + Zap.getDamage(weapon, parent, powerRatio[this.level]);
		}
		FlashFreeze(Charecter parent, int level) {
			super(parent, level);
			manaCost = new int[]{30,30,25,20};
			powerRatio = new double[]{0.3,0.4,0.5,0.6};
			basePower = new int[]{5,7,9,15};
		}
	}
	static class Shield extends Ability{
		@Override
		public boolean effect(Charecter[] target, Weapon weapon){
			target[0].auras.add(new Aura.Shield(this.parent,target[0],this.level,basePower[this.level] + (int) Math.round(powerRatio[this.level]*parent.magicDamage)));
			return true;
		}
		Shield(Charecter parent, int level) {
			super(parent, level);
			manaCost = new int[]{20,20,15,15};
			powerRatio = new double[]{1,1,1,1};
			basePower = new int[]{5,7,9,15};
			targetType = "ALLY";
		}
	}
}
