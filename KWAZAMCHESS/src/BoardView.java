import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

//SINEHAA A/P PARAMASIVAM, ONG KAI XIN
// BoardView: Handles drawing the board and pieces
class BoardView extends JPanel {
    private Model model;
    private GameView gameview;
    private Controller controller;
    private int cellSize = 80;
    private List<Position> highlightedPositions = new ArrayList<>();

    public BoardView(Model model, Controller controller) {
        this.model = model;
        this.controller = controller;
        this.setPreferredSize(new Dimension(5 * cellSize, 8 * cellSize));
        this.setBorder(BorderFactory.createLineBorder(new Color(0, 128, 128), 5)); 

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = e.getX() / cellSize;
                int row = e.getY() / cellSize;
                controller.handleBoardClick(col, row);
                repaint();
            }
        });
    }

    public int getCellSize() {
        return cellSize;
    }
    
    public GameView getGameView() {
        return gameview;
    }

    public void setGameView(GameView gameView) {
        this.gameview = gameView;
    }

    public void addCustomMouseListener(MouseAdapter adapter) {
        this.addMouseListener(adapter);
    }

    public void updateBoard(ChessPieces[][] board) {
        repaint();
    }

    public void highlightMoves(List<Position> positions) {
        highlightedPositions = positions;
        repaint();
    }

    public void clearHighlights() {
        highlightedPositions.clear();
        repaint();
    }

    public void winDisplay(String color) {
        JOptionPane.showMessageDialog(this, color + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    public void updateBorderColor(boolean isTealTurn) {
        Color borderColor = isTealTurn ? new Color(0, 128, 128) : new Color(255, 204, 0); // Teal or Yellow
        this.setBorder(BorderFactory.createLineBorder(borderColor, 5));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        drawPieces(g);
        highlightSquares(g);
    }

    private void highlightSquares(Graphics g) {
        g.setColor(new Color(0, 255, 0, 125)); 
        for (Position pos : highlightedPositions) {
            g.fillRect(pos.getX() * cellSize, pos.getY() * cellSize, cellSize, cellSize);
        }
    }
    
    private void drawBoard(Graphics g) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 5; col++) {
                g.setColor((row + col) % 2 == 0 ? Color.pink : Color.cyan);
                g.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
            }
        }
    }
    
    //CHIA KAI JUN,ONG KAI XIN
    //Display pieces on the chessboard with rotation for the opposite color
    void drawPieces(Graphics g) {
        ChessPieces[][] board = model.getBoard();
        Graphics2D g2d = (Graphics2D) g;
        boolean isBlueTurn = model.isBlue(); 
    
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 5; col++) {
                ChessPieces piece = board[row][col];
                if (piece != null) {
                    String iconPath = piece.chessIcon();
                    ImageIcon icon = new ImageIcon(iconPath);
                    Image image = icon.getImage();
    
                    // Calculate the position of the piece
                    int x = col * cellSize + 10;
                    int y = row * cellSize + 10;
    
                    // Determine if this piece should be rotated
                    String pieceColor = piece.getColor(); // Get the piece color
                    boolean shouldRotate = (isBlueTurn && pieceColor.equals("Y")) || 
                                           (!isBlueTurn && pieceColor.equals("B"));
    
                    // Save the original transform
                    AffineTransform originalTransform = g2d.getTransform();
    
                    if (shouldRotate) {
                        double angle = Math.PI;
                        g2d.translate(x + (cellSize - 20) / 2, y + (cellSize - 20) / 2); 
                        g2d.rotate(angle); 
                        g2d.drawImage(image, -(cellSize - 20) / 2, -(cellSize - 20) / 2, cellSize - 20, cellSize - 20, this);
                    } else {
                        g2d.drawImage(image, x, y, cellSize - 20, cellSize - 20, this);
                    }

                    g2d.setTransform(originalTransform);
                }
            }
        }
    }
}
