package webcrawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class ObservableHashMap<K, V> extends HashMap<K, V> {

    private final ArrayList<BiConsumer<K, V>> callbacks = new ArrayList<>();

    public boolean addConsumer(BiConsumer<K, V> consumer) {
        return callbacks.add(consumer);
    }
    
    public boolean removeConsumer(BiConsumer<K, V> consumer) {
        return callbacks.remove(consumer);
    }
    
    
    @Override
    public synchronized V put(K key, V value) {
        V output = super.put(key, value);
        for (BiConsumer<K, V> callback : callbacks) {
            callback.accept(key, value);
        }
        return output;
    }

}
