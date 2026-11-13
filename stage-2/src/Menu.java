import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Stores the menu and chooses food using either a seed or the original base rule. */
public final class Menu {
  private final MenuItem salad = new Dish("Salad", 900, 1);
  private final MenuItem burger = new Dish("Burger", 1500, 1);
  private final MenuItem pasta = new Dish("Pasta", 1800, 2);

  private final boolean seeded;
  private final long seed;

  /** Preserve the original documented choices for --base and regression tests. */
  public Menu() {
    seeded = false;
    seed = GameSettings.DEFAULT_SEED;
  }

  /** Use the same seed to reproduce the same food choices. */
  public Menu(long seed) {
    seeded = true;
    this.seed = seed;
  }

  public MenuItem getPasta() {
    return pasta;
  }

  public List<MenuItem> getItems() {
    return Collections.unmodifiableList(Arrays.asList(salad, burger, pasta));
  }

  public MenuItem regularChoice(int customerId) {
    if (seeded) {
      return seededChoice(customerId);
    }

    // The original base rule repeats Pasta, Burger, Salad by customer ID.
    int remainder = customerId % 3;
    if (remainder == 1) {
      return pasta;
    } else if (remainder == 2) {
      return burger;
    } else {
      return salad;
    }
  }

  private MenuItem seededChoice(int customerId) {
    List<MenuItem> items = getItems();
    Random random = new Random(seed);
    int selectedIndex = 0;

    // Customer ID selects a position in this seed's random sequence.
    // Recreating it keeps a customer's choice independent of staff priority.
    for (int id = 1; id <= customerId; id++) {
      selectedIndex = random.nextInt(items.size());
    }
    return items.get(selectedIndex);
  }
}
