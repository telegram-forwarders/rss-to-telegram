package rss_to_telegram;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.codehaus.jackson.map.ObjectMapper;
import rss_to_telegram.business_layer.*;
import rss_to_telegram.dto_layer.Config;
import rss_to_telegram.dto_layer.ConfigRssItem;
import rss_to_telegram.service_layer.ConfigurationManager;
import rss_to_telegram.service_layer.HttpRequest;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws Exception {
        CommandLine cmd = ConfigurationManager.loadConfiguration(args);
        if (cmd == null) return;

        String configFile = cmd.getOptionValue('c');
        Config config = getConfig(configFile);

        Logger logger = getLogger("main");
        logger.info("Configuration: " + config.toString());

        Transliterator transliterator = new Transliterator();
        HttpRequest request = new HttpRequest();
        RssParser parser = new RssParser(request, transliterator);

        TelegramBot mainBot = new TelegramBot(config.baseTelegramToken, config.baseTelegramChatId, request, logger);

        for (ConfigRssItem item : config.rssData) {
            Logger itemLogger = getLogger(item.name);
            StateManager stateManager = new StateManager();
            KeyStorage keyStorage = new KeyStorage();

            TelegramBot bot = item.telegramChatId == null
                    ? mainBot
                    : new TelegramBot(item.telegramToken, item.telegramChatId, request, itemLogger);

            Stream stream = new Stream(stateManager, keyStorage, parser, bot, itemLogger);
            stream.start(item);
        }
    }

    private static Logger getLogger(String name) throws Exception {
        name = name == null ? "default" : name;
        try {
            Properties log4jProperties = new Properties();
            log4jProperties.setProperty("log4j.logger." + name, "DEBUG, myConsoleAppender");
            log4jProperties.setProperty("log4j.appender.myConsoleAppender", "org.apache.log4j.ConsoleAppender");
            log4jProperties.setProperty("log4j.appender.myConsoleAppender.layout", "org.apache.log4j.PatternLayout");
            log4jProperties.setProperty("log4j.appender.myConsoleAppender.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
            PropertyConfigurator.configure(log4jProperties);
            return Logger.getLogger(name);
        } catch (Exception e) {
            return null;
        }
    }

    private static Config getConfig(String configPath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String filePath = new File(configPath).getAbsolutePath();
            FileReader file = new FileReader(filePath);
            return mapper.readValue(file, Config.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}