package org.example;

import org.example.exception.CurrencyIsNotSupported;
import org.example.exception.InvalidAmountException;
import org.json.JSONObject;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Command;
import java.io.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Command(name = "cviewer",
        mixinStandardHelpOptions = true,
        description = "Utility to view currencies exchange rate information.",
        subcommands = CommandLine.HelpCommand.class)
public class CurrencyViewer {
    private static final String FILE_NAME = "currencies.txt";
    private static final String DELIMITER = "=";
    private final static String CURRENCY_IS_NOT_SUPPORTED = "The currency %s is not supported.";
    private final static String INVALID_AMOUNT = "The amount cannot be less or equal to 0.";

    @Command(name = "getrates",
            description = "View the most recent exchange rate data.")
    public void getRates(@Parameters(paramLabel = "base",
                         description = "currency for which the rates are shown") String base,
                         @Parameters(paramLabel = "currencies",
                         description = "space-separated list of currencies") String[] currencies) {
        base = base.toUpperCase();
        if (currencies != null)
            currencies = Arrays.stream(currencies).map(String::toUpperCase).toArray(String[]::new);

        JSONObject data = API_Connection.getRates(base, currencies);

        try {
            if (isNotSupported(base))
                throw new CurrencyIsNotSupported(String.format(CURRENCY_IS_NOT_SUPPORTED, base));
            if (currencies != null) {
                for (String currency : currencies) {
                    if (isNotSupported(currency))
                        throw new CurrencyIsNotSupported(String.format(CURRENCY_IS_NOT_SUPPORTED, currency));
                }
            }

            String dateStr = getFormattedDateString(data.getJSONObject("meta").getString("last_updated_at"));

            if (currencies == null) {
                System.out.printf("%s rates at %s\n", base, dateStr);
            }
            else if (currencies.length == 1) {
                System.out.printf("%s rate to %s at %s\n", base,
                        String.join(", ", currencies), dateStr);
            }
            else {
                System.out.printf("%s rates to %s at %s\n", base,
                        String.join(", ", currencies), dateStr);
            }
            JSONObject currencyData = data.getJSONObject("data");
            Set<String> keys = new TreeSet<>(currencyData.keySet());
            for (String key : keys) {
                JSONObject currency = currencyData.getJSONObject(key);
                String code = currency.getString("code");
                if (!isNotSupported(code))
                    System.out.printf("%-10s%f\n", code, currency.getDouble("value"));
            }
        }
        catch (CurrencyIsNotSupported e) {
            System.out.println(e.getMessage());
        }
    }

    private String getFormattedDateString(String unformattedDateString) {
        ZonedDateTime date = ZonedDateTime.parse(unformattedDateString);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return date.format(formatter);
    }

    @Command(name = "convert",
            description = "Convert one currency to another.")
    public void convert(@Parameters(paramLabel = "amount", description = "currency amount") double amount,
                        @Parameters(paramLabel = "from", description = "currency to be converted from") String from,
                        @Parameters(paramLabel = "to", description = "currency to be converted to") String to) {
        from = from.toUpperCase();
        to = to.toUpperCase();
        JSONObject data = API_Connection.convert(amount, from, to);
        try {
            if (amount <= 0)
                throw new InvalidAmountException(INVALID_AMOUNT);
            if (isNotSupported(from))
                throw new CurrencyIsNotSupported(String.format(CURRENCY_IS_NOT_SUPPORTED, from));
            if (isNotSupported(to))
                throw new CurrencyIsNotSupported(String.format(CURRENCY_IS_NOT_SUPPORTED, to));
            System.out.printf("%-20s%s\n", "Updated date:", data.getString("updated_date"));
            System.out.printf("%-20s%f\n", "Rate", data.getJSONObject("rates")
                    .getJSONObject(to).getDouble("rate"));
            System.out.printf("%-20s%f\n", "Rate for amount", data.getJSONObject("rates")
                    .getJSONObject(to).getDouble("rate_for_amount"));
        }
        catch (InvalidAmountException | CurrencyIsNotSupported e) {
            System.out.println(e.getMessage());
        }
    }

    @Command(name = "viewcurs",
            description = "View list of available currencies.")
    public void viewCurrencies() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            saveAvailableCurrenciesToFile();
        }
        try (FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr)){
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(DELIMITER);
                System.out.printf("%-10s%s\n", data[0], data[1]);
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Map<String, Object> getAvailableCurrencies() {
        JSONObject data = API_Connection.getAvailableCurrencies();
        JSONObject currencies = data.getJSONObject("currencies");
        return currencies.toMap();
    }

    public static File saveAvailableCurrenciesToFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            try (FileWriter fw = new FileWriter(file);
                 BufferedWriter bw = new BufferedWriter(fw)) {
                Map<String, Object> data = new TreeMap<>(getAvailableCurrencies());
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    bw.write(entry.getKey() + DELIMITER + entry.getValue() + "\n");
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return file;
    }

    public static boolean isNotSupported(String currency) {
        File file = saveAvailableCurrenciesToFile();
        boolean isNotSupported = true;
        try (FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr)){
            String line;
            while ((line = br.readLine()) != null) {
                if (currency.equals(line.split(DELIMITER)[0])) {
                    isNotSupported = false;
                    break;
                }
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return isNotSupported;
    }
}