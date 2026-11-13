public final class GameResult {
  private final int paid, revenueCents, served, departed, turns;
  private final double average;

  GameResult(int paid, int revenueCents, int served, int departed, int turns, double average) {
    this.paid = paid;
    this.revenueCents = revenueCents;
    this.served = served;
    this.departed = departed;
    this.turns = turns;
    this.average = average;
  }

  public int getPaid() {
    return paid;
  }

  public int getRevenueCents() {
    return revenueCents;
  }

  public double getAverage() {
    return average;
  }

  public boolean isVictory() {
    return turns == 18 && paid >= 4 && revenueCents >= 4500 && average >= 60;
  }

  public String summary() {
    return "served="
        + served
        + ", paid="
        + paid
        + ", unhappy departures="
        + departed
        + ", revenue="
        + Money.dollars(revenueCents)
        + ", paid average="
        + (paid == 0 ? "N/A" : String.format(java.util.Locale.ROOT, "%.2f", average))
        + ", turns="
        + turns
        + "/18";
  }
}
