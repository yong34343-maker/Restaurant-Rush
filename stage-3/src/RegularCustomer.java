public final class RegularCustomer extends Customer {
  public RegularCustomer(int id) {
    super(id);
  }

  public String getType() {
    return "Regular";
  }

  protected int waitingLoss() {
    return 8;
  }

  public MenuItem chooseFood(Menu menu) {
    return menu.regularChoice(getId());
  }
}
