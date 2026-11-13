public final class Chef extends Staff {
  void act(GameState state, int customerId) {
    checkAvailable();
    if (customerId == 0) {
      consumeTask();
      state.event("Chef waits");
      return;
    }
    Customer customer = state.customer(customerId);
    Customer.require(
        customer.getOrder() != null && customer.getOrder().getStatus() == OrderStatus.PREPARING,
        "Chef needs a PREPARING order");
    consumeTask();
    customer.getOrder().prepareOneUnit();
    state.event(
        "Chef advances C"
            + customerId
            + " to "
            + customer.getOrder().getCompletedUnits()
            + "/"
            + customer.getOrder().getItem().getPreparationUnits()
            + ": "
            + customer.getOrder().getStatus());
  }
}
