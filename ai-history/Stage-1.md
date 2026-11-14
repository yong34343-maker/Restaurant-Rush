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

# Stage 1 AI History

### 1. What is Stage 1 about?

 Stage 1 is a complete baseline restaurant game with Regular customers, Salad, Burger and Pasta. The manager assigns the Waiter one seating, ordering or serving task per turn, and assigns the Chef one preparation unit. The Cashier automatically collects one eligible payment. The game includes satisfaction loss, departures, table release and both endings.

The base game lasts 18 turns, with two tables and six arrivals on turns 1, 4, 7, 10, 13 and 16. Victory needs at least four paid customers, $45 revenue and average paid-customer satisfaction of at least 60. A GUI is optional; the CLI remains required. [A Sections 2 to 4]

### 2. What does the brief require a class diagram to show in Section 5, and how do I cover each item?

Show principal entities, necessary boundary and control classes, relevant enums, key attributes and operations with visibility, inheritance, and associations with relevant multiplicities. Add meaningful notes on encapsulation, polymorphism and composition. For example, show Customer private state and controlled transitions; Customer subclasses; and the controller owning its GameState.

Section 5 of the assignment asks for the final Stage 3 diagram. The briefing also explicitly asks for one class diagram per stage. Prepare all three, matching each saved version. Include helpers only where they explain important responsibilities or dependencies. [A Section 5; B page 5]

### 3. Which Stage 1 classes are boundary, control and entity?

ConsoleUI and GameWindow are boundary classes. GameController is the main control class; DemoPolicy supplies an optional automated manager strategy. GameState, Customer, RegularCustomer, Table, Order, Menu and Dish hold domain state or behaviour. Waiter, Chef and Cashier are domain staff classes that perform tasks; Staff is their abstract superclass. CustomerFactory creates customers. GameResult represents the outcome and Money is a utility.

These are responsibility classifications, not Java keywords. Main, GuiMain and DesktopUI are launch or setup classes. Avoid calling a launcher the boundary that actually processes gameplay input. [S1 corresponding source files]

### 4. Should GameController to GameState be composition or aggregation, and why?

Composition is a reasonable design choice: each controller constructs one GameState, keeps the reference private and final, and offers no operation to replace it. Put the filled diamond at GameController and multiplicity 1 at GameState. Describe this as ownership of a game session. The Java final modifier alone does not prove composition, and returning the state through getState() does not transfer ownership. [S1 GameController.java]


### 5. Staff is abstract with final and protected methods. How do I show that with UML visibility symbols?

Use an italic class name or {abstract} for Staff. Visibility symbols are + public, - private, # protected and ~ package visibility. Use {leaf} for an operation that cannot be overridden, or a clearly defined {final} note. Final is separate from visibility.

Examples from the code: - available: boolean; + isAvailable(): boolean {leaf}; # checkAvailable(): void {leaf}; # consumeTask(): void {leaf}; ~ startTurn(): void {leaf}; ~ waitForTurn(): void {leaf}. Staff is abstract even though it declares no abstract operations. [S1 Staff.java]

### 6. Customer.discountedPrice() exists in Stage 1 but only returns the full price. Should it appear in the Stage 1 diagram?

Yes. Show + discountedPrice(cents: int): int because it exists and participates in order pricing through GameState.takeOrder(). It is a concrete operation returning its input in Stage 1. Do not mark it abstract or claim that Stage 1 already implements the VIP discount. A general extension point can exist before a later customer type is added. [S1 Customer.java and GameState.java]

### 7. What multiplicities should GameState have to Table and Customer?

GameState to Table has exactly 2 at the Table end and 1 at the GameState end. GameState to Customer has 0..6 at the Customer end and 1 at the GameState end within a game. Tables and created customers are owned parts of the session; composition is defensible when that ownership is stated.

The customers list retains all arrivals, including customers who paid or left. The queue is a separate role containing a subset of those same customers; it is not another set of owned customer objects. Label the associations by role. A Table has an occupant multiplicity of 0..1; a Customer occupies at most one table. [S1 GameState.java and Table.java]

### 8. Do the enums OrderStatus, CustomerStatus, TurnPhase and WaiterTask need to be drawn?

Include them because they explain legal states and manager commands. Draw each with the «enumeration» label and its actual constants. Link their use to Order.status, Customer.status, GameController.phase and the WaiterTask parameter respectively. A type dependency is enough; do not give an enum a composition diamond simply because it appears in a field. [S1 enum files and users of those enums]

[Back to README](../../README.md)

