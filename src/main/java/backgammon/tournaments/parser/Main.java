package backgammon.tournaments.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class Main {
    public static String getRequest(String url) throws IOException {
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        connection.disconnect();
        return response.toString();
    }
    public static void main(String[] args) throws IOException {
        String resData = getRequest("http://www.chicagopoint.com/results.html");
        System.out.println(parsePlaces("1-Armando Balbi (Brasil)","bla"));

        System.out.println("1-Zigani (Grigore, Rigon, Markotic), 2-Vienna Bulls (Higatsberger, Parlow, Edy).".split("(?=[,;])\\s*(?![^()]*\\))").length);
        System.out.println(Arrays.toString("1-Zigani (Grigore, Rigon, Markotic), 2-Vienna Bulls (Higatsberger, Parlow, Edy).".split("(?=[,;])\\s*(?![^()]*\\))")));
        Document resDoc = Jsoup.parse(resData);
        Elements resultsPages = resDoc.select("#pagetop > p:nth-child(5) > a");
//        for (Element el: resultsPages){
//            System.out.println(el.text());
//            String resultPageHref = el.attr("href");
            String resultPageHref = "http://www.chicagopoint.com/results1998.html";
            String resultPageHtml = getRequest(resultPageHref);
            Document resPageDoc = Jsoup.parse(resultPageHtml);
            pageWithoutStyles(resPageDoc);
//        }
    }

    public static PlayerResult parsePlayerNameAndCountry(String place,String subTour, String str){
        String name="";
        StringBuilder country=new StringBuilder();
        int index = str.indexOf("(");
        if (index!=-1){
            name = str.substring(0, index-1);
            for (int i = index+1; i < str.length(); i++) {
                if (str.charAt(i)!=')')
                    country.append(str.charAt(i));
            }
        }
        else name = str;
        return new PlayerResult(name, place, country.toString(), subTour);
    }
    public static List<PlayerResult> parsePlaces(String placePlayers, String subTour){
        System.out.println("aaaaaaaa");
        System.out.println(placePlayers);
        String[] placePlayerDetails = placePlayers.split("[-:]{1}");
        if (placePlayerDetails.length ==1) return List.of(parsePlayerNameAndCountry("1",subTour,placePlayerDetails[0]));
        if (placePlayerDetails[0].contains("/")) {
            String[] places = placePlayerDetails[0].split("/");
            String[] names = placePlayerDetails[1].split("/");
            return List.of(
                    parsePlayerNameAndCountry(places[0],subTour,names[0]),
                    parsePlayerNameAndCountry(places[1],subTour,names[1])
            );
        }
        else return List.of(parsePlayerNameAndCountry(placePlayerDetails[0],subTour,placePlayerDetails[1]));
    }
    public static void pageWithoutStyles(Document resPageDoc){
        Element startTag = resPageDoc.select("h4").get(0);
        Elements hrElements = resPageDoc.select("hr");
        int end = 1;
        Element currentElement = startTag.nextElementSibling();
        Pattern pattern = Pattern.compile("\\((\\d+)");
        Pattern trophyPattern = Pattern.compile("(\\w+)(?:: )?(.*)");
        Matcher matcher;
        ArrayList<PlayerResult> results = new ArrayList<>();
        while (end++<hrElements.size()) {
            while (Objects.equals(currentElement.text(), "")) currentElement = currentElement.nextElementSibling();
            Element pElement = currentElement.select("p").get(0);
            String[] details = pElement.select("font").get(0).html().trim().split("<br>");

            String title = details[0];
            // Extract the title, start/end dates, and place from the details string
            String[] detailsParts = details[1].split("[;:]{1}");
            String dateRange = detailsParts[0].trim();
            String place = detailsParts[1].trim();

            // Print the extracted details
            System.out.println("Title: " + title);
            System.out.println("Date Range: " + dateRange);
            System.out.println("Place: " + place);
            currentElement = currentElement.nextElementSibling();


            while (currentElement.select("hr").size() == 0) {
                System.out.println(currentElement);
                if (currentElement.text().length()>0 && currentElement.text().indexOf(':')!=-1)
                if (currentElement.select("p").size() == 1) {
                    String currElText = currentElement.text();

                    if (currentElement.select("i").size() > 0 || currentElement.select("font[color='#ed181e']").size()>0) {

                    } else {
                        long colonCount = currElText.chars().filter(ch -> ch == ':').count();
                        matcher= pattern.matcher(currElText);
                        int count = 0;
                        while (matcher.find()) {
                            count++;
                        }

                        if (count == 0 &&colonCount>1){
                            for (String trophy: currElText.split("\\.\\s(?=SUPERJACKPOT|#)")){
                                System.out.println(trophy);
                                String subTourName = trophy.substring(0, trophy.indexOf(':'));
                                trophy = trophy.substring(subTourName.length()+1);
                                int c = 1;
                                for (String player: trophy.split(",")) {
                                    System.out.println("PLAYER");
                                    System.out.println(player);

                                    results.addAll(parsePlaces(player, subTourName));
                                }
                            }
                            currentElement = currentElement.nextElementSibling();
                            continue;
                        }
                        String subTourName = currElText.substring(0, currElText.indexOf(':'));
                        currElText = currElText.substring(subTourName.length()+1);
                        for (String subRound : currElText.split(";")) {
                            System.out.println(subRound);
                            if (!subRound.contains(":"))
                                for (String occupiedPlace : subRound.split(", (?![^()]*\\))")) {
                                    results.addAll(parsePlaces(occupiedPlace, subTourName));
                                }
                            else {
                                for (String subSubTour: subRound.split("\\.\\s(?=SUPERJACKPOT)")) {
                                    String[] subSubTourInfo = subSubTour.split(":");
                                    for (String player : subSubTourInfo[1].split(", ")) {
                                        System.out.println("PLAYER");
                                        System.out.println(player);
                                        results.addAll(parsePlaces(player, subTourName + subSubTourInfo[0]));
                                    }
                                }
                            }
                        }
                    }
                }

                currentElement = currentElement.nextElementSibling();
            }
        }
        for (PlayerResult res: results){
            System.out.println(res);
        }
    }
}
