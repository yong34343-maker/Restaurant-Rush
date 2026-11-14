# Stage 2 AI History

## Stage 2 Customer behaviour

### 1. What is Stage 2 about?

Stage 2 adds VIPCustomer and CriticCustomer to the existing flow. Regular, VIP and Critic customers lose 8, 5 and 12 satisfaction points per waiting turn respectively. VIP customers pay 90% of the item price. Critics always choose Pasta in this implementation. CustomerFactory creates C2 as VIP and C3 as Critic; the other arrivals remain Regular. The six-arrival schedule and victory conditions stay the same. [A Section 4.2; S2 customer classes and factory]

### 2. How should I mark classes that are new or changed compared with the Stage 1 diagram?

Use a legend such as NEW, MODIFIED and UNCHANGED, with attached notes or consistent visual highlighting. These are change annotations rather than standard behavioural UML stereotypes. VIPCustomer and CriticCustomer are new. Mark an existing class modified only if its source or displayed structure actually changed, and state why in the change table. Keep inherited members distinguishable from newly declared members.

### 3. How do I show VIPCustomer and CriticCustomer extending Customer and overriding its methods?

Use solid generalisation lines with hollow triangular arrowheads pointing to Customer. Show overridden operations in each subclass when they explain different behaviour. Both classes implement getType(), waitingLoss() and chooseFood(). VIPCustomer also overrides discountedPrice(). CriticCustomer inherits the full-price implementation; do not list it as a Critic override. Keep waitingLoss() protected (#). [S2 VIPCustomer.java and CriticCustomer.java]

### 4. How do I annotate polymorphism for chooseFood(), waitingLoss() and discountedPrice()?

Add a note at the actual call sites: GameState.takeOrder() calls customer.chooseFood(menu) and customer.discountedPrice(...); Customer.loseSatisfaction() calls waitingLoss(). Dispatch selects the implementation for the runtime customer type. chooseFood() and waitingLoss() are abstract in Customer. discountedPrice() has a concrete default, overridden by VIPCustomer. The controller needs no instanceof chain. [S2 Customer.java and GameState.java]

### 5. Which classes actually changed from Stage 1 to Stage 2, and why did GameController not change?

The source comparison adds VIPCustomer.java and CriticCustomer.java. Five existing files differ: CustomerFactory.java registers C2 and C3; Menu.java adds getPasta() for the Critic preference; Main.java changes the stage banner; GameWindow.java changes stage labels and help text; GuiMain.java changes stage text in its comment and window title.

GameController.java is identical between these snapshots. GameState.java and Customer.java are also identical. Existing calls use the Customer abstraction, so new customer behaviour is supplied through subclasses and factory creation. This is a concrete example of extending behaviour while preserving stable turn-control code. [S1 to S2 source comparison]

[Back to main README](../Readme.md)
