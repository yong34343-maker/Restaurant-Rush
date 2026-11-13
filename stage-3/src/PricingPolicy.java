/** Customer discount is applied before the policy; the order stores the returned bill. */
public interface PricingPolicy {
  int quote(Customer customer, MenuItem item);
}
