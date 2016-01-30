package rss_to_telegram.business_layer;

import java.util.ArrayList;

public class KeyStorage {
    private ArrayList<String> _keys = new ArrayList<>();

    public Boolean hasKey(String key) {
        return this._keys.contains(key);
    }

    public void putKey(String key) {
        this._keys.add(key);
    }

    public void resetStorage() {
        this._keys.clear();
    }

    public Integer size() {
        return this._keys.size();
    }
}
