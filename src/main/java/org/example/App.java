package org.example;

import picocli.CommandLine;

public class App {
    public static void main(String[] args) {
        CommandLine cli = new CommandLine(CurrencyViewer.class);
        cli.execute(args);
    }
}
