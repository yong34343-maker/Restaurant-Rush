public final class Host extends Staff {
  void act(GameState state, int customerId) {
    checkAvailable();
    if (customerId == 0) {
      consumeTask();
      state.event("Host waits");
      return;
    }
    Customer c = state.customer(customerId);
    state.validateSeat(c);
    consumeTask();
    state.seat(c);
    state.event("Host completes seating task");
  }
}
