import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Desktop entry point. All game rules stay in the existing Stage 3 classes. */
public final class GuiMain {
  private GuiMain() {}

  public static void main(String[] args) {
    long selectedSeed = GameSettings.DEFAULT_SEED;
    boolean baseMode = false;
    boolean seedProvided = false;
    try {
      for (int index = 0; index < args.length; index++) {
        if (args[index].equals("--base") && !baseMode) {
          baseMode = true;
        } else if (args[index].equals("--seed") && !seedProvided && index + 1 < args.length) {
          selectedSeed = Long.parseLong(args[++index]);
          seedProvided = true;
        } else {
          throw new IllegalArgumentException(
              "Unknown, repeated or incomplete option: " + args[index]);
        }
      }
      if (baseMode && seedProvided) {
        throw new IllegalArgumentException("Choose --base or --seed, not both");
      }
    } catch (IllegalArgumentException exception) {
      System.err.println(exception.getMessage());
      System.err.println("Usage: GuiMain [--seed NUMBER | --base]");
      System.exit(2);
      return;
    }

    final long seed = selectedSeed;
    final boolean originalBaseMode = baseMode;
    SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            try {
              UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception exception) {
              // Swing's default look and feel is also fully usable.
            }
            JFrame frame = new JFrame("Restaurant Rush | Stage 3");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new GameWindow(seed, originalBaseMode));
            frame.setMinimumSize(new Dimension(1000, 760));
            frame.setSize(1180, 900);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
          }
        });
  }
}
