# Restaurant Rush

A turn-based restaurant management game for the SC2002 project, written in Java. Manage seating, orders, cooking and service across **18 turns**, using either an interactive desktop GUI or the terminal.

## Quick start

1. Install **Java 11 or newer**. The GUI needs Java with desktop support. A full JDK is only needed if you want to compile or edit the source.
2. On the GitHub repository page, select **Code → Download ZIP**.
3. Right-click the downloaded ZIP and choose **Extract All**. Open the extracted repository folder; do not run the game from inside the ZIP.
4. Double-click the launcher for your preferred game interface:

   | Launcher | Interface |
   | --- | --- |
   | `PLAY_GUI.bat` | Graphical desktop window with buttons and dropdowns |
   | `PLAY_CLI.bat` | Terminal game controlled by typed commands |

5. When the launcher menu appears, press **1**, **2** or **3** to choose your stage, or **Q** to quit. The stage starts immediately after your selection.
6. Both launchers use **seed 42** by default. In the GUI, leave **Original base choices** unticked. If you change the seed, click **New game** to apply it.
7. Open [guides/PLAY_AND_WIN.pdf](guides/PLAY_AND_WIN.pdf) and follow the walkthrough for your stage and seed. For CLI play, translate each guide action using the [terminal controls](#terminal-controls) below.

**Launcher location:** keep `PLAY_GUI.bat` and `PLAY_CLI.bat` in the main repository folder, beside `stage-1`, `stage-2`, `stage-3` and `guides`. These two launchers replace the previous three stage-specific launchers and run the prebuilt JARs without compiling.

To play a different stage, run the launcher again and choose another stage. The CLI pauses after the game finishes so you can read the result. If Java fails to launch, the launchers keep the error visible.

### Choose a seed when launching

Open **Command Prompt** in the repository folder and run one of these commands, then choose your stage from the menu:

```bat
PLAY_GUI.bat --seed 43
PLAY_CLI.bat --seed 43
```

For a complete automatic terminal demonstration at seed 42:

```bat
PLAY_CLI.bat --seed 42 --demo victory
```

The GUI launcher accepts `--seed NUMBER` or `--base`. The CLI launcher also accepts `--demo victory` or `--demo defeat`. Double-clicking either launcher starts the normal seed-42 game after stage selection.

If a launcher is unavailable or fails, use one of the direct launch methods below.

## What is different in each stage?

| Stage | Features | Manual action order each turn |
| --- | --- | --- |
| 1 | Regular customers; Salad, Burger and Pasta | Waiter → Chef → Finish turn |
| 2 | Adds VIP and Critic customers with different satisfaction loss and pricing behaviour | Waiter → Chef → Finish turn |
| 3 | Adds an independent Host, Combo Meal and a one-use Happy Hour | Host → Waiter → Chef → Finish turn |

Each stage is a separate game. You can launch any stage directly; you do not need to clear an earlier stage first.

## How to win

Complete all **18 turns** and meet **all three** targets:

| Target | Required result |
| --- | --- |
| Paid customers | At least **4** |
| Revenue | At least **$45.00** |
| Average satisfaction of paid customers | At least **60** |

Serving food alone does not count as payment. Revenue and the satisfaction average are recorded when customers pay. Continue until the final result appears, even if you reach the targets earlier.

## How to use the guides to pass the game

The [guides folder](guides/) contains the playing guides. Start with [PLAY_AND_WIN.pdf](guides/PLAY_AND_WIN.pdf): it explains the controls and provides turn-by-turn walkthroughs for seeds **42** and **43**. You can also read [TURN_BY_TURN.md](guides/TURN_BY_TURN.md) on GitHub, or open the matching `stage-N-seed-42.tsv` or `stage-N-seed-43.tsv` file as a table.

### 1. Match the walkthrough settings

1. Choose the correct **stage and seed** in the guide.
2. **GUI:** untick **Original base choices**, enter the walkthrough seed and click **New game**. Check the displayed active seed. Editing the field alone does not change a running game.
3. **CLI:** double-click `PLAY_CLI.bat` for seed 42, or run `PLAY_CLI.bat --seed 43` for seed 43. Select the matching stage at the launcher menu. Check the seed printed at startup.
4. Follow the guide from turn 1. Use a fresh manual game rather than switching from a partially played demo.

The seed controls ordinary customers' food choices. The same seed reproduces those choices; customer arrivals and all other rules are not fully randomised. A walkthrough for a different seed or original-base mode may produce a different result.

### 2. Treat each guide row as one complete turn

1. **Stage 3 only:** apply any Happy Hour instruction **before** starting the turn. For the **seed-42 Stage 3 walkthrough**, activate it immediately before **turn 4**. For other walkthroughs, follow their own timing.
2. Click **Start turn**.
3. In Stage 3, perform the guide's **Host** action first. In Stages 1 and 2, start with the **Waiter**.
4. Select the required **Task** and **Customer**, then click **Perform action** once for that staff role.
5. Perform the **Chef** action in the same way.
6. At the final phase, use **Finish turn**. This processes eating, automatic payment, waiting satisfaction loss and, in Stage 3, Happy Hour expiry.
7. Compare your cumulative **Paid**, **Revenue** and **Average** with that row in the guide before continuing.

| Guide instruction | What to select in the GUI |
| --- | --- |
| `Seat C2` | Task: **Seat**; Customer: **C2** |
| `Order C2` | Task: **Take order**; Customer: **C2** |
| `Serve C2` | Task: **Serve**; Customer: **C2** |
| `Cook C2` | Task: **Cook**; Customer: **C2** |
| `Wait` | Task: **Wait**; no customer is needed |

**Example: Stage 1, seed 42, turn 2.** Click **Start turn**, have the Waiter **Take order** for **C1**, have the Chef **Cook** for **C1**, then **Finish turn**. Do not serve C1 yet: this Pasta order needs another cooking action on turn 3.

If your checkpoint differs, restart the walkthrough with the same settings: click **New game** in the GUI, or close and relaunch the CLI. Later rows assume that every earlier row was followed exactly.

### 3. Check the expected ending

Following the seed-42 walkthroughs gives these final results:

| Stage | Paid customers | Revenue | Paid satisfaction average | Result |
| --- | --- | --- | --- | --- |
| 1 | 5 | $63.00 | 74.40 | Victory |
| 2 | 4 | $62.10 | 70.25 | Victory |
| 3 | 5 | $66.48 | 88.80 | Victory |

### Learn by watching the built-in demo

For a guided example, start a fresh seed-42 game and click **Victory demo: next turn** **18 times**. Each click performs one whole turn, including its staff actions and end-of-turn processing. The Stage 3 victory demo handles its own Happy Hour activation.

Use **New game** before switching back to a manual walkthrough. The demo is a sample strategy and is **not guaranteed to win every seed**. **Defeat demo: next turn** demonstrates an unsuccessful strategy.

## Gameplay tips

- There are only **two tables**. A table stays occupied until its customer pays or leaves.
- Each staff role gets one action per turn. Choose **Wait** when that role has no useful legal action.
- Customers choose their own food when the Waiter takes an order; you do not select their dish manually.
- The Chef completes one preparation unit per action. Check preparation progress: some orders need several cooking actions.
- The Waiter acts before the Chef. Food that becomes **READY** this turn can only be served on a later turn.
- A customer served on turn `t` becomes **READY_TO_PAY** on `t + 1` and can pay on `t + 2`. The Cashier collects payment automatically; allow enough time before turn 18.
- Waiting reduces satisfaction. In Stage 2 and Stage 3, Critics lose satisfaction faster than regular customers, while VIP customers receive a discount.
- Stage 3 Happy Hour can be used **once**, lasts **two turns**, restores satisfaction and applies an additional discount to orders placed while active. The bill is locked when an order is taken, so activation timing affects both satisfaction and revenue.

## Other ways to run the game

The following commands are run from the **repository root**. Replace `stage-1` with `stage-2` or `stage-3` to play another stage. Each stage has its own source and build output; compile stages separately.

### Run a prebuilt JAR — Windows, macOS or Linux

These commands need Java 11+ but do not require `javac`:

```sh
# Desktop GUI (requires a graphical desktop)
java -jar stage-1/RestaurantRush_GUI.jar --seed 42

# Interactive terminal game
java -jar stage-1/RestaurantRush_CLI.jar --seed 42

# Automatic terminal victory demonstration
java -jar stage-1/RestaurantRush_CLI.jar --seed 42 --demo victory
```

### Compile and launch on Windows

With a full JDK 11+ installed, double-click `run_gui.bat` inside the stage folder, or run these commands in **Command Prompt**:

```bat
stage-1\run_gui.bat --seed 42
stage-1\run_cli.bat --seed 42
```

Each command above launches a separate game. To rebuild both JAR files after editing source:

```bat
stage-1\build.bat
```

### Compile on macOS or Linux

With a full JDK 11+ installed:

```sh
cd stage-1
sh build.sh
java -jar RestaurantRush_GUI.jar --seed 42
```

For the terminal instead, run `sh run_cli.sh --seed 42` from that stage folder.

### Terminal controls

Enter the action when prompted. Use the numeric customer ID: type `1`, not `C1`.

| Prompt | Accepted actions |
| --- | --- |
| Host, Stage 3 only | `seat 1` or `wait` |
| Waiter | `seat 1`, `order 1`, `serve 1` or `wait` |
| Chef | `cook 1` or `wait` |
| Happy Hour, Stage 3 only, before it has been used | `on` or `off` |

In the terminal, end-of-turn processing happens automatically after the Chef action. There is no separate `Start turn` or `Finish turn` command to type.

**Example: Stage 1, seed 42.** For turn 1, enter `seat 1` at the Waiter prompt and `wait` at the Chef prompt. For turn 2, enter `order 1` and then `cook 1`. Compare the printed end-of-turn totals with the guide.

**Stage 3, seed-42 walkthrough:** answer `off` before turns 1, 2 and 3, then `on` before turn 4. After Happy Hour has been used, the game stops asking about activation. Each turn, enter the guide's Host action, then Waiter action, then Chef action.

### Change the seed or use original-base mode

- **GUI:** change **Seed**, then click **New game**.
- **Terminal/JAR:** pass `--seed 43` or another integer.
- **Source default:** edit `DEFAULT_SEED` in that stage's `src/GameSettings.java`, then recompile. Rebuild the JARs if you launch with `java -jar`.
- **Original-base mode:** tick **Original base choices** and click **New game**, or launch with `--base` instead of `--seed 42`.

Do not combine `--base` and `--seed`. Changing the seed does not guarantee victory; use the matching guide or adapt your strategy.

## Troubleshooting

| Problem | What to do |
| --- | --- |
| `java` is not recognised | Install Java 11+ and make its `bin` folder available on `PATH`. Reopen the terminal and check `java -version`. |
| `javac` is not recognised / compilation fails because no compiler is installed | Install a full JDK 11+ and check `javac -version`, or use the prebuilt JAR commands. |
| Launcher uses an old Java installation | Check `JAVA_HOME` and `PATH`. The root launchers prefer `JAVA_HOME\bin\java.exe` when it exists; otherwise they use `java` from `PATH`. |
| Launcher cannot find the game JAR | Put both launchers beside the stage folders and extract the full repository. Keep the JAR filenames unchanged. |
| Double-clicking a JAR does nothing | Open a terminal in the repository folder and use `java -jar stage-1/RestaurantRush_GUI.jar` to see any error. |
| `UnsupportedClassVersionError` | Check which Java installation the launcher uses and select a compatible newer runtime. The source build scripts target Java 11. |
| GUI cannot open in a server/headless environment | Run `RestaurantRush_CLI.jar`, or use a computer with a graphical desktop and desktop-capable Java. |
| A button is disabled or no customer can be selected | Check the current phase and selected task. Only eligible customers are listed. Choose another legal task or **Wait**. |
| Both tables are occupied | Wait for payment or departure, or perform another legal action. An invalid action does not consume the staff action. |
| Result differs from the guide | Check the stage, active seed, original-base setting, action order and Stage 3 Happy Hour timing. Restart from turn 1. |
| Seed changed but food choices did not update | Click **New game** after editing the seed field. |
| Guide link is missing locally | Download the full repository, including `guides/`, and keep it beside the stage folders. |

## Repository contents

| Location | Purpose |
| --- | --- |
| `PLAY_GUI.bat` | Choose Stage 1, 2 or 3 and launch the graphical game |
| `PLAY_CLI.bat` | Choose Stage 1, 2 or 3 and launch the terminal game |
| `guides/` | Playing instructions and seed-specific winning walkthroughs |
| `stage-1/`, `stage-2/`, `stage-3/` | Separate runnable versions of the game |
| `stage-N/src/` | Java game logic and Swing GUI source for that stage |
| `stage-N/build/` | Compiled classes; recreated by the build scripts |
| `stage-N/RestaurantRush_GUI.jar` | Prebuilt desktop version |
| `stage-N/RestaurantRush_CLI.jar` | Prebuilt terminal version |
| `stage-N/run_gui.bat`, `stage-N/run_cli.bat` | Compile source and launch on Windows |
| `stage-N/build.bat`, `stage-N/build.sh` | Rebuild the stage's GUI and CLI JARs |

Here, `stage-N` means `stage-1`, `stage-2` or `stage-3`. The game uses Java's standard libraries, including Swing; no external library, database, Maven or Gradle setup is required.


Validation used Java 21.0.13's compiler module with `-source 11 -target 11` because this environment lacks a standalone javac launcher and the Java 11 release symbol archive. GUI components were exercised/rendered headlessly on Swing's event-dispatch thread; Windows native-window and `.bat` execution were not tested here. The provided scripts use `javac --release 11` on a full JDK. This is compilation, component and behaviour validation, not a native Windows launch certification.

