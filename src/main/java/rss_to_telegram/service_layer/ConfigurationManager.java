package rss_to_telegram.service_layer;

import org.apache.commons.cli.*;

import java.io.OutputStream;
import java.io.PrintWriter;

public class ConfigurationManager {

    public static Options options = new Options();

    public static CommandLine loadConfiguration(String[] args) {

        Option pOption = new Option("c", "config", true, "Path to config file");
        pOption.setArgs(1);
        pOption.setOptionalArg(false);
        options.addOption(pOption);

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
