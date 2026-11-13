/** All stored money is integer cents; percentage arithmetic rounds half up. */
public final class Money {
  private Money() {}

  public static int percent(int cents, int percentage) {
    if (cents < 0 || percentage < 0) throw new IllegalArgumentException("Negative amount");
    return Math.toIntExact((cents * (long) percentage + 50) / 100);
  }

  public static String dollars(int cents) {
    return String.format(java.util.Locale.ROOT, "$%d.%02d", cents / 100, cents % 100);
  }
}
