package org.example;

import org.json.JSONObject;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Command;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.TreeSet;

@Command(name = "cviewer",
        mixinStandardHelpOptions = true,
        description = "Utility to view currencies exchange rate information.")
public class CurrencyViewer {
    @Command(name = "getrates",
            description = "View the most recent exchange rate data.")
    public void getRates(@Parameters(paramLabel = "base",
                         description = "currency for which the rates are shown") String base,
                         @Parameters(paramLabel = "currencies",
                         description = "space-separated list of currencies") String[] currencies) {
        JSONObject data = API_Connection.getRates(base, currencies);
        if (data != null) {
            ZonedDateTime date = ZonedDateTime.parse(data.getJSONObject("meta").getString("last_updated_at"));
            String dateStr = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            if (currencies != null) {
                System.out.printf("%s rates to %s at %s\n", base.toUpperCase(),
                        String.join(", ", currencies).toUpperCase(), dateStr);
            } else {
                System.out.printf("%s rates at %s\n", base, dateStr);
            }
            JSONObject currencyData = data.getJSONObject("data");
            Set<String> keys = new TreeSet<>(currencyData.keySet());
            for (String key : keys) {
                JSONObject currency = currencyData.getJSONObject(key);
                System.out.printf("%-10s%f\n", currency.getString("code"), currency.getDouble("value"));
            }
        }
    }

    @Command(name = "convert",
            description = "Convert one currency to another.")
    public void convert(@Parameters(paramLabel = "amount", description = "currency amount") double amount,
                        @Parameters(paramLabel = "from", description = "currency to be converted from") String from,
                        @Parameters(paramLabel = "to", description = "currency to be converted to") String to) {
        JSONObject data = API_Connection.convert(amount, from, to);
        if (data != null) {
            System.out.printf("%-20s%s\n", "Updated date:", data.getString("updated_date"));
            System.out.printf("%-20s%f\n", "Rate", data.getJSONObject("rates")
                    .getJSONObject(to).getDouble("rate"));
            System.out.printf("%-20s%f\n", "Rate for amount", data.getJSONObject("rates")
                    .getJSONObject(to).getDouble("rate_for_amount"));
        }
    }

    @Command(name = "viewcurs",
            description = "View list of available currencies.")
    public void viewCurrencies() {
        JSONObject data = API_Connection.getAvailableCurrencies();
        if (data != null) {
            JSONObject currencies = data.getJSONObject("currencies");
            Set<String> keys = new TreeSet<>(currencies.keySet());
            for (String key : keys) {
                System.out.printf("%-10s%s\n", key, currencies.getString(key));
            }
        }
    }
}
