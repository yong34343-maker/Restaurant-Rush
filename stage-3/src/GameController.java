/** Rules and action ordering live here, independent of terminal I/O. */
public final class GameController {
  private final GameState state;

  /** Preserve the original base setting for regression tests and replays. */
  public GameController() {
    state = new GameState();
  }

  /** A reproducible seeded game, used by the normal CLI launch. */
  public GameController(long seed) {
    state = new GameState(seed);
  }

  private final Host host = new Host();

  public Host getHost() {
    return host;
  }

  private final Waiter waiter = new Waiter();

  private final Chef chef = new Chef();

  private final Cashier cashier = new Cashier();
  private TurnPhase phase = TurnPhase.BETWEEN_TURNS;

  public GameState getState() {
    return state;
  }

  public TurnPhase getPhase() {
    return phase;
  }

  public Waiter getWaiter() {
    return waiter;
  }

  public Chef getChef() {
    return chef;
  }

  public Cashier getCashier() {
    return cashier;
  }

  public boolean isFinished() {
    return state.getTurn() == 18 && phase == TurnPhase.BETWEEN_TURNS;
  }

  public void beginTurn() {
    beginTurn(false);
  }

  public void beginTurn(boolean activateHappyHour) {
    Customer.require(
        phase == TurnPhase.BETWEEN_TURNS && !isFinished(), "Cannot start another turn now");
    if (activateHappyHour) state.getHappyHour().activate();
    state.beginTurn();
    state.getHappyHour().recover(state.getCustomers());
    host.startTurn();
    waiter.startTurn();
    chef.startTurn();
    cashier.startTurn();
    phase = TurnPhase.HOST;
  }

  public void hostAction(int customerId) {
    Customer.require(phase == TurnPhase.HOST, "Host acts before Waiter");
    host.act(state, customerId);
    phase = TurnPhase.WAITER;
  }

  public void waiterAction(WaiterTask task, int customerId) {
    Customer.require(phase == TurnPhase.WAITER, "Waiter acts only before Chef");
    waiter.act(state, task, customerId);
    phase = TurnPhase.CHEF;
  }

  public void chefAction(int customerId) {
    Customer.require(phase == TurnPhase.CHEF, "Chef acts after Waiter");
    chef.act(state, customerId);
    phase = TurnPhase.END;
  }

  public void endTurn() {
    Customer.require(phase == TurnPhase.END, "Complete staff choices first");
    state.finishEating();
    cashier.collectOldest(state);
    state.applyWaitingLoss();
    state.getHappyHour().endTurn();
    phase = TurnPhase.BETWEEN_TURNS;
  }
}
