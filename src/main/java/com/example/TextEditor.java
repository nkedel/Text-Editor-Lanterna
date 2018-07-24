package com.example;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class TextEditor {

    private TextEditor() {
    }

    public static void main(String[] args) throws IOException {
        FileBuffer fileBuffer;
        if (args.length > 0) {
            Path path = Paths.get(args[0]);
            fileBuffer = new FileBuffer(path);
        } else {
            fileBuffer = new FileBuffer();
        }
        BufferView bufferView = new BufferView(fileBuffer);
    }
}

