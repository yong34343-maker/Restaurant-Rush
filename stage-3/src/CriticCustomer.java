/** Critics always order Pasta, a distinct deterministic preference. */
public final class CriticCustomer extends Customer {
  public CriticCustomer(int id) {
    super(id);
  }

  public String getType() {
    return "Critic";
  }

  protected int waitingLoss() {
    return 12;
  }

  public MenuItem chooseFood(Menu menu) {
    return menu.getPasta();
  }
}
