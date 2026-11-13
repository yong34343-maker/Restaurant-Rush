public final class Order {
  private final MenuItem item;
  private final int billCents;
  private int completedUnits;
  private OrderStatus status = OrderStatus.PREPARING;

  public Order(MenuItem item, int billCents) {
    if (item == null || billCents < 0) throw new IllegalArgumentException("Invalid order");
    this.item = item;
    this.billCents = billCents;
  }

  public MenuItem getItem() {
    return item;
  }

  public int getBillCents() {
    return billCents;
  }

  public int getCompletedUnits() {
    return completedUnits;
  }

  public OrderStatus getStatus() {
    return status;
  }

  void prepareOneUnit() {
    Customer.require(status == OrderStatus.PREPARING, "Order is not PREPARING");
    completedUnits++;
    if (completedUnits == item.getPreparationUnits()) status = OrderStatus.READY;
  }

  void serve() {
    Customer.require(status == OrderStatus.READY, "Order is not READY");
    status = OrderStatus.SERVED;
  }

  void pay() {
    Customer.require(status == OrderStatus.SERVED, "Order was not served");
    status = OrderStatus.PAID;
  }

  void cancel() {
    Customer.require(
        status == OrderStatus.PREPARING || status == OrderStatus.READY,
        "Cannot cancel a served order");
    status = OrderStatus.CANCELLED;
  }
}
