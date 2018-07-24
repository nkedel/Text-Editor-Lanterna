package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBuffer extends Buffer {
    private Path savePath = null;
    private File file = null;

    FileBuffer(Path path) throws IOException {
        open(path);
    }

    FileBuffer() throws IOException {

    }

    void save() throws IOException {
        saveAs(savePath);
    }

    private void saveAs(Path path) throws IOException {
        Files.write(path, getLineList(), Charset.forName("UTF-8"));
    }

    private void open(Path path) throws FileNotFoundException, IOException {  //fills the buffer
        this.savePath = path;
        file = path.toFile();
        List<StringBuilder> lineList = getLineList();
        if (file.exists() && !file.isDirectory()) {
            for (String line : Files.readAllLines(path, Charset.defaultCharset())) {
                StringBuilder sb = new StringBuilder(line);
                lineList.add(sb);
            }
            if (!lineList.isEmpty())
                lineList.remove(0);
            int row = lineList.size();
            int col = lineList.get(lineList.size() - 1).length();
            setCursor(row, col);
        }
    }

    @Override
    public void insert(char c) {
        super.insert(c);
    }
}
