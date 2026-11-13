public final class VIPCustomer extends Customer {
  public VIPCustomer(int id) {
    super(id);
  }

  public String getType() {
    return "VIP";
  }

  protected int waitingLoss() {
    return 5;
  }

  public MenuItem chooseFood(Menu menu) {
    return menu.regularChoice(getId());
  }

  public int discountedPrice(int cents) {
    return Money.percent(cents, 90);
  }
}
