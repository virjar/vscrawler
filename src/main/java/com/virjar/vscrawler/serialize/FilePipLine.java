package com.virjar.vscrawler.serialize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Collection;

/**
 * Created by virjar on 17/4/16.
 */
public class FilePipLine implements Pipline {
    private String filepath;

    private PrintWriter printWriter;

    public FilePipLine(String filepath) throws FileNotFoundException {
        this.filepath = filepath;
        printWriter = new PrintWriter(new FileOutputStream(new File(filepath)));
    }

    @Override
    public void saveItem(Collection<String> itemJson) {
        for (String str : itemJson) {
            printWriter.println(str);
        }
        printWriter.flush();
    }
}
