package Logic;

import java.util.LinkedList;
import java.util.Stack;


public class Player
{
    private int m_ID;
    private BoardInfo m_Board;
    private LinkedList<GameMove> m_AllMoves;
    private Stack<GameMove> m_UndoMoves;
    private GameMove m_RedoMove;
    private float m_Score;

    // cstor
    public Player(int i_ID, int i_Cols, int i_Rows)
    {
        m_ID = i_ID;
        m_Board = new BoardInfo(i_Rows, i_Cols);
        m_AllMoves = new LinkedList<>();
        m_UndoMoves = new Stack<>();
        m_RedoMove = null;
    }

    // protected abstract void doMove(GameMove i_CurrMove);     ???

    // Public Function
    public void AddNewMoveUpdateBoard(GameMove i_MovesToEnter)
    {
        // save cells before (to be able to do "Undo"
        LinkedList<Cell> cellBeforeNewMove = m_Board.backUpCellForUndo(i_MovesToEnter);
        m_UndoMoves.add(new GameMove((cellBeforeNewMove)));

        // save & aplly new GameMove
        m_AllMoves.add((i_MovesToEnter));
        m_Board.EnterNewMoveToBoard(i_MovesToEnter.getCells());

        m_RedoMove = null;   // only after Undo Can do Redo !!
    }

    public void UndoMove()
    {
        if (!m_UndoMoves.empty())
        {
            GameMove lastGameMove = m_UndoMoves.pop(); // take & delete
            m_RedoMove = lastGameMove;
            m_Board.EnterNewMoveToBoard(lastGameMove.getCells());
        }
    }

    public void RedoMove()
    {
        if (m_RedoMove != null)
        {
            m_Board.EnterNewMoveToBoard(m_RedoMove.getCells());
            m_AllMoves.add(m_RedoMove);
        }
    }

    // GET / SET
    public BoardInfo getBoard() {
        return m_Board;
    }
    public LinkedList<GameMove> getAllMoves() {
        return m_AllMoves;
    }
    public int getAllMovesCounter() { return m_AllMoves.size(); }
    public int getUndoMovesCounter() { return m_UndoMoves.size(); }
    public float getScore() { return m_Score; }
}