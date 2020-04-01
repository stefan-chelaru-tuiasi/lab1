package lab1;

import java.net.URL;

public class Main {

    private static String workdir = "webdir/";
    private static FileManager fileManager;
    private static URLFrontier urlFrontier;
    private static HttpDownloader httpDownloader;

    public static void main(String[] args) throws Exception {
        fileManager = new FileManager(workdir);
        urlFrontier = new URLFrontier(fileManager);
        urlFrontier.addUrl(new URL("http://riweb.tibeica.com/crawl"));
        httpDownloader = new HttpDownloader(urlFrontier, fileManager);


        while (!urlFrontier.isEmpty() && fileManager.getSaved() < 100) {
            try {
                httpDownloader.download();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        urlFrontier.saveJson();

    }
}
