import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GameState {
  private final Menu menu;

  /** Original deterministic base setting, used by the original tests/replays. */
  public GameState() {
    menu = new Menu();
  }

  /** Seeded game: only ordinary food choices vary. */
  public GameState(long seed) {
    menu = new Menu(seed);
  }

  private final HappyHourPolicy happyHour = new HappyHourPolicy();
  private final PricingPolicy pricingPolicy = happyHour;

  public HappyHourPolicy getHappyHour() {
    return happyHour;
  }

  private final CustomerFactory factory = new CustomerFactory();

  private final List<Customer> customers = new ArrayList<>();

  private final List<Customer> queue = new ArrayList<>();

  private final List<Table> tables = java.util.Arrays.asList(new Table(1), new Table(2));

  private final List<String> events = new ArrayList<>();
  private int turn;
  private int revenueCents;
  private int paidCount;
  private int paidSatisfactionTotal;
  private int servedCount;
  private int departures;

  public int getTurn() {
    return turn;
  }

  public Menu getMenu() {
    return menu;
  }

  public List<Customer> getCustomers() {
    return Collections.unmodifiableList(customers);
  }

  public List<Customer> getQueue() {
    return Collections.unmodifiableList(queue);
  }

  public List<Table> getTables() {
    return Collections.unmodifiableList(tables);
  }

  public List<String> getEvents() {
    return Collections.unmodifiableList(events);
  }

  public Customer customer(int id) {
    for (Customer customer : customers) {
      if (customer.getId() == id) {
        return customer;
      }
    }
    throw new IllegalArgumentException("No customer C" + id);
  }

  void event(String text) {
    events.add(text);
  }

  void beginTurn() {
    turn++;
    events.clear();
    if ((turn - 1) % 3 == 0) {
      Customer customer = factory.create((turn - 1) / 3 + 1);
      customers.add(customer);
      queue.add(customer);
      event("Arrival C" + customer.getId() + " " + customer.getType() + " satisfaction=100");
    }
  }

  public boolean hasFreeTable() {
    for (Table table : tables) {
      if (table.isFree()) {
        return true;
      }
    }
    return false;
  }

  void validateSeat(Customer customer) {
    Customer.require(
        customer.getStatus() == CustomerStatus.WAITING && queue.contains(customer),
        "Customer is not waiting");
    Customer.require(hasFreeTable(), "Both tables are occupied");
  }

  void seat(Customer customer) {
    validateSeat(customer);
    Table freeTable = null;
    for (Table table : tables) {
      if (table.isFree()) {
        freeTable = table;
        break;
      }
    }
    // validateSeat() has already checked that a free table exists.

    customer.seat();
    queue.remove(customer);
    freeTable.occupy(customer);
    event("Seats C" + customer.getId() + " at table " + freeTable.getId());
  }

  void takeOrder(Customer customer) {
    MenuItem item = customer.chooseFood(menu);
    int billCents = pricingPolicy.quote(customer, item);
    Order order = new Order(item, billCents);
    customer.placeOrder(order);
    event(
        "C"
            + customer.getId()
            + " chooses "
            + item.getName()
            + ", locked bill="
            + Money.dollars(customer.getOrder().getBillCents())
            + ", units="
            + item.getPreparationUnits());
  }

  void recordServed() {
    servedCount++;
  }

  void finishEating() {
    for (Customer customer : customers) {
      CustomerStatus before = customer.getStatus();
      customer.finishEating(turn);
      if (before == CustomerStatus.EATING && customer.getStatus() == CustomerStatus.READY_TO_PAY)
        event("C" + customer.getId() + " finishes eating: READY_TO_PAY (eligible next turn)");
    }
  }

  void collectPayment(Customer customer) {
    customer.pay(turn);
    revenueCents += customer.getOrder().getBillCents();
    paidCount++;
    paidSatisfactionTotal += customer.getSatisfaction();
    release(customer);
    event(
        "Cashier collects "
            + Money.dollars(customer.getOrder().getBillCents())
            + " from C"
            + customer.getId()
            + ": PAID");
  }

  private void release(Customer customer) {
    queue.remove(customer);
    for (Table table : tables) {
      if (table.getOccupant() == customer) {
        table.release();
      }
    }
  }

  void applyWaitingLoss() {
    for (Customer customer : customers) {
      CustomerStatus before = customer.getStatus();
      customer.loseSatisfaction();
      if (before != CustomerStatus.LEFT && customer.getStatus() == CustomerStatus.LEFT) {
        departures++;
        release(customer);
        event("C" + customer.getId() + " leaves unhappy without paying");
      }
    }
  }

  public GameResult result() {
    return new GameResult(
        paidCount,
        revenueCents,
        servedCount,
        departures,
        turn,
        paidCount == 0 ? Double.NaN : paidSatisfactionTotal / (double) paidCount);
  }
}
