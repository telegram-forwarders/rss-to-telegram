package com.soramusoka.rss_to_telegram;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

public class ConfigurationManager {
    private CommandLine _cmd;

    public ConfigurationManager(CommandLine cmd) {
        this._cmd = cmd;
    }

    public String getString(String key) {
        return this.getString(key, null);
    }

    public String getString(String key, String defaultValue) {
        return this._cmd.hasOption(key) ? this._cmd.getOptionValue(key) : defaultValue;
    }

    public Integer getInteger(String key) {
        return this.getInteger(key, 9999999); // TODO: should be change in future release
    }

    public Integer getInteger(String key, Integer defaultValue) {
        return this._cmd.hasOption(key) ? Integer.decode(this._cmd.getOptionValue(key)) : defaultValue;
    }

    public static ConfigurationManager loadConfiguration(String[] args) {
        try {
            Options options = new Options();
            options.addOption("k", "key", true, "Consul key for bot token");
            options.addOption("t", "token", true, "Telegram bot token");
            options.addOption("ci", "chatId", true, "Telegram chat id");
            options.addOption("a", "author", true, "Articles author name");
            options.addOption("i", "interval", true, "Pooling interval");
            options.addOption("n", "name", true, "Application name");
            options.addOption("u", "rssUrl", true, "Rss url channel");

            CommandLineParser optionsParser = new DefaultParser();
            CommandLine cmd = optionsParser.parse(options, args);
            return new ConfigurationManager(cmd);
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }
}
