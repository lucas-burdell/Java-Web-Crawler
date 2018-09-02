package webcrawler;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Platform;

public class ObservableAtomicInteger extends AtomicInteger {
    
    private ArrayList<Runnable> callbacks = new ArrayList<>();
    
    public void addListener(Runnable runnable) {
        callbacks.add(runnable);
    }
    
    public void runCallbacks() {
        for (Runnable callback : callbacks) {
            Platform.runLater(callback);
        }
    }
    
    public synchronized void add(int x) {
        super.addAndGet(x);
        runCallbacks();
    }
    
}
