# Stage 1

All game and Swing GUI source is together in src/.

- open_gui.bat: launch the prebuilt desktop GUI (Java 11+).
- run.bat / run_gui.bat: compile local source and launch the GUI (full JDK 11+).
- build.bat: rebuild GUI and CLI JARs from this folder only.
- run_cli.bat: compile and play in the terminal.
- test.bat: original tests, seed tests, Swing checks and both winning guide replays.

Default seed: 42. Change it in the GUI and press New game. The GUI and CLI use the SAME controller/domain classes.

Manual winning instructions: ../guides/PLAY_AND_WIN.pdf, Stage 1, seed 42 or 43. The TSV guide and matching replays are supplied.

Original victory.txt / defeat.txt scripts require --base. Seeded scripts use --seed, e.g.:

java -jar RestaurantRush_CLI.jar --seed 42 --script replays/seed-42-victory.txt

Stage 1 can compile and launch alone. The two guide-replay tests expect the adjacent ../guides folder in the parent package. macOS/Linux have corresponding .sh scripts.

## Stage-1 AI history

[Read the Stage 1 AI History](../ai-history/Stage-1.md)

[All stage reviews](../Readme.md)
