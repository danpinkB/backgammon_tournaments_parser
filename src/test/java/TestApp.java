import backgammon.tournaments.parser.ConstsM;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestApp {
    @Test
    public void test(){
        String regex = ", (?![^()]*\\))";
        String[] parts = "1-Theo Bruns (GER), 2-R.M. van der Pluijm (HOL), 3/4-Mads K. Madsen (DEN) / Anne Dahlin (DEN)".split(regex);

        for (String part : parts) {
            System.out.println(part);
        }
        parts = "1-Zigani (Grigore, Rigon, Markotic), 2-Vienna Bulls (Higatsberger, Parlow, Edy).".split(regex);
        for (String part : parts) {
            System.out.println(part);
        }
    }

    @Test
    public void test2(){
        Pattern pattern = Pattern.compile("\\((\\d+)");
        String regex = "\\.\\s(?=SUPERJACKPOT|#)";
        String[] parts = "MICROBLITZ #1 (32): 1/2-Frank Frigo (KY) / Frank Talbot (MI). #2 (32): 1-Don Faix (NY).".split(regex);
        Matcher matcher= pattern.matcher("MICROBLITZ #1 (32): 1/2-Frank Frigo (KY) / Frank Talbot (MI). #2 (32): 1-Don Faix (NY).");
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        System.out.println(count);
        for (String part : parts) {
            System.out.println(part);
        }
    }
    @Test
    public void testTourEl(){
        String bla = "November 4-7, 1993, Stockholm, Sweden";
        int ind = bla.indexOf(",", bla.indexOf(",")+1);
        System.out.println(bla.substring(ind+2));
        System.out.println(bla.substring(0,ind));
        String str = "26th JAPAN OPEN BACKGAMMON TOURNAMENT May 3-5, 2022; Tokyo, Japan";
        int i=0;
        int index = str.indexOf(ConstsM.monthes[0]);
        while (index==-1) index = str.indexOf(ConstsM.monthes[++i]);
        String title = str.substring(0,index);
        String dateLocation = str.substring(index);
        System.out.println(title);
        System.out.println(dateLocation);
    }
    @Test
    public void parseDate(){

        String input = "Dec. 25-27, 1998";
        String pattern = "[.,]{1}";

        for (String bla: input.replaceAll(" ","").split(pattern)) System.out.println(bla);
    }

    @Test
    public void testRegex(){
        String[] parts = "ABT - ACE POINT HOLIDAY TOURNAMENT Dec. 25-27, 1998; New York, NY".split("\\s(?=[A-Za-z]+\\s\\d+-\\d+(,\\s\\d{4})?;)|(?<=\\\")\\s");
        for (String part : parts) {
            System.out.println(part);
        }
        parts = "14th NORDIC \"WIDE\" OPEN March 28-April 1; Copenhagen, Denmark".split("\\s(?=[A-Za-z]+\\s\\d+-\\d+,\\s\\d{4};)|(?<=\")\\s|(?<=\\s)\\s");
        for (String part : parts) {
            System.out.println(part);
        }
    }
}
