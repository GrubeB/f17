package pl.app.inventory.shared;

public enum OfficerType {
    GRANDMASTER, // Increases the attack power of an attacking army by 10%
    MASTER_OF_LOOT, // Increases the carrying capacity of an attacking army by 50%.
    MEDIC, // Reduces the number of casualties of an attacking army by 10%
    DECEIVER, //Slows the travel speed of the entire army down to that of a Nobleman's.
    RANGER, // The attacking army only engages in battle if they would be victorious, otherwise they return home.
    TACTICIAN // Raises the speed of a supporting army to light cavalry speed.
}
