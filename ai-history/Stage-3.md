# Stage 3 AI History

### 1. What is Stage 3 about?

Stage 3 adds Host, ComboMeal and Happy Hour. The Host seats at most one waiting customer before the Waiter acts; the Waiter can still seat. A combo has two menu-item components, adds their preparation units and costs 90% of their combined price. Happy Hour can be activated once, lasts two turns, discounts newly placed orders by 20%, and restores 15 satisfaction at turn start to customers waiting for a table or food, capped at 100. VIP pricing is applied first. [A Section 4.3; S3 implementation]

### 2. ComboMeal implements MenuItem and also holds two MenuItems. How do I show both relationships?

Draw a dashed realisation line with a hollow triangle pointing from ComboMeal to MenuItem. Separately draw an association labelled components from ComboMeal to MenuItem, with multiplicity 2 at the component end.

A plain association is the safest description of this implementation: Menu creates Salad and Pasta and shares those same objects with the combo. The combo owns its fixed list of references, but the component items are not exclusive parts of that combo. Shared aggregation may be used if its meaning is defined; a filled composition diamond would imply stronger ownership than the code supports. [S3 ComboMeal.java and Menu.java]

### 3. How do I draw the PricingPolicy interface and HappyHourPolicy implementing it?

Draw PricingPolicy with «interface» and + quote(customer: Customer, item: MenuItem): int. Draw a dashed realisation line from HappyHourPolicy with its hollow triangle pointing at PricingPolicy. In GameState, show - pricingPolicy: PricingPolicy and - happyHour: HappyHourPolicy. Both references point to the same HappyHourPolicy instance, so do not imply that two policy objects are created. [S3 PricingPolicy.java, HappyHourPolicy.java and GameState.java]

### 4. Should Host be drawn as another Staff subclass?

Yes. Host extends Staff, alongside Waiter, Chef and Cashier. Its operation is ~ act(state: GameState, customerId: int): void. It uses the inherited availability checks and task consumption. Show GameController owning one Host and the Stage 3 phase order HOST, WAITER, CHEF, END, then BETWEEN_TURNS. [S3 Host.java, Staff.java and GameController.java]

### 5. Which use case covers arrival, ordering, Waiter and Chef, READY then SERVED, eating, payment, the result check and an extension behaviour? Why VIP during Happy Hour?

Use a Stage 3 scenario following C2, the VIP, from arrival on turn 4 through payment, then continue the game to the final turn-18 result. Activate Happy Hour at the start of turn 4 and take C2's order while it is active. This shows customer polymorphism and the pricing policy in one flow. State the starting conditions, including a free table and the selected food mode or seed.

Show a manager choice that changes the outcome: when C2 has a READY order, serve now or wait. Waiting delays service and causes another waiting loss, subject to abandonment rules. Show the resulting satisfaction or timing difference. A successful C2 payment alone does not prove overall victory; the result also depends on all other customers and decisions. [A Section 5; S3 controller and customer flow]

### 6. How do I show the manager serve-or-wait choice as an alt frame?

At the Waiter phase, use an alt fragment with guards [manager chooses serve and C2 order is READY] and [manager chooses wait]. In the serve operand, ConsoleUI calls GameController.waiterAction(SERVE, 2), which calls Waiter.act(state, SERVE, 2), then Customer.serve(turn), Order.serve() and GameState.recordServed(). In the wait operand, use waiterAction(WAIT, 0) and Waiter.act(state, WAIT, 0).

The manager supplies the decision through the CLI; the domain Customer does not make it. Draw only one selected branch per execution. Later turn-end processing applies waiting loss in the wait branch. [S3 ConsoleUI.java and Waiter.java]

### 7. How do I show readyToPayTurn < turn, the loop over turns and the opt frame for arrivals?

Wrap gameplay in loop [!game.isFinished()]. Inside GameState.beginTurn(), use opt [(turn - 1) % 3 == 0] for CustomerFactory.create((turn - 1) / 3 + 1), followed by customer creation and queue insertion.

At endTurn(), show finishEating(), Cashier.collectOldest(state), applyWaitingLoss() and HappyHourPolicy.endTurn() in that order. In the Cashier search, use [status == READY_TO_PAY and readyToPayTurn < turn]; among eligible customers choose the earliest readyToPayTurn, breaking ties by customer ID. No eligible customer means no payment.

Customer.finishEating(turn) separately requires servedTurn < turn. A customer served on turn t becomes READY_TO_PAY on t+1 and can pay on t+2 at the earliest. After the outer loop, show GameState.result() and GameResult.isVictory(). [S3 corresponding methods]

### 8. Is C2's bill 648 cents correct for $9.00 times 0.9 VIP times 0.8 Happy Hour?

Yes, if C2 chooses the $9 Salad and places the order while Happy Hour is active: 900 cents at 90% becomes 810, then 810 at 80% becomes 648 cents ($6.48). HappyHourPolicy.quote() calls customer.discountedPrice() first. Money.percent() rounds half up at each percentage calculation. Order stores the quoted bill; later payment does not recompute it.

This is conditional on the food choice. In the original deterministic base mode, C2 chooses Burger, giving 1500 × 90% × 80% = 1080 cents. Seeded mode may give Salad or another item. State and verify the selected seed before labelling a replay with a 648-cent bill. [S3 Menu.java, VIPCustomer.java, HappyHourPolicy.java, Money.java and Order.java]

### 9. Does every message in the sequence diagram match a real method in the Stage 3 code?

The proposed message names below match source declarations and call paths. An existing diagram was not supplied, so its messages still need to be checked individually. This review uses static source inspection; the Java sources were not compiled or executed in this review.

Arrival: GameController.beginTurn(boolean) → GameState.beginTurn() → CustomerFactory.create(int). Happy Hour activation is performed before GameState.beginTurn(); recover(...) follows it.

Order: GameController.waiterAction(...) → Waiter.act(...) → GameState.takeOrder(Customer) → Customer.chooseFood(Menu); PricingPolicy.quote(Customer, MenuItem) dispatches to HappyHourPolicy, calls discountedPrice(int), and returns the price. GameState constructs Order(MenuItem, int), then calls Customer.placeOrder(Order).

Kitchen and serving: GameController.chefAction(int) → Chef.act(...) → Order.prepareOneUnit(); serving later uses Customer.serve(int) → Order.serve().

Eating and payment: GameState.finishEating() → Customer.finishEating(int); Cashier.collectOldest(GameState) → GameState.collectPayment(Customer) → Customer.pay(int) → Order.pay(). GameState then updates totals and releases the table.

Result: ConsoleUI calls GameState.result(), then GameResult.isVictory(). There is no GameController.checkVictory(), Chef.cook() or Waiter.serve() method in this snapshot. READY is a state reached inside prepareOneUnit(), not a message named ready().

[Back to main README](../Readme.md)
