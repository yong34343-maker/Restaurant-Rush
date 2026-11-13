public final class Waiter extends Staff {
  void act(GameState state, WaiterTask task, int customerId) {
    checkAvailable();
    if (task == WaiterTask.WAIT) {
      consumeTask();
      state.event("Waiter waits");
      return;
    }
    Customer customer = state.customer(customerId);
    switch (task) {
      case SEAT:
        state.validateSeat(customer);
        consumeTask();
        state.seat(customer);
        break;
      case ORDER:
        Customer.require(
            customer.getStatus() == CustomerStatus.SEATED && customer.getOrder() == null,
            "Order needs a seated customer with no order");
        consumeTask();
        state.takeOrder(customer);
        break;
      case SERVE:
        Customer.require(
            customer.getStatus() == CustomerStatus.SEATED
                && customer.getOrder() != null
                && customer.getOrder().getStatus() == OrderStatus.READY,
            "Serve needs a READY order");
        consumeTask();
        customer.serve(state.getTurn());
        state.recordServed();
        state.event("Waiter serves C" + customer.getId() + ": SERVED");
        break;
      default:
        throw new IllegalArgumentException("Unknown task");
    }
  }
}
