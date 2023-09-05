package backgammon.tournaments.parser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.javatuples.Triplet;
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
//    public static void main2(String[] args) throws IOException, ParseException, SQLException, ClassNotFoundException {
//        String resData = getRequest("http://www.chicagopoint.com/results.html");
//        Document resDoc = Jsoup.parse(resData);
//        Elements resultsPages = resDoc.select("#pagetop > p:nth-child(5) > a");
//
//        DbConnection connection = new DbConnection();
//        for (Element el: resultsPages){
//            String resultPageHref = el.attr("href");
////            String resultPageHref = "http://www.chicagopoint.com/results1998.html";
//            String resultPageHtml = getRequest(resultPageHref);
//            Document resPageDoc = Jsoup.parse(resultPageHtml);
//            Elements container = resPageDoc.select("div[id='results']");
//            if (container.size()>0)
//                parsePageContent(container.get(0), "hr, h4",connection, Main::styledPageCurrentElement);
//            else parsePageContent(resPageDoc, "hr",connection, Main::cleanPageCurrentElement);
//        }
//    }
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, ParseException {
        String resData = getRequest("http://www.chicagopoint.com/results.html");
        Document resDoc = Jsoup.parse(resData);
        Elements resultsPages = resDoc.select("#pagetop > p:nth-child(5) > a");
        DbConnection connection = new DbConnection();
//        DbConnection connection = new DbConnection();
        for (Element el: resultsPages){
            String resultPageHref = el.attr("href");
//            String resultPageHref = "http://www.chicagopoint.com/results1998.html";
            Path path = Paths.get(System.getProperty("user.dir")+String.format("/src/main/resources/%s.html", el.text().strip()));
            File file = new File(path.toUri());
            if (!Files.exists(path)) {
                String resultPageHtml = getRequest(resultPageHref);

                FileWriter fw = new FileWriter(path.toString());
                fw.write(resultPageHtml);
            }
            String pathString = path.toString();
            int lastSlash = pathString.lastIndexOf('/');
            int dotHtml = pathString.lastIndexOf(".html");
            String year = pathString.substring(lastSlash + 1, dotHtml);
            Document resPageDoc = Jsoup.parse(file,"UTF-8");
            System.out.println(path.toString());
            Elements container = resPageDoc.select("div[id='results']");
            if (container.size()>0)
                parsePageContent(container.get(0), "hr, h4",connection, Main::styledPageCurrentElement, year);
            else parsePageContent(resPageDoc, "hr",connection, Main::cleanPageCurrentElement, year);
        }
    }

    public static PlayerResult parsePlayerNameAndCountry(String place,String subTour, String str){
        String name="";
        StringBuilder country=new StringBuilder();
        String countryStr = "";
        int index = str.indexOf("(");
        if (index!=-1&&!str.contains("&")){
            int i = index;
            for (; i < str.length(); i++) {
                if (str.charAt(i)!=')')
                    country.append(str.charAt(i));
            }

            name = str.replace(str.substring(index, i),"");
            countryStr = country.toString().replace("(","").replace(")","");
        }
        else name = str;
        return new PlayerResult(name, place.trim(), countryStr, subTour);
    }

    public static List<PlayerResult> parsePlaces(String placePlayers, String subTour){
        String[] placePlayerDetails = placePlayers.split("[-:]{1}");
        if (placePlayerDetails.length == 1) return List.of(parsePlayerNameAndCountry("1",subTour,placePlayerDetails[0]));
        if (placePlayerDetails[0].contains("/")) {
            String[] places = placePlayerDetails[0].split("/");
            String[] names = placePlayerDetails[1].split("[/1]{1}");
            if (places.length==1||names.length==1)return List.of(parsePlayerNameAndCountry(placePlayerDetails[0],subTour,placePlayerDetails[1]));
            return List.of(
                    parsePlayerNameAndCountry(places[0],subTour,names[0]),
                    parsePlayerNameAndCountry(places[1],subTour,names[1])
            );
        }
        else return List.of(parsePlayerNameAndCountry(placePlayerDetails[0],subTour,placePlayerDetails[1]));
    }
    public static List<PlayerResult> parseElement(Element currentElement){
        String currElText = currentElement.text();
        List<PlayerResult> results = new ArrayList<>();

        String subTourName = currElText.substring(0, currElText.indexOf(':'));
        currElText = currElText.substring(subTourName.length() + 1);
        for (String subRound : currElText.split("[;.]{1}")) {

            if (!subRound.contains(":"))
                for (String occupiedPlace : subRound.split(", (?![^()]*\\))")) {
                    results.addAll(parsePlaces(occupiedPlace, subTourName));
                }
            else {
//                System.out.println(subRound);
                for (String subSubTour : subRound.split("\\.\\s(?=SUPERJACKPOT)")) {
                    String[] subSubTourInfo = subSubTour.split(":");
                    for (String player : subSubTourInfo[1].split(", ")) {

                        results.addAll(parsePlaces(player, subTourName + subSubTourInfo[0]));
                    }
                }
            }
        }


        return results;
    }
//    public static Triplet<String,String,String> parseCleanPageArgs(Element currentElement, String year){
////        Element pElement = currentElement.select("p").get(0);
//        int index = 0;
//
//        String[] details;
//        currentElement.text();
//        List<Element> fonts = currentElement.select("b, font").stream().filter(x->x.text().length()>5).toList();
//        if (fonts.size()>1) {
//            details = new String[]{fonts.get(0).text(),fonts.get(1).text()};
//        }
//        else details = fonts.get(0).html().trim().split("<br>");
//        if (details.length>2)
//            index++;
//        String title;
//        String dateRange;
//        String place;
//        if (details.length==1){
//            title = currentElement.text();
//            String[] detailsParts = details[0].split("[;:,]{1}");
//            dateRange = detailsParts[index].trim();
//            place = detailsParts[index].trim();
//        }
//        else {
//            title = details[0];
//            // Extract the title, start/end dates, and place from the details string
//            String[] detailsParts = details[index + 1].split("[;:,]{1}");
//            dateRange = detailsParts[index].trim();
//            place = detailsParts[index].trim();
//        }
//        if (!dateRange.contains(year))
//            dateRange = dateRange.concat(year);
//        return new Triplet<>(title, dateRange, place);
//    }
    public static Element cleanPageCurrentElement(Element el){
        return el.select("h4 , hr").get(0).nextElementSibling();
    }

    public static Triplet<String,String,String> parsePageArgs(Element currentElement, String year){
        String str = currentElement.text();
        int i=0;
        int index = str.indexOf(ConstsM.monthes[0]);
        while (index==-1) index = str.indexOf(ConstsM.monthes[++i]);
        String title = str.substring(0, index);
        String dateLocation = str.substring(index);
        String[] details = dateLocation.split(";");
        String dateRange;
        String place;
        if (details.length==2) {
            dateRange = details[0].trim();
            place = details[1].trim();
        }
        else {
            int indexP = dateLocation.indexOf(",", dateLocation.indexOf(",") + 1);
            dateRange = dateLocation.substring(0,indexP);
            place = dateLocation.substring(indexP+1);
        }
        if (!dateRange.contains(year))
            dateRange = dateRange.concat(",").concat(year);
        return new Triplet<>(title, dateRange.replace(" ",""), place.replace(" ",""));
    }
    public static Element styledPageCurrentElement(Element el){return el.select("p").get(0);}

    public static void parsePageContent(Element resPageDoc, String hrSelector,DbConnection connection, Function<Element,Element> currentElementFunction, String year) throws ParseException, SQLException {
        Elements hrElements=resPageDoc.select(hrSelector);
        Element currentElement = currentElementFunction.apply(resPageDoc);
        List<PlayerResult> playerResults;
        ArrayList<PlayerResult> results;
        int end = 1;
        while (end++<hrElements.size()) {
            results = new ArrayList<>();
            while (currentElement!=null && currentElement.text().length()<10) currentElement = currentElement.nextElementSibling();
            if (currentElement==null) continue;
            if (currentElement.text().toLowerCase().contains("return")) continue;
            Triplet<String,String,String> parsedArgs = parsePageArgs(currentElement, year);
            String title = parsedArgs.getValue0(), dateRange= parsedArgs.getValue1(), city= parsedArgs.getValue2();

            currentElement = currentElement.nextElementSibling();
//            System.out.println(currentElement);
            while (currentElement.select("hr").size() == 0) {
                System.out.println(currentElement);
                if (currentElement.text().length()>0 && currentElement.text().indexOf(':')!=-1)
                    if (currentElement.select("p").size() == 1) {

                        playerResults = parseElement(currentElement);
                        if (playerResults!=null)
                            results.addAll(playerResults);

                    }
                currentElement = currentElement.nextElementSibling();
                if (currentElement==null) break;
            }
            for (PlayerResult res: results){
                connection.insertResult(res,title,dateRange,city);
            }
        }

    }
}
