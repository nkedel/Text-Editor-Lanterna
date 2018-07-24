package com.example;

import com.example.Edit.EditOp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Buffer {
    private ArrayList<StringBuilder> lineList;
    private LinkedList<Edit> undoList;
    private boolean editMode = false; //previne loops no undo
    private int cursorRow, cursorCol;
    private int markRow, markCol;
    private boolean marked = false;
    private StringBuilder clipBoard;

    Buffer() {
        StringBuilder str = new StringBuilder();
        lineList = new ArrayList<>();
        lineList.add(str);
        cursorRow = 0;
        cursorCol = 0;
        undoList = new LinkedList<>();
    }

    public static void main(String[] args) {

    }

    void setMark(int line, int col) {
        System.out.println("set mark");
        markRow = line;
        markCol = col;
        marked = true;
        System.out.println(markRow + "," + markCol);
        System.out.println("set mark");
    }

    private void unsetMark() {
        marked = false;
    }

    void copy() {
        System.out.println("copy");
        if (marked) {
            clipBoard = new StringBuilder();
            int index, limit;
            for (int i = markRow; i <= cursorRow; i++) {
                if (i == markRow) index = markCol;
                else index = 0;
                if (i == cursorRow) {
                    limit = cursorCol;
                } else {
                    limit = lineList.get(i).length();
                }
                for (int j = index; j < limit; j++) {
                    clipBoard.append(lineList.get(i).charAt(j));
                }
            }
            unsetMark();
        }
    }

    void paste() {
        System.out.println("paste");
        if (clipBoard != null)
            if (clipBoard.length() > 0)
                for (int i = 0; i < clipBoard.length(); i++) {
                    lineList.get(cursorRow).insert(cursorCol + i, clipBoard.charAt(i));
                }
    }

    void cut() {
        System.out.println("cut");
        if (marked) {
            int index, limit;
            for (int i = markRow; i <= cursorRow; i++) {
                if (i == markRow) index = markCol;
                else index = 0;
                if (i == cursorRow) {
                    limit = cursorCol;
                } else {
                    limit = lineList.get(i).length();
                }
                for (int j = index; j < limit; j++) {
                    lineList.get(i).deleteCharAt(j);
                }
            }
        }
        unsetMark();
    }

    List<StringBuilder> getLineList() {
        return Collections.unmodifiableList(lineList);
    }

    public void insert(char c) {
        if (c != '\n') {
            lineList.get(cursorRow).insert(cursorCol, c);
            cursorCol++;
        } else { //criar uma linha nova
            StringBuilder str = new StringBuilder();
            lineList.add(cursorRow + 1, str);

            String s = lineList.get(cursorRow).substring(cursorCol, lineList.get(cursorRow).length());

            lineList.get(cursorRow).delete(cursorCol, lineList.get(cursorRow).length());
            lineList.get(cursorRow + 1).append(s);
            cursorCol = 0;
            cursorRow++;
        }
        if (!editMode) {
            Edit e = new Edit(EditOp.INSERT, c, cursorRow, cursorCol);
            undoList.add(e);
        }

    }

    void newLine() {
        insert('\n');
        if (!editMode) {
            Edit e = new Edit(EditOp.INSERT, '\n', cursorRow, cursorCol);
            undoList.add(e);
        }
    }

    void delete() {
        char c = ' ';
        if ((cursorCol == 0) && (cursorRow > 0)) { //mover cursor para o final da linha anterior
            cursorRow--;
            cursorCol = lineList.get(cursorRow).length();
            lineList.get(cursorRow).append(lineList.remove(cursorRow + 1)); //mover a linha atual para o final da linha anterior
            c = '\n';
        } else if (cursorCol > 0) { //apagar caracter antes do cursor
            c = lineList.get(cursorRow).charAt(cursorCol - 1);
            lineList.get(cursorRow).deleteCharAt(cursorCol - 1);
            cursorCol--;
        }
        if (!editMode) { //Nao podemos fazer undo dum undo - entra em ciclo
            Edit e = new Edit(EditOp.DELETE, c, cursorRow, cursorCol);
            undoList.add(e);
        }

    }

    public void moveUp() {
        if (cursorRow > 0) {
            cursorRow--;
            if (cursorCol > lineList.get(cursorRow).length())
                cursorCol = lineList.get(cursorRow).length();
        }
    }

    public void moveDown() {
        if (cursorRow < (lineList.size() - 1)) {
            cursorRow++;
            if (lineList.get(cursorRow).length() < cursorCol)
                cursorCol = lineList.get(cursorRow).length();
        }
    }

    void moveLeft() {
        if (cursorCol > 0)
            cursorCol--;
        else if (cursorRow > 0) {
            cursorRow--;
            cursorCol = lineList.get(cursorRow).length();
        }
    }

    void moveRight() {
        if (cursorCol < lineList.get(cursorRow).length())
            cursorCol++;
        else if (cursorRow < (lineList.size() - 1)) {
            cursorRow++;
            cursorCol = 0;
        }
    }

    int getCursorRow() {
        return cursorRow;
    }

    int getCursorCol() {
        return cursorCol;
    }

    boolean setCursor(int row, int col) {
        if ((row >= 0) && (row < lineList.size())) {
            if ((col >= 0) && (col <= lineList.get(row).length())) {
                cursorRow = row;
                cursorCol = col;
                return true;
            }
            return false;
        }
        return false;
    }

    int getNumberOfLines() {
        return lineList.size();
    }

    String getLine(int index) {
        if ((index < 0) || (index >= lineList.size()))
            throw new IllegalArgumentException("Argumento inválido");

        return lineList.get(index).toString();
    }

    public boolean testInvariants() {
        //Ha sempre, pelo menos, uma linha vazia
        if (lineList.isEmpty()) return false;

        //Posiçao do cursor sempre valida
        if ((cursorRow >= 0) && (cursorRow < lineList.size()))
            if (cursorCol < 0 || cursorCol > lineList.get(cursorRow).length()) {
                return false;
            }
        else return false;

        //Linhas logicas nao contem carater de mudança de linha
        return lineList.stream().noneMatch(l -> (l.indexOf("\n") != -1));
    }

    void undo() {
        System.out.println(editMode);
        if (undoList.size() <= 0) {
            return;
        }
        Edit e = undoList.removeLast();
        cursorRow = e.getCursorRow();
        cursorCol = e.getCursorCol();

        switch (e.op) {
            case INSERT:
                editMode = true;
                delete();
                break;
            case DELETE:
                editMode = true;
                insert(e.getChar());
                break;
            default:
                break;
        }
        editMode = false;

    }

    int getUndoRow() {
        if (undoList.isEmpty()) {
            return -1;
        }
        return undoList.getLast().getCursorRow();
    }

    int getUndoCol() {
        if (undoList.isEmpty()) {
            return -1;
        }
        return undoList.getLast().getCursorCol();
    }

    int getUndoSize() {
        return undoList.size();
    }

}
