package webcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
/**
 *
 * @author lucas.burdell
 */
public class Webpage {

    private String link;
    private URL url;
    private HttpURLConnection connection;
    private String content;

    public Webpage(String link) {
        //System.out.println("loading " + link);
        this.link = link;
        try {
            this.url = new URL(link);
            if (link.startsWith("https://")) {
                
                this.connection = (HttpsURLConnection) url.openConnection();
            } else if (link.startsWith("http://")) {
                this.connection = (HttpURLConnection) url.openConnection();
            }
            this.connection.setConnectTimeout(2000);
            /*
            this.connection.setReadTimeout(2000);
            this.connection.setReadTimeout(2000);
            */
            
            try {
                if (this.connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    loadContent();
                }
            } catch (SocketTimeoutException ste) {
                System.err.println(ste + " " + link);
            } catch (SocketException | ProtocolException e) {
                System.err.println(e + " " + link);
            } catch (Exception | Error e) {
                // make the errors go away
                System.err.println(e + " " + link);
            }
        } catch (SocketTimeoutException ste) {
            System.err.println("Timed out on " + link);  
        } catch (SocketException | ProtocolException se) {
            
        } catch (MalformedURLException ex) {
            System.err.println(ex.getMessage());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        } catch (Exception e) {
            System.err.println("got an error with link " + link + "\nURL: " + url);
            //e.printStackTrace();
        } catch (Error s) {
            System.err.println(s.getMessage());
        }
    }

    private final void loadContent() {
        BufferedReader br
                = null;
        StringBuilder output = new StringBuilder();
        try {
            br = new BufferedReader(
                    new InputStreamReader(getConnection().getInputStream()));
            
            String input;
            //System.out.println("loading " + this);
            while ((input = br.readLine()) != null) {
                //System.out.println("current input: " + input);
                output = output.append(input);
            }
            br.close();
            
        } catch (Exception ex) {
            System.err.println(this + " " + ex.getMessage());
            //ex.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        setContent(output.toString());
    }
    
    public ArrayList<String> getLinks() {
        if (getContent() == null) {
            return new ArrayList<>();
        }
        Pattern pattern = Pattern.compile("href=\"([\\w\\.:/]++)\"");
        //System.out.println(content);
        Matcher matcher = pattern.matcher(this.getContent());
        ArrayList<String> pages = new ArrayList<>();
        while (matcher.find()) {
            String link = matcher.group(1);
            //System.out.println("found match " + link);
            if (link.startsWith("https://") || link.startsWith("http://")) {
                if (!link.equals(this.link)) {
                    pages.add(link);
                }
            } else {
                //System.out.println(this.url.getPath());
                if (link.startsWith("/")) {
                    link = link.substring(1);
                }
                String urlString = this.getUrl().toString();
                if (!urlString.endsWith("/")) {
                    urlString = urlString + "/";
                }
                pages.add(urlString + link);
            }
        }
        return pages;
    }
    
    @Override
    public String toString() {
        return getUrl().toExternalForm();
    }

    /**
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link the link to set
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * @return the url
     */
    public URL getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(URL url) {
        this.url = url;
    }

    /**
     * @return the connection
     */
    public HttpURLConnection getConnection() {
        return connection;
    }

    /**
     * @param connection the connection to set
     */
    public void setConnection(HttpURLConnection connection) {
        this.connection = connection;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }
}
