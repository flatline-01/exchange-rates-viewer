package org.example;

import picocli.CommandLine.Command;

public class CommandHandler {
    @Command(description = "View the most recent exchange rate data.")
    public void getRates() {}

    @Command(description = "Convert one currency to another.")
    public void convert(double amount, String from, String to) {}
}
