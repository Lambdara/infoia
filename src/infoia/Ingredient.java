package infoia;

import java.util.ArrayList;

public class Ingredient {
	public static enum Flavour {
		Sweet, Sour, Bitter, Salty, Savory, Aromatic, Spicy
	}
	
	public static enum Structure {
	    Solid, Fluid, Paste, Powder
	}
	
	private String name;
	private ArrayList<Flavour> flavours;
	private Structure structure;

	Ingredient (String name) {
		this.name = name;
		this.structure = null;
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
	
	public void setStructure(Structure structure) {
	    this.structure = structure;
	}
	
	public Structure getStructure() {
	    return structure;
	}
}
