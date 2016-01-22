package rss_to_telegram;

import org.apache.commons.cli.*;

import java.io.OutputStream;
import java.io.PrintWriter;

public class ConfigurationManager {

    public static Options options = new Options();

    public static CommandLine loadConfiguration(String[] args) {

        Option tOption = new Option("t", "token", true, "Telegram bot token. [required, string]");
        tOption.setArgs(1);
        tOption.setOptionalArg(false);
        options.addOption(tOption);

        Option cOption = new Option("c", "chatId", true, "Telegram chat id. [required, integer]");
        cOption.setArgs(1);
        cOption.setOptionalArg(false);
        options.addOption(cOption);

        Option iOption = new Option("i", "interval", true, "Pooling interval. [optional(default=300), integer]");
        iOption.setArgs(1);
        iOption.setOptionalArg(true);
        options.addOption(iOption);

        Option uOption = new Option("u", "rssUrl", true, "RSS/Author combination. Use :: as a delimiter. Example: http://rss.com::john_doe or http://rss.com. [required, string]");
        uOption.setArgs(1);
        uOption.setOptionalArg(false);
        options.addOption(uOption);

        try {
            CommandLineParser optionsParser = new DefaultParser();
            return optionsParser.parse(options, args);
        } catch (Exception e) {
            printHelp(options, 80, "Options", "", 3, 5, true, System.out);
            return null;
        }
    }

    public static void printDefaultHelp() {
        printHelp(options, 80, "Options", "", 3, 5, true, System.out);
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
