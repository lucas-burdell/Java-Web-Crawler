package webcrawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author lucas.burdell
 */
public class Runner implements Runnable {

    public static final ObservableHashMap<String, Integer> KNOWN_LINKS = new ObservableHashMap<>();
    public static final ObservableHashMap<String, Integer> HOSTS = new ObservableHashMap<>();
    private static final ConcurrentLinkedQueue<String> LINKS_TO_VISIT = new ConcurrentLinkedQueue();

    private static final ObservableAtomicInteger NUMBER_OF_SITES_VISITED = new ObservableAtomicInteger();
    private static final ObservableAtomicInteger NUMBER_OF_LINKS_VISITED = new ObservableAtomicInteger();
    private static final ObservableAtomicInteger NUMBER_OF_UNIQUE_SITES = new ObservableAtomicInteger();
    private static final ObservableAtomicInteger NUMBER_OF_SKIPPED_LINKS = new ObservableAtomicInteger();
    private static int CUTOFF_NUMBER = 10;
    private boolean running;
    private Webpage startPage;

    public static ObservableAtomicInteger getNumberOfWebsitesVisited() {
        return NUMBER_OF_SITES_VISITED;
    }

    public static ObservableAtomicInteger getNumberOfUniqueWebsites() {
        return NUMBER_OF_UNIQUE_SITES;
    }

    public static ObservableAtomicInteger getNumberOfLinksVisited() {
        return NUMBER_OF_LINKS_VISITED;
    }

    public static ObservableAtomicInteger getNumberOfSkippedLinks() {
        return NUMBER_OF_SKIPPED_LINKS;
    }

    public static int getCutoffNumber() {
        return CUTOFF_NUMBER;
    }

    public static void setCutoffNumber(int cutoff) {
        CUTOFF_NUMBER = cutoff;
    }

    public Runner() {
        running = true;
    }

    public Runner(Webpage startPage) {
        this.startPage = startPage;
        LINKS_TO_VISIT.addAll(startPage.getLinks());
        running = true;
    }

    @Override
    public void run() {
        while (running) {
            if (!LINKS_TO_VISIT.isEmpty()) {
                String link = null;
                try {
                    link = LINKS_TO_VISIT.remove();
                } catch (Exception e) {
                    continue;
                }

                if (link == null) {
                    continue;
                }

                try {
                    URL url = new URL(link);
                    String host = url.getHost();
                    if (HOSTS.get(host) != null && HOSTS.get(host) >= CUTOFF_NUMBER) {
                        getNumberOfSkippedLinks().add(1);
                        continue;
                    } else if (HOSTS.get(host) != null) {
                        HOSTS.put(host, HOSTS.get(host) + 1);
                    } else {
                        NUMBER_OF_UNIQUE_SITES.add(1);
                        HOSTS.put(host, 1);
                    }
                    NUMBER_OF_SITES_VISITED.add(1);
                } catch (MalformedURLException ex) {
                }
                NUMBER_OF_LINKS_VISITED.add(1);
                if (KNOWN_LINKS.get(link) != null) {
                    KNOWN_LINKS.put(link, KNOWN_LINKS.get(link) + 1);
                } else if (KNOWN_LINKS.get(link) == null) {
                    KNOWN_LINKS.put(link, 1);
                    Webpage page = new Webpage(link);
                    LINKS_TO_VISIT.addAll(page.getLinks());
                }
            } else {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException ex) { }

            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}