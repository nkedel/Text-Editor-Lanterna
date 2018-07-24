package com.example;

class Edit {
    EditOp op;
    private int cursorRow, cursorCol;
    private char c;

    Edit(EditOp op, char c, int cursorRow, int cursorCol) {
        this.op = op;
        this.c = c;
        this.cursorCol = cursorCol;
        this.cursorRow = cursorRow;
    }

    int getCursorRow() {
        return cursorRow;
    }

    int getCursorCol() {
        return cursorCol;
    }

    char getChar() {
        return c;
    }

    enum EditOp {
        INSERT, DELETE
    }
}
