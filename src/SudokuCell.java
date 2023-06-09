import java.util.Arrays;

public class SudokuCell {

    int value;
    int iPos;
    int jPos;
    static int gridSize = 9;
    boolean[] possibleValues = new boolean [gridSize];

    public SudokuCell(int iPos, int jPos, int value) {
        this.iPos = iPos;
        this.jPos = jPos;
        this.value = value;
        if (value == 0) {
            Arrays.fill(possibleValues, true);
        }
    }

    public void setValue (int valueIn){
        value = valueIn;
        Arrays.fill(possibleValues, false);
    }

    public boolean getPossibleValue(int possValue) {
        return possibleValues[possValue - 1];
    }

    public void setPossibleValueTrue(int possValue) {
        possibleValues[possValue - 1] = true;
    }

    public void setPossibleValueFalse(int possValue) {
        possibleValues[possValue - 1] = false;
    }

    public void setNotPossibleValue(int possValue) {
        this.possibleValues[possValue - 1] = false;
    }

    public int getNumberOfPossibleSolutions() {
        int numberOfPossibleSolutions = 0;
        for (int i=0; i<gridSize; i++){
            if (possibleValues[i]){
                numberOfPossibleSolutions++;
            }
        }
        return numberOfPossibleSolutions;
    }

    public static int compare (SudokuCell cell1, SudokuCell cell2){
        int result = -1;
        for (int i = 0; i < gridSize; i++) {

            if (Boolean.compare(cell1.possibleValues[i], cell2.possibleValues[i]) != 0){
            return result;
            }
        }
        return ++result;
    }

}
