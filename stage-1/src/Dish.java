public final class Dish implements MenuItem {
  private final String name;
  private final int priceCents;
  private final int preparationUnits;

  public Dish(String name, int priceCents, int preparationUnits) {
    if (name == null || name.trim().isEmpty() || priceCents <= 0 || preparationUnits <= 0)
      throw new IllegalArgumentException("Invalid dish");
    this.name = name;
    this.priceCents = priceCents;
    this.preparationUnits = preparationUnits;
  }

  public String getName() {
    return name;
  }

  public int getPriceCents() {
    return priceCents;
  }

  public int getPreparationUnits() {
    return preparationUnits;
  }
}
