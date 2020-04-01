package lab1;

import java.io.BufferedWriter;
import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;


import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;


public class FileManager {
    private String dir;
    private int saved = 0;

    public FileManager(String dir) {
        this.dir = dir;

        File directory = new File(dir);

        if (!directory.exists()) {
            if (!directory.mkdir()) {
                System.err.println("Nu se poate salva directorul " + directory.getPath());
                System.exit(1);
            }
        } else if (!directory.isDirectory()) {
            System.err.println(directory.getPath() + " nu este un director");
            System.exit(1);
        } else {
            try {
                FileUtils.cleanDirectory(directory);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public void save(Document document, URL url) throws IOException {
        File file = new File(getPath(url));
        file.getParentFile().mkdirs();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(document.toString());
        writer.close();
        saved++;
    }

    boolean exists(URL url) {
        return new File(getPath(url)).exists();
    }

    private String getPath(URL url) {
        String path = dir + url.getAuthority() + url.getPath();
        if (!(path.endsWith(".html") || path.endsWith(".htm"))) {
            path += path.endsWith("/") ? "index.html" : "/index.html";
        }
        return path;
    }

    public long getSaved() {
        return saved;
    }
}
