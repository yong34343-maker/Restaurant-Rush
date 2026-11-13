import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 * Swing boundary: displays state and forwards button choices to GameController. The existing
 * controller and domain classes still enforce every game rule.
 */
public final class GameWindow extends JPanel {
  private static final Color BACKGROUND = new Color(243, 246, 245);
  private static final Color TEAL = new Color(0, 118, 122);
  private static final Color TEXT = new Color(30, 39, 48);
  private static final Color ERROR = new Color(165, 35, 35);

  private GameController game;
  private int shownEvents;
  private boolean refreshing;
  private long activeSeed;
  private boolean activeBaseMode;

  private final JLabel heading = namedLabel("heading", 29);
  private final JLabel summary = namedLabel("summary", 17);
  private final JLabel floor = namedLabel("floor", 13);
  private final JLabel queue = namedLabel("queue", 13);
  private final JLabel staffInfo = namedLabel("staffInfo", 13);
  private final JLabel mode = namedLabel("mode", 12);
  private final JLabel phase = namedLabel("phase", 15);
  private final JLabel feedback = namedLabel("feedback", 12);
  private final JTextField seedField = new JTextField(12);
  private final JCheckBox baseMode = new JCheckBox("Original base choices");
  private final JComboBox<String> taskBox = new JComboBox<>();
  private final JComboBox<CustomerChoice> customerBox = new JComboBox<>();
  private final JButton perform = new JButton("Perform action");
  private final JButton start = new JButton("Start turn 1");
  private final JButton victoryDemo = new JButton("Victory demo: next turn");
  private final JButton defeatDemo = new JButton("Defeat demo: next turn");
  private final JButton newGame = new JButton("New game");
  private final JButton help = new JButton("How to play");
  private final JTextArea journal = new JTextArea();
  private final DefaultTableModel customerModel =
      new DefaultTableModel(
          new String[] {
            "Customer", "Type", "Satisfaction", "Status", "Food", "Order", "Prep", "Locked bill"
          },
          0) {
        @Override
        public boolean isCellEditable(int row, int column) {
          return false;
        }
      };
  private final JTable customerTable = new JTable(customerModel);

  public GameWindow(long seed, boolean originalBaseMode) {
    setLayout(new BorderLayout(0, 14));
    setBackground(BACKGROUND);
    setBorder(BorderFactory.createEmptyBorder(24, 24, 18, 24));
    seedField.setName("seed");
    seedField.setText(Long.toString(seed));
    baseMode.setName("baseMode");
    baseMode.setSelected(originalBaseMode);
    taskBox.setName("task");
    customerBox.setName("customer");
    perform.setName("perform");
    start.setName("start");
    victoryDemo.setName("victoryDemo");
    defeatDemo.setName("defeatDemo");
    newGame.setName("newGame");
    help.setName("help");
    customerTable.setName("customers");
    journal.setName("journal");

    add(buildHeader(), BorderLayout.NORTH);
    add(buildGameView(), BorderLayout.CENTER);
    add(buildControls(), BorderLayout.SOUTH);
    connectButtons();
    resetGame();
  }

  private static JLabel namedLabel(String name, int size) {
    JLabel label = new JLabel(" ");
    label.setName(name);
    label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, size));
    label.setForeground(TEXT);
    label.setMaximumSize(new Dimension(Integer.MAX_VALUE, size + 12));
    return label;
  }

  private JPanel verticalPanel() {
    JPanel panel = new JPanel();
    panel.setBackground(BACKGROUND);
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    return panel;
  }

  private JPanel buildHeader() {
    JPanel header = verticalPanel();
    heading.setAlignmentX(LEFT_ALIGNMENT);
    header.add(heading);
    header.add(Box.createVerticalStrut(18));
    JLabel subtitle = namedLabel("subtitle", 12);
    subtitle.setText("MANAGER'S DESK   /   STAGE 2   /   OFFLINE BASE SETTING");
    header.add(subtitle);
    header.add(Box.createVerticalStrut(21));
    summary.setForeground(TEAL);
    header.add(summary);
    header.add(Box.createVerticalStrut(18));
    JLabel targets = namedLabel("targets", 12);
    targets.setText(
        "Victory after turn 18: 4+ paid customers  |  $45.00+ revenue  |  60+ paid satisfaction");
    header.add(targets);
    header.add(Box.createVerticalStrut(19));

    JPanel floorPanel = new JPanel(new GridLayout(4, 1, 0, 5));
    floorPanel.setBackground(new Color(233, 236, 235));
    floorPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
    floorPanel.add(floor);
    floorPanel.add(queue);
    floorPanel.add(staffInfo);
    floorPanel.add(mode);
    floorPanel.setAlignmentX(LEFT_ALIGNMENT);
    header.add(floorPanel);
    return header;
  }

  private JSplitPane buildGameView() {
    customerTable.setRowHeight(28);
    customerTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
    customerTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
    customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    customerTable.setFillsViewportHeight(true);
    customerTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    customerTable.getColumnModel().getColumn(3).setPreferredWidth(145);
    customerTable.getColumnModel().getColumn(4).setPreferredWidth(220);
    customerTable.getColumnModel().getColumn(5).setPreferredWidth(110);
    JScrollPane customers = new JScrollPane(customerTable);
    customers.setColumnHeaderView(customerTable.getTableHeader());
    customers.setMinimumSize(new Dimension(300, 120));

    journal.setEditable(false);
    journal.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    journal.setLineWrap(true);
    journal.setWrapStyleWord(true);
    journal.setBorder(BorderFactory.createEmptyBorder(7, 9, 7, 9));
    JScrollPane journalPane = new JScrollPane(journal);
    journalPane.setBorder(BorderFactory.createTitledBorder("Turn journal"));
    journalPane.setMinimumSize(new Dimension(300, 110));

    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, customers, journalPane);
    split.setResizeWeight(0.57);
    split.setDividerLocation(215);
    split.setDividerSize(7);
    split.setBorder(BorderFactory.createEmptyBorder());
    split.setPreferredSize(new Dimension(1050, 360));
    return split;
  }

  private JPanel row() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 4));
    panel.setBackground(BACKGROUND);
    panel.setAlignmentX(LEFT_ALIGNMENT);
    return panel;
  }

  private JPanel buildControls() {
    JPanel controls = verticalPanel();
    controls.add(phase);
    controls.add(Box.createVerticalStrut(5));
    JPanel actionRow = row();
    actionRow.add(new JLabel("Task"));
    taskBox.setPreferredSize(new Dimension(145, 28));
    actionRow.add(taskBox);
    actionRow.add(new JLabel("Customer"));
    customerBox.setPreferredSize(new Dimension(150, 28));
    actionRow.add(customerBox);
    actionRow.add(perform);
    actionRow.add(start);
    controls.add(actionRow);

    JPanel optionsRow = row();
    optionsRow.add(victoryDemo);
    optionsRow.add(defeatDemo);
    optionsRow.add(help);
    controls.add(optionsRow);

    JPanel seedRow = row();
    seedRow.add(new JLabel("Seed"));
    seedRow.add(seedField);
    baseMode.setOpaque(false);
    seedRow.add(baseMode);
    seedRow.add(newGame);
    JLabel hint = new JLabel("Change seed, then press New game.");
    hint.setForeground(new Color(85, 97, 105));
    seedRow.add(hint);
    controls.add(seedRow);
    controls.add(feedback);
    return controls;
  }

  private void connectButtons() {
    start.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent event) {
            try {
              beginTurn();
              refresh();
              showMessage("Choose the Waiter's task, or Wait.", false);
            } catch (IllegalArgumentException exception) {
              showMessage(exception.getMessage(), true);
            }
          }
        });
    perform.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent event) {
            performSelectedAction();
          }
        });
    taskBox.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent event) {
            if (!refreshing) {
              updateCustomerChoices();
            }
          }
        });
    victoryDemo.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent event) {
            playDemoTurn(true);
          }
        });
    defeatDemo.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent event) {
            playDemoTurn(false);
          }
        });
    newGame.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent event) {
            resetGame();
          }
        });
    help.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent event) {
            JOptionPane.showMessageDialog(
                GameWindow.this,
                "1. Start turn: a scheduled customer arrives.\n"
                    + "2. Waiter: seat, take an order, serve READY food, or Wait.\n"
                    + "3. Chef: cook one preparation unit, or Wait.\n"
                    + "4. Finish turn: eating, automatic payment and waiting loss.\n\n"
                    + "Customers choose their own food. Staff tasks are limited.\n"
                    + "Food finished now can be served next turn at the earliest.\n"
                    + "Service -> next turn READY_TO_PAY -> following turn payment.\n"
                    + "The demo buttons play one full turn using the sample strategy.\n"
                    + "The game always runs all 18 turns.\n",
                "How to play Stage 2",
                JOptionPane.INFORMATION_MESSAGE);
          }
        });
    customerTable
        .getSelectionModel()
        .addListSelectionListener(
            new ListSelectionListener() {
              @Override
              public void valueChanged(ListSelectionEvent event) {
                if (refreshing || event.getValueIsAdjusting()) {
                  return;
                }
                int selectedRow = customerTable.getSelectedRow();
                if (selectedRow < 0) {
                  return;
                }
                String customerId = customerModel.getValueAt(selectedRow, 0).toString();
                for (int index = 0; index < customerBox.getItemCount(); index++) {
                  CustomerChoice choice = customerBox.getItemAt(index);
                  if (customerId.equals("C" + choice.id)) {
                    customerBox.setSelectedIndex(index);
                    break;
                  }
                }
              }
            });
    baseMode.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent event) {
            seedField.setEnabled(!baseMode.isSelected());
          }
        });
  }

  private void resetGame() {
    try {
      // Validate before replacing the current game, so a typo cannot erase it.
      long seed = GameSettings.DEFAULT_SEED;
      if (!baseMode.isSelected()) {
        seed = Long.parseLong(seedField.getText().trim());
      }
      if (baseMode.isSelected()) {
        game = new GameController();
      } else {
        game = new GameController(seed);
      }
      activeSeed = seed;
      activeBaseMode = baseMode.isSelected();
      shownEvents = 0;
      journal.setText("");
      seedField.setEnabled(!baseMode.isSelected());
      refresh();
      showMessage("Ready. Start turn 1 to welcome the first customer.", false);
    } catch (NumberFormatException exception) {
      showMessage(
          "Enter a whole-number seed between " + Long.MIN_VALUE + " and " + Long.MAX_VALUE + ".",
          true);
    }
  }

  private void beginTurn() {
    game.beginTurn();
    shownEvents = 0;
    journal.append("\n=== TURN " + game.getState().getTurn() + " ===\n");
    appendNewEvents();
  }

  private void performSelectedAction() {
    String task = (String) taskBox.getSelectedItem();
    if (task == null) {
      return;
    }
    CustomerChoice selected = (CustomerChoice) customerBox.getSelectedItem();
    int customerId = selected == null ? 0 : selected.id;
    try {
      TurnPhase current = game.getPhase();
      if (current == TurnPhase.WAITER) {
        WaiterTask waiterTask = WaiterTask.WAIT;
        if (task.equals("Seat")) {
          waiterTask = WaiterTask.SEAT;
        } else if (task.equals("Take order")) {
          waiterTask = WaiterTask.ORDER;
        } else if (task.equals("Serve")) {
          waiterTask = WaiterTask.SERVE;
        }
        game.waiterAction(waiterTask, customerId);
      } else if (current == TurnPhase.CHEF) {
        game.chefAction(task.equals("Wait") ? 0 : customerId);
      } else if (current == TurnPhase.END) {
        finishTurn();
      }
      appendNewEvents();
      refresh();
      if (game.isFinished()) {
        showFinalResult();
      } else {
        showMessage("Action completed. " + phase.getText(), false);
      }
    } catch (IllegalArgumentException exception) {
      // The controller preserves the task budget after an invalid choice.
      showMessage("Invalid choice: " + exception.getMessage(), true);
    }
  }

  private void finishTurn() {
    game.endTurn();
    appendNewEvents();
    journal.append("END: " + game.getState().result().summary() + "\n");
  }

  private void playDemoTurn(boolean tryToWin) {
    try {
      beginTurn();
      DemoPolicy.choose(game, tryToWin);
      appendNewEvents();
      finishTurn();
      refresh();
      if (game.isFinished()) {
        showFinalResult();
      } else {
        showMessage(
            "Demo completed one turn. You can continue manually or run another demo turn.", false);
      }
    } catch (IllegalArgumentException exception) {
      showMessage(exception.getMessage(), true);
    }
  }

  private void appendNewEvents() {
    for (int index = shownEvents; index < game.getState().getEvents().size(); index++) {
      journal.append("  " + game.getState().getEvents().get(index) + "\n");
    }
    shownEvents = game.getState().getEvents().size();
    journal.setCaretPosition(journal.getDocument().getLength());
  }

  private void refresh() {
    refreshing = true;
    GameState state = game.getState();
    GameResult result = state.result();
    heading.setText("Restaurant Rush   /   Turn " + state.getTurn() + " of 18");
    String average = "N/A";
    if (result.getPaid() > 0) {
      average = String.format(Locale.ROOT, "%.2f", result.getAverage());
    }
    summary.setText(
        "Revenue "
            + Money.dollars(result.getRevenueCents())
            + "    |    Paid "
            + result.getPaid()
            + " / 4    |    Satisfaction "
            + average);
    String floorText = "FLOOR    ";
    for (Table table : state.getTables()) {
      floorText += "Table " + table.getId() + ": ";
      floorText += table.isFree() ? "FREE" : "C" + table.getOccupant().getId();
      floorText += "       ";
    }
    floor.setText(floorText);
    String queueText = "WAITING QUEUE   ";
    for (Customer customer : state.getQueue()) {
      queueText += "C" + customer.getId() + " (" + customer.getType() + ")   ";
    }
    if (state.getQueue().isEmpty()) {
      queueText += "Empty";
    }
    queue.setText(queueText);
    staffInfo.setText(
        "Staff: Waiter 1 task | Chef 1 preparation unit | Cashier 1 automatic payment per turn");
    mode.setText(
        activeBaseMode
            ? "Food choices: original deterministic base rule"
            : "Food choices: seeded random | seed: " + activeSeed);

    customerModel.setRowCount(0);
    for (Customer customer : state.getCustomers()) {
      Order order = customer.getOrder();
      customerModel.addRow(
          new Object[] {
            "C" + customer.getId(),
            customer.getType(),
            customer.getSatisfaction(),
            customer.getStatus(),
            order == null ? "-" : order.getItem().getName(),
            order == null ? "-" : order.getStatus(),
            order == null
                ? "-"
                : order.getCompletedUnits() + "/" + order.getItem().getPreparationUnits(),
            order == null ? "-" : Money.dollars(order.getBillCents())
          });
    }

    taskBox.removeAllItems();
    if (game.isFinished()) {
      phase.setText(
          result.isVictory()
              ? "VICTORY - all three targets met."
              : "DEFEAT - not all three targets were met.");
      phase.setForeground(result.isVictory() ? TEAL : ERROR);
    } else {
      phase.setForeground(TEXT);
      switch (game.getPhase()) {
        case BETWEEN_TURNS:
          phase.setText("Between turns - choose how to start the next turn.");
          break;
        case WAITER:
          phase.setText("Waiter - choose one seating, ordering or serving task.");
          taskBox.addItem("Seat");
          taskBox.addItem("Take order");
          taskBox.addItem("Serve");
          taskBox.addItem("Wait");
          break;
        case CHEF:
          phase.setText("Chef - prepare one unit on one order, or Wait.");
          taskBox.addItem("Cook");
          taskBox.addItem("Wait");
          break;
        case END:
          phase.setText("Finish turn - process eating, automatic payment and waiting loss.");
          taskBox.addItem("Finish turn");
          break;
        default:
          break;
      }
    }
    boolean betweenTurns = game.getPhase() == TurnPhase.BETWEEN_TURNS && !game.isFinished();
    start.setText("Start turn " + Math.min(18, state.getTurn() + 1));
    start.setEnabled(betweenTurns);
    victoryDemo.setEnabled(betweenTurns);
    defeatDemo.setEnabled(betweenTurns);
    taskBox.setEnabled(taskBox.getItemCount() > 0);
    refreshing = false;
    updateCustomerChoices();
    revalidate();
    repaint();
  }

  private void updateCustomerChoices() {
    String task = (String) taskBox.getSelectedItem();
    customerBox.removeAllItems();
    if (task == null) {
      customerBox.setEnabled(false);
      perform.setEnabled(false);
      return;
    }
    boolean needsCustomer = !task.equals("Wait") && !task.equals("Finish turn");
    if (needsCustomer) {
      for (Customer customer : game.getState().getCustomers()) {
        // UI suggestions only. GameController/domain objects still validate the actual action.
        Order order = customer.getOrder();
        boolean candidate = false;
        if (task.equals("Seat")) {
          candidate = customer.getStatus() == CustomerStatus.WAITING;
        } else if (task.equals("Take order")) {
          candidate = customer.getStatus() == CustomerStatus.SEATED && order == null;
        } else if (task.equals("Serve")) {
          candidate =
              customer.getStatus() == CustomerStatus.SEATED
                  && order != null
                  && order.getStatus() == OrderStatus.READY;
        } else if (task.equals("Cook")) {
          candidate = order != null && order.getStatus() == OrderStatus.PREPARING;
        }
        if (candidate) {
          customerBox.addItem(new CustomerChoice(customer.getId(), customer.getType()));
        }
      }
    }
    customerBox.setEnabled(needsCustomer && customerBox.getItemCount() > 0);
    perform.setText(task.equals("Finish turn") ? "Finish turn" : "Perform action");
    perform.setEnabled(!needsCustomer || customerBox.getItemCount() > 0);
    if (needsCustomer && customerBox.getItemCount() == 0) {
      perform.setToolTipText("No customer is eligible for this task. Choose another task or Wait.");
    } else {
      perform.setToolTipText(null);
    }
  }

  private void showFinalResult() {
    GameResult result = game.getState().result();
    String ending = result.isVictory() ? "VICTORY" : "DEFEAT";
    journal.append("FINAL: " + ending + "\n");
    String average = "N/A";
    if (result.getPaid() > 0) {
      average = String.format(Locale.ROOT, "%.2f", result.getAverage());
    }
    showMessage(
        ending
            + " - Paid: "
            + result.getPaid()
            + ", revenue: "
            + Money.dollars(result.getRevenueCents())
            + ", paid satisfaction: "
            + average
            + ".",
        !result.isVictory());
  }

  private void showMessage(String text, boolean error) {
    feedback.setText(text);
    feedback.setToolTipText(text);
    feedback.setForeground(error ? ERROR : TEAL);
  }

  private static final class CustomerChoice {
    private final int id;
    private final String type;

    private CustomerChoice(int id, String type) {
      this.id = id;
      this.type = type;
    }

    @Override
    public String toString() {
      return "C" + id + " (" + type + ")";
    }
  }
}
