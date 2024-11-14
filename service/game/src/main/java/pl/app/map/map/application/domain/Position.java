package pl.app.map.map.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position position)) return false;
        return Objects.equals(x, position.x) && Objects.equals(y, position.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
