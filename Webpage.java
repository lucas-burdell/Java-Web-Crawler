package webcrawler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;

public class Webpage {

    private String link;
    private URL url;
    private HttpURLConnection connection;
    private String content;

    public Webpage(String link) {
        this.link = link;
        try {
            this.url = new URL(link);
            if (link.startsWith("https://")) {
                
                this.connection = (HttpsURLConnection) url.openConnection();
            } else if (link.startsWith("http://")) {
                this.connection = (HttpURLConnection) url.openConnection();
            }
            this.connection.setConnectTimeout(2000);
            
            try {
                if (this.connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    loadContent();
                }
            } catch (SocketTimeoutException ste) {
                System.err.println(ste + " " + link);
            } catch (SocketException | ProtocolException e) {
                System.err.println(e + " " + link);
            } catch (Exception | Error e) {
                System.err.println(e + " " + link);
            }
        } catch (SocketTimeoutException ste) {
            System.err.println("Timed out on " + link);  
        } catch (SocketException | ProtocolException se) {

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } catch (Error s) {
            System.err.println(s.getMessage());
        }
    }

    private void loadContent() {

        StringBuilder output = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(getConnection().getInputStream()))) {
            String input;
            while ((input = br.readLine()) != null) {
                output = output.append(input);
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        setContent(output.toString());
    }
    
    public ArrayList<String> getLinks() {
        if (getContent() == null) {
            return new ArrayList<>();
        }
        Pattern pattern = Pattern.compile("href=\"([\\w\\.:/]++)\"");
        Matcher matcher = pattern.matcher(this.getContent());
        ArrayList<String> pages = new ArrayList<>();
        while (matcher.find()) {
            String link = matcher.group(1);
            if (link.startsWith("https://") || link.startsWith("http://")) {
                if (!link.equals(this.link)) {
                    pages.add(link);
                }
            } else {
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public HttpURLConnection getConnection() {
        return connection;
    }

    public void setConnection(HttpURLConnection connection) {
        this.connection = connection;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}