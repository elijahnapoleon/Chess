import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;

public class Chess {
	
	// Execute application
	public static void main(String args[]) {
		// Build the frame
  		JFrame frame = new JFrame("Chess");
  		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	

  		// Build the board
  		ChessBoard panel = new ChessBoard();

		//Build the menu
		ChessMenu menu = new ChessMenu();



  		// Add the panel to the frame and make it visible
  		frame.add(menu);
		frame.add(panel);
		frame.setResizable(false);
  		frame.setSize(574, 594);
  		frame.setVisible(true);
	}
}

// For storing coordinates of each piece
class Coordinates {
	private int rowCoordinate = 0, colCoordinate = 0;
	
	public void setRowCoordinate(int c) {
		rowCoordinate = c;
	}

	public void setColCoordinate(int c) {
		colCoordinate = c;
	}

	public int getRowCoordinate() {
		return rowCoordinate;
	}

	public int getColCoordinate() {
		return colCoordinate;
	}
}

class ChessMenu extends JPanel
{
    private Image background;

    public ChessMenu()
    {
    setSize(574,594);
    setLayout(new FlowLayout(FlowLayout.CENTER,500,55));
    try {
        background = ImageIO.read(getClass().getResourceAsStream("wood.png"));
    } catch (IOException e) {
    
        e.printStackTrace();
    }

        JLabel title;
        BufferedImage bimage;
        ImageIcon icon = new ImageIcon();
        try
        {
        bimage= ImageIO.read(getClass().getResourceAsStream("title.png"));
        
        Image image = bimage.getScaledInstance(200, 200, Image.SCALE_DEFAULT);
        icon = new ImageIcon(image);
        }
        catch
        (IOException e)
        {
            e.printStackTrace();
        }
		
        title = new JLabel();
        title.setIcon(icon);
        add(title);

        JLabel play = new JLabel();
        try
        {
        bimage= ImageIO.read(getClass().getResourceAsStream("play.png"));
        
        Image image = bimage.getScaledInstance(250, 63, Image.SCALE_DEFAULT);
        icon = new ImageIcon(image);
        }
        catch
        (IOException e)
        {
            e.printStackTrace();
        }  

        play.setIcon(icon);
        add(play);
       
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), null); 
    }
}



// Custom panel to draw on (Creates the board)
class ChessBoard extends JPanel {
	// Private data member variables
	 
	// For keeping track of rows and cols
	private int rows = 8, cols = 8;

	// Size and position of each square in the board
	private int sizeSquares = 70, squareX = 0, squareY = 0;

	// To keep track of the pieces and their positions
	private ImageIcon[][] pieces;
	private Coordinates[][] pieceCoordinates;
	private String[][] trueImagePaths;
	private String[][] colors;
	private int pieceSetX = 0, pieceSetY = 0;

	// For the current row
	private int currentRowSelected = 0, currentColSelected = 0;

	// Keep track of original position
	private int beginningX = 0, beginningY = 0;

	// To keep track of where the user clicks
	private int rowSelected = -1, colSelected = -1;

	// Height and width of the panel
	private int panelWidth = getWidth();
	private int panelHeight = getHeight();

	// For keeping track of player turns
	private boolean whiteToMove = true;
	private int moveTracker = 0;	
	private boolean onItself = false;

	// For castling
	private boolean moveWhiteRookRight = false;
	private boolean moveWhiteRookLeft = false;
	private boolean moveBlackRookRight = false;
	private boolean moveBlackRookLeft = false;

	private boolean moveKingWhite = false;
	private boolean moveKingBlack = false;

	// For determining if in check
	private boolean isinCheck = false;

	// For queening
	private boolean queenPawnWhite = false;
	private boolean queenPawnBlack = false;

	// Constructor
	public ChessBoard() {
		// Set up the pieces
		pieces = new ImageIcon[rows][cols];
		trueImagePaths = new String[rows][cols];
		pieceCoordinates = new Coordinates[rows][cols];
		colors = new String[rows][cols];

		int setX = 0;
		int setY = 0;

		// Set up the coordinates
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				pieceCoordinates[i][j] = new Coordinates();
				pieceCoordinates[i][j].setRowCoordinate(setX);
				pieceCoordinates[i][j].setColCoordinate(setY);

				// Move to the next square
				setX += sizeSquares;
			}
			// Move down a row
			setY += sizeSquares;
			setX = 0;
		}

		// Add the mouse listeners
				
		// Load the pieces for the board
		loadPieces();

		addMouseListener(new MouseAdapter() {
			// Override for clicking
			public void mousePressed (MouseEvent e) {
				// Get the position where user clicks
				int mouseX = e.getX();
				int mouseY = e.getY();

				// Check if clicked inside board
				if ((mouseX >= 0 && mouseX <= sizeSquares * 8) &&
					(mouseY >= 0 && mouseY <= sizeSquares * 8)) {
					// Get the icon that was clicked
					rowSelected = mouseY / sizeSquares;
					colSelected = mouseX / sizeSquares;

					beginningX = rowSelected * sizeSquares;
					beginningY = colSelected * sizeSquares;
				}
			}

			// Override for releasing
			public void mouseReleased(MouseEvent e) {
				// If valid mouse release

				// Get coordinates of current mouse location
				int mouseX = e.getX();
				int mouseY = e.getY();

				// Get the new row and column index
				if ((mouseX >= 0 && mouseX <= sizeSquares * 8) &&
					(mouseY >= 0 && mouseY <= sizeSquares * 8)) {
					// Get the icon that was clicked
					currentRowSelected = mouseY / sizeSquares;
					currentColSelected = mouseX / sizeSquares;
				}

				if (rowSelected != -1 && colSelected != -1) {
					// Check for valid move
	
					// Get the current path
					String tempPath = "";
					for (int i = 0; i < rows; i++) {
						for (int j = 0; j < cols; j++) {
							if (i == rowSelected && j == colSelected) {
								// Found the string
								tempPath = trueImagePaths[i][j];
							} 
						}
					} 

					// Check if moved onto itself
					if (currentRowSelected == rowSelected && currentColSelected == colSelected) 
						onItself = true;
					
					if (!isValid(tempPath)) {
						// Set the piece back to the original position
						pieceCoordinates[rowSelected][colSelected].setRowCoordinate(beginningY);
						pieceCoordinates[rowSelected][colSelected].setColCoordinate(beginningX);

						// Redraw the board and break out of the function
						repaint();
						return;
					}

					// Swap the coordinates tracking the current piece
					Coordinates tempCoordinate = pieceCoordinates[currentRowSelected][currentColSelected];
       					pieceCoordinates[currentRowSelected][currentColSelected] = pieceCoordinates[rowSelected][colSelected];
       		 			pieceCoordinates[rowSelected][colSelected] = tempCoordinate;
	
					// Get the centered coordinate of the piece
					int newRow = currentRowSelected * sizeSquares;	
					int newCol = currentColSelected * sizeSquares;

					// Set the centered coordinates
					pieceCoordinates[currentRowSelected][currentColSelected].setRowCoordinate(newCol);
					pieceCoordinates[currentRowSelected][currentColSelected].setColCoordinate(newRow);

					// Update the pieces array
					pieces[currentRowSelected][currentColSelected] = pieces[rowSelected][colSelected];
					pieces[rowSelected][colSelected] = null;
			
					// Update the image paths array
					trueImagePaths[currentRowSelected][currentColSelected] = trueImagePaths[rowSelected][colSelected];
					trueImagePaths[rowSelected][colSelected] = "";

					// Update the colors array
					colors[currentRowSelected][currentColSelected] = colors[rowSelected][colSelected];
					colors[rowSelected][colSelected] = "";

					// Check if king was castled
					if (moveWhiteRookRight == true) {
						swapPieces(rowSelected, colSelected + 1, currentRowSelected, currentColSelected + 1);
						moveWhiteRookRight = false;
					} else if (moveWhiteRookLeft == true) {
						swapPieces(rowSelected, colSelected - 1, currentRowSelected, currentColSelected - 2);
						moveWhiteRookLeft = false;
					} else if (moveBlackRookLeft == true) {
						swapPieces(rowSelected, colSelected + 1, currentRowSelected, currentColSelected + 1);
						moveBlackRookLeft = false;
					} else if (moveBlackRookRight == true) {
						swapPieces(rowSelected, colSelected - 1, currentRowSelected, currentColSelected - 2);
						moveBlackRookRight = false;
					}

					// Check if need to queen a pawn
					if (queenPawnWhite) {
						queenPawnWhite = false;

						// Update the image paths array
						trueImagePaths[currentRowSelected][currentColSelected] = new String("wQ.png");
						pieces[currentRowSelected][currentColSelected] = new ImageIcon(getClass().getResource("wQ.png"));
					} else if (queenPawnBlack) {
						queenPawnBlack = false;

						// Update the image paths array
						trueImagePaths[currentRowSelected][currentColSelected] = new String("bQ.png");
						pieces[currentRowSelected][currentColSelected] = new ImageIcon(getClass().getResource("bQ.png"));
					}
					
					// Reset row and column trackers
					rowSelected = -1;
					colSelected = -1;
		
					repaint();
				}
			}		
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			// Override for dragging
			public void mouseDragged(MouseEvent e) {
				int mouseX = 0, mouseY = 0;
				// Check for invalid index
				if (rowSelected != -1 && colSelected != -1) {
					// Get the mouse position
					mouseX = e.getX();
					mouseY = e.getY();

					// Get the beginning position
					int originalMouseX = pieceCoordinates[rowSelected][colSelected].getRowCoordinate(); 
					int originalMouseY = pieceCoordinates[rowSelected][colSelected].getColCoordinate(); 
					
					// Get the change as the user drags
					int changeX = mouseX - originalMouseX - sizeSquares / 2;
					int changeY = mouseY - originalMouseY - sizeSquares  / 2;

					// Update the position of the piece
					pieceCoordinates[rowSelected][colSelected].setRowCoordinate(originalMouseX + changeX);
					pieceCoordinates[rowSelected][colSelected].setColCoordinate(originalMouseY + changeY);

					repaint();
 				}
			}
		});
	}

	// For swapping two pieces positions
	public void swapPieces(int crs, int ccs, int rs, int cs) {
		// Swap the coordinates tracking the current piece
		Coordinates tempCoordinate = pieceCoordinates[crs][ccs];
       		pieceCoordinates[crs][ccs] = pieceCoordinates[rs][cs];
       		pieceCoordinates[rowSelected][colSelected] = tempCoordinate;
	
		// Get the centered coordinate of the piece
		int newRow = crs * sizeSquares;	
		int newCol = ccs * sizeSquares;

		// Set the centered coordinates
		pieceCoordinates[crs][ccs].setRowCoordinate(newCol);
		pieceCoordinates[crs][ccs].setColCoordinate(newRow);

		// Update the pieces array
		pieces[crs][ccs] = pieces[rs][cs];
		pieces[rs][cs] = null;
			
		// Update the image paths array
		trueImagePaths[crs][ccs] = trueImagePaths[rs][cs];
		trueImagePaths[rs][cs] = "";

		// Update the colors array
		colors[crs][ccs] = colors[rs][cs];
		colors[rs][cs] = "";
	}

	// For loading the pieces
	public void loadPieces() {
		// Array of image paths of the white and black pieces
		String[] imagePaths = {
			"bR.png", "bN.png", "bB.png", "bQ.png", "bK.png",
			"bB.png", "bN.png", "bR.png", "bP.png", "wR.png",
			"wN.png", "wB.png", "wQ.png", "wK.png", "wB.png",
			"wN.png", "wR.png", "wP.png"
		};

		// Load the original board
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				// Store the correct piece
				if (i == 0) {
					pieces[i][j] = new ImageIcon(getClass().getResource(imagePaths[j]));
					trueImagePaths[i][j] = new String(imagePaths[j]);

					// Store whether it's a white or black piece
					char letter = trueImagePaths[i][j].charAt(0);

					if (letter == 'w') 
						colors[i][j] = "white";
					else 
						colors[i][j] = "black";
				} else if (i == 1) {
					pieces[i][j] = new ImageIcon(getClass().getResource(imagePaths[8]));
					trueImagePaths[i][j] = new String(imagePaths[8]);

					// Store whether it's a white or black piece
					char letter = trueImagePaths[i][j].charAt(0);
					if (letter == 'w') 
						colors[i][j] = "white";
					else 
						colors[i][j] = "black";
				} else if (i == 6) {
					pieces[i][j] = new ImageIcon(getClass().getResource(imagePaths[17]));	
					trueImagePaths[i][j] = new String(imagePaths[17]);

					// Store whether it's a white or black piece
					char letter = trueImagePaths[i][j].charAt(0);
					if (letter == 'w') 
						colors[i][j] = "white";
					else 
						colors[i][j] = "black";
				} else if (i == 7) {
					pieces[i][j] = new ImageIcon(getClass().getResource(imagePaths[j + 9]));
					trueImagePaths[i][j] = new String(imagePaths[j + 9]);

					// Store whether it's a white or black piece
					char letter = trueImagePaths[i][j].charAt(0);
					if (letter == 'w') 
						colors[i][j] = "white";
					else 
						colors[i][j] = "black";
				} else { 
					pieces[i][j] = null;
					trueImagePaths[i][j] = new String(imagePaths[j]);
					colors[i][j] = "";
				}
			}
		}
	}
	
	// For checking if a move is valid
	public boolean isValid(String tempPath) {
		boolean moveValid = true;
		boolean isCorrectLoop = false;
		
		// Check for null image path
		if (tempPath == null) {
			moveValid = false;
			return moveValid;
		}

		// Get the current players turn
		if (moveTracker % 2 == 0)
			whiteToMove = true;
		else
			whiteToMove = false;

		// Check for correct piece clicked
		if (whiteToMove == false && tempPath.charAt(0) == 'w') {
			// It's black turn but a white piece was clicked
			moveValid = false;
			return moveValid;
		} else if (whiteToMove == true && tempPath.charAt(0) == 'b') {
			// It's white turn but a black piece was clicked
			moveValid = false;
			return moveValid;
		}
		
		// For next turn
		if (onItself) {
			onItself = false;
			moveValid = false;
			return moveValid;
		}
		
		// Check for piece outside the board
		if (currentRowSelected * sizeSquares > sizeSquares * 8 || currentColSelected * sizeSquares > sizeSquares * 8) {
			moveValid = false;
			return moveValid;
		}

		// Check each piece
		if (tempPath.equals("wP.png")) {
			// For white pawns
			
			// Check for queening
			if (currentRowSelected == 0) 	
				queenPawnWhite = true;

			// Check if diagonal capture
			if (colors[currentRowSelected][currentColSelected].equals("black")) {
				if (currentRowSelected * sizeSquares == rowSelected * sizeSquares - sizeSquares
					&& currentColSelected * sizeSquares == colSelected * sizeSquares - sizeSquares
					|| currentRowSelected * sizeSquares == rowSelected * sizeSquares - sizeSquares
					&& currentColSelected * sizeSquares == colSelected * sizeSquares + sizeSquares) {
					moveTracker++;
					return moveValid;
				}
			}

			// Check for moving forward
			if (!((currentRowSelected * sizeSquares == rowSelected * sizeSquares - sizeSquares && currentColSelected == colSelected)
				|| (currentRowSelected * sizeSquares == rowSelected * sizeSquares - (sizeSquares * 2) && currentColSelected == colSelected))) {
				moveValid = false;	
				return moveValid;
			}
		} else if (tempPath.equals("bP.png")) {
			// For black pawns
	
			// Check for queening
			if (currentRowSelected == 7)
				queenPawnBlack = true;

			// Check if diagonal capture
			if (colors[currentRowSelected][currentColSelected].equals("white")) {
				if (currentRowSelected * sizeSquares == rowSelected * sizeSquares + sizeSquares
					&& currentColSelected * sizeSquares == colSelected * sizeSquares - sizeSquares
					|| currentRowSelected * sizeSquares == rowSelected * sizeSquares + sizeSquares
					&& currentColSelected * sizeSquares == colSelected * sizeSquares + sizeSquares) {
					moveTracker++;
					return moveValid;
				}
			}

			// Check for moving forward
			if (!((currentRowSelected * sizeSquares == rowSelected * sizeSquares + sizeSquares && currentColSelected == colSelected)
				|| (currentRowSelected * sizeSquares == rowSelected * sizeSquares + (sizeSquares * 2) && currentColSelected == colSelected))) {
				moveValid = false;
				return moveValid;
			}
		} else if (tempPath.equals("wK.png") || tempPath.equals("bK.png")) {
			// For the kings

			// Check for same piece collisions 
			if (tempPath.equals("wK.png") && colors[currentRowSelected][currentColSelected] == "white") {
				moveValid = false;
				return moveValid;
			} else if (tempPath.equals("bK.png") && colors[currentRowSelected][currentColSelected] == "black") {
				moveValid = false;
				return moveValid;
			} 

			// Check for castling
			if (tempPath.equals("wK.png")) {
				if (currentRowSelected == rowSelected && currentColSelected * sizeSquares == colSelected * sizeSquares + (sizeSquares * 2)) {
					// Check for no pieces in the way
					if (pieces[7][5] == null && pieces[7][6] == null && moveKingWhite == false) {
						moveValid = true;
						moveKingWhite = true;
						moveWhiteRookRight = true;
						moveTracker++;
						return moveValid;		
					}

				} else if (currentRowSelected == rowSelected && currentColSelected * sizeSquares == colSelected * sizeSquares - (sizeSquares * 2)) {
					// Check for no pieces in the way
					if (pieces[7][1] == null && pieces[7][2] == null && pieces[7][3] == null && moveKingWhite == false) {
						moveValid = true;
						moveKingWhite = true;
						moveWhiteRookLeft = true;	
						moveTracker++;
						return moveValid;		
					}
				}
			}

			if (tempPath.equals("bK.png")) {
				// Check for castling
				if (currentRowSelected == rowSelected && currentColSelected * sizeSquares == colSelected * sizeSquares + (sizeSquares * 2)) {
					// Check for no pieces in the way
					if (pieces[0][5] == null && pieces[0][6] == null && moveKingBlack == false) {
						moveValid = true;
						moveKingBlack = true;
						moveBlackRookLeft = true;
						moveTracker++;
						return moveValid;		
					}
				} else if (currentRowSelected == rowSelected && currentColSelected * sizeSquares == colSelected * sizeSquares - (sizeSquares * 2)) {
					// Check for no pieces in the way
					if (pieces[0][1] == null && pieces[0][2] == null && pieces[0][3] == null && moveKingBlack == false) {
						moveValid = true;
						moveKingBlack = true;
						moveBlackRookRight = true;
						moveTracker++;
						return moveValid;		
					}
				}

			}

			// Move the pawn
			if (!((currentRowSelected * sizeSquares == rowSelected * sizeSquares - sizeSquares && currentColSelected == colSelected)
				|| (currentRowSelected * sizeSquares == rowSelected * sizeSquares + sizeSquares && currentColSelected == colSelected)
				|| (currentColSelected * sizeSquares == colSelected * sizeSquares + sizeSquares && currentRowSelected == rowSelected)
				|| (currentColSelected * sizeSquares == colSelected * sizeSquares - sizeSquares && currentRowSelected == rowSelected)
				|| (currentRowSelected * sizeSquares == rowSelected * sizeSquares - sizeSquares 
			
				// Account for diagnals
				&& currentColSelected * sizeSquares == colSelected * sizeSquares - sizeSquares)
				|| (currentRowSelected * sizeSquares == rowSelected * sizeSquares + sizeSquares 
				&& currentColSelected * sizeSquares == colSelected * sizeSquares - sizeSquares)
				|| (currentRowSelected * sizeSquares == rowSelected * sizeSquares - sizeSquares 
				&& currentColSelected * sizeSquares == colSelected * sizeSquares + sizeSquares)
				|| (currentRowSelected * sizeSquares == rowSelected * sizeSquares + sizeSquares 
				&& currentColSelected * sizeSquares == colSelected * sizeSquares + sizeSquares))) {
				moveValid = false;	
			} else 
				moveKingWhite = true;
		} else if (tempPath.equals("wR.png") || tempPath.equals("bR.png")) {
			// For the rooks

			// Check for same piece collisions 
			if (tempPath.equals("wR.png") && colors[currentRowSelected][currentColSelected] == "white") {
				moveValid = false;
				return moveValid;
			} else if (tempPath.equals("bR.png") && colors[currentRowSelected][currentColSelected] == "black") {
				moveValid = false;
				return moveValid;
			} 

			for (int i = 1; i < 8; i++) {
				// Check for piece outside the board
				if (currentRowSelected * sizeSquares > sizeSquares * 8 || currentColSelected * sizeSquares > sizeSquares * 8) {
					moveValid = false;
					return moveValid;
				} else {
					if (((currentRowSelected * sizeSquares == rowSelected * sizeSquares - (sizeSquares * i) && currentColSelected == colSelected)
						|| (currentRowSelected * sizeSquares == rowSelected * sizeSquares + (sizeSquares * i) && currentColSelected == colSelected)
						|| (currentColSelected * sizeSquares == colSelected * sizeSquares + (sizeSquares * i) && currentRowSelected == rowSelected)
						|| (currentColSelected * sizeSquares == colSelected * sizeSquares - (sizeSquares * i) && currentRowSelected == rowSelected))) {
						// Correct position found
						isCorrectLoop = true;	
						break;
					}
				}
			} 

			// If a correct position wasn't found
			if (!isCorrectLoop) {
				moveValid = false;
				return moveValid;
			}
		} else if (tempPath.equals("wB.png") || tempPath.equals("bB.png")) {
			// For the bishops 

			// Check for same piece collisions 
			if (tempPath.equals("wB.png") && colors[currentRowSelected][currentColSelected] == "white") {
				moveValid = false;
				return moveValid;
			} else if (tempPath.equals("bB.png") && colors[currentRowSelected][currentColSelected] == "black") {
				moveValid = false;
				return moveValid;
			} 

			for (int i = 1; i < 8; i++) {
				// Check for piece outside the board
				if (currentRowSelected * sizeSquares > sizeSquares * 8 || currentColSelected * sizeSquares > sizeSquares * 8) {
					moveValid = false;
					return moveValid;
				} else {
					if (((currentRowSelected * sizeSquares == rowSelected * sizeSquares - (sizeSquares * i)
						&& currentColSelected * sizeSquares == colSelected * sizeSquares - (sizeSquares * i))
						|| (currentRowSelected * sizeSquares == rowSelected * sizeSquares + (sizeSquares * i) 
						&& currentColSelected * sizeSquares == colSelected * sizeSquares + (sizeSquares * i))
						|| (currentColSelected * sizeSquares == colSelected * sizeSquares + (sizeSquares * i) 
						&& currentRowSelected * sizeSquares == rowSelected * sizeSquares - (sizeSquares * i))
						|| (currentColSelected * sizeSquares == colSelected * sizeSquares - (sizeSquares * i) 
						&& currentRowSelected * sizeSquares == rowSelected * sizeSquares + (sizeSquares * i)))) {
						// Correct position found
						isCorrectLoop = true;	
						break;
					}
				}
			} 

			// If a correct position wasn't found
			if (!isCorrectLoop) {
				moveValid = false;
				return moveValid;
			}

		} else if (tempPath.equals("wQ.png") || tempPath.equals("bQ.png")) {
			// For the Queens

			// Check for same piece collisions 
			if (tempPath.equals("wQ.png") && colors[currentRowSelected][currentColSelected] == "white") {
				moveValid = false;
				return moveValid;
			} else if (tempPath.equals("bQ.png") && colors[currentRowSelected][currentColSelected] == "black") {
				moveValid = false;
				return moveValid;
			} 

			// Check rows and cols
			for (int i = 1; i < 8; i++) {
				// Check for piece outside the board
				if (currentRowSelected * sizeSquares > sizeSquares * 8 || currentColSelected * sizeSquares > sizeSquares * 8) {
					moveValid = false;
					return moveValid;
				} else {
					if (((currentRowSelected * sizeSquares == rowSelected * sizeSquares - (sizeSquares * i) && currentColSelected == colSelected)
						|| (currentRowSelected * sizeSquares == rowSelected * sizeSquares + (sizeSquares * i) && currentColSelected == colSelected)
						|| (currentColSelected * sizeSquares == colSelected * sizeSquares + (sizeSquares * i) && currentRowSelected == rowSelected)
						|| (currentColSelected * sizeSquares == colSelected * sizeSquares - (sizeSquares * i) && currentRowSelected == rowSelected))) {
						// Correct position found
						isCorrectLoop = true;	
						break;
					}
				}
			} 

			// Check diagonals
			for (int i = 1; i < 8; i++) {
				// Check for piece outside the board
				if (currentRowSelected * sizeSquares > sizeSquares * 8 || currentColSelected * sizeSquares > sizeSquares * 8) {
					moveValid = false;
					return moveValid;
				} else {
					if (((currentRowSelected * sizeSquares == rowSelected * sizeSquares - (sizeSquares * i)
						&& currentColSelected * sizeSquares == colSelected * sizeSquares - (sizeSquares * i))
						|| (currentRowSelected * sizeSquares == rowSelected * sizeSquares + (sizeSquares * i) 
						&& currentColSelected * sizeSquares == colSelected * sizeSquares + (sizeSquares * i))
						|| (currentColSelected * sizeSquares == colSelected * sizeSquares + (sizeSquares * i) 
						&& currentRowSelected * sizeSquares == rowSelected * sizeSquares - (sizeSquares * i))
						|| (currentColSelected * sizeSquares == colSelected * sizeSquares - (sizeSquares * i) 
						&& currentRowSelected * sizeSquares == rowSelected * sizeSquares + (sizeSquares * i)))) {
						// Correct position found
						isCorrectLoop = true;	
						break;
					}
				}
			} 

			// If a correct position wasn't found
			if (!isCorrectLoop) {
				moveValid = false;
				return moveValid;
			}
		} else if (tempPath.equals("wN.png") || tempPath.equals("bN.png")) {
			// For the knights

			// Check for same piece collisions 
			if (tempPath.equals("wN.png") && colors[currentRowSelected][currentColSelected] == "white") {
				moveValid = false;
				return moveValid;
			} else if (tempPath.equals("bN.png") && colors[currentRowSelected][currentColSelected] == "black") {
				moveValid = false;
				return moveValid;
			} 

			// 8 posibilities for a knight jump
			if (currentRowSelected * sizeSquares == rowSelected * sizeSquares - (sizeSquares * 2)
				&& currentColSelected * sizeSquares == colSelected * sizeSquares - sizeSquares) {
				moveValid = true;
			} else if (currentRowSelected * sizeSquares == rowSelected * sizeSquares - (sizeSquares * 2)
				&& currentColSelected * sizeSquares == colSelected * sizeSquares + sizeSquares) {
				moveValid = true;
			} else if (currentRowSelected * sizeSquares == rowSelected * sizeSquares + (sizeSquares * 2)
				&& currentColSelected * sizeSquares == colSelected * sizeSquares - sizeSquares)  {
				moveValid = true;
			} else if (currentRowSelected * sizeSquares == rowSelected * sizeSquares + (sizeSquares * 2)
				&& currentColSelected * sizeSquares == colSelected * sizeSquares + sizeSquares)  {
				moveValid = true;
			} else if (currentColSelected * sizeSquares == colSelected * sizeSquares - (sizeSquares * 2)
				&& currentRowSelected * sizeSquares == rowSelected * sizeSquares - sizeSquares) {
				moveValid = true;
			} else if (currentColSelected * sizeSquares == colSelected * sizeSquares - (sizeSquares * 2)
				&& currentRowSelected * sizeSquares == rowSelected * sizeSquares + sizeSquares) {
				moveValid = true;
			} else if (currentColSelected * sizeSquares == colSelected * sizeSquares + (sizeSquares * 2)
				&& currentRowSelected * sizeSquares == rowSelected * sizeSquares - sizeSquares) {
				moveValid = true;
			} else if (currentColSelected * sizeSquares == colSelected * sizeSquares + (sizeSquares * 2)
				&& currentRowSelected * sizeSquares == rowSelected * sizeSquares + sizeSquares) {
				moveValid = true;
			} else {
				moveValid = false;
			}
		}

		// Go to the next turn
		moveTracker++;
		return moveValid;
	} 

	// Overried for drawing
	public void paintComponent(Graphics g) {
		// Call superclass's paintComponent
  		super.paintComponent(g);

 		// Cast g to Graphics 2D
  		Graphics2D g2d = (Graphics2D) g;  

		// Get width and height of panel
		int panelWidth = getWidth(), panelHeight = getHeight();

		// For getting the color of each square
		int determineColor = 0;
		
		// Reset variables
		squareX = 0;
		squareY = 0;
		sizeSquares = 70;

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				// Set the color
				if ((determineColor++) % 2 == 0)
					g2d.setColor(new Color(204, 153, 102));
				else 
					g2d.setColor(new Color(101, 67, 33));

				// Draw each square
				g2d.fillRect(squareX, squareY, sizeSquares, sizeSquares);

				// Move to the next square
				squareX += sizeSquares;
			}

			// Get the color pattern for the next row
			if (i % 2 == 0)
				determineColor = 1;
			else 
				determineColor = 0;

			// Move down a row
			squareY += sizeSquares;
			squareX = 0;
		}
		
		// Draw the pieces 
		for (int i = 0; i < rows; i++) {
      			for (int j = 0; j < cols; j++) {
				// Get each row and column position
         			int setPieceX = pieceCoordinates[i][j].getRowCoordinate();
            			int setPieceY = pieceCoordinates[i][j].getColCoordinate();

				// Draw the piece
		   		if (pieces[i][j] != null) 
                			pieces[i][j].paintIcon(this, g2d, setPieceX, setPieceY);
       			}
    		}	
	}
}