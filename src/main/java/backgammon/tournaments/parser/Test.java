package backgammon.tournaments.parser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Test {
    public static void main(String[] args) {
        try {
            // Connect to the website
            String url = "http://www.chicagopoint.com/results.html";
            Document doc = Jsoup.connect(url).get();

            // Locate the table containing the tour results
            Element resultsTable = doc.select("table").first();

            // Initialize variables to store tour information
            String tourName = "";
            String tourLocation = "";
            String tourDates = "";

            // Iterate through rows and columns of the table
            Elements rows = resultsTable.select("tr");
            for (Element row : rows) {
                Elements columns = row.select("td");
                if (columns.size() >= 3) {
                    // Extract tour information from the first row
                    if (row == rows.first()) {
                        tourName = columns.get(0).text();
                        tourLocation = columns.get(1).text();
                        tourDates = columns.get(2).text();
                    } else {
                        // Extract player information from subsequent rows
                        String subTourName = columns.get(0).text();
                        String playerName = columns.get(1).text();
                        String playerPlace = columns.get(2).text();
                        String playerCountry = columns.get(3).text();

                        // Print or process the extracted data as needed
                        System.out.println("Tour Name: " + tourName);
                        System.out.println("Tour Location: " + tourLocation);
                        System.out.println("Tour Dates: " + tourDates);
                        System.out.println("Sub Tour Name: " + subTourName);
                        System.out.println("Player Name: " + playerName);
                        System.out.println("Player Place: " + playerPlace);
                        System.out.println("Player Country: " + playerCountry);
                        System.out.println("-------------------");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
