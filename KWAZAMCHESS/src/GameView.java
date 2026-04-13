import java.awt.*;
import javax.swing.*;

//SINEHAA A/P PARAMASIVAM, WEE JIA SHEEN, CHIA KAI JUN
//Handles the gui of the game
class GameView extends JFrame {
    private BoardView boardView;
    private Controller controller;
    private JLabel turnLabel;
    private JLabel timerLabel;
    private static final int TURN_TIME_LIMIT = 60000;
    private JLabel tealPlayerInfo;
    private JLabel yellowPlayerInfo;

    private Timer gameTimer;
    private int remainingSeconds;
    private boolean isTealTurn = true; // Start with Teal's turn

    public GameView(Model model, Controller controller) {
        this.controller = controller;
        this.boardView = new BoardView(model, controller);
        this.boardView.setGameView(this); 

        setTitle("Kwazam Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Add the main board to the center
        add(boardView, BorderLayout.CENTER);

        // Add the sidebar
        add(createSidebar(), BorderLayout.EAST);

        // Add the menu bar
        add(createMenuBar(), BorderLayout.NORTH);

        pack();
        setResizable(true);
        setLocationRelativeTo(null);

        gameTimer = new Timer(1000, e->
        {
            remainingSeconds --;
            updateTimerLabel();

            if(remainingSeconds <= 0)
            {
                gameTimer.stop();
                controller.switchTurn();
                updateTurn();
                restartTimer();

            }


        });
        
        startTurnTimer(1); // Start the turn timer with 1 minute
    }

    public BoardView getBoardView() {
        return boardView;
    }

    //Sidebar for displaying Timer and Current Turn
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 600));
        sidebar.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // Timer Section
        JPanel timerPanel = new JPanel();
        timerPanel.setLayout(new BoxLayout(timerPanel, BoxLayout.Y_AXIS));
        timerPanel.setBorder(BorderFactory.createTitledBorder("Turn Timer"));
        timerPanel.setMaximumSize(new Dimension(180, 100));
        timerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        timerLabel = new JLabel("Timer: 01:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerPanel.add(timerLabel);

        // Current Turn Section
        turnLabel = new JLabel("Move: Blue");
        turnLabel.setFont(new Font("Arial", Font.BOLD, 16));
        turnLabel.setForeground(new Color(0, 128, 128));
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(Box.createVerticalStrut(80));
        sidebar.add(timerPanel);
        sidebar.add(turnLabel);

        return sidebar;
    }

    //Dropdown Menu
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem newItem = new JMenuItem("New Game");
        newItem.addActionListener(e -> controller.startGame());

        JMenuItem saveItem = new JMenuItem("Save Game");
        saveItem.addActionListener(e -> controller.saveGame());
        
        JMenuItem loadItem = new JMenuItem("Load Game");
        loadItem.addActionListener(e -> controller.loadGame());
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(newItem);
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.add(exitItem);
        
        menuBar.add(fileMenu);
        return menuBar;
    }

    public void updateTimerLabel()
    {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);
        timerLabel.setText("Timer: " + timeString);
    }

    public void updateTurn() {
        isTealTurn = !isTealTurn;
    
        // Update the turn label text and color
        turnLabel.setText("Move: " + (isTealTurn ? "Blue" : "Yellow"));
        turnLabel.setForeground(isTealTurn ? new Color(0, 128, 128) : new Color(255, 204, 0));
    
        // Update the border color of the board
        boardView.setBorder(BorderFactory.createLineBorder(isTealTurn ? new Color(0, 128, 128) : new Color(255, 204, 0), 5));
    }
    
    //Winning Display
    public void updatePlayerStats(String player, int played, int won) {
        if (player.equalsIgnoreCase("Yellow")) {
            yellowPlayerInfo.setText("Played: " + played + " | Won: " + won);
        } else if (player.equalsIgnoreCase("Blue")) {
            tealPlayerInfo.setText("Played: " + played + " | Won: " + won);
        }
    }

    public Timer timer()
    {
        return this.gameTimer;
    }

    public int remainingseconds()
    {
        return this.remainingSeconds;
    }

    public GameView setGameView()
    {
        return this;
    }

    public void resetTimer()
    {
        gameTimer.stop();
        gameTimer.setInitialDelay(TURN_TIME_LIMIT);
        gameTimer.start();
    }

    public void restartTimer()
    {
        remainingSeconds = 60;
        updateTimerLabel();
        this.gameTimer.start();

    }

    private void startTurnTimer(int minuetes)
    {
        remainingSeconds = minuetes * 60;
        updateTimerLabel();
        if(gameTimer != null)
        {
            gameTimer.start();
        }
    }
}