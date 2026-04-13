import javax.swing.SwingUtilities;

//Main file to compile and run the program
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Model model = new Model();
            model.initialize(); // Ensure board is initialized
            
            Controller controller = new Controller(model, null);
            GameView gameView = new GameView(model, controller);
            controller.setView(gameView.getBoardView()); 
            model.setView(gameView.getBoardView()); 
            gameView.setVisible(true);
        });
    }
}


