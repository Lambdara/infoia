package infoia;

public class Pair {
	private Ingredient i;
	private Double similarity;
	
	public Pair(Ingredient i, Double similarity) {
		this.i = i;
		this.similarity = similarity;
	}
	
	public Ingredient getIngredient() {
		return i;
	}
	
	public Double getSimilarity() {
		return similarity;
	}
	
	public void setIngredient(Ingredient i) {
		this.i = i;
	}
	
	public void setSimilarity(Double similarity) {
		this.similarity = similarity;
	}
}
