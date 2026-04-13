import java.util.*;

//ONG KAI XIN, WEE JIA SHEEN
//handles positioning ,moving and upgrading chess pieces
class PositionBoard
{
    
    private ChessPieces[][] board;
    private final int x = 5;
    private final int y = 8;
    private List<ChessPieces> initialPiecesRed;
    private List<ChessPieces> initialPiecesBlue;

    private void initializePieces()
    {
        initialPiecesRed = new ArrayList<>();
        initialPiecesRed.add(new Tor("Y"));
        initialPiecesRed.add(new Biz("Y"));
        initialPiecesRed.add(new Sau("Y"));
        initialPiecesRed.add(new Biz("Y"));
        initialPiecesRed.add(new Xor("Y"));
        initialPiecesBlue = new ArrayList<>();
        initialPiecesBlue.add(new Xor("B"));
        initialPiecesBlue.add(new Biz("B"));
        initialPiecesBlue.add(new Sau("B"));
        initialPiecesBlue.add(new Biz("B"));
        initialPiecesBlue.add(new Tor("B"));
        
    }

    public ChessPieces[][] getBoardArray() {
        return board; 
    }
    

    public void initializeBoard()
    {
        for(int i = 0; i < y; i++)
        {
            for(int j = 0; j < x; j++)
            {
                switch (i) {
                    case 0 -> {
                        board[i][j] = initialPiecesRed.get(j);
                        board[i][j].setBoard(this);
                        board[i][j].setCurrentPosition(new Position(j,i));
                    }
                    case (y-1) -> {
                        board[i][j] = initialPiecesBlue.get(j);
                        board[i][j].setBoard(this);
                        board[i][j].setCurrentPosition(new Position(j,i));
                    }
                    case 1 -> {
                        board[i][j] = new Ram("Y");
                        board[i][j].setBoard(this);
                        board[i][j].setCurrentPosition(new Position(j,i));
                    }
                    case (y-2) -> {
                        board[i][j] = new Ram("B");
                        board[i][j].setBoard(this);
                        board[i][j].setCurrentPosition(new Position(j,i));
                    }
                    default -> board[i][j] = null;
                }

            }
        }
    }

    public PositionBoard()
    {
      
        board = new ChessPieces[y][x];
        initializePieces();
        initializeBoard();
         
    }

    public ChessPieces getPieceAt(int x, int y)
    {
        return board[y][x];
    }

    public void makeMove(Position current, Position target) {
        if (isValidMove(current, target)) {
            ChessPieces piece = board[current.getY()][current.getX()];
            board[target.getY()][target.getX()] = piece;
            board[current.getY()][current.getX()] = null;
            piece.setCurrentPosition(target);
            piece.setBoard(this);
        }
    }

    //Check where the pieces is able to move
    public boolean isValidMove(Position current, Position target)
    {
        ChessPieces piece = board[current.getY()][current.getX()];
        if(piece == null)
        {
            return false;
        }

        List<Position> validMoves = piece.getValidMove(current);
        return validMoves.stream().anyMatch(pos -> pos.getX() == target.getX() && pos.getY() == target.getY());
    }

    //get the available position where the pieces is able to move
    public List<Position> getValidMoves(Position current)
    {
        ChessPieces piece = board[current.getY()][current.getX()];
        if(piece == null)
        {
            return new ArrayList<>();
        }
        return piece.getValidMove(current);
    }

    //upgrade tor to xor and xor to tor 
    public void upgrade()
    {
      
        for(int i = 0; i < y; i++)
        {
            for(int j = 0; j < x; j++)
            {
                ChessPieces txor = board[i][j];
                if(txor != null)
                {
                if(txor instanceof Tor)
                {
                    ChessPieces xor = new Xor(txor.getColor());
                    xor.setBoard(this);
                    xor.setCurrentPosition(new Position(j, i));
                    board[i][j] = xor;
                    System.out.println("***TOR UPGRADE TO XOR***");
                }
                else if(txor instanceof Xor)
                {
                    ChessPieces tor = new Tor(txor.getColor());
                    tor.setBoard(this);
                    tor.setCurrentPosition(new Position(j,i));
                    board[i][j] = tor;
                    System.out.println("***XOR UPGRADE TO TOR***");
                }

                }
            }
        }
      
    }

    public void flipBoard() {
        ChessPieces[][] newBoard = new ChessPieces[8][5];
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 5; col++) {
                ChessPieces piece = board[row][col];
                if (piece != null) {
                    // Move the existing piece instead of creating a new one
                    newBoard[7 - row][4 - col] = piece;
                    // Update the piece's position
                    piece.setCurrentPosition(new Position(4 - col, 7 - row));
                    piece.setBoard(this);
                }
            }
        }
        board = newBoard;
    }
    
    public void clearBoard()
    {
        for(int i = 0; i < y; i++)
        {
            for(int j = 0; j < x; j++)
            {
              board[i][j] = null;
            }
        }
    }

    public void setPieceAt(int x, int y, ChessPieces piece, String color)
    {
        piece.setCurrentPosition(new Position(x,y));
        piece.setBoard(this);
        board[y][x] = piece;
    }
    


}