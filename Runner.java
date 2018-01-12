package webcrawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author lucas.burdell
 */
public class Runner implements Runnable {

    public static final ObservableHashMap<String, Integer> KNOWN_LINKS = new ObservableHashMap<>();
    public static final ObservableHashMap<String, Integer> HOSTS = new ObservableHashMap<>();
    private static final ConcurrentLinkedQueue<String> LINKS_TO_VISIT = new ConcurrentLinkedQueue();

    private static final ObservableAtomicInteger numberOfWebsitesVisited = new ObservableAtomicInteger();
    private static final ObservableAtomicInteger numberOfLinksVisited = new ObservableAtomicInteger();
    private static final ObservableAtomicInteger numberOfUniqueWebsites = new ObservableAtomicInteger();
    private static final ObservableAtomicInteger numberOfSkippedLinks = new ObservableAtomicInteger();
    private static int CUTOFF_NUMBER = 10;
    private boolean running;

    /**
     * @return the numberOfWebsitesVisited
     */
    public static ObservableAtomicInteger getNumberOfWebsitesVisited() {
        return numberOfWebsitesVisited;
    }

    /**
     * @return the numberOfUniqueWebsites
     */
    public static ObservableAtomicInteger getNumberOfUniqueWebsites() {
        return numberOfUniqueWebsites;
    }

    /**
     * @return the numberOfLinksVisited
     */
    public static ObservableAtomicInteger getNumberOfLinksVisited() {
        return numberOfLinksVisited;
    }

    /**
     * @return the numberOfSkippedLinks
     */
    public static ObservableAtomicInteger getNumberOfSkippedLinks() {
        return numberOfSkippedLinks;
    }

    /**
     * @return the CUTOFF_NUMBER
     */
    public static int getCUTOFF_NUMBER() {
        return CUTOFF_NUMBER;
    }

    /**
     * @param aCUTOFF_NUMBER the CUTOFF_NUMBER to set
     */
    public static void setCUTOFF_NUMBER(int aCUTOFF_NUMBER) {
        CUTOFF_NUMBER = aCUTOFF_NUMBER;
    }

    private Webpage startPage;

    public Runner() {
        running = true;
    }

    public Runner(Webpage startPage) {
        this.startPage = startPage;
        LINKS_TO_VISIT.addAll(startPage.getLinks());
        //System.out.println("added page");
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
                    try {
                        //System.err.println("oh noes");

                        Thread.sleep(250);
                    } catch (InterruptedException ex) {
                    }
                    continue;
                }

                if (link == null) {
                    continue;
                }

                //System.out.println(link);
                try {
                    URL url = new URL(link);
                    String host = url.getHost();
                    if (HOSTS.get(host) != null && HOSTS.get(host) >= CUTOFF_NUMBER) {
                        //System.out.println("Skipping " + host);
                        getNumberOfSkippedLinks().add(1);
                        continue;
                    } else if (HOSTS.get(host) != null) {
                        HOSTS.put(host, HOSTS.get(host) + 1);
                    } else {
                        //System.out.println("new host:  " + host);
                        numberOfUniqueWebsites.add(1);
                        HOSTS.put(host, 1);
                    }
                    numberOfWebsitesVisited.add(1);
                } catch (MalformedURLException ex) {
                }
                /*
                if (KNOWN_LINKS.get(link) != null) {
                    //KNOWN_LINKS.put(link, KNOWN_LINKS.get(link) + 1);
                    getNumberOfSkippedLinks().add(1);
                } else if (KNOWN_LINKS.get(link) == null) {
                    KNOWN_LINKS.put(link, 1);
                    Webpage page = new Webpage(link);
                    //System.out.println(link);
                    if (page.getContent() == null) {
                        HOSTS.put(page.getUrl().getHost(), HOSTS.get(page.getUrl().getHost()) + 1);
                        getNumberOfSkippedLinks().add(1);
                    } else {
                        numberOfLinksVisited.add(1);
                        LINKS_TO_VISIT.addAll(page.getLinks());
                    }
                }
                 */
                numberOfLinksVisited.add(1);
                if (KNOWN_LINKS.get(link) != null) {
                    KNOWN_LINKS.put(link, KNOWN_LINKS.get(link) + 1);
                } else if (KNOWN_LINKS.get(link) == null) {
                    KNOWN_LINKS.put(link, 1);
                    Webpage page = new Webpage(link);
                    //System.out.println(link);
                    LINKS_TO_VISIT.addAll(page.getLinks());
                }
            } else {
                try {
                    //System.err.println("oh noes");
                    Thread.sleep(250);
                } catch (InterruptedException ex) {
                }

            }
        }
    }

    /**
     * @return the running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * @param running the running to set
     */
    public void setRunning(boolean running) {
        this.running = running;
    }
}
