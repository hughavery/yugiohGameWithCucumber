package uc.seng301.cardbattler.asg3.cli;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Interface for handling command line input to the application
 */
public class CommandLineInterface {
    private final Scanner cli;
    private final PrintStream printer;

    /**
     * Creates a new interface using the provided input and print streams
     * 
     * @param inputStream Stream to get input from
     * @param printStream Stream to print to
     */
    public CommandLineInterface(InputStream inputStream, PrintStream printStream) {
        cli = new Scanner(inputStream);
        printer = printStream;
    }

    /**
     * Get the next value from the input stream
     * 
     * @return Next values from input stream as String
     */
    public String getNextLine() {
        return cli.nextLine();
    }

    /**
     * Print the string to the output stream
     * 
     * @param toPrint String to print
     */
    public void printLine(String toPrint) {
        printer.println(toPrint);
    }
}
