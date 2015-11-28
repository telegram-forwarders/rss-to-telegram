package com.soramusoka.rss_to_telegram;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Node;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TimerTask;

public class Main {

    public static void main(String[] args) throws Exception {
        ConfigurationManager config = ConfigurationManager.loadConfiguration(args);
        if (config == null) {
            throw new Exception("App configuration fail");
        }

        // TODO: create proper config without String keys, just to avoid any typo
        String RSS_CHANNEL = config.getString("rssUrl");
        String APP_NAME = config.getString("name", "defaultApp");
        String TELEGRAM_TOKEN = config.getString("token");
        String TELEGRAM_CHAT_ID = config.getString("chatId");
        String ARTICLE_AUTHOR = config.getString("author");
        int POOLING_INTERVAL = config.getInteger("interval", 300);

        Logger logger = getLogger(APP_NAME);
        if (logger == null) {
            throw new Exception("Logger configuration fail");
        }

        logger.info("Configuration: " + RSS_CHANNEL + ", " + ARTICLE_AUTHOR + ", " + APP_NAME + ", " + TELEGRAM_CHAT_ID + ", " + TELEGRAM_TOKEN);

        HttpRequest request = new HttpRequest();
        TelegramBot bot = new TelegramBot(TELEGRAM_TOKEN, TELEGRAM_CHAT_ID, request, logger);
        RssParser parser = new RssParser(request);

        StateManager stateManager = new StateManager();
        KeyStorage keyStorage = new KeyStorage();

        startPooling(POOLING_INTERVAL, () -> {
            try {
                stateManager.resetCounters();

                ArrayList<Node> data = parser.getRawData(RSS_CHANNEL, ARTICLE_AUTHOR);
                ArrayList<Article> articles = parser.convertToArticle(data); // TODO: SOLID v

                articles.forEach(article -> {
                    if (stateManager.getFirstRun()) {
                        keyStorage.putKey(article.link);
                    }

                    if (!keyStorage.hasKey(article.link)) {
                        keyStorage.putKey(article.link);
                        stateManager.incrNewArticleCounter();
                        logger.debug("Received article: " + article.title);
                        bot.sendMessage(article.link);
                    } else {
                        stateManager.incrOldArticleCounter();
                    }
                });
                if (stateManager.getFirstRun()) {
                    logger.info("First run");
                    stateManager.setFirstRun(false);
                }
                logger.debug("Articles summary, new: " + stateManager.getNewArticleCounter() + ", old: " + stateManager.getOldArticleCounter());
            } catch (Exception e) {
                bot.sendMessage("StartPooling error: " + e.toString());
            }
        });
    }

    private static Logger getLogger(String appName) throws Exception {
        try {
            Properties log4jProperties = new Properties();
            log4jProperties.setProperty("log4j.logger." + appName, "DEBUG, myConsoleAppender");
            log4jProperties.setProperty("log4j.appender.myConsoleAppender", "org.apache.log4j.ConsoleAppender");
            log4jProperties.setProperty("log4j.appender.myConsoleAppender.layout", "org.apache.log4j.PatternLayout");
            log4jProperties.setProperty("log4j.appender.myConsoleAppender.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
            PropertyConfigurator.configure(log4jProperties);
            return Logger.getLogger(appName);
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return null;
        }
    }

    private static void startPooling(int intervalSec, final Runnable runnable) {
        java.util.Timer t = new java.util.Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        }, 0, intervalSec * 1000);
    }
}