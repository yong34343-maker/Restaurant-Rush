/** A kitchen item supplies its own price and preparation effort. */
public interface MenuItem {
  String getName();

  int getPriceCents();

  int getPreparationUnits();
}
