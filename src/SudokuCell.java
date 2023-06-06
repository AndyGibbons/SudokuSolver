package com.company;

public class SudokuCell {

    int value = 0;
    int iPos;
    int jPos;
    static int gridSize = 9;
    boolean[] possibleValues = new boolean [gridSize];
    static boolean memberOfPair = false;

    public SudokuCell(int iPos, int jPos, int value) {
        this.iPos = iPos;
        this.jPos = jPos;
        this.value = value;
        if (value == 0) {
            for (int i = 0; i < gridSize; i++) {
                possibleValues[i] = true;
            }
        }
    }

    public void setValue (int valueIn){
        value = valueIn;
        for (int i=0; i < gridSize; i++)
            possibleValues[i] = false;
    }

    public int getValue () {
        return value;
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

    public void printCell(){
        System.out.println(value);
        System.out.println(iPos);
        System.out.println(jPos);
        System.out.println(gridSize);
        for (int k = 1; k <= gridSize; k++){
            if (getPossibleValue(k)){
                System.out.print(k + " ");
            }
        }
        System.out.println();
        System.out.println();
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

    public boolean isMemberOfPair(){
        return memberOfPair;
    }

    public void setMemberOfPair() {
        memberOfPair = true;
    }
}
