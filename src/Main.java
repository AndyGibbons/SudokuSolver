import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

class SudokuSolver {

    private static final String fileName = "Sudoku5.txt";
    private static String puzzleLine = null;
    private static final String delimiter = ",";
    private static final int gridSize = 9;
    private static int i = 0;
    private static final int[][] grid = new int[gridSize][gridSize];
    private static SudokuCell[][] puzzleGrid;
    private static SudokuCell[][] puzzleGridCopy;


    public static void main(String[] args) {

        readInNumberGrid();
        // create the Sudoku puzzle grid
        puzzleGrid = new SudokuCell[gridSize][gridSize];
        //create a copy of the puzzle grid to use for solution guessing (if required)
        puzzleGridCopy = new SudokuCell[gridSize][gridSize];
        populatePuzzleGrid();
        System.out.println("SUDOKU Solver");
        System.out.println("=============");
        System.out.println();
        System.out.println("Starting position:");
        PrintPuzzleGridValues();
        boolean validPuzzle = validatePuzzleGrid();
        System.out.println("Valid puzzle? = " + validPuzzle);
        if (validPuzzle) {
            boolean solved = solvePuzzle();
            if (solved) {
                System.out.println("Puzzle solved!");
            }else{
                solved = startGuessSolution();
                if (solved) {
                    System.out.println("Puzzle solved - after guessing!");
                }else{
                    System.out.println("Puzzle NOT solved!");
                }
            }

        }

        System.out.println("Final position:");
        PrintPuzzleGridValues();
    }

    private static void readInNumberGrid() {
        // Input puzzle
        String[] puzzleEntries;
        try {
            File puzzleIn = new File(fileName);
            Scanner myReader = new Scanner(puzzleIn);

            while (myReader.hasNextLine()) {
                //read next line of puzzle
                puzzleLine = myReader.nextLine();

                // remove square brackets
                puzzleLine = puzzleLine.replaceAll("\\[", "").replaceAll("]", "");

                // split line input into String array
                puzzleEntries = puzzleLine.split(delimiter);

                //if line of number
                if (puzzleEntries.length > 1) {

                    for (int j = 0; j < 9; j++) {
                        grid[i][j] = Integer.parseInt(puzzleEntries[j].trim());
                    }
                    i++;
                }
            }

            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("The given file does not exist.");
        }

        if (puzzleLine == null) {
            System.out.println("Input file is empty");
        }
    }

    private static void populatePuzzleGrid() {
        // populate the puzzle grid with cell objects
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                puzzleGrid[i][j] = new SudokuCell(i, j, grid[i][j]);
            }
        }
    }

    private static boolean validatePuzzleGrid() {
        //confirm the puzzle grid represents a valid Sudoku puzzle
        //ensure each row, column and 3x3 grid has no duplicate numbers
        boolean[] validPuzzle = new boolean[9];
        boolean[] usedValues = new boolean[gridSize];

        //check rows
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (puzzleGrid[i][j].value != 0 && usedValues[puzzleGrid[i][j].value - 1]) {
                    return false;
                }
                if (puzzleGrid[i][j].value != 0) {
                    usedValues[puzzleGrid[i][j].value - 1] = true;
                }
            }
            //reset usedValues array
            Arrays.fill(usedValues, false);
        }

        //check columns
        for (int j = 0; j < gridSize; j++) {
            for (int i = 0; i < gridSize; i++) {
                if (puzzleGrid[i][j].value != 0 && usedValues[puzzleGrid[i][j].value - 1]) {
                    return false;
                }
                if (puzzleGrid[i][j].value != 0) {
                    usedValues[puzzleGrid[i][j].value - 1] = true;
                }
            }
            //reset usedValues array
            Arrays.fill(usedValues, false);
        }

        validPuzzle[0] = valid3x3(0, 0);
        validPuzzle[1] = valid3x3(0, 3);
        validPuzzle[2] = valid3x3(0, 6);
        validPuzzle[3] = valid3x3(3, 0);
        validPuzzle[4] = valid3x3(3, 3);
        validPuzzle[5] = valid3x3(3, 6);
        validPuzzle[6] = valid3x3(6, 0);
        validPuzzle[7] = valid3x3(6, 3);
        validPuzzle[8] = valid3x3(6, 6);

        for (int i = 0; i < 9; i++) {
            if (!validPuzzle[i]) {
                return false;
            }
        }
        return true;
    }

    private static boolean valid3x3(int iStart, int jStart) {
        //check each 3 x 3 grid has no repeated numbers

        boolean[] usedValues = new boolean[gridSize];

        for (int i = iStart; i < iStart + 3; i++) {
            for (int j = jStart; j < jStart + 3; j++) {
                if (puzzleGrid[i][j].value != 0 && usedValues[puzzleGrid[i][j].value - 1]) {
                    return false;
                }
                if (puzzleGrid[i][j].value != 0) {
                    usedValues[puzzleGrid[i][j].value - 1] = true;
                }
            }
        }
        return true;
    }

    private static boolean solvePuzzle() {

        int limit = 0;

        while (!isSolved() && limit < 10) {

            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    if (puzzleGrid[i][j].value == 0) {
                        findPossibleValues(puzzleGrid[i][j]);
                    }
                }
            }

            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    if (puzzleGrid[i][j].value == 0) {
                        findPairSolutions(puzzleGrid[i][j]);
                    }
                }
            }

            checkForSolutionsInTripleRows();
            checkForSolutionsInTripleColumns();

            limit++;
        }
        return isSolved();
    }

    private static void findPossibleValues(SudokuCell currentCell) {
        // apply "pencil marks" to each of the cells in the puzzle of possible answers for that cell
        //check row
        for (int i = currentCell.iPos; i == currentCell.iPos; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (currentCell.jPos != j) {
                    if (puzzleGrid[i][j].value != 0) {
                        puzzleGrid[currentCell.iPos][currentCell.jPos].setNotPossibleValue(puzzleGrid[i][j].value);
                    }
                }
            }
        }

        //check column
        for (int j = currentCell.jPos; j == currentCell.jPos; j++) {
            for (int i = 0; i < gridSize; i++) {
                if (currentCell.iPos != i) {
                    if (puzzleGrid[i][j].value != 0) {
                        puzzleGrid[currentCell.iPos][currentCell.jPos].setNotPossibleValue(puzzleGrid[i][j].value);
                    }
                }
            }

        }
        // check 3 x 3 grid

        if (currentCell.iPos < 3 && currentCell.jPos < 3) {
            findIn3x3(currentCell, 0, 0);
        } else {
            if (currentCell.iPos < 3 && currentCell.jPos < 6) {
                findIn3x3(currentCell, 0, 3);
            } else {
                if (currentCell.iPos < 3 && currentCell.jPos < 9) {
                    findIn3x3(currentCell, 0, 6);
                } else {
                    if (currentCell.iPos < 6 && currentCell.jPos < 3) {
                        findIn3x3(currentCell, 3, 0);
                    } else {
                        if (currentCell.iPos < 6 && currentCell.jPos < 6) {
                            findIn3x3(currentCell, 3, 3);
                        } else {
                            if (currentCell.iPos < 6 && currentCell.jPos < 9) {
                                findIn3x3(currentCell, 3, 6);
                            } else {
                                if (currentCell.iPos < 9 && currentCell.jPos < 3) {
                                    findIn3x3(currentCell, 6, 0);
                                } else {
                                    if (currentCell.iPos < 9 && currentCell.jPos < 6) {
                                        findIn3x3(currentCell, 6, 3);
                                    } else {
                                        if (currentCell.iPos < 9 && currentCell.jPos < 9) {
                                            findIn3x3(currentCell, 6, 6);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (currentCell.value == 0 && currentCell.getNumberOfPossibleSolutions() == 1) {
            for (int k = 1; k <= gridSize; k++) {
                if (puzzleGrid[currentCell.iPos][currentCell.jPos].getPossibleValue(k)) {
                    puzzleGrid[currentCell.iPos][currentCell.jPos].setValue(k);
                    removeValueFromPossibleValueLists(currentCell, k);
                }
            }
        }
    }

    private static void findIn3x3(SudokuCell currentCell, int iStart, int jStart) {
        //check relevant 3 x 3 for possible answers

        for (int i = iStart; i < iStart + 3; i++) {
            for (int j = jStart; j < jStart + 3; j++) {
                if (currentCell.iPos != i && currentCell.jPos != j) {
                    if (puzzleGrid[i][j].value != 0) {
                        puzzleGrid[currentCell.iPos][currentCell.jPos].setNotPossibleValue(puzzleGrid[i][j].value);
                    }
                }
            }
        }
    }

    private static void findPairSolutions(SudokuCell currentCell) {
        // use "pencil marks" to find solution for cell
        // Look for pairs of possible answers in the cells. If two cells in a row, column,
        // or 3x3 grid have the same pair, these values can be removed from possible values
        // of all other cells
        int aPair = 2;
        for (int k = 1; k <= gridSize; k++) {
            if (currentCell.getPossibleValue(k)) {

                //check row
                if (currentCell.getNumberOfPossibleSolutions() == aPair) {
                    for (int j = 0; j < gridSize; j++) {
                        if (currentCell.jPos != j && puzzleGrid[currentCell.iPos][j].value == 0) {
                            if (SudokuCell.compare(puzzleGrid[currentCell.iPos][j], currentCell) == 0) {
                                removePairFromOtherCellsInRow(currentCell.iPos, j, k);
                            }
                        }
                    }
                }

                //check column
                if (currentCell.getNumberOfPossibleSolutions() == aPair) {
                    for (int i = 0; i < gridSize; i++) {
                        if (currentCell.iPos != i && puzzleGrid[i][currentCell.jPos].value == 0) {
                            if (SudokuCell.compare(puzzleGrid[i][currentCell.jPos], currentCell) == 0) {
                                removePairFromOtherCellsInColumn(i, currentCell.jPos, k);
                            }
                        }
                    }
                }

                // check 3 x 3 grid

                if (currentCell.iPos < 3 && currentCell.jPos < 3) {
                    checkForPairSolutionsIn3x3(currentCell, 0, 0, k);
                } else {
                    if (currentCell.iPos < 3 && currentCell.jPos < 6) {
                        checkForPairSolutionsIn3x3(currentCell, 0, 3, k);
                    } else {
                        if (currentCell.iPos < 3 && currentCell.jPos < 9) {
                            checkForPairSolutionsIn3x3(currentCell, 0, 6, k);
                        } else {
                            if (currentCell.iPos < 6 && currentCell.jPos < 3) {
                                checkForPairSolutionsIn3x3(currentCell, 3, 0, k);
                            } else {
                                if (currentCell.iPos < 6 && currentCell.jPos < 6) {
                                    checkForPairSolutionsIn3x3(currentCell, 3, 3, k);
                                } else {
                                    if (currentCell.iPos < 6 && currentCell.jPos < 9) {
                                        checkForPairSolutionsIn3x3(currentCell, 3, 6, k);
                                    } else {
                                        if (currentCell.iPos < 9 && currentCell.jPos < 3) {
                                            checkForPairSolutionsIn3x3(currentCell, 6, 0, k);
                                        } else {
                                            if (currentCell.iPos < 9 && currentCell.jPos < 6) {
                                                checkForPairSolutionsIn3x3(currentCell, 6, 3, k);
                                            } else {
                                                if (currentCell.iPos < 9 && currentCell.jPos < 9) {
                                                    checkForPairSolutionsIn3x3(currentCell, 6, 6, k);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void checkForPairSolutionsIn3x3(SudokuCell currentCell, int iStart, int jStart, int k) {
        //check relevant 3 x 3 for possible answers
        int aPair = 2;

        if (currentCell.getNumberOfPossibleSolutions() == aPair) {
            for (int i = iStart; i < iStart + 3; i++) {
                for (int j = jStart; j < jStart + 3; j++) {
                    if (!(currentCell.iPos == i && currentCell.jPos == j) && puzzleGrid[i][j].value == 0) {
                        if (SudokuCell.compare(puzzleGrid[i][j], currentCell) == 0) {
                            removePairFromOtherCellsIn3x3Grid(i, j, k, iStart, jStart);
                        }
                    }
                }
            }
        }
    }

    private static void removePairFromOtherCellsInRow(int row, int col, int possVal) {

        int secondPossVal = 0;

        for (int k = 1; k <= gridSize; k++) {
            if (puzzleGrid[row][col].getPossibleValue(k) && k != possVal) {
                secondPossVal = k;
                break;
            }
        }

        for (int j = 0; j < gridSize; j++) {
            if (j != col && puzzleGrid[row][j].value == 0 && SudokuCell.compare(puzzleGrid[row][j], puzzleGrid[row][col]) != 0) {
                puzzleGrid[row][j].setNotPossibleValue(possVal);
                puzzleGrid[row][j].setNotPossibleValue(secondPossVal);
            }
        }
    }

    private static void removePairFromOtherCellsInColumn(int row, int col, int possVal) {

        int secondPossVal = 0;

        for (int k = 1; k <= gridSize; k++) {
            if (puzzleGrid[row][col].getPossibleValue(k) && k != possVal) {
                secondPossVal = k;
                break;
            }
        }

        for (int i = 0; i < gridSize; i++) {
            if (i != row && puzzleGrid[i][col].value == 0 && SudokuCell.compare(puzzleGrid[i][col], puzzleGrid[row][col]) != 0) {
                puzzleGrid[i][col].setNotPossibleValue(possVal);
                puzzleGrid[i][col].setNotPossibleValue(secondPossVal);
            }
        }
    }

    private static void removePairFromOtherCellsIn3x3Grid(int row, int col, int possVal, int iStart, int jStart) {

        int secondPossVal = 0;

        for (int k = 1; k <= gridSize; k++) {
            if (puzzleGrid[row][col].getPossibleValue(k) && k != possVal) {
                secondPossVal = k;
                break;
            }
        }

        for (int i = iStart; i < iStart + 3; i++) {
            for (int j = jStart; j < jStart + 3; j++) {
                if (i != row && j != col && puzzleGrid[i][j].value == 0 && SudokuCell.compare(puzzleGrid[i][j], puzzleGrid[row][col]) != 0) {
                    puzzleGrid[i][j].setNotPossibleValue(possVal);
                    puzzleGrid[i][j].setNotPossibleValue(secondPossVal);
                }
            }
        }
    }

    private static void removeValueFromPossibleValueLists(SudokuCell currentCell, int valueToRemove) {

        if (currentCell.value != 0) {

            //remove value from possible value lists in the current cell's row
            for (int j = 0; j < gridSize; j++) {
                if (currentCell.jPos != j && puzzleGrid[currentCell.iPos][j].value == 0) {
                    puzzleGrid[currentCell.iPos][j].setNotPossibleValue(valueToRemove);
                }
            }

            //remove value from possible value lists in the current cell's column

            for (int i = 0; i < gridSize; i++) {
                if (currentCell.iPos != i && puzzleGrid[i][currentCell.jPos].value == 0) {
                    puzzleGrid[i][currentCell.jPos].setNotPossibleValue(valueToRemove);
                }
            }
            // remove value from possible value lists in the current cell's 3 x 3 grid

            if (currentCell.iPos < 3 && currentCell.jPos < 3) {
                removeValueFromPossibleValueListsIn3x3Grid(currentCell, 0, 0, valueToRemove);
            } else {
                if (currentCell.iPos < 3 && currentCell.jPos < 6) {
                    removeValueFromPossibleValueListsIn3x3Grid(currentCell, 0, 3, valueToRemove);
                } else {
                    if (currentCell.iPos < 3 && currentCell.jPos < 9) {
                        removeValueFromPossibleValueListsIn3x3Grid(currentCell, 0, 6, valueToRemove);
                    } else {
                        if (currentCell.iPos < 6 && currentCell.jPos < 3) {
                            removeValueFromPossibleValueListsIn3x3Grid(currentCell, 3, 0, valueToRemove);
                        } else {
                            if (currentCell.iPos < 6 && currentCell.jPos < 6) {
                                removeValueFromPossibleValueListsIn3x3Grid(currentCell, 3, 3, valueToRemove);
                            } else {
                                if (currentCell.iPos < 6 && currentCell.jPos < 9) {
                                    removeValueFromPossibleValueListsIn3x3Grid(currentCell, 3, 6, valueToRemove);
                                } else {
                                    if (currentCell.iPos < 9 && currentCell.jPos < 3) {
                                        removeValueFromPossibleValueListsIn3x3Grid(currentCell, 6, 0, valueToRemove);
                                    } else {
                                        if (currentCell.iPos < 9 && currentCell.jPos < 6) {
                                            removeValueFromPossibleValueListsIn3x3Grid(currentCell, 6, 3, valueToRemove);
                                        } else {
                                            if (currentCell.iPos < 9 && currentCell.jPos < 9) {
                                                removeValueFromPossibleValueListsIn3x3Grid(currentCell, 6, 6, valueToRemove);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    private static void removeValueFromPossibleValueListsIn3x3Grid(SudokuCell currentCell, int iStart, int jStart, int valueToRemove) {
        //remove value from possible value lists in the current cell's row
        for (int i = iStart; i < iStart + 3; i++) {
            for (int j = jStart; j < jStart + 3; j++) {
                if (currentCell.iPos != i && currentCell.jPos != j && puzzleGrid[currentCell.iPos][currentCell.jPos].value == 0) {
                    puzzleGrid[i][j].setNotPossibleValue(valueToRemove);
                }
            }
        }
    }

    private static void checkForSolutionsInTripleRows() {

        for (int k = 1; k <= gridSize; k++) {
            if (inAllThreeRows(0, k)) {
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        if (puzzleGrid[i][j].value == k) {
                            checkForValueInTripleRow(i, k);
                        }
                    }
                }
            }
        }

        for (int k = 1; k <= gridSize; k++) {
            if (inAllThreeRows(3, k)) {
                for (int i = 3; i < 5; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        if (puzzleGrid[i][j].value == k) {
                            checkForValueInTripleRow(i, k);
                        }
                    }
                }
            }
        }

        for (int k = 1; k <= gridSize; k++) {
            if (inAllThreeRows(6, k)) {
                for (int i = 6; i < 8; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        if (puzzleGrid[i][j].value == k) {
                            checkForValueInTripleRow(i, k);
                        }
                    }
                }
            }
        }
    }

    private static void checkForValueInTripleRow(int currentRow, int valueToCheckFor) {

        if (currentRow == 0 || currentRow == 3 || currentRow == 6) {

            int nextRow = currentRow + 1;
            int lastRow = currentRow + 2;

            for (int i = nextRow; i < lastRow; i++) {
                for (int j = 0; j < gridSize; j++) {
                    if (puzzleGrid[i][j].value == valueToCheckFor) {
                        if (i == nextRow) {
                            lookForValueInOtherRow(lastRow, valueToCheckFor);
                        } else {
                            lookForValueInOtherRow(nextRow, valueToCheckFor);
                        }
                        return;
                    }
                }
            }
        } else {

            int firstRow = currentRow - 1;
            int lastRow = currentRow + 1;

            for (int j = 0; j < gridSize; j++) {
                if (puzzleGrid[lastRow][j].value == valueToCheckFor) {
                    lookForValueInOtherRow(firstRow, valueToCheckFor);
                    return;
                }
            }
        }
    }

    private static void lookForValueInOtherRow(int row, int valueToLookFor) {

        int numberOfInstancesOfValue = 0;

        for (int j = 0; j < gridSize; j++) {
            if (puzzleGrid[row][j].value == 0) {
                if (puzzleGrid[row][j].getPossibleValue(valueToLookFor)) {
                    numberOfInstancesOfValue++;
                }
            }
        }
        if (numberOfInstancesOfValue == 1) {
            for (int j = 0; j < gridSize; j++) {
                if (puzzleGrid[row][j].value == 0) {
                    if (puzzleGrid[row][j].getPossibleValue(valueToLookFor)) {
                        puzzleGrid[row][j].setValue(valueToLookFor);
                        removeValueFromPossibleValueLists(puzzleGrid[row][j], valueToLookFor);
                    }
                }
            }
        }
    }

    private static boolean inAllThreeRows(int firstRow, int valueToLookFor) {
        boolean inFirstRow = false;
        boolean inSecondRow = false;
        boolean inThirdRow = false;
        int secondRow = firstRow + 1;
        int thirdRow = firstRow + 2;

        for (int j = 0; j < gridSize; j++) {
            if (puzzleGrid[firstRow][j].value == valueToLookFor) {
                inFirstRow = true;
                break;
            }
        }

        for (int j = 0; j < gridSize; j++) {
            if (puzzleGrid[secondRow][j].value == valueToLookFor) {
                inSecondRow = true;
                break;
            }
        }

        for (int j = 0; j < gridSize; j++) {
            if (puzzleGrid[thirdRow][j].value == valueToLookFor) {
                inThirdRow = true;
                break;
            }
        }

        return !inFirstRow || !inSecondRow || !inThirdRow;
    }

    private static void checkForSolutionsInTripleColumns() {

        for (int k = 1; k <= gridSize; k++) {
            if (inAllThreeColumns(0, k)) {
                for (int j = 0; j < 2; j++) {
                    for (int i = 0; i < gridSize; i++) {
                        if (puzzleGrid[i][j].value == k) {
                            checkForValueInTripleColumns(j, k);
                        }
                    }
                }
            }
        }

        for (int k = 1; k <= gridSize; k++) {
            if (inAllThreeColumns(3, k)) {
                for (int j = 3; j < 5; j++) {
                    for (int i = 0; i < gridSize; i++) {
                        if (puzzleGrid[i][j].value == k) {
                            checkForValueInTripleColumns(j, k);
                        }
                    }
                }
            }
        }

        for (int k = 1; k <= gridSize; k++) {
            if (inAllThreeColumns(6, k)) {
                for (int j = 6; j < 8; j++) {
                    for (int i = 0; i < gridSize; i++) {
                        if (puzzleGrid[i][j].value == k) {
                            checkForValueInTripleColumns(j, k);
                        }
                    }
                }
            }
        }
    }

    private static void checkForValueInTripleColumns(int currentColumn, int valueToCheckFor) {

        if (currentColumn == 0 || currentColumn == 3 || currentColumn == 6) {

            int nextColumn = currentColumn + 1;
            int lastColumn = currentColumn + 2;

            for (int j = nextColumn; j < lastColumn; j++) {
                for (int i = 0; i < gridSize; i++) {
                    if (puzzleGrid[i][j].value == valueToCheckFor) {
                        if (j == nextColumn) {
                            lookForValueInOtherColumns(lastColumn, valueToCheckFor);
                        } else {
                            lookForValueInOtherColumns(nextColumn, valueToCheckFor);
                        }
                        return;
                    }
                }
            }
        } else {

            int firstColumn = currentColumn - 1;
            int lastColumn = currentColumn + 1;

            for (int i = 0; i < gridSize; i++) {
                if (puzzleGrid[i][lastColumn].value == valueToCheckFor) {
                    lookForValueInOtherColumns(firstColumn, valueToCheckFor);
                    return;
                }
            }
        }
    }

    private static void lookForValueInOtherColumns(int column, int valueToLookFor) {

        int numberOfInstancesOfValue = 0;

        for (int i = 0; i < gridSize; i++) {
            if (puzzleGrid[i][column].value == 0) {
                if (puzzleGrid[i][column].getPossibleValue(valueToLookFor)) {
                    numberOfInstancesOfValue++;
                }
            }
        }
        if (numberOfInstancesOfValue == 1) {
            for (int i = 0; i < gridSize; i++) {
                if (puzzleGrid[i][column].value == 0) {
                    if (puzzleGrid[i][column].getPossibleValue(valueToLookFor)) {
                        puzzleGrid[i][column].setValue(valueToLookFor);
                        removeValueFromPossibleValueLists(puzzleGrid[i][column], valueToLookFor);
                    }
                }
            }
        }
    }

    private static boolean inAllThreeColumns(int firstColumn, int valueToLookFor) {
        boolean inFirstColumn = false;
        boolean inSecondColumn = false;
        boolean inThirdColumn = false;
        int secondColumn = firstColumn + 1;
        int thirdColumn = firstColumn + 2;

        for (int i = 0; i < gridSize; i++) {
            if (puzzleGrid[i][firstColumn].value == valueToLookFor) {
                inFirstColumn = true;
                break;
            }
        }

        for (int i = 0; i < gridSize; i++) {
            if (puzzleGrid[i][secondColumn].value == valueToLookFor) {
                inSecondColumn = true;
                break;
            }
        }

        for (int i = 0; i < gridSize; i++) {
            if (puzzleGrid[i][thirdColumn].value == valueToLookFor) {
                inThirdColumn = true;
                break;
            }
        }

        return !inFirstColumn || !inSecondColumn || !inThirdColumn;
    }

    private static boolean isSolved() {

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (puzzleGrid[i][j].value == 0) {
                    return false;
                }
            }
        }
        return validatePuzzleGrid();
    }

    private static void PrintPuzzleGridValues() {
        System.out.println("[");
        for (int i = 0; i < gridSize; i++) {
            System.out.print("\t[");
            for (int j = 0; j < gridSize - 1; j++) {
                System.out.print(puzzleGrid[i][j].value + ", ");
            }
            System.out.println(puzzleGrid[i][gridSize - 1].value +"],");
        }
        System.out.println("]");
    }

    private static void backupPuzzleGrid(){
        for (int i = 0 ; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                puzzleGridCopy[i][j] = new SudokuCell(i, j, puzzleGrid[i][j].value);
            }
        }
    }

    private static void restorePuzzleGrid(){
        for (int i = 0 ; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                puzzleGrid[i][j].setValue(puzzleGridCopy[i][j].value);
                if (puzzleGrid[i][j].value == 0) {
                    for (int k = 1; k <= gridSize; k++) {
                        if (puzzleGridCopy[i][j].getPossibleValue(k)) {
                            puzzleGrid[i][j].setPossibleValueTrue(k);
                        }else{
                            puzzleGrid[i][j].setPossibleValueFalse(k);
                        }
                    }
                }
            }
        }
    }

    private static boolean startGuessSolution() {
    // If the strategies used do not sole the puzzle, a guess is made as to the correct value for any cells which have just two
    // possible answers. The first possible value is tried, and the puzzle fed back into the solver. If that does not lead to a
    // solution, the other value is tried. This continues if necessary until all cells with a pair of possible solutions have been
    // tried. At this point, if no solution has been found, the puzzle cannot be solved by the solver.

        int aPair = 2;

        backupPuzzleGrid();
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (puzzleGrid[i][j].getNumberOfPossibleSolutions() == aPair) {
                    for (int k = 1; k <= gridSize; k++) {
                        if (puzzleGrid[i][j].getPossibleValue(k)) {
                            puzzleGrid[i][j].setValue(k);
                            removeValueFromPossibleValueLists(puzzleGrid[i][j], k);
                            if (solvePuzzle()){
                                return true;
                            }else{
                                restorePuzzleGrid();
                                if (solvePuzzle()) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}