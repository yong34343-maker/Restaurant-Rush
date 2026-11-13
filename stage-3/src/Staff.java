/** Shared one-task-per-turn contract. Validation precedes consumeTask. */
public abstract class Staff {
  private boolean available = true;

  final void startTurn() {
    available = true;
  }

  public final boolean isAvailable() {
    return available;
  }

  protected final void checkAvailable() {
    Customer.require(available, "Staff task already used this turn");
  }

  protected final void consumeTask() {
    checkAvailable();
    available = false;
  }

  final void waitForTurn() {
    consumeTask();
  }
}
