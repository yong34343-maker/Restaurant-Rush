public final class HappyHourPolicy implements PricingPolicy {
  private boolean used;
  private int remaining;

  public boolean isUsed() {
    return used;
  }

  public int getRemaining() {
    return remaining;
  }

  public boolean isActive() {
    return remaining > 0;
  }

  void activate() {
    Customer.require(!used, "Happy Hour has already been activated");
    used = true;
    remaining = 2;
  }

  public int quote(Customer customer, MenuItem item) {
    int customerPrice = customer.discountedPrice(item.getPriceCents());
    return isActive() ? Money.percent(customerPrice, 80) : customerPrice;
  }

  void recover(Iterable<Customer> customers) {
    if (isActive()) for (Customer c : customers) c.recoverSatisfaction(15);
  }

  void endTurn() {
    if (remaining > 0) remaining--;
  }
}
