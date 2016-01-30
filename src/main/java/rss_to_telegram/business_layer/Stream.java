package rss_to_telegram.business_layer;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import rss_to_telegram.dto_layer.Article;
import rss_to_telegram.dto_layer.ConfigRssItem;

import java.util.ArrayList;
import java.util.TimerTask;

public class Stream {
    public StateManager StateManager;
    public KeyStorage KeyStorage;
    public RssParser Parser;
    public TelegramBot Bot;
    public Logger Logger;

    public Stream() {
    }

    public Stream(StateManager stateManager, KeyStorage keyStorage, RssParser parser, TelegramBot bot, Logger logger) {
        this.StateManager = stateManager;
        this.KeyStorage = keyStorage;
        this.Parser = parser;
        this.Bot = bot;
        this.Logger = logger;
    }

    private void startPooling(int intervalSec, final Runnable runnable) {
        java.util.Timer t = new java.util.Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        }, 0, intervalSec * 1000);
    }

    public void start(ConfigRssItem config) {
        this.Logger.info("Start pooling: " + config.interval);
        startPooling(config.interval, () -> {
            try {
                this.StateManager.resetCounters();

                ArrayList<Node> data = this.Parser.getRawData(config.url, config.author);
                ArrayList<Article> articles = this.Parser.convertToArticle(data);

                articles.forEach(article -> {
                    if (this.StateManager.getFirstRun()) {
                        this.KeyStorage.putKey(article.link);
                    }

                    if (!this.KeyStorage.hasKey(article.link)) {
                        this.KeyStorage.putKey(article.link);
                        this.StateManager.incrNewArticleCounter();
                        this.Logger.debug("Received article: " + article.title);
                        this.Bot.sendMessage(article.link);
                    } else {
                        this.StateManager.incrOldArticleCounter();
                    }
                });
                if (this.StateManager.getFirstRun()) {
                    this.Logger.info("First run");
                    this.StateManager.setFirstRun(false);
                }
                this.Logger.debug("Articles summary, new: " + this.StateManager.getNewArticleCounter() + ", old: " + this.StateManager.getOldArticleCounter());
            } catch (Exception e) {
                this.Logger.error("e.toString(): " + e.toString());
            }
        });
    }
}