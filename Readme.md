# Restaurant Rush - all three simplified stages + GUI + fixed seed

## Play on Windows

1. Extract the entire ZIP into one folder.
2. Double-click `PLAY_STAGE_1.bat`, `PLAY_STAGE_2.bat` or `PLAY_STAGE_3.bat`.
3. Leave **Original base choices** unticked, set **Seed = 42**, and click **New game**.
4. Open `guides/PLAY_AND_WIN.pdf` (or double-click `OPEN_GUIDE.bat`). It gives a tested winning action for every turn in every stage.

Java 11 or newer with desktop support is required. The prebuilt GUI JARs are included; no compiler is needed just to play. If a window does not open, run `java -jar stage-3/RestaurantRush_GUI.jar` in a terminal to see Java's error message.

## One parent folder, connected source

```text
RestaurantRush_All_Stages/
    PLAY_STAGE_1.bat
    PLAY_STAGE_2.bat
    PLAY_STAGE_3.bat
    OPEN_GUIDE.bat
    START_HERE.md
    CHANGES.md
    guides/
        PLAY_AND_WIN.pdf
        TURN_BY_TURN.md
        stage-N-seed-42.tsv
        stage-N-seed-43.tsv
    stage-1/
        src/       (simplified game classes AND GUI classes)
        tests/
        replays/
        RestaurantRush_GUI.jar
        RestaurantRush_CLI.jar
        open_gui.bat, run.bat, build.bat, run_cli.bat, test.bat
    stage-2/       (same self-contained structure)
    stage-3/       (same self-contained structure)
    verification/
    reference/
        RestaurantRush_Reviewed.zip  (your uploaded archive, unchanged)
```

Each GUI calls its own stage's simplified `GameController` and domain objects. It does not use a separate hidden game engine. Unlike the uploaded package, rebuilding does not depend on source under a separate `original` directory. Do not merge the three `src` folders: the stages intentionally reuse Java class names.

The launch name `DesktopUI` is retained as a compatibility adapter to `GuiMain`. The interface follows your photo and includes the seed field. Stage 1 has no VIP/Critic/Host/combo/Happy Hour features; Stage 2 adds VIP/Critic; Stage 3 adds Host/combo/Happy Hour.

## What "simplified" means

Ordinary loops and if statements replace stream pipelines, lambdas and method references in game source. Cashier selection, customer searches, sample-strategy searches and combo calculations use named, explicit steps. Formatting and local variable names are clearer. Encapsulation, inheritance, customer polymorphism, staff roles and menu/pricing abstractions remain intact.

No original source archive was overwritten. Original gameplay method contracts and rules are preserved, apart from the explicitly requested seeded ordinary food-selection option. Original deterministic food selection is still available in base mode and all unchanged original tests pass. The redesigned GUI is an optional boundary over the simplified code.

## Changing the seed

The fixed default is **42**, not the current time. For a new variation, type a whole-number seed in the GUI and click **New game**. The displayed active seed confirms what is running. Changing the field alone does not alter a game in progress.

Seeds 42 and 43 both have complete winning walkthroughs in the guide. Do not reuse one seed's table for a different seed: menu choices and preparation workload can change. Same seed, same stage and same manager choices reproduce the same result; different seeds can share some choices because the menu is small.

The seed changes Regular/VIP food choices, including Stage 3 combos. The Critic's Pasta preference, six arrival turns, staff/table counts, prices, waiting loss, payment timing, Happy Hour rules and victory thresholds stay unchanged. The manager still controls staff priorities and does not choose food.

Permanent default: edit `DEFAULT_SEED` in the stage's `src/GameSettings.java`, then use `build.bat` to rebuild JARs, or `run.bat` to compile and launch source. Each stage has its own setting so it remains independently runnable. CLI override:

```powershell
java -jar stage-3/RestaurantRush_GUI.jar --seed 2026
java -jar stage-3/RestaurantRush_CLI.jar --seed 2026 --demo victory
```

## Play successfully

For a verified first win, use seed 42 and follow the PDF's manual table. Each row means: Start turn, Host if Stage 3, Waiter, Chef, then Finish turn. `Order C2` means the GUI task **Take order** with customer C2. Each role gets one task, including Wait. The table gives cumulative paid/revenue/average after Finish turn.

| Stage, seed 42 | Paid | Revenue | Paid average | Ending |
| --- | --- | --- | --- | --- |
| 1 | 5 | $63.00 | 74.40 | Victory |
| 2 | 4 | $62.10 | 70.25 | Victory |
| 3 | 5 | $66.48 | 88.80 | Victory |

In Stage 3's verified walkthrough, activate Happy Hour **before starting turn 4**, not on turn 1. All games run the full 18 turns. Served count and paid count are different; an unpaid meal adds no revenue.

For an automatic example, press **Victory demo: next turn** eighteen times on a fresh seed-42 game. Each click plays one entire turn. It uses the same controller and strategy as the guide, not a forced ending. Other seeds can require different choices; the button name is not a guarantee of victory for every seed. **Defeat demo: next turn** waits each turn.

The PDF also explains general strategy, turn delays, choosing another seed, error recovery and the differences between stages. Editable Markdown and TSV guides are supplied. Seeded input replays can be run in the CLI:

```powershell
java -jar stage-3/RestaurantRush_CLI.jar --seed 42 --script stage-3/replays/seed-42-victory.txt
```

The command above assumes the terminal is in this parent folder. Inside `stage-3/`, use `--script replays/seed-42-victory.txt` instead.

## Original base mode

Tick **Original base choices** and click New game to use the original documented ID-based menu. Old `victory.txt` and `defeat.txt` replays are for this mode:

```powershell
java -jar stage-3/RestaurantRush_CLI.jar --base --script stage-3/replays/victory.txt
```

No-argument `Menu`, `GameState` and `GameController` constructors retain base behaviour for source/API compatibility and unchanged original tests. Constructors accepting `long seed` implement seeded play. Normal GUI/CLI launch uses the seeded constructor with 42 unless another seed or base mode is requested.

## Build and test

A full JDK 11+ is needed to compile.

- Within a stage, `run.bat` compiles that stage's local `src` and launches the GUI using javaw.
- `build.bat` rebuilds both the GUI and CLI JAR from that same local source.
- `run_cli.bat` compiles and launches the command-line game.
- `test.bat` runs original tests, seeded tests, GUI checks and both guide replays.
- From the parent folder, `VERIFY_ALL.bat` checks all stages.
- Corresponding `.sh` launch/build/test scripts are included for a graphical macOS/Linux desktop.

The original 26 scenarios / 915 assertions pass. Seed sweeps and GUI checks pass for all stages. All six guide tables were executed through real Swing buttons/dropdowns with every paid/revenue/average checkpoint verified; matching CLI input replays were also checked. Per-stage logs are in `stage-N/verification`, with a combined summary in `verification/`.

Validation used Java 21.0.13's compiler module with `-source 11 -target 11` because this environment lacks a standalone javac launcher and the Java 11 release symbol archive. GUI components were exercised/rendered headlessly on Swing's event-dispatch thread; Windows native-window and `.bat` execution were not tested here. The provided scripts use `javac --release 11` on a full JDK. This is compilation, component and behaviour validation, not a native Windows launch certification.

