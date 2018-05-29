package infoia;

public class Pair {
	private Ingredient i;
	private Double value;
	
	public Pair(Ingredient i, Double value) {
		this.i = i;
		this.value = value;
	}
	
	public Ingredient getIngredient() {
		return i;
	}
	
	public Double getValue() {
		return value;
	}
	
	public void setIngredient(Ingredient i) {
		this.i = i;
	}
	
	public void setValue(Double value) {
		this.value = value;
	}
}
