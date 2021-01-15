package sample;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

public class CurrencyWebScrapping {

    //function to get the data from the website
    public static void webScrapping() {
        final String url = "https://www.maybank2u.com.my/maybank2u/malaysia/en/personal/rates/forex_rates.page";

        try {
            final Document document = Jsoup.connect(url).get();

            for (Element row : document.select("table.table tr")) {
                if (row.select(".currency").text().equals(""))
                    continue;

                else {
                    String currency = row.select(".currency").text();

                    if (currency.equals("1 Brunei Dollar") || currency.equals("100 Chinese Renminbi") || currency.equals("1 Euro") || currency.equals("100 Indian Rupee") ||
                            currency.equals("100 Japanese Yen") || currency.equals("100 Swedish Krona") || currency.equals("1 Singapore Dollar") ||
                            currency.equals("1 Sterling Pound") || currency.equals("1 US Dollar")) {
                        ArrayList<Double> rate = new ArrayList<>();
                        try {
                            rate.add(Double.parseDouble(row.select("td:nth-of-type(4)").text()));
                            rate.add(Double.parseDouble(row.select("td:nth-of-type(3)").text()));
                        } catch (Exception e) { continue; }

                        ReadFile.DataStorage.currencyMap.put(currency, rate);
                    }
                }

            }

        } catch (Exception e) { e.printStackTrace(); }
    }
}
