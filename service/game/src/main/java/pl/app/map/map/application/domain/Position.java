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
}
