package Logic;

import java.awt.*;
import java.util.LinkedList;

public class GameMove
{
    private LinkedList<Cell> m_Cells;
    private BoardInfo.Directions m_Directions = BoardInfo.Directions.EMPTY;

    // cstor
    public GameMove(LinkedList<Cell> i_Cells)
    {
        m_Cells = i_Cells;
    }

    public GameMove(LinkedList<Cell> i_Cells, BoardInfo.Directions i_Directions) {
        m_Cells = i_Cells;
        m_Directions = i_Directions;
    }

    @Override
    public String toString()
    {
        int numOfCells = m_Cells.size();
        BoardInfo.BoardOptions status = m_Cells.getFirst().getStatus();
        Point startingPoint = m_Cells.getFirst().getPoint();

        String outputString = "From" + startingPoint.toString()  +
                              "take " + numOfCells + " cells with direction " +
                               m_Directions.toString() + " to be " +
                              status.toString() + ".";
        return outputString;
    }

    // GET / SET
    public LinkedList<Cell> getCells() {
        return m_Cells;
    }
    public BoardInfo.Directions getDirections() {
        return m_Directions;
    }
}