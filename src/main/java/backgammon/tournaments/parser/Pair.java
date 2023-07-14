package backgammon.tournaments.parser;

import lombok.Data;

@Data
public class Pair<X,Y> {
    private X x;
    private Y y;
    public Pair(X x, Y y) {
        this.x = x;
        this.y = y;
    }
}
