/** Six fixed arrivals, numbered by arrival order. */
public final class CustomerFactory {
  public Customer create(int id) {
    return new RegularCustomer(id);
  }
}
