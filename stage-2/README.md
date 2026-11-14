# Restaurant Rush Stage 2

Stage 2 extends Stage 1 with customer-specific behaviour while retaining the same turn flow and victory conditions.

| Customer | Waiting loss per turn | Price | Food choice |
| --- | --- | --- | --- |
| Regular | 8 points | Full price | Seeded or original base rule |
| VIP | 5 points | 10% discount | Seeded or original base rule |
| Critic | 12 points | Full price | Always Pasta |

C2 is the VIP and C3 is the Critic. C1, C4, C5 and C6 are Regular customers. There are still six arrivals. Stage 2 has no Host, combo or Happy Hour.

## Quick start

Extract the entire project before launching. Java 11 or newer is required to run the supplied JARs. A full JDK 11 or newer is required to compile the source. The game works offline and has no external library dependencies.

From the project parent folder on Windows:

- Open [`PLAY_GUI.bat`](../PLAY_GUI.bat), then choose **2** for this stage.
- Open [`PLAY_CLI.bat`](../PLAY_CLI.bat), then choose **2** for this stage.

These two launchers run the prebuilt JARs. They do not rebuild edited source.

Alternatively, open a terminal **inside `stage-2`** and run:

```shell
java -jar RestaurantRush_GUI.jar
```

For the terminal version:

```shell
java -jar RestaurantRush_CLI.jar
```

## Build and run from source

On Windows, from this stage folder:

```bat
build.bat
run_cli.bat
```

`build.bat` compiles `src/*.java` with Java 11 compatibility and rebuilds both JARs. `run_cli.bat` recompiles and runs the CLI. To recompile and open the GUI, use:

```bat
run_gui.bat
```

On macOS or Linux:

```sh
sh build.sh
sh run_cli.sh
```

After building, launch the GUI with `java -jar RestaurantRush_GUI.jar`. A desktop display is required for the GUI. Windows scripts use `JAVA_HOME` when set; otherwise Java tools must be on `PATH`. The shell scripts use tools on `PATH`.

## Game objective

The game always runs **18 turns**. At the end of turn 18, all three conditions must be met:

| Target | Required result |
| --- | --- |
| Paid customers | At least 4 |
| Collected revenue | At least $45.00 |
| Average satisfaction of paid customers | At least 60 |

There are two tables. One customer arrives on turns **1, 4, 7, 10, 13 and 16**, starting with satisfaction 100. Customers choose their own food; the manager chooses staff priorities. Revenue includes only collected payments. The average uses satisfaction recorded when a customer pays and displays N/A until someone pays.

## Menu

| Item | Price before customer or Happy Hour discounts | Preparation units |
| --- | --- | --- |
| Salad | $9.00 | 1 |
| Burger | $15.00 | 1 |
| Pasta | $18.00 | 2 |

## Play a turn

1. Start the turn; any scheduled customer joins the queue.
2. Give the Waiter one `seat ID`, `order ID`, `serve ID` task or `wait`.
3. Give the Chef one `cook ID` task or `wait`.
4. Finish the turn. Eating, automatic payment and waiting loss are processed.

In the CLI, answer each staff prompt with a command such as `seat 1`, `order 1`, `serve 1`, `cook 1` or `wait`. Turn-end processing runs automatically after the staff choices. Customer IDs are positive numbers; type `1`, not `C1`, in a CLI command.

In the GUI, click **Start turn**, choose the current task and customer, then click **Perform action**. Follow each staff phase in order and perform **Finish turn** at the end. The **Victory demo: next turn** and **Defeat demo: next turn** buttons automate one turn per click.

### Timing and validation

- The Waiter acts before the Chef. An order completed by the Chef this turn becomes READY and can be served next turn at the earliest.
- A customer served on turn t becomes READY_TO_PAY on t+1 and can pay on t+2 at the earliest.
- The Cashier handles at most one eligible payment per turn, choosing the earliest READY_TO_PAY turn and then the lowest customer ID to break a tie.
- Tables remain occupied until payment or departure. Waiting customers and seated customers who have not been served lose satisfaction at turn end. At zero, they leave without paying and their table is freed.
- Invalid staff choices show an explanation and allow another choice without consuming the task.
- Serving one customer successfully does not end the game; the final result is checked after turn 18.

## Seeds and original base choices

The default seed is **42**. Use the same stage, seed and manager actions to reproduce a game:

```shell
java -jar RestaurantRush_CLI.jar --seed 42
java -jar RestaurantRush_GUI.jar --seed 43
```

In the GUI, change the seed and click **New game**. Keep **Original base choices** unchecked for seeded play. In original base mode, choices follow the documented customer-ID rule rather than randomness:

```shell
java -jar RestaurantRush_CLI.jar --base
java -jar RestaurantRush_GUI.jar --base
```

Use `--base` or `--seed`, never both. For Regular and VIP customers, seeded choices are based on the customer ID's position in the random sequence, so the staff action order does not reroll food. Critics, where present, always choose Pasta.

## Demos and walkthroughs

For repeatable original-base demos:

```shell
java -jar RestaurantRush_CLI.jar --base --demo victory
java -jar RestaurantRush_CLI.jar --base --demo defeat
```

The demo strategy is a sample strategy and does not guarantee victory for every seed. For manual seeded play, use the matching stage section in the [turn-by-turn guide](../guides/TURN_BY_TURN.md). Seed 42 and 43 guides are supplied; do not mix rows from different stages or seeds.

The CLI also supports `--script FILE` for a plain-text file containing the responses that would normally be typed at the prompts. Supply one response per line and enough responses to complete all 18 turns. `--script` and `--demo` cannot be combined. No ready-made replay `.txt` files or test launchers are included in this upload; the guide `.tsv` files are reference tables, not direct CLI input scripts.

### Prebuilt CLI checks

The supplied JAR was run with Java 21 using both base-mode demo commands above. The victory demo completed with:

| Paid | Revenue | Paid satisfaction average | Ending |
| --- | --- | --- | --- |
| 4 | $67.50 | 70.25 | VICTORY |

The defeat demo completed with 0 paid customers, $0.00 revenue and N/A average, ending in DEFEAT. These checks exercised the supplied CLI JARs; Windows batch launchers, GUI interaction and a source rebuild were not executed during this documentation update.

## Code responsibilities

| Role | Main classes |
| --- | --- |
| Boundary | `ConsoleUI`, `GameWindow` collect decisions and display state |
| Control | `GameController` coordinates staff actions and turn phases |
| Domain state and behaviour | `GameState`, `Customer`, `Order`, `Table`, `Menu` and staff classes |
| Entry points | `Main` starts the CLI; `GuiMain` starts the Swing GUI |
| Reproducibility and money | `GameSettings`, `Menu`, `Money` |

The CLI and GUI use the same controller and domain classes. Game rules are separate from input and display code.

## Stage changes

Compared with Stage 1:

| File | Change |
| --- | --- |
| `VIPCustomer.java` | New subclass for VIP waiting loss and discounted pricing |
| `CriticCustomer.java` | New subclass with higher waiting loss and a Pasta preference |
| `CustomerFactory.java` | Creates VIP C2 and Critic C3 |
| `Menu.java` | Adds `getPasta()` for the Critic preference |
| `Main.java`, `GuiMain.java`, `GameWindow.java` | Update stage labels and presentation text |

`GameController.java`, `GameState.java` and `Customer.java` are unchanged from Stage 1 in this upload. Calls through the `Customer` abstraction select the subclass behaviour without adding customer-type checks to the controller.

## Files in this stage

| Path | Purpose |
| --- | --- |
| `src/` | Java source for this independently runnable stage |
| `build/` | Compiled classes; build scripts regenerate them |
| `RestaurantRush_CLI.jar` | Prebuilt terminal game |
| `RestaurantRush_GUI.jar` | Prebuilt Swing game |
| `build.bat`, `build.sh` | Compile and package both JARs |
| `run_cli.bat`, `run_cli.sh` | Compile and run the CLI |
| `run_gui.bat` | Compile and launch the Windows GUI |

## Troubleshooting

- **Java is not recognised:** check `java -version`, then check `PATH` and any `JAVA_HOME` setting.
- **javac is not recognised:** install or select a full JDK to compile, or run the prebuilt JAR with Java 11 or newer.
- **Edited code does not appear:** rebuild before using a JAR or the parent `PLAY_*.bat` launchers.
- **Game files cannot be found:** extract the ZIP and keep the parent launchers beside the stage folders.
- **GUI will not open:** run `java -jar RestaurantRush_GUI.jar` from a terminal to see the error message; ensure a desktop display is available.

[Project overview](../Readme.md) · [Turn-by-turn walkthroughs](../guides/TURN_BY_TURN.md)
