package parser;

import org.apache.commons.cli.*;
import processor.Extactor;
import processor.Type;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by chiranz on 7/14/17.
 */
public class Cli {
    private static final Logger log = Logger.getLogger(Cli.class.getName());

    private String[] args = null;
    private Options options = new Options();

    public Cli(String[] args) throws MalformedURLException {

        this.args = args;

        options.addOption("h", "help", false, "show help.");
        //https://www.w3schools.com/cssref/css_selectors.asp
        options.addOption("p", "directory", true, "Directory Path");
        options.addOption("q", "query", true, "css query \n" +
                " For more info about css selectors visit "+new URL("https://www.w3schools.com/cssref/css_selectors.asp"));
        options.addOption("o", "outer", true, "outer html off ");
        options.addOption("r", "result", true, "get result print on off");
        options.addOption("t", "type", true, "set query type text , comments or default xhtml");

    }

    public boolean parse() {
        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h"))
                help();
            boolean withParent = true;
            boolean withResut = false;
            Type type=Type.xhtml;
            if (cmd.hasOption("o")) {
                if (cmd.getOptionValue("o").equalsIgnoreCase("off")) {
                    withParent = false;
                }
            }

            if (cmd.hasOption("r")) {
                if (cmd.getOptionValue("r").equalsIgnoreCase("on")) {
                    withResut = true;
                }

            }


            if (cmd.hasOption("t")) {
                if (cmd.getOptionValue("t").equalsIgnoreCase("comment")) {
                    type = Type.comment;
                }else if (cmd.getOptionValue("t").equalsIgnoreCase("text")) {
                    type = Type.text;
                }

            }

            if (cmd.hasOption("q") && cmd.hasOption("p")) {

                try {
                    String path = cmd.getOptionValue("p");
                    String query = cmd.getOptionValue("q");
                    new Extactor().getElements(path, query, withParent, withResut,type);
                    return true;
                } catch (Exception e) {
                    log.log(Level.SEVERE, e.getMessage());
                    help();

                }
            } else {
                help();
                log.log(Level.SEVERE, "Missing p and q option");
            }

            if (cmd.hasOption("p")) {
                log.log(Level.INFO, "Using cli argument -p=" + cmd.getOptionValue("p"));
                // Whatever you want to do with the setting goes here
            } else {
                log.log(Level.SEVERE, "Missing p option");
                help();
            }


            if (cmd.hasOption("q")) {
                log.log(Level.INFO, "Using cli argument -q=" + cmd.getOptionValue("q"));
                // Whatever you want to do with the setting goes here
            } else {
                log.log(Level.SEVERE, "Missing q option");
                help();
            }

            return true;


        } catch (ParseException e) {

            log.log(Level.SEVERE, "Failed to parse comand line properties", e.getMessage());
            help();
            return false;
        }
    }

    private void help() {
        // This prints out some help
        HelpFormatter formater = new HelpFormatter();

        formater.printHelp("Main", options);

        System.exit(0);
    }
}
