package pl.app.map.map.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    private Integer x;
    private Integer y;
    private Province province;

    public static double calculateDistance(Position pos1, Position pos2) {
        return Math.sqrt(Math.pow(pos2.getX() - pos1.getX(), 2)
                + Math.pow(pos2.getY() - pos1.getY(), 2)
        );
    }
}
