import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List; 
 
//CHIA KAI JUN, WEE JIA SHEEN
//Controller of the program
public class Controller
{
    private Model model;
    private BoardView view;
    private ChessPieces selectedPiece;

    public Controller(Model model, BoardView view)
    {
        this.model = model;
        this.view = view;
    }

    public void setView(BoardView view) 
    {
        this.view = view;
    }

    //start the chess game
    public void startGame()
    {
        model.initialize();
        view.updateBoard(model.getBoard());
        view.repaint();
    }

    public void saveGame()
    {
        model.saveGame("Chess Board State");
    }

    public void loadGame()
    {
        model.loadGame("Chess Board State");
        for(MouseListener listener: view.getMouseListeners())
        {
            view.removeMouseListener(listener);
        }
        view.addCustomMouseListener(new MouseAdapter() {
           public void mouseClicked(MouseEvent e)
           {
            int col = e.getPoint().x / view.getCellSize();
            int row = e.getPoint().y/ view.getCellSize();
            handleBoardClick(col, row);
            view.repaint();
           } 
        });
        view.updateBoard(model.getBoard());
        view.repaint();
    }

    public void switchTurn()
    {
        model.switchTurn();
    }

    public void handleBoardClick(int x, int y) {

        ChessPieces[][] board = model.getBoard();
        ChessPieces clickedPiece = board[y][x];
    
        // First click - selecting a piece
        if (selectedPiece == null) {
            if (clickedPiece != null && isTurnPiece(clickedPiece)) {
                selectedPiece = clickedPiece;
                Position selected = new Position(x, y);
                List<Position> validMoves = selectedPiece.getValidMove(selected);
    
                if (validMoves.isEmpty())
                 {
                    selectedPiece = null;
                    view.clearHighlights();
                    return;
                }
    
                view.highlightMoves(validMoves);
            } 
            else
             {

                view.clearHighlights();
            }
        } 
        // Second click - moving the piece
        else {
            Position currentPos = new Position(selectedPiece.getX(selectedPiece), selectedPiece.getY(selectedPiece));
            Position targetPos = new Position(x, y);
            List<Position> validMoves = selectedPiece.getValidMove(currentPos);
    
            // Check if the target position is in the valid moves list
            boolean isValidMove = validMoves.stream()
                .anyMatch(pos -> pos.getX() == x && pos.getY() == y);
    
            if (isValidMove) {
                model.makeMove(currentPos, targetPos);
                view.clearHighlights();
                selectedPiece = null;
                view.getGameView().restartTimer();

                if(view instanceof BoardView && ((BoardView)view).getGameView()!=null)
                {
                    ((BoardView)view).getGameView().restartTimer();
                }
            } else {
                // If clicking on another piece of same color, select that piece instead
                if (clickedPiece != null && isTurnPiece(clickedPiece)) {
                    selectedPiece = clickedPiece;
                    Position selected = new Position(x, y);
                    validMoves = selectedPiece.getValidMove(selected);
                    view.highlightMoves(validMoves);
                } 
                else
                 {
                    view.clearHighlights();
                    selectedPiece = null;
                }
            }
        }
    }

    private boolean isTurnPiece(ChessPieces piece) {
        if (model.isBlue()) {
            return piece.getColor().equals("B");
        } else {
            return piece.getColor().equals("Y") || piece.getColor().equals("R");  // Handle both Yellow and Red pieces
        }
    }
}