import java.util.*;
//Game logic for the chess pieces

//ONG KAI XIN, WEE JIA SHEEN
//Superclass for ChessPieces
abstract class ChessPieces
{
    protected PositionBoard board;
    private Position currentPosition;
    private String color;
    
    public abstract String chessIcon();
    public abstract List<Position> getValidMove(Position position);

    public int getX(ChessPieces piece)
    {
        return piece.currentPosition.getX();
    }

    public int getY(ChessPieces piece)
    {
        return piece.currentPosition.getY();
    }

    protected void updatePosition(Position newPosition)
    {
        this.currentPosition = newPosition;
    }

    public void setBoard(PositionBoard board)
    {
        this.board = board;
    }

    public void setCurrentPosition(Position position)
    {
        this.currentPosition = position;
    }

    public ChessPieces(String color)
    {
       this.color = color;
    }

    public Boolean isValidPosition(int newX, int newY)
    {
        return newX >= 0 && newX <5 && newY >= 0 && newY < 8;
    }

    public String getColor()
    {
        return color;
    }

    public boolean sameTeam(ChessPieces piece) {
        return (this.getColor().equals("Y") && piece.getColor().equals("Y"))||
               (this.getColor().equals("B") && piece.getColor().equals("B"));
    }
    

    
}

//Movement for Ram Pieces
class Ram extends ChessPieces 
{
    private boolean reachedEnd;
    private boolean isMovingUp;

    public Ram(String color) 
    {
        super(color);
        this.reachedEnd = false;
        this.isMovingUp = color.equals("B");  // Blue starts moving up, others start moving down
    }      
       
    @Override
    public String chessIcon() 
    {
        String filename = (getColor().equals("B") ? "Teal_" : "Yellow_") + this.getClass().getSimpleName() + ".png";
        String path = "assets/" + filename;
        return path;
    }
    
    @Override
    protected void updatePosition(Position newPosition) 
    {
        super.updatePosition(newPosition);
        
        // Update reachedEnd based on new position
        if (getColor().equals("Y") ) {
            if (newPosition.getY() == 7) {
                reachedEnd = true;
                isMovingUp = true;
            } else if (newPosition.getY() == 0) {
                reachedEnd = false;
                isMovingUp = false;
            }
        } else { // Blue piece
            if (newPosition.getY() == 0) {
                reachedEnd = true;
                isMovingUp = false;
            } else if (newPosition.getY() == 7) {
                reachedEnd = false;
                isMovingUp = true;
            }
        }
    }

    @Override
    public List<Position> getValidMove(Position position) {
        List<Position> moves = new ArrayList<>();

        // Force direction change at boundaries
        if (position.getY() == 0) {
            if (getColor().equals("B")) {
                isMovingUp = false;  // Blue piece at bottom should move down
                reachedEnd = true;
            } else {
                isMovingUp = false;  // Yellow piece at bottom should move up
                reachedEnd = false;
            }
        } else if (position.getY() == 7) {
            if (getColor().equals("B")) {
                isMovingUp = true;   // Blue piece at top should move up
                reachedEnd = false;
            } else {
                isMovingUp = true;   // Yellow piece at top should move down
                reachedEnd = true;
            }
        }

        // Handle Yellow pieces at top specially
        if (getColor().equals("Y") && position.getY() >= 6) {
            isMovingUp = true;
        }

        // Determine movement direction
        int direction = isMovingUp ? -1 : 1;

        int newX = position.getX();
        int newY = position.getY() + direction;

        // Check if move would be out of bounds
        if (!isValidPosition(newX, newY)) {
            // If at boundary, reverse direction and try again
            direction *= -1;
            newY = position.getY() + direction;
            isMovingUp = !isMovingUp;
            
            if (!isValidPosition(newX, newY)) {
                return moves;
            }
        }

        ChessPieces checkPieces = board.getPieceAt(newX, newY);
        if (checkPieces == null) {
            moves.add(new Position(newX, newY)); // Empty space
        } else if (!checkPieces.getColor().equals(this.getColor()) && !sameTeam(checkPieces)) {
            moves.add(new Position(newX, newY)); // Enemy piece
        } 

        return moves;
    }
}


    
    


//Movement for Biz Pieces
class Biz extends ChessPieces
{
  

       public Biz(String color)
       {
          super(color);
       
       }      
       
       @Override
       public String chessIcon() {
        String filename = (getColor().equals("B") ? "Teal_" : "Yellow_") + this.getClass().getSimpleName() + ".png";
        String path = "assets/" + filename;
    
        return path;
    }
    

       @Override
       public List<Position> getValidMove(Position position) // possible moves list
       {
        int x = position.getX();
        int y = position.getY();
         List<Position> validMoves = new ArrayList<>();
         int[][] directions = {{2,1}, {-2,1}, {-2,-1}, {2,-1}, {1,2}, {-1,2}, {-1,-2},{1,-2}};
         for (int[]dir : directions)
         {
           int newX = x + dir[0];
           int newY = y + dir[1];
           if(isValidPosition(newX, newY))
           {
            ChessPieces checkPieces = board.getPieceAt(newX, newY);
            if(checkPieces == null || !checkPieces.getColor().equals(getColor()))
            {
                validMoves.add(new Position(newX, newY));
            }
           }
         }
         return validMoves;

       }

}

//Movement for Tor Pieces
class Tor extends ChessPieces
{

       public Tor(String color)
       {
          super(color);
       
       }      
       
       @Override
       public String chessIcon() {
        String filename = (getColor().equals("B") ? "Teal_" : "Yellow_") + this.getClass().getSimpleName() + ".png";
        String path = "assets/" + filename;
    
        return path;
    }
    
        
    private List<Position> getValidDirections(Position position, int dx, int dy) {
        List<Position> validMoves = new ArrayList<>();
        int currentX = position.getX();
        int currentY = position.getY();
        
        while (true) {
            currentX += dx;
            currentY += dy;
            
            // Check bounds first
            if (!isValidPosition(currentX, currentY)) {
                break;
            }
            
            ChessPieces pieceAtPosition = board.getPieceAt(currentX, currentY);
            
            // If there's a piece at this position
            if (pieceAtPosition != null) {
                String pieceColor = pieceAtPosition.getColor();
                String myColor = getColor();
                
                // If same team (including Red pieces as Yellow team)
                if (myColor.equals(pieceColor) || 
                   (myColor.equals("Y") && pieceColor.equals("Y")) ) {
                    // Don't add this position and stop looking in this direction
                    break;
                } else {
                    // Enemy piece - add this position as a capture and stop looking
                    validMoves.add(new Position(currentX, currentY));
                    break;
                }
            } else {
                // Empty square - add it and continue looking
                validMoves.add(new Position(currentX, currentY));
            }
        }
        
        return validMoves;
    }

       @Override
       public List<Position> getValidMove(Position position) // possible moves list
       {
        List<Position> validMoves = new ArrayList<>();
        validMoves.addAll(getValidDirections(position, 1, 0)); // right 
        validMoves.addAll(getValidDirections(position, 0, 1)); // down
        validMoves.addAll(getValidDirections(position, -1, 0)); // left
        validMoves.addAll(getValidDirections(position, 0, -1)); // up
        
        return validMoves;
        
       }

}

//Movement for Xor Pieces
class Xor extends ChessPieces
{

       public Xor(String color)
       {
          super(color);
       
       }      
       
       public String chessIcon() {
        String filename = (getColor().equals("B") ? "Teal_" : "Yellow_") + this.getClass().getSimpleName() + ".png";
        String path = "assets/" + filename;
    
        return path;
    }
    

        
    private List<Position> getValidDirections(Position position, int dx, int dy) {
        List<Position> validMoves = new ArrayList<>();
        int currentX = position.getX();
        int currentY = position.getY();
        
        while (true) {
            currentX += dx;
            currentY += dy;
            
            // Check bounds first
            if (!isValidPosition(currentX, currentY)) {
                break;
            }
            
            ChessPieces pieceAtPosition = board.getPieceAt(currentX, currentY);
            
            // If there's a piece at this position
            if (pieceAtPosition != null) {
                String pieceColor = pieceAtPosition.getColor();
                String myColor = getColor();
                
                // If same team (including Yellow pieces as Yellow team)
                if (myColor.equals(pieceColor) || 
                   (myColor.equals("Y") && pieceColor.equals("Y")) ) {
                    // Don't add this position and stop looking in this direction
                    break;
                } else {
                    // Enemy piece - add this position as a capture and stop looking
                    validMoves.add(new Position(currentX, currentY));
                    break;
                }
            } else {
                // Empty square - add it and continue looking
                validMoves.add(new Position(currentX, currentY));
            }
        }
        
        return validMoves;
    }

       @Override
       public List<Position> getValidMove(Position position) // possible moves list
       {
        List<Position> validMoves = new ArrayList<>();
        validMoves.addAll(getValidDirections(position, 1, 1)); // down right 
        validMoves.addAll(getValidDirections(position, -1, 1)); // down left
        validMoves.addAll(getValidDirections(position, 1, -1)); // up right
        validMoves.addAll(getValidDirections(position, -1, -1)); // up left
        
        return validMoves;
        
       }

}

//Movement for Sau Pieces
class Sau extends ChessPieces
{

       public Sau(String color)
       {
          super(color);
       }      
       
       @Override
       public String chessIcon() {
        String filename = (getColor().equals("B") ? "Teal_" : "Yellow_") + this.getClass().getSimpleName() + ".png";
        String path = "assets/" + filename;
    
        return path;
    }
    

       @Override
       public List<Position> getValidMove(Position position) {
        int x = position.getX();
        int y = position.getY();
           List<Position> moves = new ArrayList<>();
           
               int[][] direction = 
               {
                {1,0}, {-1,0}, {0,-1}, {0,1},
                {1,-1}, {-1,1}, {1,1}, {-1,-1}
               };

               for (int[] dir : direction) {
                int newX = x + dir[0];
                int newY = y + dir[1];
                if (isValidPosition(newX, newY)) 
                {
                    ChessPieces checkPieces = board.getPieceAt(newX, newY);
                    if (checkPieces == null || (!checkPieces.getColor().equals(getColor()) && !sameTeam(checkPieces))) {
                        {
                        moves.add(new Position(newX, newY));
                    }
                }
            }
        }
           return moves;
       
    }
}
