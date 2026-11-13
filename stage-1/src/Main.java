import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/** Starts either a fixed-seed game or the original deterministic base game. */
public final class Main {
  private Main() {}

  public static void main(String[] args) {
    long seed = GameSettings.DEFAULT_SEED;
    boolean baseMode = false;
    boolean seedProvided = false;
    String demo = null;
    String script = null;
    try {
      for (int index = 0; index < args.length; index++) {
        String option = args[index];
        if (option.equals("--base") && !baseMode) {
          baseMode = true;
        } else if (option.equals("--seed") && !seedProvided && index + 1 < args.length) {
          seed = Long.parseLong(args[++index]);
          seedProvided = true;
        } else if (option.equals("--demo") && demo == null && index + 1 < args.length) {
          demo = args[++index];
          if (!demo.equals("victory") && !demo.equals("defeat")) {
            throw new IllegalArgumentException("Demo must be victory or defeat");
          }
        } else if (option.equals("--script") && script == null && index + 1 < args.length) {
          script = args[++index];
        } else {
          throw new IllegalArgumentException("Unknown, repeated or incomplete option: " + option);
        }
      }
      if (baseMode && seedProvided) {
        throw new IllegalArgumentException("Choose --base or --seed, not both");
      }
      if (demo != null && script != null) {
        throw new IllegalArgumentException("Choose --demo or --script, not both");
      }
    } catch (IllegalArgumentException exception) {
      System.err.println("Invalid arguments: " + exception.getMessage());
      System.err.println(
          "Usage: Main [--seed NUMBER | --base] [--demo victory|defeat | --script FILE]");
      System.exit(2);
      return;
    }
    System.out.println("Restaurant Rush - Stage 1 - base setting");
    if (baseMode) {
      System.out.println("Food mode: original deterministic base choices");
    } else {
      System.out.println("Food mode: seeded random choices; seed=" + seed);
    }
    System.out.println(
        "Targets after 18 turns: paid >= 4, revenue >= $45.00, paid satisfaction average >= 60");
    GameController game;
    if (baseMode) {
      game = new GameController();
    } else {
      game = new GameController(seed);
    }
    try (BufferedReader input = openInput(script)) {
      ConsoleUI ui = new ConsoleUI(input, System.out);
      ui.play(game, demo);
    } catch (IOException exception) {
      System.err.println("Game interrupted: " + exception.getMessage());
      System.exit(1);
    }
  }

  private static BufferedReader openInput(String script) throws IOException {
    if (script == null) {
      return new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    }
    return Files.newBufferedReader(Paths.get(script), StandardCharsets.UTF_8);
  }
}
