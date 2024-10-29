package pl.app.item.item.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class Officers implements Serializable {
    private Boolean grandmaster;
    private Boolean masterOfLoot;
    private Boolean medic;
    private Boolean deceiver;
    private Boolean ranger;
    private Boolean tactician;

    public Officers() {
        this.grandmaster = false;
        this.masterOfLoot = false;
        this.medic = false;
        this.deceiver = false;
        this.ranger = false;
        this.tactician = false;
    }

    public Officers(Boolean grandmaster, Boolean masterOfLoot, Boolean medic, Boolean deceiver, Boolean ranger, Boolean tactician) {
        this.grandmaster = grandmaster;
        this.masterOfLoot = masterOfLoot;
        this.medic = medic;
        this.deceiver = deceiver;
        this.ranger = ranger;
        this.tactician = tactician;
    }
}
