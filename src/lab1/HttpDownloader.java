package lab1;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.panforge.robotstxt.RobotsTxt;

public class HttpDownloader {
    private static final String userAgent = "RIWEB_CRAWLER";

    private URLFrontier urlFrontier;
    private FileManager fileManager;

    private Map<String, RobotsTxt> robotsTxtMap = new HashMap<>();

    public HttpDownloader(URLFrontier frontier, FileManager fileManager) {
        this.urlFrontier = frontier;
        this.fileManager = fileManager;
    }

    public void download() throws Exception {

        URL url = urlFrontier.getNext();

        boolean robots = false;
        boolean allowed = false;

        if (robotsTxtMap.get(url.getAuthority()) == null) {
            robots = true;

            urlFrontier.addUrl(url);
            url = new URL("http://" + url.getAuthority() + "/robots.txt");

        } else {
            RobotsTxt robotsTxt;
            try (InputStream robotsTxtStream = new URL("http://" + url.getAuthority() + "/robots.txt").openStream()) {
                robotsTxt = RobotsTxt.read(robotsTxtStream);
            }
            allowed = robotsTxt.query(userAgent.toLowerCase(), url.getAuthority() + url.getPath());
        }

        if (robots || allowed) {

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Host", url.getAuthority());
            con.setRequestProperty("User-Agent", userAgent);

            switch (con.getResponseCode()) {
                case 301:
                    String location = con.getHeaderField("Location");

                    urlFrontier.updateLocation(url.getAuthority(), location);

                    urlFrontier.addUrl(new URL((location.startsWith("http") ? "" : "http://") + location + url.getPath()));
                    break;
                case 200:
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer content = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();

                    Document doc = Jsoup.parse(content.toString());
                    doc.setBaseUri(url.toString().endsWith(".html") || url.toString().endsWith(".htm") ? url.toString().substring(0, url.toString().lastIndexOf("/")) + "/" : url.toString() + "/");
                    if (robots) {
                        RobotsTxt robotsTxt;
                        try (InputStream robotsTxtStream = new URL("http://" + url.getAuthority() + "/robots.txt").openStream()) {
                            robotsTxt = RobotsTxt.read(robotsTxtStream);
                        }
                        robotsTxtMap.put(url.getAuthority(), robotsTxt);
                    } else {
                        System.out.println(url);
                        fileManager.save(doc, url);
                        System.out.println(fileManager.getSaved() + ". " + url.toString());
                        String urlName = url.toString();
                        urlFrontier.addDocumentRefs(doc, urlName);

                    }
                    break;
                default:
                    break;
            }
        }
    }
}
