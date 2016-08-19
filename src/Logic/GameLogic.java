package Logic;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;

public class GameLogic
{
    private Player [] m_Players;
    private int m_CurrPlayer;
    private BoardInfo.BoardOptions[][]  m_WinBoard;
    private Instant m_StartTime;
    private ArrayList<int[]> m_RowBlocks, m_ColBlocks;

    public GameLogic(Instant i_StartTime, int i_Rows,int i_Cols, ArrayList<int[]> i_RowBlocks, ArrayList<int[]> i_ColBlocks, ArrayList<Cell> i_WinnerCells)
    {
        m_StartTime = i_StartTime;
        m_CurrPlayer = 0;
        m_Players = new Player[1]; // only one player, for now

        m_ColBlocks = i_ColBlocks;
        m_RowBlocks = i_RowBlocks;
        m_WinBoard = new BoardInfo.BoardOptions[i_Rows][i_Cols];
        enterVluesOfWinnerBoard(i_WinnerCells, i_Rows,i_Cols);

        initPlayers();
    }

    // Private Functions
    // This function is like the init board in boardinfo
    private void enterVluesOfWinnerBoard(ArrayList<Cell> i_WinnerCells,int i_Row, int i_Col)
    {
        for(int i = 0; i < i_Row; i++)
        {
            for(int j = 0; j < i_Col; j++)
            {
                m_WinBoard[i][j]= BoardInfo.BoardOptions.UNDEFINED;
            }
        }
        for(Cell winner: i_WinnerCells)
        {
            m_WinBoard[winner.getPoint().x - 1][winner.getPoint().y - 1] = BoardInfo.BoardOptions.BLACK;
        }
    }

    private void initPlayers() {
        for (Player m_player : m_Players) {
            
        }
    }

    private void passTurnToNextPlayer()
    {
        m_CurrPlayer = ++m_CurrPlayer % m_Players.length;
    }

    // Public Function
    public void AddNewMoveToPlayer(GameMove i_MovesToEnter)
    {
        m_Players[m_CurrPlayer].AddNewMoveUpdateBoard(i_MovesToEnter);
    }

    public void UndoLastMoveFromPlayer()
    {
        m_Players[m_CurrPlayer].UndoMove();
    }
    public void RedoLastMoveFromPlayer()
    {
        m_Players[m_CurrPlayer].RedoMove();
    }

    // GET / SET
    public BoardInfo getCurrentBoard() {
        return m_Players[m_CurrPlayer].getBoard();
    }
    public LinkedList<GameMove> getAllMovesFromPlayer(){
        return m_Players[m_CurrPlayer].getAllMoves();
    }
    public Player getPlayer() {
        return m_Players[m_CurrPlayer];
    }
    public Instant getStartGameTime() { return m_StartTime; }
    public int getAllMovesCounter() { return m_Players[m_CurrPlayer].getAllMovesCounter(); }
    public int getUndoMovesCounter() { return m_Players[m_CurrPlayer].getUndoMovesCounter(); }
    public float getScore() { return m_Players[m_CurrPlayer].getScore(); }
}