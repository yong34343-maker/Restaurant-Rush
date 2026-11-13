/** A sample manager strategy. It does not guarantee victory for every seed. */
public final class DemoPolicy {
  private DemoPolicy() {}

  public static void choose(GameController game, boolean tryToWin) {
    GameState state = game.getState();
    // The Host acts independently before the Waiter in Stage 3.
    if (tryToWin && state.hasFreeTable() && !state.getQueue().isEmpty()) {
      game.hostAction(state.getQueue().get(0).getId());
    } else {
      game.hostAction(0);
    }
    if (!tryToWin) {
      game.waiterAction(WaiterTask.WAIT, 0);
      game.chefAction(0);
      return;
    }
    Customer readyCustomer = findReadyCustomer(state);
    Customer needsOrder = findCustomerWithoutOrder(state);
    if (readyCustomer != null) {
      game.waiterAction(WaiterTask.SERVE, readyCustomer.getId());
    } else if (needsOrder != null) {
      game.waiterAction(WaiterTask.ORDER, needsOrder.getId());
    } else if (state.hasFreeTable() && !state.getQueue().isEmpty()) {
      game.waiterAction(WaiterTask.SEAT, state.getQueue().get(0).getId());
    } else {
      game.waiterAction(WaiterTask.WAIT, 0);
    }
    Customer cookingCustomer = findPreparingCustomer(state);
    if (cookingCustomer == null) {
      game.chefAction(0);
    } else {
      game.chefAction(cookingCustomer.getId());
    }
  }

  private static Customer findReadyCustomer(GameState state) {
    for (Customer customer : state.getCustomers()) {
      Order order = customer.getOrder();
      if (customer.getStatus() == CustomerStatus.SEATED
          && order != null
          && order.getStatus() == OrderStatus.READY) {
        return customer;
      }
    }
    return null;
  }

  private static Customer findCustomerWithoutOrder(GameState state) {
    for (Customer customer : state.getCustomers()) {
      if (customer.getStatus() == CustomerStatus.SEATED && customer.getOrder() == null) {
        return customer;
      }
    }
    return null;
  }

  private static Customer findPreparingCustomer(GameState state) {
    for (Customer customer : state.getCustomers()) {
      Order order = customer.getOrder();
      if (order != null && order.getStatus() == OrderStatus.PREPARING) {
        return customer;
      }
    }
    return null;
  }
}
