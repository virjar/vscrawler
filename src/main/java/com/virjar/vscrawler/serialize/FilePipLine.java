package com.virjar.vscrawler.serialize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Collection;

import com.virjar.vscrawler.seed.Seed;

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
        printWriter = new PrintWriter(new FileOutputStream(new File(filepath)));
    }

    @Override
    public void saveItem(Collection<String> itemJson, Seed seed) {
        for (String str : itemJson) {
            printWriter.println(str);
        }
        printWriter.flush();
    }
}
