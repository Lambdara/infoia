package infoia;

import java.util.ArrayList;

public class Ingredient {
	public static enum Flavour {
		Sweet, Sour, Bitter, Salty, Savory, Aromatic, Spicy
	}
	
	private String name;
	private ArrayList<Flavour> flavours;

	Ingredient (String name) {
		this.name = name;
		this.flavours = new ArrayList<Flavour>();
	}
	
	public ArrayList<Flavour> getFlavours() {
		return flavours;
	}

	public String getName() {
		return name;
	}
	
	public String toString() {
		return name + ", " + flavours;
	}
	
	public void addFlavour(Flavour flavour) {
		if(!flavours.contains(flavour))
			flavours.add(flavour);
	}
}
