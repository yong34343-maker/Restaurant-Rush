/** State transitions and satisfaction are private to the customer. */
public abstract class Customer {
  private final int id;
  private int satisfaction = 100;
  private CustomerStatus status = CustomerStatus.WAITING;
  private Order order;
  private int servedTurn = -1;
  private int readyToPayTurn = -1;

  protected Customer(int id) {
    if (id <= 0) throw new IllegalArgumentException("Invalid customer ID");
    this.id = id;
  }

  public abstract String getType();

  protected abstract int waitingLoss();

  public abstract MenuItem chooseFood(Menu menu);

  public int discountedPrice(int cents) {
    return cents;
  }

  public final int getId() {
    return id;
  }

  public final int getSatisfaction() {
    return satisfaction;
  }

  public final CustomerStatus getStatus() {
    return status;
  }

  public final Order getOrder() {
    return order;
  }

  public final int getReadyToPayTurn() {
    return readyToPayTurn;
  }

  public final boolean isWaitingForService() {
    return status == CustomerStatus.WAITING || status == CustomerStatus.SEATED;
  }

  final void seat() {
    require(status == CustomerStatus.WAITING, "Customer is not in the waiting queue");
    status = CustomerStatus.SEATED;
  }

  final void placeOrder(Order order) {
    require(status == CustomerStatus.SEATED && this.order == null, "Customer cannot order now");
    this.order = order;
  }

  final void serve(int turn) {
    require(
        status == CustomerStatus.SEATED && order != null && order.getStatus() == OrderStatus.READY,
        "Customer's order is not READY");
    order.serve();
    status = CustomerStatus.EATING;
    servedTurn = turn;
  }

  final void finishEating(int turn) {
    if (status == CustomerStatus.EATING && servedTurn < turn) {
      status = CustomerStatus.READY_TO_PAY;
      readyToPayTurn = turn;
    }
  }

  final void pay(int turn) {
    require(status == CustomerStatus.READY_TO_PAY && readyToPayTurn < turn, "Payment is too early");
    order.pay();
    status = CustomerStatus.PAID;
  }

  final void loseSatisfaction() {
    if (!isWaitingForService()) return;
    satisfaction = Math.max(0, satisfaction - waitingLoss());
    if (satisfaction == 0) {
      status = CustomerStatus.LEFT;
      if (order != null) order.cancel();
    }
  }

  static void require(boolean condition, String reason) {
    if (!condition) throw new IllegalArgumentException(reason);
  }
}
