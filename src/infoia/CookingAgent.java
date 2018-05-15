package infoia;

public class CookingAgent {
	public static void main(String[] args) {
		Ingredient tomato = new Ingredient("Tomato");
		Ingredient pasta = new Ingredient("Pasta");
		Ingredient garlic = new Ingredient("Garlic");
		Ingredient onion = new Ingredient("Onion");
		Ingredient spanishPepper = new Ingredient("Spanish Pepper");

		Recipe pastaRabiata = new Recipe("Pasta Rabiata");
		pastaRabiata.add(new Portion(tomato, 8, "piece"));
		pastaRabiata.add(new Portion(pasta, 200, "gramme"));
		pastaRabiata.add(new Portion(garlic, 2, "clove"));
		pastaRabiata.add(new Portion(onion, 1, "piece"));
		pastaRabiata.add(new Portion(spanishPepper, 1, "piece"));

		System.out.println(pastaRabiata.toString());
	}
}
