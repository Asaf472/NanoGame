package Logic;

import java.awt.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;

public class BoardInfo
{
    private int m_Rows;
    private int m_Cols;
    private BoardOptions[][] m_Board;
    private ArrayList<List<Integer>> m_RowsBlocks;
    private ArrayList<List<Integer>> m_ColsBlocks;

    public static enum BoardOptions
    {
        UNDEFINED, EMPTY, BLACK;

        @Override
        public String toString()
        {
            if(this.equals(UNDEFINED)) {
                return "?";
            }
            else if(this.equals(EMPTY)){
                return " ";
            }
            else{
                return "X";
            }
        }
        public static BoardOptions Parse(String i_StrToCheck) throws InputMismatchException
        {
            if(i_StrToCheck.length() != 1) {
                throw new InputMismatchException();
            }
            else
            {
                char checkInput = i_StrToCheck.toCharArray()[0];
                switch (checkInput){
                    case 'B':
                        return BoardOptions.BLACK;
                    case 'E':
                        return BoardOptions.EMPTY;
                    case 'U':
                        return BoardOptions.UNDEFINED;
                    default:
                        throw new InputMismatchException();
                }
            }
        }
    }

    public static enum Directions
    {
        RIGHT, DOWN, EMPTY;

        @Override
        public String toString()
        {
            if(this.equals(DOWN)) {
                return "DOWN;";
            }
            else if(this.equals(RIGHT)){
                return "RIGHT";
            }
            else {
                return "EMPTY";
            }
        }

        public static Directions Parse(String i_StrToCheck)
        {
            if(i_StrToCheck.length() != 1) {
                throw new InputMismatchException();
            }
            else
            {
                char checkInput = i_StrToCheck.toCharArray()[0];
                switch (checkInput){
                    case 'D':
                        return Directions.DOWN;
                    case 'R':
                        return Directions.RIGHT;
                    default:
                        throw new InputMismatchException();
                }
            }
        }
    }

    // cstor
    public BoardInfo(int i_Rows, int i_Cols)
    {
        m_Rows = i_Rows;
        m_Cols = i_Cols;
        m_Board = new BoardOptions[m_Rows][m_Cols];
        m_RowsBlocks = new ArrayList<>();
        m_ColsBlocks = new ArrayList<>();

        initBoard();
    }

    private void initBoard()
    {
        for (int currRow = 0; currRow < m_Rows; currRow++) {
            for (int currCol = 0; currCol < m_Cols; currCol++) {
                m_Board[currCol][currRow] = BoardOptions.UNDEFINED;
            }
        }
    }

    // Public Functions
    public LinkedList<Cell> backUpCellForUndo(GameMove i_movesToEnter)
    {
        LinkedList<Cell> cellsBeforeNewMove = new LinkedList<>();
        int row, col;
        for (Cell cell : i_movesToEnter.getCells()) {
            row = cell.getPoint().x;
            col = cell.getPoint().y;
            cellsBeforeNewMove.add(new Cell(row,col,m_Board[row][col]));
        }
        return cellsBeforeNewMove;
    }

    public void EnterNewMoveToBoard(LinkedList<Cell> i_CellsToEnter)
    {
        for (Cell currCel : i_CellsToEnter) {
            Point cellPoint = currCel.getPoint();
            m_Board[cellPoint.x][cellPoint.y] = currCel.getStatus();
        }
        /** need to invoke function to tell the ui that we updated**/
    }

    // Get / Set
    public int getBoardHeight(){
        return m_Rows;
    }
    public int getBoardWidth (){
        return m_Cols;
    }
}
