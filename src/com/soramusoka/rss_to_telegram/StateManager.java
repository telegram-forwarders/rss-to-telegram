package com.soramusoka.rss_to_telegram;

import java.util.HashMap;

public class StateManager {
    private HashMap<String, Integer> _articleStat = new HashMap<>();
    private Boolean _firstRun = true;

    public StateManager() {
        this.resetCounters();
    }

    public Boolean getFirstRun() {
        return this._firstRun;
    }

    public void setFirstRun(Boolean value) {
        this._firstRun = value;
    }

    public Integer getNewArticleCounter() {
        return this._articleStat.get("new");
    }

    public Integer getOldArticleCounter() {
        return this._articleStat.get("old");
    }

    public void incrNewArticleCounter() {
        this.increment("new");
    }

    public void incrOldArticleCounter() {
        this.increment("old");
    }

    public void resetCounters() {
        this._articleStat.put("new", 0);
        this._articleStat.put("old", 0);
    }

    private void increment(String key) {
        Integer value = this._articleStat.get(key);
        this._articleStat.replace(key, ++value);
    }
}
