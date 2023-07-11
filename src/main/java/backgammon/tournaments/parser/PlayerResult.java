package backgammon.tournaments.parser;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
class PlayerResult{
    public PlayerResult(String name, String place, String country, String subTour) {
        this.name = name;
        this.place = place;
        this.country = country;
        this.subTour = subTour;
    }
    private String subTour;
    private String name;
    private String place;
    private String country;
}