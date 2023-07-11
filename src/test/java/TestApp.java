import org.junit.jupiter.api.Test;

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
        String regex = "\\.\\s(?=SUPERJACKPOT|#)";
        String[] parts = "MICROBLITZ #1 (32): 1/2-Frank Frigo (KY) / Frank Talbot (MI). #2 (32): 1-Don Faix (NY).".split(regex);

        for (String part : parts) {
            System.out.println(part);
        }
    }
}
