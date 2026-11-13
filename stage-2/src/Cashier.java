/** Collects at most one payment, from the oldest eligible customer. */
public final class Cashier extends Staff {
  void collectOldest(GameState state) {
    checkAvailable();
    Customer oldest = null;
    for (Customer customer : state.getCustomers()) {
      boolean ready = customer.getStatus() == CustomerStatus.READY_TO_PAY;
      boolean waitedOneTurn = customer.getReadyToPayTurn() < state.getTurn();
      if (!ready || !waitedOneTurn) {
        continue;
      }
      if (oldest == null) {
        oldest = customer;
      } else if (customer.getReadyToPayTurn() < oldest.getReadyToPayTurn()) {
        oldest = customer;
      } else if (customer.getReadyToPayTurn() == oldest.getReadyToPayTurn()
          && customer.getId() < oldest.getId()) {
        oldest = customer;
      }
    }
    consumeTask();
    if (oldest == null) {
      state.event("Cashier has no eligible payment");
    } else {
      state.collectPayment(oldest);
    }
  }
}
