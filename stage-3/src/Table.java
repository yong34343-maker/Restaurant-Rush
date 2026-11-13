public final class Table {
  private final int id;
  private Customer occupant;

  public Table(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public Customer getOccupant() {
    return occupant;
  }

  public boolean isFree() {
    return occupant == null;
  }

  void occupy(Customer customer) {
    Customer.require(isFree(), "Table is occupied");
    occupant = customer;
  }

  void release() {
    occupant = null;
  }
}
