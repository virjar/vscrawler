package com.virjar.vscrawler.core.serialize;

import com.virjar.vscrawler.core.processor.GrabResult;
import com.virjar.vscrawler.core.seed.Seed;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Created by virjar on 17/4/16.
 *
 * @author virjar
 * @since 0.0.1
 */
public class FilePipLine implements Pipeline {
    private String filepath;

    private PrintWriter printWriter;

    public FilePipLine(String filepath) throws FileNotFoundException {
        this.filepath = filepath;
        printWriter = new PrintWriter(new FileOutputStream(new File(filepath), true));
    }

    @Override
    public void saveItem(GrabResult grabResult, Seed seed) {
        for (String str : grabResult.allResult()) {
            printWriter.println(str);
        }
        printWriter.flush();
    }
}
