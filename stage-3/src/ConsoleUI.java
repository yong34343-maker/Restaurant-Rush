import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

/** Boundary object: parses input and renders state; never changes game rules. */
public final class ConsoleUI {
  private final BufferedReader input;
  private final PrintStream output;
  private int eventsShown;

  public ConsoleUI(BufferedReader input, PrintStream output) {
    this.input = input;
    this.output = output;
  }

  public void play(GameController game, String demo) throws IOException {
    while (!game.isFinished()) {
      boolean activate =
          demo == null
              ? readHappyHour(game)
              : demo.equals("victory") && game.getState().getTurn() + 1 == 4;
      game.beginTurn(activate);
      display(game);
      eventsShown = 0;
      showNewEvents(game);
      if (demo != null) {
        DemoPolicy.choose(game, demo.equals("victory"));
        showNewEvents(game);
      } else {
        readHost(game);
        showNewEvents(game);
        readWaiter(game);
        showNewEvents(game);
        readChef(game);
        showNewEvents(game);
      }
      game.endTurn();
      showNewEvents(game);
      output.println("END: " + game.getState().result().summary());
    }
    output.println("FINAL: " + (game.getState().result().isVictory() ? "VICTORY" : "DEFEAT"));
  }

  /** Prints events recorded since the last call, so each choice shows its result at once. */
  private void showNewEvents(GameController game) {
    java.util.List<String> events = game.getState().getEvents();
    for (int i = eventsShown; i < events.size(); i++) output.println("  " + events.get(i));
    eventsShown = events.size();
  }

  private void display(GameController game) {
    GameState state = game.getState();
    output.println("\n=== TURN " + state.getTurn() + " / 18 ===");
    output.println(state.result().summary());
    output.print("Queue: ");
    for (Customer customer : state.getQueue()) output.print("C" + customer.getId() + " ");
    output.println();
    for (Table table : state.getTables())
      output.println(
          "Table "
              + table.getId()
              + ": "
              + (table.isFree() ? "free" : "C" + table.getOccupant().getId()));
    for (Customer customer : state.getCustomers()) {
      Order order = customer.getOrder();
      output.println(
          "C"
              + customer.getId()
              + " "
              + customer.getType()
              + " satisfaction="
              + customer.getSatisfaction()
              + " "
              + customer.getStatus()
              + " order="
              + (order == null
                  ? "none"
                  : order.getStatus()
                      + " "
                      + order.getItem().getName()
                      + " "
                      + order.getCompletedUnits()
                      + "/"
                      + order.getItem().getPreparationUnits()
                      + " bill="
                      + Money.dollars(order.getBillCents())));
    }
    output.println(
        "Happy Hour: "
            + state.getHappyHour().getRemaining()
            + " active turns remaining; used="
            + state.getHappyHour().isUsed());
    output.println("Host=" + game.getHost().isAvailable() + " [seat/wait]");
    output.println(
        "Available: Waiter="
            + game.getWaiter().isAvailable()
            + " [seat/order/serve/wait], Chef="
            + game.getChef().isAvailable()
            + " [cook/wait], Cashier="
            + game.getCashier().isAvailable()
            + " [automatic]");
  }

  private boolean readHappyHour(GameController game) throws IOException {
    if (game.getState().getHappyHour().isUsed()) return false;
    while (true) {
      output.print("Start turn " + (game.getState().getTurn() + 1) + ": Happy Hour on | off: ");
      String answer = line().trim();
      if (answer.equalsIgnoreCase("on")) return true;
      if (answer.equalsIgnoreCase("off")) return false;
      output.println("Invalid choice: use on or off. Try again.");
    }
  }

  private void readHost(GameController game) throws IOException {
    while (true) {
      output.print("Host> seat ID | wait: ");
      try {
        String[] words = line().trim().split("\\s+");
        int id;
        if (words[0].equalsIgnoreCase("wait") && words.length == 1) id = 0;
        else if (words[0].equalsIgnoreCase("seat")) id = parseId(words);
        else throw new IllegalArgumentException("Use seat ID or wait");
        game.hostAction(id);
        return;
      } catch (IllegalArgumentException ex) {
        output.println("Invalid choice: " + ex.getMessage() + ". Try again.");
      }
    }
  }

  private void readWaiter(GameController game) throws IOException {
    while (true) {
      output.print("Waiter> seat ID | order ID | serve ID | wait: ");
      String line = line();
      try {
        String[] words = line.trim().split("\\s+");
        WaiterTask task = WaiterTask.valueOf(words[0].toUpperCase(java.util.Locale.ROOT));
        int id = task == WaiterTask.WAIT ? 0 : parseId(words);
        if (task == WaiterTask.WAIT && words.length != 1)
          throw new IllegalArgumentException("Use wait alone");
        game.waiterAction(task, id);
        return;
      } catch (IllegalArgumentException ex) {
        output.println("Invalid choice: " + ex.getMessage() + ". Try again.");
      }
    }
  }

  private void readChef(GameController game) throws IOException {
    while (true) {
      output.print("Chef> cook ID | wait: ");
      try {
        String[] words = line().trim().split("\\s+");
        int id;
        if (words[0].equalsIgnoreCase("wait") && words.length == 1) id = 0;
        else if (words[0].equalsIgnoreCase("cook")) id = parseId(words);
        else throw new IllegalArgumentException("Use cook ID or wait");
        game.chefAction(id);
        return;
      } catch (IllegalArgumentException ex) {
        output.println("Invalid choice: " + ex.getMessage() + ". Try again.");
      }
    }
  }

  private int parseId(String[] words) {
    if (words.length != 2)
      throw new IllegalArgumentException("Give exactly one positive numeric customer ID");
    int id = Integer.parseInt(words[1]);
    if (id <= 0) throw new IllegalArgumentException("ID must be positive");
    return id;
  }

  private String line() throws IOException {
    String value = input.readLine();
    if (value == null) throw new IOException("Input ended before 18 turns were completed");
    return value;
  }
}
