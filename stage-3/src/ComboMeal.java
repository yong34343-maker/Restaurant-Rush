import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** A combo owns a fixed pair of component references and is itself a MenuItem. */
public final class ComboMeal implements MenuItem {
  private final List<MenuItem> components;

  public ComboMeal(MenuItem first, MenuItem second) {
    if (first == null || second == null)
      throw new IllegalArgumentException("Combo needs two items");
    components = Collections.unmodifiableList(Arrays.asList(first, second));
  }

  public List<MenuItem> getComponents() {
    return components;
  }

  public String getName() {
    return components.get(0).getName() + " + " + components.get(1).getName() + " Combo";
  }

  public int getPriceCents() {
    int totalCents = 0;
    for (MenuItem item : components) {
      totalCents += item.getPriceCents();
    }
    return Money.percent(totalCents, 90);
  }

  public int getPreparationUnits() {
    int totalUnits = 0;
    for (MenuItem item : components) {
      totalUnits += item.getPreparationUnits();
    }
    return totalUnits;
  }
}
