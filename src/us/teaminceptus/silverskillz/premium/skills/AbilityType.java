package us.teaminceptus.silverskillz.premium.skills;

public enum AbilityType {
	
	ACTION_POTION_COOLDOWN(0),
	DURATION_COOLDOWN(1),
	ACTION(2),
	EVENT(3),
	POTION_COOLDOWN(4)
	;
	
	private final int id;
	
	private AbilityType(int id) {
		this.id = id;
	}
	
	public final int getId() {
		return this.id;
	}
	
	public static final AbilityType getById(int id) {
		for (AbilityType t : values()) {
			if (t.id == id) return t;
			else continue;
		}
		
		return null;
	}
}
