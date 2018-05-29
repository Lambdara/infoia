package infoia;

public class Ingredient {
	public static enum Flavour {
		SWEET, SOUR, BITTER, SALTY, UMAMI
	}
	
	private String name;
	private Flavour flavour;

	Ingredient (String name, Flavour flavour) {
		this.name = name;
		this.flavour = flavour;
	}
	
	public Flavour getFlavour() {
		return flavour;
	}

	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}
}
