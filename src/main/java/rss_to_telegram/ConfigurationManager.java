package rss_to_telegram;

import org.apache.commons.cli.*;

import java.io.OutputStream;
import java.io.PrintWriter;

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

    public Integer getInteger(String key, Integer defaultValue) {
        return this._cmd.hasOption(key) ? Integer.decode(this._cmd.getOptionValue(key)) : defaultValue;
    }

    public static ConfigurationManager loadConfiguration(String[] args) {
        Options options = new Options();

        Option tOption = new Option("t", "token", true, "Telegram bot token.");
        tOption.setArgs(1);
        tOption.setOptionalArg(false);
        options.addOption(tOption);

        Option cOption = new Option("c", "chatId", true, "Telegram chat id.");
        cOption.setArgs(1);
        cOption.setOptionalArg(false);
        options.addOption(cOption);

        Option aOption = new Option("a", "author", true, "Articles author name.");
        aOption.setArgs(1);
        aOption.setOptionalArg(true);
        options.addOption(aOption);

        Option iOption = new Option("i", "interval", true, "Pooling interval.");
        iOption.setArgs(1);
        iOption.setOptionalArg(true);
        options.addOption(iOption);

        Option uOption = new Option("u", "rssUrl", true, "RSS url channel.");
        uOption.setArgs(1);
        uOption.setOptionalArg(true);
        options.addOption(uOption);

        try {
            CommandLineParser optionsParser = new DefaultParser();
            CommandLine cmd = optionsParser.parse(options, args);
            return new ConfigurationManager(cmd);
        } catch (Exception e) {
            printHelp(options, 80, "Options", "", 3, 5, true, System.out);
            return null;
        }
    }

    public static void printHelp(final Options options, final int printedRowWidth, final String header, final String footer, final int spacesBeforeOption,
                                 final int spacesBeforeOptionDescription, final boolean displayUsage, final OutputStream out) {
        final String commandLineSyntax = "java rss_to_telegram.jar";
        final PrintWriter writer = new PrintWriter(out);
        final HelpFormatter helpFormatter = new HelpFormatter();

        helpFormatter.printHelp(writer, printedRowWidth, commandLineSyntax, header, options, spacesBeforeOption, spacesBeforeOptionDescription,
                footer, displayUsage);
        writer.flush();
    }
}
