public final class CustomerFactory {
  public Customer create(int id) {
    if (id == 2) return new VIPCustomer(id);
    if (id == 3) return new CriticCustomer(id);
    return new RegularCustomer(id);
  }
}
