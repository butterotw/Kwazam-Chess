import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

//ONG KAI XIN
//Model of the program
class Model
{
    private PositionBoard gameBoard;
    private boolean isBlue;
    private int turn;
    private BoardView view;
    private GameView gameview;

    public Model()
    {
        this.gameBoard = new PositionBoard();
        this.isBlue = true;
        this.turn = 0;
        
    }
    public void setView(BoardView view)
    {
        this.view = view;
    }

    public void setGameView(GameView setGameView)
    {
        this.gameview = setGameView;
    }

    public boolean isBlue()
    {
        return this.isBlue;
    }

    public void initialize()
    {
        gameBoard.initializeBoard();

    }

    public ChessPieces[][] getBoard()
    {
        if (gameBoard == null) 
        {
            return new ChessPieces[8][5]; 
        }
        return gameBoard.getBoardArray();
    }

    //Player Moves Chess Pieces
    public void makeMove(Position current, Position target) {
        ChessPieces piece = gameBoard.getPieceAt(current.getX(), current.getY());
        if (piece == null) {
            return;
        }
        
        // Check for Sau capture (win condition)
        ChessPieces capturedPiece = gameBoard.getPieceAt(target.getX(), target.getY());
        if (capturedPiece != null && capturedPiece instanceof Sau) {
            if (view != null) {
                String winTeam = piece.getColor().equals("Y")? "Yellow":"Blue";
                view.winDisplay(winTeam);
            }
            initialize();
            return;
        }
    
        gameBoard.makeMove(current, target);
        if (view != null) {
            view.updateBoard(getBoard());
            view.repaint();
            view.getGameView().updateTurn();
            view.getGameView().restartTimer();
            
        }

        switchTurn();
        
    }
    
    //Flip the chessboard after a move is made and upgrade tor and xor pieces after 2 turns
    public void switchTurn() {
        this.isBlue = !this.isBlue;
        this.turn++;
    
        if (this.turn == 4) {
            gameBoard.upgrade();
        }
    
        gameBoard.flipBoard();  // Flip the board on every turn switch
    
        if (view != null) {
            view.updateBoard(getBoard());
            view.repaint();
        }
    }

    public String getTurn() 
    {
       return isBlue? "It is Blue's Turn" : "It is Yellow's Turn";
    }

    //save game in Chess Board State
    public void saveGame(String filename)
    {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filename)))
        {
            writer.write("Total Turn Count: "+ this.turn);
            writer.newLine();
            ChessPieces[][] boardArray = getBoard();
            for(int i = 0; i < boardArray.length; i++)
            {
                for (int j = 0; j < boardArray[i].length; j++)
                {
                    ChessPieces piece = boardArray[i][j];
                    if(piece != null)
                    {
                        writer.write(piece.getClass().getSimpleName() + "    " + piece.getColor() + "    " + j + "    " + i);
                        writer.newLine();
                    }
                }
            }
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }


    //load game from Chess Board State
    public void loadGame (String filename)
    {
        try(BufferedReader reader = new BufferedReader(new FileReader(filename)))
        {
            String line = reader.readLine();
            if(line != null && line.startsWith("Total Turn Count: "))
            {
            try{
                this.turn = Integer.parseInt(line.split(":")[1].trim());
                this.isBlue = (this.turn %2 != 0 );
            }catch (NumberFormatException e)
            {
                System.err.println("Error: Invalid format in save file!");
                return;
            }
            }

            reader.readLine();
            gameBoard.clearBoard();

            while((line = reader.readLine()) != null)
            {
                String[] parts = line.split("\\s+");
                if (parts.length < 4) {
                    System.err.println("Error: Invalid line format: " + line);
                    continue;
                }
                try{
                   String pieceType = parts[0];
                   String color = parts[1];
                   int x = Integer.parseInt(parts[2].trim());
                   int y = Integer.parseInt(parts[3].trim());

                ChessPieces piece = createPiece(pieceType, color);
                if(piece != null)
                {
                    piece.setBoard(gameBoard);
                    piece.setCurrentPosition(new Position(x,y));
                    gameBoard.setPieceAt(x,y,piece,color);
                }

            }catch (NumberFormatException e)
            {
                System.err.println("Error: Invalid format in save file: "+line);
            }
        }
        if (!isBlue)
         {
            gameBoard.flipBoard(); 
         }   
         if (view != null) 
         {
            view.updateBoard(getBoard());
            view.repaint();
        } 

        }catch (IOException e) { e.printStackTrace(); }
    }
    
private ChessPieces createPiece(String pieceType, String color)
    {
        return switch (pieceType) {
            case "Ram" -> new Ram(color);
            case "Biz" -> new Biz(color);
            case "Tor" -> new Tor(color);
            case "Xor" -> new Xor(color);
            case "Sau" -> new Sau(color);
            default -> null;
        };
    }

}