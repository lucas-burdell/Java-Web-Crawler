package webcrawler;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainFXMLController extends Application implements Initializable {

    @FXML
    private TextField threadCount;
    @FXML
    private Button startButton;
    @FXML
    private TextField startUrl;
    @FXML
    private TextArea webOutput;

    private final ArrayList<Runner> runners = new ArrayList<>();
    private final ArrayList<Thread> threads = new ArrayList<>();
    @FXML
    private Label numberOfLinksVisitedText;
    @FXML
    private Label numberOfUniqueWebsitesText;
    @FXML
    private Label numberOfWebsitesSkippedText;
    @FXML
    private TextField skipWebNumber;
    @FXML
    private Label linksPerSecond;

    private boolean isRunning = false;

    private long startTime;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        numberOfLinksVisitedText.setText("Number of Links Visited: 0");
        numberOfUniqueWebsitesText.setText("Number of Unique Websites: 0");
        numberOfWebsitesSkippedText.setText("Number of Websites Skipped: 0");
        linksPerSecond.setText("Links per second: 0");

        Runner.HOSTS.addConsumer((String t, Integer u) -> {
            Platform.runLater(() -> {
                if (u <= 1) {
                    String output = (t + "\n");
                    webOutput.appendText(output);
                }
            });
        });

        Runner.getNumberOfLinksVisited().addListener(() -> {
            numberOfLinksVisitedText.setText("Number of "
                    + "links visited: " + Runner.getNumberOfLinksVisited());
            updateLinksPerSecond(Runner.getNumberOfLinksVisited().get());
        });

        Runner.getNumberOfUniqueWebsites().addListener(() -> {
            numberOfUniqueWebsitesText.setText("Number of "
                    + "unique websites visited: " + Runner.getNumberOfUniqueWebsites());
        });

        Runner.getNumberOfSkippedLinks().addListener(() -> {
            numberOfWebsitesSkippedText.setText("Number of "
                    + "links skipped: " + Runner.getNumberOfSkippedLinks());
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainFXML.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch();
    }

    @FXML
    private void onStartPressed(ActionEvent event) {
        if (!isRunning) {
            isRunning = true;
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }
            String startUrlString = startUrl.getText();
            int numberOfThreads = Integer.parseInt(threadCount.getText());
            int cutoff = Integer.parseInt(skipWebNumber.getText());
            Runner.setCutoffNumber(cutoff);
            startButton.setText("Stop");
            startThreads(startUrlString, numberOfThreads);
        } else {
            isRunning = false;
            stopThreads();
            startButton.setText("Start");
        }
    }

    public void stopThreads() {
        for (Runner runner : runners) {
            runner.setRunning(false);
        }
    }

    public void updateLinksPerSecond(long distance) {
        final DecimalFormat formatter = new DecimalFormat("#.##");
        long time = System.currentTimeMillis() - startTime;
        double rate = ((double) distance) / (time / 1000.);
        linksPerSecond.setText("Links per second: " + formatter.format(rate));
    }

    public void startThreads(String startUrl, int numThreads) {
        runners.clear();
        threads.clear();
        runners.add(new Runner(new Webpage(startUrl)));
        for (int i = 1; i < numThreads; i++) {
            runners.add(new Runner());
        }
        for (int i = 0; i < runners.size(); i++) {
            Runner runner = runners.get(i);
            Thread thread = new Thread(runner);
            threads.add(thread);
            thread.setDaemon(true);
            thread.setName("Crawler thread " + Integer.toString(i));
            thread.start();
        }
    }
}