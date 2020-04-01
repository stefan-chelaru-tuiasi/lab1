package lab1;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class URLFrontier {

    private FileManager fileManager;
    public Map<String, Collection> collection = new LinkedHashMap<String, Collection>();
    public Collection abc = new ArrayList();
    private Map<String, URLQueue> authorityVsUrls = new LinkedHashMap<String, URLQueue>();

    public URLFrontier(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void addUrl(URL url) {
        String protocol = url.getProtocol();

        if (protocol != null && protocol.compareTo("http") == 0) {
            String authority = url.getAuthority();
            if (authority != null) {
                if (!authorityVsUrls.containsKey(authority)) {
                    authorityVsUrls.put(authority, new URLQueue());

                }
                authorityVsUrls.get(authority).push(url);
            }
        }
    }

    public URL getNext() {
        String host = authorityVsUrls.entrySet().iterator().next().getKey();
        URL url = authorityVsUrls.get(host).remove();

        if (authorityVsUrls.get(host).isEmpty()) {
            authorityVsUrls.remove(host);

        }

        return url;
    }

    public boolean isEmpty() {
        return authorityVsUrls.isEmpty();
    }

    public void updateLocation(String host, String location) throws MalformedURLException {
        if (authorityVsUrls.containsKey(host)) {
            URLQueue queue = authorityVsUrls.remove(host);

            while (!queue.isEmpty()) {
                addUrl(new URL(location + queue.remove().getPath()));
            }
        }
    }

    public void saveJson() throws IOException {
        PrintWriter out = new PrintWriter("filename.json", "UTF-8");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(collection);
        out.println(json);
        out.close();
    }

    public void addDocumentRefs(Document document, String urlName) throws MalformedURLException {
        abc.clear();

        for (Element aElem : document.select("a[href]:not([href*=#])")) {
            String href = aElem.attr("abs:href");

            if (href != null) {
                URL url = new URL(href);
                if (!fileManager.exists(url)) {
                    System.out.println(url);
                    abc.add(url);
                    addUrl(url);
                }
            }
        }
        ArrayList noDuplicate = (ArrayList) abc.stream().distinct().collect(Collectors.toList());
        collection.put(urlName, noDuplicate);
    }

}
