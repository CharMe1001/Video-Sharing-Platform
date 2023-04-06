package Services;

import java.util.HashMap;

public class Service<T> {
    protected HashMap<Integer, T> itemHashMap;

    public Service() {
        this.itemHashMap = new HashMap<>();
    }

    private int generateID() {
        int id = 0;
        while (this.itemHashMap.containsKey(++id)) ;

        return id;
    }

    public void add(T item) {
        this.itemHashMap.put(this.generateID(), item);
    }

    public void remove(int id) {
        this.itemHashMap.remove(id);
    }

    public void set(int id, T item) {
        this.itemHashMap.put(id, item);
    }

    public T get(int id) {
        return this.itemHashMap.get(id);
    }

}
