package org.example;

import org.example.exception.InvalidAmountException;
import org.json.JSONException;
import org.json.JSONObject;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Command;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.TreeSet;

@Command(name = "cviewer",
        mixinStandardHelpOptions = true,
        description = "Utility to view currencies exchange rate information.",
        subcommands = CommandLine.HelpCommand.class)
public class CurrencyViewer {
    @Command(name = "getrates",
            description = "View the most recent exchange rate data.")
    public void getRates(@Parameters(paramLabel = "base",
                         description = "currency for which the rates are shown") String base,
                         @Parameters(paramLabel = "currencies",
                         description = "space-separated list of currencies") String[] currencies) {
        JSONObject data = API_Connection.getRates(base, currencies);
        try{
            String dateStr = getFormattedDateString(data.getJSONObject("meta").getString("last_updated_at"));
            if (currencies != null) {
                System.out.printf("%s rates to %s at %s\n", base.toUpperCase(),
                        String.join(", ", currencies).toUpperCase(), dateStr);
            } else {
                System.out.printf("%s rates at %s\n", base.toUpperCase(), dateStr);
            }
            JSONObject currencyData = data.getJSONObject("data");
            Set<String> keys = new TreeSet<>(currencyData.keySet());
            for (String key : keys) {
                JSONObject currency = currencyData.getJSONObject(key);
                System.out.printf("%-10s%f\n", currency.getString("code"), currency.getDouble("value"));
            }
        }
        catch (JSONException e) {
            System.out.println(data.getJSONObject("errors").getJSONArray("base_currency").get(0));
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
        JSONObject data = API_Connection.convert(amount, from, to);
        try {
            if (amount <= 0)
                throw new InvalidAmountException("The amount cannot be less or equal to 0.");
            System.out.printf("%-20s%s\n", "Updated date:", data.getString("updated_date"));
            System.out.printf("%-20s%f\n", "Rate", data.getJSONObject("rates")
                    .getJSONObject(to).getDouble("rate"));
            System.out.printf("%-20s%f\n", "Rate for amount", data.getJSONObject("rates")
                    .getJSONObject(to).getDouble("rate_for_amount"));
        }
        catch (JSONException e) {
            System.out.println(data.getJSONObject("error").getString("message"));
        }
        catch (InvalidAmountException e) {
            System.out.println(e.getMessage());
        }
    }

    @Command(name = "viewcurs",
            description = "View list of available currencies.")
    public void viewCurrencies() {
        JSONObject data = API_Connection.getAvailableCurrencies();
        try {
            JSONObject currencies = data.getJSONObject("currencies");
            Set<String> keys = new TreeSet<>(currencies.keySet());
            for (String key : keys) {
                System.out.printf("%-10s%s\n", key, currencies.getString(key));
            }
        }
        catch (JSONException e) {
            System.out.println(data.getJSONObject("errors").getJSONArray("base_currency").get(0));
        }
    }
}