package rss_to_telegram;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Node;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TimerTask;

public class Main {

    public static void main(String[] args) throws Exception {
        CommandLine cmd = ConfigurationManager.loadConfiguration(args);
        if (cmd == null) return;

        String[] RSS_CHANNELS = cmd.getOptionValues('u');
        String TELEGRAM_TOKEN = cmd.getOptionValue('t');
        String TELEGRAM_CHAT_ID = cmd.getOptionValue('c');
        String poolingValue = cmd.getOptionValue('i', "300");
        int POOLING_INTERVAL = Integer.parseInt(poolingValue);

        Logger logger = getLogger();
        if (logger == null) {
            throw new Exception("Logger configuration fail");
        }

        logger.info("Configuration: " + RSS_CHANNELS + ", " + TELEGRAM_CHAT_ID + ", " + TELEGRAM_TOKEN);

        Transliterator transliterator = new Transliterator();
        HttpRequest request = new HttpRequest();
        TelegramBot bot = new TelegramBot(TELEGRAM_TOKEN, TELEGRAM_CHAT_ID, request, logger);
        RssParser parser = new RssParser(request, transliterator);

        StateManager stateManager = new StateManager();
        KeyStorage keyStorage = new KeyStorage();

        startPooling(POOLING_INTERVAL, () -> {
            try {
                stateManager.resetCounters();

                for (int i = 0; i < RSS_CHANNELS.length; i++) {
                    String channel = RSS_CHANNELS[i];
                    String author = null;
                    String delimiter = "::";

                    if (channel.indexOf(delimiter) != 0) {
                        String[] splittedUrl = channel.split(delimiter);

                        if (splittedUrl.length != 2) {
                            throw new Exception("Url should be like: url::author");
                        }

                        channel = splittedUrl[0];
                        author = splittedUrl[1];
                    }

                    ArrayList<Node> data = parser.getRawData(channel, author);
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
                }
                if (stateManager.getFirstRun()) {
                    logger.info("First run");
                    stateManager.setFirstRun(false);
                }
                logger.debug("Articles summary, new: " + stateManager.getNewArticleCounter() + ", old: " + stateManager.getOldArticleCounter());
            } catch (Exception e) {
                System.out.println("e.toString(): " + e.toString());
                bot.sendMessage("StartPooling error: " + e.toString());
            }
        });
    }

    private static Logger getLogger() throws Exception {
        try {
            Properties log4jProperties = new Properties();
            log4jProperties.setProperty("log4j.logger.app", "DEBUG, myConsoleAppender");
            log4jProperties.setProperty("log4j.appender.myConsoleAppender", "org.apache.log4j.ConsoleAppender");
            log4jProperties.setProperty("log4j.appender.myConsoleAppender.layout", "org.apache.log4j.PatternLayout");
            log4jProperties.setProperty("log4j.appender.myConsoleAppender.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
            PropertyConfigurator.configure(log4jProperties);
            return Logger.getLogger("app");
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