package org.example;

import org.json.JSONObject;
import picocli.CommandLine.Command;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

@Command(name = "cviewer", description = "Utility to view currencies exchange rates info.")
public class CurrencyViewer{
    @Command(name = "getrates",
            helpCommand = true,
            description = "View the most recent exchange rate data.",
            parameterListHeading = "base currencies")
    public void getRates(String base, String[] currencies) {
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
            helpCommand = true,
            description = "Convert one currency to another.",
            parameterListHeading = "amount from to")
    public void convert(double amount, String from, String to) {
        JSONObject data = API_Connection.convert(amount, from, to);
        if (data != null) {
            System.out.printf("%-15s%s\n", "Updated date:", data.getString("updated_date"));
            System.out.printf("%-15s%f\n", "Rate", data.getJSONObject("rates")
                    .getJSONObject(to).getDouble("rate"));
            System.out.printf("%-15s%f\n", "Rate for amount",
                    data.getJSONObject("rates")
                    .getJSONObject(to).getDouble("rate_for_amount"));
        }
    }

    @Command(name = "viewcurs",
            helpCommand = true,
            description = "View list of available currencies.")
    public void viewCurrencies() {
        JSONObject data = API_Connection.getAvailableCurrencies();
        if (data != null) {
            JSONObject currencies = data.getJSONObject("currencies");
            Set<String> keys = new TreeSet<>(currencies.keySet());
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                System.out.printf("%-10s%s\n", key, currencies.getString(key));
            }
        }
    }
}
