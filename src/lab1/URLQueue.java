package lab1;

import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class URLQueue {
    private LinkedHashSet<URL> urls;

    URLQueue() {
        urls = new LinkedHashSet<>();
    }

    void push(URL url) {
        urls.add(url);

    }

    URL remove() {
        Iterator<URL> iterator = urls.iterator();

        if (iterator.hasNext()) {
            URL url = iterator.next();
            iterator.remove();
            return url;
        }
        return null;
    }

    boolean isEmpty() {
        return urls.isEmpty();
    }
}
