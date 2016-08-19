package Interface;

import java.util.*;
import Logic.*;

import javafx.util.Pair;
import java.time.Duration;
import java.time.Instant;

import java.io.FileInputStream;
import java.io.InputStream;

import jaxb.schema.generated.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class UI
{
    private static final int NUMBER_OF_MENU_OPTIONS = 9, MIN_SIZE_BOARD = 10,MAX_SIZE_BOARD = 100;
    private enum EMenuOptions{INITIALIZE,
                            OPEN_XML_FILE,
                            START_GAME,
                            SHOW_PLAYER_BOARD
                            ,MAKE_MOVE,
                            SHOW_ALL_PLAYER_MOVES,
                            UNDO_LAST_MOVE,
                            REDO_MOVE,
                            GET_STATISTICS_OF_PLAYER,
                            EXIT_GAME}

    private boolean m_GameLoaded = false, m_GameStarted = false;
    private GameLogic m_GameLogic;
    private GameDescriptor m_GameData;

    public UI()
    {
        m_GameData = new GameDescriptor();
    }

    public boolean RunMenu()
{
    EMenuOptions userChoise;
    boolean finishGame;
    do {
        showMenu();
        userChoise = readValidUserChoiseMenuOption();
        finishGame = impementChoice(userChoise);
    } while(userChoise != EMenuOptions.EXIT_GAME || finishGame);
    return finishGame;
}

    private void showMenu()
    {
        StringBuilder menu= new StringBuilder(
                        "1. Open XML file"                   + System.lineSeparator() +
                        "2. Start Game"                      + System.lineSeparator() +
                        "3. Show player board"               + System.lineSeparator() +
                        "4. Make move"                       + System.lineSeparator() +
                        "5. Show all moves done by player"   + System.lineSeparator() +
                        "6. Undo last move"                  + System.lineSeparator() +
                        "7. Redo Move"                       + System.lineSeparator() +
                        "8. Get statistics player"           + System.lineSeparator() +
                        "9. Exit Game"                       + System.lineSeparator()
        );
        System.out.println(menu);
    }

    private EMenuOptions readValidUserChoiseMenuOption ()
    {
        boolean keepReadFromUser = true;
        Scanner scanner = new Scanner(System.in);
        EMenuOptions userChoise = EMenuOptions.INITIALIZE;
        String inputFromUser;
        Integer inputAsInt;

        System.out.println("Enter your menu choise: ");
        while (keepReadFromUser)
        {
            try {
                inputFromUser = scanner.nextLine();
                inputAsInt = Integer.parseInt(inputFromUser);
                if (inputAsInt >= 1 && inputAsInt <= NUMBER_OF_MENU_OPTIONS)
                {
                    userChoise = EMenuOptions.values()[inputAsInt];
                    if(checkVaildMenuChoise(userChoise)){
                        keepReadFromUser = false;
                    }
                }
                else System.out.println("Number must be 1 to " + NUMBER_OF_MENU_OPTIONS);
            }
            catch (NumberFormatException e) {
                System.out.println("Must Enter Number!");
            }
            catch (Exception e) {
                System.out.println("Error: general error in readValidUserChoiseMenuOption");
            }
        }
        return userChoise;
    }

    private boolean checkVaildMenuChoise(EMenuOptions userChoise) {
        boolean result = true;
        if (userChoise == EMenuOptions.EXIT_GAME){
            result = true;
        }
        else if (userChoise == EMenuOptions.OPEN_XML_FILE && m_GameLoaded == true){
            System.out.println("You Cant reload XML now.");
            result = false;
        }
        else if (userChoise == EMenuOptions.START_GAME && m_GameStarted == true){
            System.out.println("Game already started.");
            result = false;
        }
        else if((userChoise != EMenuOptions.OPEN_XML_FILE && userChoise != EMenuOptions.START_GAME) && (!m_GameLoaded && !m_GameStarted)) {  // if choose 3-8 in menu but not load and run game
            System.out.println("must load and start game before choose those actions.");
            result = false;
        }
        return result;
    }

    private boolean impementChoice(EMenuOptions i_UserChoise)
    {
        boolean finishGame = false;

        switch (i_UserChoise){
            case OPEN_XML_FILE:                    /// WHY EMenuOptions.OPEN_XML_FILE NOT GOOD?!?
                m_GameLoaded = loadXML();
                break;
            case START_GAME:
                m_GameStarted = true;
                startGame();
                break;
            case SHOW_PLAYER_BOARD:
                showCurrPlayerBoard();
                break;
            case MAKE_MOVE:
                makePlayerMove();
                break;
            case SHOW_ALL_PLAYER_MOVES:
                showAllMovesUntilNow();
                break;
            case UNDO_LAST_MOVE:
                undoLastMoveFromPlayer();
                break;
            case REDO_MOVE:
                ReDoLastMoveFromPlayer();
                break;
            case GET_STATISTICS_OF_PLAYER:
                printStatistics();
                break;
            case EXIT_GAME:
                finishGame = true;
                break;
        }
        return finishGame;
    }

    // Menu Option Number 1
    private boolean loadXML()
    {
        boolean returnValue;
        try {
            returnValue=(getAndCheckFileXml() && checkDataEnteredFromXML());
        } catch (Exception e) {
            System.out.print("Wrong data From XML :(");
            returnValue = false;
        }
        return returnValue;
    }

    private boolean checkDataEnteredFromXML() {
        return (CheckPlayersFromXml() && checkBoardDataFromXml()); // && m_GameData.getGameType().equals("Single Player")
    }

    private boolean checkBoardDataFromXml()
    {
        return (checkDefinitionFromXML()&& checkSolutionFromXML());
    }

    private boolean checkSolutionFromXML()
    {
        boolean rightData = true;
        try {
            int rows = m_GameData.getBoard().getDefinition().getRows().intValue();
            int cols = m_GameData.getBoard().getDefinition().getColumns().intValue();
            List<Square> solSquares = m_GameData.getBoard().getSolution().getSquare();
            for (Square currSquare : solSquares)
            {
                int currR, currC;
                currR = currSquare.getRow().intValue();
                currC = currSquare.getColumn().intValue();
                if (currR <= 0 || currR > rows || currC <= 0 || currC > cols) {
                    rightData = false;
                }
            }
        }catch (Exception e){
            rightData=false;
        }
        return rightData;
    }

    private boolean checkDefinitionFromXML()
    {
        int rows = m_GameData.getBoard().getDefinition().getRows().intValue();
        int cols = m_GameData.getBoard().getDefinition().getColumns().intValue();
        return (rows >= 0 && rows <= MAX_SIZE_BOARD && cols >= MIN_SIZE_BOARD && cols <= MAX_SIZE_BOARD && checkSlicesFromXML(rows,cols));
    }

    private boolean checkSlicesFromXML(int i_Row, int i_Col)
    {
        List<Slice> allSlices = m_GameData.getBoard().getDefinition().getSlices().getSlice();
        int countRow = 0, countCol = 0;
        boolean rightData = false;
        boolean[] idRows, idCols;
        idRows = new boolean[i_Row + 1];
        idCols = new boolean[i_Col + 1];
        try {
            if (allSlices.size() == (i_Row + i_Col))
            {
                for (Slice currSlice : allSlices)
                {
                    String orientation = currSlice.getOrientation();
                    if (orientation.equals("row"))
                    {
                        if (checkValueOfSlices(currSlice,i_Col)) {
                            countRow += idCorrectFromXML(1, i_Row, idRows, currSlice.getId().intValue());
                        }
                    } else if (orientation.equals("column"))
                    {
                        if (checkValueOfSlices(currSlice,i_Row)) {
                            countCol += idCorrectFromXML(1, i_Col, idCols, currSlice.getId().intValue());
                        }
                    }
                }
            }
            if (allSlices.size() == (countCol + countRow)) {
                rightData = true;
            }
        }
        catch (Exception e) {
            rightData = false;
        }
        finally {
            return rightData;
        }
    }

    private boolean checkValueOfSlices(Slice i_Slice,int i_CompParam)
    {
        boolean retValue = false;
        LinkedList<Integer> slices = new LinkedList<>();
        int sum = 0;
        String[] sizeB = i_Slice.getBlocks().trim().split(",");
        if(sizeB.length < (i_CompParam + 1) / 2)
        {
            for (String currSlice : sizeB)
            {
                currSlice=currSlice.trim();
                slices.addLast(Integer.parseInt((currSlice)));
                sum+=slices.getLast();
            }
            if(slices.size()+sum-1 <= i_CompParam)
            {
                retValue=true;
            }
        }
        return retValue;
    }

    private boolean CheckPlayersFromXml()
    {
        boolean rightData = false;
        boolean []idPlayers;
        int count = 0;
        List<jaxb.schema.generated.Player> allPlayers = m_GameData.getMultiPlayers().getPlayers().getPlayer();
        int amountOfPly = allPlayers.size();
        idPlayers = new boolean[amountOfPly+1];

        for(jaxb.schema.generated.Player currP : allPlayers)
        {
            if(!currP.getName().isEmpty() &&
                    (currP.getPlayerType().equals("Human")||currP.getPlayerType().equals("Computer")))
            {
                int plyID = currP.getId().intValue(); /** maybe trow exception */
                count += idCorrectFromXML(1, amountOfPly, idPlayers, plyID);
            }
        }
        if(count == amountOfPly){
            rightData=true;
        }
        return rightData;
    }

    private int idCorrectFromXML(int i_MinValue, int i_MaxValue,boolean[] i_IdArray, int i_CurrId)
    {
        int count = 0;
        if (i_CurrId >= i_MinValue && i_CurrId <= i_MaxValue && !i_IdArray[i_CurrId])
        {
            i_IdArray[i_CurrId] = true;
            count++;
        }
        return count;
    }

    public boolean getAndCheckFileXml() throws Exception
    {
        boolean fileCorrect = false;
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter the file path" + System.lineSeparator());
        String pathFile = scanner.nextLine();
        String s = "C:\\Users\\nicom\\Desktop\\Java Class\\Home Work\\gridler-mini-example-master.xml";
        if(!pathFile.isEmpty() && pathFile.endsWith(".xml"))
        {
            InputStream xmlFile = new FileInputStream(s);

            fromXmlFileToObject(xmlFile);
            fileCorrect = true;
        }
        return fileCorrect;
    }

    private  void fromXmlFileToObject(InputStream i_FiletoGetXml ) throws Exception
    {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(GameDescriptor.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            m_GameData = (GameDescriptor) jaxbUnmarshaller.unmarshal(i_FiletoGetXml);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

    // Menu Option Number 2
    private void startGame()
    {
        int rows = m_GameData.getBoard().getDefinition().getRows().intValue();
        int cols = m_GameData.getBoard().getDefinition().getColumns().intValue();
        ArrayList<Cell>solutionCells = new ArrayList<Cell>();
        ArrayList<int[]> rowBlocks, colsBlock;
        rowBlocks = new ArrayList<int[]>();
        colsBlock = new ArrayList<int[]>();
        getBlocks(rowBlocks,colsBlock);
        List<Square>solutionSquares = m_GameData.getBoard().getSolution().getSquare();
        for(Square currSquare :solutionSquares)
        {
            int currR = currSquare.getRow().intValue();
            int currC = currSquare.getColumn().intValue();
            solutionCells.add(new Cell(currR, currC, null));
        }
        m_GameLogic = new GameLogic(Instant.now(), rows, cols, rowBlocks, colsBlock, solutionCells);
    }

    private void getBlocks(ArrayList<int[]>i_RowBlocks,ArrayList<int[]>i_CollBlocks)
    {
        List<Slice>allSlices = m_GameData.getBoard().getDefinition().getSlices().getSlice();
        allSlices.sort((a,b)->a.getId().intValue()-b.getId().intValue());
        int indexArray;
        int [] block;
        for(Slice currSlice: allSlices)
        {
            String[] sizeB = currSlice.getBlocks().trim().split(",");
            indexArray = 0;
            block = new int[sizeB.length];
            for (String currBlock : sizeB) {
                currBlock = currBlock.trim();
                block[indexArray] = (Integer.parseInt((currBlock)));
                indexArray++;
            }
            if(currSlice.getOrientation().equals("row")){
                i_RowBlocks.add(block);
            }
            else{
                i_CollBlocks.add(block);
            }
        }
    }

    // Menu Option Number 3
    private void showCurrPlayerBoard() {

    }

    // Menu Option Number 4
    private void makePlayerMove()
    {
        Pair<LinkedList<Cell>, BoardInfo.Directions> newUserMove = readValidUserMove();
        if(newUserMove != null) {
            GameMove newGameMove = new GameMove(newUserMove.getKey(), newUserMove.getValue());
            m_GameLogic.AddNewMoveToPlayer(newGameMove);
        }
        else System.out.println("ERROR USER MOVE");
    }

    private Pair<LinkedList<Cell>, BoardInfo.Directions> readValidUserMove()
    {
        boolean keepReadFromUser = true;
        Scanner scanner = new Scanner(System.in);
        String inputFromUser;
        Pair<LinkedList<Cell>, BoardInfo.Directions> newMove = null;

        System.out.println("Enter new move:");
        while (keepReadFromUser)
        {
            try {
                inputFromUser = scanner.nextLine();
                newMove = getPlayerMove(inputFromUser);
                keepReadFromUser = false;
            }
            catch (NumberFormatException e) {
                System.out.println("Error: Must entered valid numbers!");
            }
            catch (IndexOutOfBoundsException e) {
                System.out.println("Error: Numbers are out of bounds!");
            }
            catch (InputMismatchException e) {
                System.out.println(e.getMessage());
            }
            catch (Exception e) {
                System.out.println("Error: general error in readValidUserMove");
            }
        }
        return newMove;
    }

    private Pair<LinkedList<Cell>, BoardInfo.Directions> getPlayerMove(String i_UserMoveInput) throws InputMismatchException, IndexOutOfBoundsException, NumberFormatException
    {
        StringTokenizer strToken = new StringTokenizer(i_UserMoveInput, ",");

        if (strToken.countTokens() != 5) {
            throw new InputMismatchException("Error: structure of move input is incorrect!");
        }

        Integer row = Integer.parseInt(strToken.nextToken());
        Integer col = Integer.parseInt(strToken.nextToken());
        Integer amount = Integer.parseInt(strToken.nextToken());
        String direction = strToken.nextToken();
        String status = strToken.nextToken();

        BoardInfo.Directions userChoiseDirection = BoardInfo.Directions.Parse(direction);
        BoardInfo.BoardOptions userChoiseStatus = BoardInfo.BoardOptions.Parse(status);

        checkValuesAreInRange(row, col, amount, userChoiseDirection);

        return createGameMove(row, col, amount, userChoiseDirection, userChoiseStatus);
    }

    private void checkValuesAreInRange(int row, int col, int amount, BoardInfo.Directions dir) throws InputMismatchException
    {
        int maxWidth = m_GameLogic.getPlayer().getBoard().getBoardWidth();
        int maxHeight = m_GameLogic.getPlayer().getBoard().getBoardHeight();

        if (row < 0 || col < 0 || amount < 0)
            throw new IndexOutOfBoundsException();

        if (BoardInfo.Directions.valueOf("DOWN").equals(BoardInfo.Directions.DOWN.toString())) {
            if(col + amount >= maxHeight) {
                throw new IndexOutOfBoundsException();
            }
        }
        else{ // if (BoardInfo.Directions.valueOf("RIGHT").equals(BoardInfo.Directions.RIGHT.toString()))
            if(row + amount >= maxWidth) {
                throw new IndexOutOfBoundsException();
            }
        }
    }

    private Pair<LinkedList<Cell>, BoardInfo.Directions> createGameMove(int i_row, int i_col, int i_amount, BoardInfo.Directions i_Dir, BoardInfo.BoardOptions i_Status)
    {
        LinkedList<Cell> newMove = new LinkedList<Cell>();
        boolean runOnRows;

        if (i_Dir.toString().equals(BoardInfo.Directions.DOWN.toString()))
            runOnRows = true;
        else runOnRows = false;

        for(int ind = 0; ind < i_amount; ind++)
        {
            if (runOnRows) newMove.add(new Cell(i_row + ind, i_col, i_Status));
            else           newMove.add(new Cell(i_row, i_col + ind, i_Status));
        }

        Pair<LinkedList<Cell>, BoardInfo.Directions> newGameMove =  new Pair<>(newMove, i_Dir);
        return newGameMove;
    }

    // Menu Option Number 5
    private void showAllMovesUntilNow()
    {
        LinkedList<GameMove> allMovesFromPlayer = m_GameLogic.getAllMovesFromPlayer();
        for (GameMove gameMove : allMovesFromPlayer) {
            System.out.println(gameMove.toString());
        }
    }

    // Menu Option Number 6
    private void undoLastMoveFromPlayer()
    {
        m_GameLogic.UndoLastMoveFromPlayer();
    }

    // Menu Option Number 7
    private void ReDoLastMoveFromPlayer()
    {
        m_GameLogic.RedoLastMoveFromPlayer();
    }

    // Menu Option Number 8
    private void printStatistics()
    {
        Instant checkTimer = Instant.now();
        Duration duration = Duration.between(m_GameLogic.getStartGameTime(), checkTimer);
        StringBuilder statistic = new  StringBuilder();

        statistic.append(m_GameLogic.getAllMovesCounter()+ " Moves till now." + System.lineSeparator() +
                m_GameLogic.getUndoMovesCounter() + " Undo action done." + System.lineSeparator() +
                "The game run " + duration.toString() + System.lineSeparator() +
                "Player points: "+ m_GameLogic.getScore() + System.lineSeparator());

        System.out.println(statistic.toString());
    }
}
