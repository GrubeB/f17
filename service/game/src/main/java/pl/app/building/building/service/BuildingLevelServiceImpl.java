package pl.app.building.building.service;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import pl.app.building.building.model.BuildingLevel;
import pl.app.building.building.model.BuildingType;
import pl.app.building.building.model.building_level.*;
import pl.app.resource.share.Resource;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.*;
import static pl.app.building.building.model.BuildingLevel.Requirement;
import static pl.app.building.building.model.BuildingType.*;

@Component
@NoArgsConstructor
class BuildingLevelServiceImpl implements BuildingLevelService {
    private final Set<AcademyLevel> academyLevels = Set.of(
            new AcademyLevel(1, new Resource(60_000, 60_000, 60_000, 80), Duration.of(10, HOURS), Set.of(new Requirement(HEADQUARTERS, 20)))
    );
    private final Set<BarracksLevel> barrackLevels = Set.of(
            new BarracksLevel(1, new Resource(400, 500, 600, 7), Duration.of(1, MINUTES), Set.of(new Requirement(HEADQUARTERS, 2))),
            new BarracksLevel(2, new Resource(480, 600, 720, 1), Duration.of(2, MINUTES), Set.of(new Requirement(HEADQUARTERS, 2))),
            new BarracksLevel(3, new Resource(580, 720, 860, 2), Duration.of(3, MINUTES), Set.of(new Requirement(HEADQUARTERS, 2))),
            new BarracksLevel(4, new Resource(700, 760, 1030, 2), Duration.of(4, MINUTES), Set.of(new Requirement(HEADQUARTERS, 2))),
            new BarracksLevel(5, new Resource(840, 1030, 1020, 3), Duration.of(6, MINUTES), Set.of(new Requirement(HEADQUARTERS, 2))),

            new BarracksLevel(6, new Resource(1800, 2000, 2200, 3), Duration.of(8, MINUTES), Set.of(new Requirement(HEADQUARTERS, 2))),
            new BarracksLevel(7, new Resource(2200, 2400, 2600, 4), Duration.of(10, MINUTES), Set.of(new Requirement(HEADQUARTERS, 2))),
            new BarracksLevel(8, new Resource(2600, 2900, 3100, 4), Duration.of(12, MINUTES), Set.of(new Requirement(HEADQUARTERS, 2))),
            new BarracksLevel(9, new Resource(3100, 3500, 3700, 5), Duration.of(14, MINUTES), Set.of(new Requirement(HEADQUARTERS, 2))),
            new BarracksLevel(10, new Resource(3700, 4200, 4400, 6), Duration.of(16, MINUTES), Set.of(new Requirement(HEADQUARTERS, 2))),

            new BarracksLevel(11, new Resource(6000, 7000, 8000, 7), Duration.of(18, MINUTES), Set.of(new Requirement(HEADQUARTERS, 2))),
            new BarracksLevel(12, new Resource(7200, 8400, 9600, 9), Duration.of(20, MINUTES), Set.of(new Requirement(HEADQUARTERS, 2))),
            new BarracksLevel(13, new Resource(8600, 10100, 11500, 10), Duration.of(25, MINUTES), Set.of(new Requirement(HEADQUARTERS, 2))),
            new BarracksLevel(14, new Resource(10300, 12100, 13800, 13), Duration.of(30, MINUTES), Set.of(new Requirement(HEADQUARTERS, 2))),
            new BarracksLevel(15, new Resource(12400, 14500, 16600, 15), Duration.of(45, MINUTES), Set.of(new Requirement(HEADQUARTERS, 2))),

            new BarracksLevel(16, new Resource(21000, 23000, 25000, 21), Duration.of(1, HOURS), Set.of(new Requirement(HEADQUARTERS, 2))),
            new BarracksLevel(17, new Resource(22000, 25000, 26000, 26), Duration.of(2, HOURS), Set.of(new Requirement(HEADQUARTERS, 2))),
            new BarracksLevel(18, new Resource(23000, 28000, 27000, 31), Duration.of(3, HOURS), Set.of(new Requirement(HEADQUARTERS, 2))),
            new BarracksLevel(19, new Resource(24000, 31000, 28000, 38), Duration.of(4, HOURS), Set.of(new Requirement(HEADQUARTERS, 2))),
            new BarracksLevel(20, new Resource(25000, 34000, 29000, 44), Duration.of(5, HOURS), Set.of(new Requirement(HEADQUARTERS, 2))),

            new BarracksLevel(21, new Resource(26000, 37000, 30000, 54), Duration.of(6, HOURS), Set.of(new Requirement(HEADQUARTERS, 2))),
            new BarracksLevel(22, new Resource(27000, 41000, 32000, 64), Duration.of(7, HOURS), Set.of(new Requirement(HEADQUARTERS, 2))),
            new BarracksLevel(23, new Resource(28000, 45000, 34000, 64), Duration.of(8, HOURS), Set.of(new Requirement(HEADQUARTERS, 2))),
            new BarracksLevel(24, new Resource(29000, 50000, 36000, 78), Duration.of(9, HOURS), Set.of(new Requirement(HEADQUARTERS, 2))),
            new BarracksLevel(25, new Resource(30000, 55000, 38000, 92), Duration.of(10, HOURS), Set.of(new Requirement(HEADQUARTERS, 2)))
    );
    private final Set<ChapelLevel> chapelLevels = Set.of(
            new ChapelLevel(1, new Resource(400, 500, 600, 7), Duration.of(1, MINUTES), new HashSet<>())
    );
    private final Set<ChurchLevel> churchLevels = Set.of(
            new ChurchLevel(1, new Resource(400, 500, 600, 7), Duration.of(1, MINUTES), new HashSet<>()),
            new ChurchLevel(2, new Resource(480, 600, 720, 1), Duration.of(2, MINUTES), new HashSet<>()),
            new ChurchLevel(3, new Resource(580, 720, 860, 2), Duration.of(3, MINUTES), new HashSet<>())
    );
    private final Set<ResourceLevel> clayPitLevels = Set.of(
            new ResourceLevel(1, new Resource(30, 50, 40, 1), Duration.of(5, SECONDS), new HashSet<>(), 120),
            new ResourceLevel(2, new Resource(40, 70, 60, 1), Duration.of(1, MINUTES), new HashSet<>(), 132),
            new ResourceLevel(3, new Resource(50, 90, 70, 1), Duration.of(1, MINUTES).plus(30, SECONDS), new HashSet<>(), 146),
            new ResourceLevel(4, new Resource(60, 110, 80, 1), Duration.of(2, MINUTES), new HashSet<>(), 162),
            new ResourceLevel(5, new Resource(80, 150, 90, 1), Duration.of(2, MINUTES).plus(30, SECONDS), new HashSet<>(), 180),

            new ResourceLevel(6, new Resource(100, 200, 100, 1), Duration.of(3, MINUTES).plus(30, SECONDS), new HashSet<>(), 200),
            new ResourceLevel(7, new Resource(200, 200, 100, 1), Duration.of(5, MINUTES), new HashSet<>(), 222),
            new ResourceLevel(8, new Resource(200, 200, 100, 2), Duration.of(10, MINUTES), new HashSet<>(), 248),
            new ResourceLevel(9, new Resource(200, 300, 200, 2), Duration.of(15, MINUTES), new HashSet<>(), 276),
            new ResourceLevel(10, new Resource(300, 400, 200, 3), Duration.of(20, MINUTES), new HashSet<>(), 308),

            new ResourceLevel(11, new Resource(400, 500, 300, 3), Duration.of(25, MINUTES), new HashSet<>(), 343),
            new ResourceLevel(12, new Resource(600, 500, 300, 4), Duration.of(30, MINUTES), new HashSet<>(), 383),
            new ResourceLevel(13, new Resource(600, 700, 400, 4), Duration.of(40, MINUTES), new HashSet<>(), 429),
            new ResourceLevel(14, new Resource(700, 900, 500, 5), Duration.of(50, MINUTES), new HashSet<>(), 480),
            new ResourceLevel(15, new Resource(900, 1000, 700, 5), Duration.of(60, MINUTES), new HashSet<>(), 536),

            new ResourceLevel(16, new Resource(1000, 1000, 1000, 6), Duration.of(1, HOURS), new HashSet<>(), 600),
            new ResourceLevel(17, new Resource(1000, 2000, 1000, 6), Duration.of(2, HOURS), new HashSet<>(), 673),
            new ResourceLevel(18, new Resource(2000, 2000, 1000, 7), Duration.of(3, HOURS), new HashSet<>(), 756),
            new ResourceLevel(19, new Resource(2000, 3000, 2000, 7), Duration.of(4, HOURS), new HashSet<>(), 846),
            new ResourceLevel(20, new Resource(3000, 3000, 2000, 8), Duration.of(5, HOURS), new HashSet<>(), 948),

            new ResourceLevel(21, new Resource(3000, 3000, 2000, 9), Duration.of(6, HOURS), new HashSet<>(), 1064),
            new ResourceLevel(22, new Resource(4000, 5000, 3000, 10), Duration.of(7, HOURS), new HashSet<>(), 1194),
            new ResourceLevel(23, new Resource(5000, 6000, 4000, 12), Duration.of(8, HOURS), new HashSet<>(), 1340),
            new ResourceLevel(24, new Resource(6000, 8000, 5000, 14), Duration.of(9, HOURS), new HashSet<>(), 1504),
            new ResourceLevel(25, new Resource(8000, 10000, 6000, 16), Duration.of(10, HOURS), new HashSet<>(), 1690),

            new ResourceLevel(26, new Resource(10000, 12000, 7000, 18), Duration.of(12, HOURS), new HashSet<>(), 1890),
            new ResourceLevel(27, new Resource(12000, 16000, 9000, 20), Duration.of(14, HOURS), new HashSet<>(), 2130),
            new ResourceLevel(28, new Resource(16000, 19000, 12000, 22), Duration.of(16, HOURS), new HashSet<>(), 2390),
            new ResourceLevel(29, new Resource(19000, 24000, 15000, 24), Duration.of(18, HOURS), new HashSet<>(), 2688),
            new ResourceLevel(30, new Resource(24000, 30000, 18000, 26), Duration.of(20, HOURS), new HashSet<>(), 3020)
    );
    private final Set<FarmLevel> farmLevels = Set.of(
            new FarmLevel(1, new Resource(30, 50, 40, 1), Duration.of(30, SECONDS), new HashSet<>(), 240),
            new FarmLevel(2, new Resource(40, 70, 60, 1), Duration.of(1, MINUTES), new HashSet<>(), 281),
            new FarmLevel(3, new Resource(50, 90, 70, 1), Duration.of(1, MINUTES).plus(30, SECONDS), new HashSet<>(), 330),
            new FarmLevel(4, new Resource(60, 110, 80, 1), Duration.of(2, MINUTES), new HashSet<>(), 386),
            new FarmLevel(5, new Resource(80, 150, 90, 1), Duration.of(2, MINUTES).plus(30, SECONDS), new HashSet<>(), 453),

            new FarmLevel(6, new Resource(100, 200, 100, 1), Duration.of(3, MINUTES).plus(30, SECONDS), new HashSet<>(), 531),
            new FarmLevel(7, new Resource(200, 200, 100, 1), Duration.of(5, MINUTES), new HashSet<>(), 622),
            new FarmLevel(8, new Resource(200, 200, 100, 2), Duration.of(10, MINUTES), new HashSet<>(), 729),
            new FarmLevel(9, new Resource(200, 300, 200, 2), Duration.of(15, MINUTES), new HashSet<>(), 855),
            new FarmLevel(10, new Resource(300, 400, 200, 3), Duration.of(20, MINUTES), new HashSet<>(), 1002),

            new FarmLevel(11, new Resource(400, 500, 300, 3), Duration.of(25, MINUTES), new HashSet<>(), 1175),
            new FarmLevel(12, new Resource(600, 500, 300, 4), Duration.of(30, MINUTES), new HashSet<>(), 1377),
            new FarmLevel(13, new Resource(600, 700, 400, 4), Duration.of(40, MINUTES), new HashSet<>(), 1671),
            new FarmLevel(14, new Resource(700, 900, 500, 5), Duration.of(50, MINUTES), new HashSet<>(), 1891),
            new FarmLevel(15, new Resource(900, 1000, 700, 5), Duration.of(60, MINUTES), new HashSet<>(), 2217),

            new FarmLevel(16, new Resource(1000, 1000, 1000, 6), Duration.of(1, HOURS), new HashSet<>(), 2598),
            new FarmLevel(17, new Resource(1000, 2000, 1000, 6), Duration.of(2, HOURS), new HashSet<>(), 3046),
            new FarmLevel(18, new Resource(2000, 2000, 1000, 7), Duration.of(3, HOURS), new HashSet<>(), 3570),
            new FarmLevel(19, new Resource(2000, 3000, 2000, 7), Duration.of(4, HOURS), new HashSet<>(), 4184),
            new FarmLevel(20, new Resource(3000, 3000, 2000, 8), Duration.of(5, HOURS), new HashSet<>(), 4904),

            new FarmLevel(21, new Resource(3000, 3000, 2000, 9), Duration.of(6, HOURS), new HashSet<>(), 5748),
            new FarmLevel(22, new Resource(4000, 5000, 3000, 10), Duration.of(7, HOURS), new HashSet<>(), 6737),
            new FarmLevel(23, new Resource(5000, 6000, 4000, 12), Duration.of(8, HOURS), new HashSet<>(), 7897),
            new FarmLevel(24, new Resource(6000, 8000, 5000, 14), Duration.of(9, HOURS), new HashSet<>(), 9256),
            new FarmLevel(25, new Resource(8000, 10000, 6000, 16), Duration.of(10, HOURS), new HashSet<>(), 10849),

            new FarmLevel(26, new Resource(10000, 12000, 7000, 18), Duration.of(12, HOURS), new HashSet<>(), 12716),
            new FarmLevel(27, new Resource(12000, 16000, 9000, 20), Duration.of(14, HOURS), new HashSet<>(), 14904),
            new FarmLevel(28, new Resource(16000, 19000, 12000, 22), Duration.of(16, HOURS), new HashSet<>(), 17469),
            new FarmLevel(29, new Resource(19000, 24000, 15000, 24), Duration.of(18, HOURS), new HashSet<>(), 20486),
            new FarmLevel(30, new Resource(24000, 30000, 18000, 26), Duration.of(20, HOURS), new HashSet<>(), 24000)
    );
    private final Set<HeadquartersLevel> headquartersLevels = Set.of(
            new HeadquartersLevel(1, new Resource(400, 500, 600, 7), Duration.of(1, MINUTES), new HashSet<>(), Duration.of(30, SECONDS)),
            new HeadquartersLevel(2, new Resource(480, 600, 720, 1), Duration.of(2, MINUTES), new HashSet<>(), Duration.of(60, SECONDS)),
            new HeadquartersLevel(3, new Resource(580, 720, 860, 2), Duration.of(3, MINUTES), new HashSet<>(), Duration.of(90, SECONDS)),
            new HeadquartersLevel(4, new Resource(700, 760, 1030, 2), Duration.of(4, MINUTES), new HashSet<>(), Duration.of(120, SECONDS)),
            new HeadquartersLevel(5, new Resource(840, 1030, 1020, 3), Duration.of(6, MINUTES), new HashSet<>(), Duration.of(150, SECONDS)),

            new HeadquartersLevel(6, new Resource(1800, 2000, 2200, 3), Duration.of(8, MINUTES), new HashSet<>(), Duration.of(180, SECONDS)),
            new HeadquartersLevel(7, new Resource(2200, 2400, 2600, 4), Duration.of(10, MINUTES), new HashSet<>(), Duration.of(210, SECONDS)),
            new HeadquartersLevel(8, new Resource(2600, 2900, 3100, 4), Duration.of(12, MINUTES), new HashSet<>(), Duration.of(240, SECONDS)),
            new HeadquartersLevel(9, new Resource(3100, 3500, 3700, 5), Duration.of(14, MINUTES), new HashSet<>(), Duration.of(270, SECONDS)),
            new HeadquartersLevel(10, new Resource(3700, 4200, 4400, 6), Duration.of(16, MINUTES), new HashSet<>(), Duration.of(300, SECONDS)),

            new HeadquartersLevel(11, new Resource(6000, 7000, 8000, 7), Duration.of(18, MINUTES), new HashSet<>(), Duration.of(330, SECONDS)),
            new HeadquartersLevel(12, new Resource(7200, 8400, 9600, 9), Duration.of(20, MINUTES), new HashSet<>(), Duration.of(360, SECONDS)),
            new HeadquartersLevel(13, new Resource(8600, 10100, 11500, 10), Duration.of(25, MINUTES), new HashSet<>(), Duration.of(390, SECONDS)),
            new HeadquartersLevel(14, new Resource(10300, 12100, 13800, 13), Duration.of(30, MINUTES), new HashSet<>(), Duration.of(420, SECONDS)),
            new HeadquartersLevel(15, new Resource(12400, 14500, 16600, 15), Duration.of(45, MINUTES), new HashSet<>(), Duration.of(450, SECONDS)),

            new HeadquartersLevel(16, new Resource(21000, 23000, 25000, 21), Duration.of(1, HOURS), new HashSet<>(), Duration.of(480, SECONDS)),
            new HeadquartersLevel(17, new Resource(22000, 25000, 26000, 26), Duration.of(2, HOURS), new HashSet<>(), Duration.of(510, SECONDS)),
            new HeadquartersLevel(18, new Resource(23000, 28000, 27000, 31), Duration.of(3, HOURS), new HashSet<>(), Duration.of(540, SECONDS)),
            new HeadquartersLevel(19, new Resource(24000, 31000, 28000, 38), Duration.of(4, HOURS), new HashSet<>(), Duration.of(570, SECONDS)),
            new HeadquartersLevel(20, new Resource(25000, 34000, 29000, 44), Duration.of(5, HOURS), new HashSet<>(), Duration.of(600, SECONDS)),

            new HeadquartersLevel(21, new Resource(26000, 37000, 30000, 54), Duration.of(6, HOURS), new HashSet<>(), Duration.of(630, SECONDS)),
            new HeadquartersLevel(22, new Resource(27000, 41000, 32000, 64), Duration.of(7, HOURS), new HashSet<>(), Duration.of(660, SECONDS)),
            new HeadquartersLevel(23, new Resource(28000, 45000, 34000, 64), Duration.of(8, HOURS), new HashSet<>(), Duration.of(690, SECONDS)),
            new HeadquartersLevel(24, new Resource(29000, 50000, 36000, 78), Duration.of(9, HOURS), new HashSet<>(), Duration.of(720, SECONDS)),
            new HeadquartersLevel(25, new Resource(30000, 55000, 38000, 92), Duration.of(10, HOURS), new HashSet<>(), Duration.of(750, SECONDS))
    );
    private final Set<HospitalLevel> hospitalLevels = Set.of(
            new HospitalLevel(1, new Resource(30, 50, 40, 1), Duration.of(30, SECONDS), Set.of(new Requirement(HEADQUARTERS, 3)), 100),
            new HospitalLevel(2, new Resource(60, 110, 80, 1), Duration.of(2, MINUTES), Set.of(new Requirement(HEADQUARTERS, 3)), 140),

            new HospitalLevel(3, new Resource(200, 200, 100, 1), Duration.of(5, MINUTES), Set.of(new Requirement(HEADQUARTERS, 3)), 170),

            new HospitalLevel(4, new Resource(600, 700, 400, 4), Duration.of(40, MINUTES), Set.of(new Requirement(HEADQUARTERS, 3)), 200),

            new HospitalLevel(5, new Resource(2000, 3000, 2000, 7), Duration.of(2, HOURS), Set.of(new Requirement(HEADQUARTERS, 3)), 250),

            new HospitalLevel(6, new Resource(5000, 6000, 4000, 12), Duration.of(4, HOURS), Set.of(new Requirement(HEADQUARTERS, 3)), 320),
            new HospitalLevel(7, new Resource(6000, 8000, 5000, 14), Duration.of(8, HOURS), Set.of(new Requirement(HEADQUARTERS, 3)), 460),

            new HospitalLevel(8, new Resource(10000, 12000, 7000, 18), Duration.of(12, HOURS), Set.of(new Requirement(HEADQUARTERS, 3)), 600),
            new HospitalLevel(9, new Resource(16000, 19000, 12000, 22), Duration.of(16, HOURS), Set.of(new Requirement(HEADQUARTERS, 3)), 800),
            new HospitalLevel(10, new Resource(24000, 30000, 18000, 26), Duration.of(20, HOURS), Set.of(new Requirement(HEADQUARTERS, 3)), 1000)
    );
    private final Set<ResourceLevel> ironMineLevels = Set.of(
            new ResourceLevel(1, new Resource(30, 50, 40, 1), Duration.of(5, SECONDS), new HashSet<>(), 120),
            new ResourceLevel(2, new Resource(40, 70, 60, 1), Duration.of(1, MINUTES), new HashSet<>(), 132),
            new ResourceLevel(3, new Resource(50, 90, 70, 1), Duration.of(1, MINUTES).plus(30, SECONDS), new HashSet<>(), 146),
            new ResourceLevel(4, new Resource(60, 110, 80, 1), Duration.of(2, MINUTES), new HashSet<>(), 162),
            new ResourceLevel(5, new Resource(80, 150, 90, 1), Duration.of(2, MINUTES).plus(30, SECONDS), new HashSet<>(), 180),

            new ResourceLevel(6, new Resource(100, 200, 100, 1), Duration.of(3, MINUTES).plus(30, SECONDS), new HashSet<>(), 200),
            new ResourceLevel(7, new Resource(200, 200, 100, 1), Duration.of(5, MINUTES), new HashSet<>(), 222),
            new ResourceLevel(8, new Resource(200, 200, 100, 2), Duration.of(10, MINUTES), new HashSet<>(), 248),
            new ResourceLevel(9, new Resource(200, 300, 200, 2), Duration.of(15, MINUTES), new HashSet<>(), 276),
            new ResourceLevel(10, new Resource(300, 400, 200, 3), Duration.of(20, MINUTES), new HashSet<>(), 308),

            new ResourceLevel(11, new Resource(400, 500, 300, 3), Duration.of(25, MINUTES), new HashSet<>(), 343),
            new ResourceLevel(12, new Resource(600, 500, 300, 4), Duration.of(30, MINUTES), new HashSet<>(), 383),
            new ResourceLevel(13, new Resource(600, 700, 400, 4), Duration.of(40, MINUTES), new HashSet<>(), 429),
            new ResourceLevel(14, new Resource(700, 900, 500, 5), Duration.of(50, MINUTES), new HashSet<>(), 480),
            new ResourceLevel(15, new Resource(900, 1000, 700, 5), Duration.of(60, MINUTES), new HashSet<>(), 536),

            new ResourceLevel(16, new Resource(1000, 1000, 1000, 6), Duration.of(1, HOURS), new HashSet<>(), 600),
            new ResourceLevel(17, new Resource(1000, 2000, 1000, 6), Duration.of(2, HOURS), new HashSet<>(), 673),
            new ResourceLevel(18, new Resource(2000, 2000, 1000, 7), Duration.of(3, HOURS), new HashSet<>(), 756),
            new ResourceLevel(19, new Resource(2000, 3000, 2000, 7), Duration.of(4, HOURS), new HashSet<>(), 846),
            new ResourceLevel(20, new Resource(3000, 3000, 2000, 8), Duration.of(5, HOURS), new HashSet<>(), 948),

            new ResourceLevel(21, new Resource(3000, 3000, 2000, 9), Duration.of(6, HOURS), new HashSet<>(), 1064),
            new ResourceLevel(22, new Resource(4000, 5000, 3000, 10), Duration.of(7, HOURS), new HashSet<>(), 1194),
            new ResourceLevel(23, new Resource(5000, 6000, 4000, 12), Duration.of(8, HOURS), new HashSet<>(), 1340),
            new ResourceLevel(24, new Resource(6000, 8000, 5000, 14), Duration.of(9, HOURS), new HashSet<>(), 1504),
            new ResourceLevel(25, new Resource(8000, 10000, 6000, 16), Duration.of(10, HOURS), new HashSet<>(), 1690),

            new ResourceLevel(26, new Resource(10000, 12000, 7000, 18), Duration.of(12, HOURS), new HashSet<>(), 1890),
            new ResourceLevel(27, new Resource(12000, 16000, 9000, 20), Duration.of(14, HOURS), new HashSet<>(), 2130),
            new ResourceLevel(28, new Resource(16000, 19000, 12000, 22), Duration.of(16, HOURS), new HashSet<>(), 2390),
            new ResourceLevel(29, new Resource(19000, 24000, 15000, 24), Duration.of(18, HOURS), new HashSet<>(), 2688),
            new ResourceLevel(30, new Resource(24000, 30000, 18000, 26), Duration.of(20, HOURS), new HashSet<>(), 3020)
    );
    private final Set<MarketLevel> marketLevels = Set.of(
            new MarketLevel(1, new Resource(30, 50, 40, 1), Duration.of(30, SECONDS), Set.of(new Requirement(HEADQUARTERS, 6)), 1),
            new MarketLevel(2, new Resource(40, 70, 60, 1), Duration.of(1, MINUTES), Set.of(new Requirement(HEADQUARTERS, 6)), 2),
            new MarketLevel(3, new Resource(50, 90, 70, 1), Duration.of(1, MINUTES).plus(30, SECONDS), Set.of(new Requirement(HEADQUARTERS, 6)), 3),
            new MarketLevel(4, new Resource(60, 110, 80, 1), Duration.of(2, MINUTES), Set.of(new Requirement(HEADQUARTERS, 6)), 4),
            new MarketLevel(5, new Resource(80, 150, 90, 1), Duration.of(2, MINUTES).plus(30, SECONDS), Set.of(new Requirement(HEADQUARTERS, 6)), 5),

            new MarketLevel(6, new Resource(100, 200, 100, 1), Duration.of(3, MINUTES).plus(30, SECONDS), Set.of(new Requirement(HEADQUARTERS, 6)), 6),
            new MarketLevel(7, new Resource(200, 200, 100, 1), Duration.of(5, MINUTES), Set.of(new Requirement(HEADQUARTERS, 6)), 7),
            new MarketLevel(8, new Resource(200, 200, 100, 2), Duration.of(10, MINUTES), Set.of(new Requirement(HEADQUARTERS, 6)), 8),
            new MarketLevel(9, new Resource(200, 300, 200, 2), Duration.of(15, MINUTES), Set.of(new Requirement(HEADQUARTERS, 6)), 9),
            new MarketLevel(10, new Resource(300, 400, 200, 3), Duration.of(20, MINUTES), Set.of(new Requirement(HEADQUARTERS, 6)), 10),

            new MarketLevel(11, new Resource(400, 500, 300, 3), Duration.of(25, MINUTES), Set.of(new Requirement(HEADQUARTERS, 6)), 11),
            new MarketLevel(12, new Resource(600, 500, 300, 4), Duration.of(30, MINUTES), Set.of(new Requirement(HEADQUARTERS, 6)), 14),
            new MarketLevel(13, new Resource(600, 700, 400, 4), Duration.of(40, MINUTES), Set.of(new Requirement(HEADQUARTERS, 6)), 19),
            new MarketLevel(14, new Resource(700, 900, 500, 5), Duration.of(50, MINUTES), Set.of(new Requirement(HEADQUARTERS, 6)), 26),
            new MarketLevel(15, new Resource(900, 1000, 700, 5), Duration.of(60, MINUTES), Set.of(new Requirement(HEADQUARTERS, 6)), 35),

            new MarketLevel(16, new Resource(1000, 1000, 1000, 6), Duration.of(1, HOURS), Set.of(new Requirement(HEADQUARTERS, 6)), 46),
            new MarketLevel(17, new Resource(1000, 2000, 1000, 6), Duration.of(2, HOURS), Set.of(new Requirement(HEADQUARTERS, 6)), 59),
            new MarketLevel(18, new Resource(2000, 2000, 1000, 7), Duration.of(3, HOURS), Set.of(new Requirement(HEADQUARTERS, 6)), 76),
            new MarketLevel(19, new Resource(2000, 3000, 2000, 7), Duration.of(4, HOURS), Set.of(new Requirement(HEADQUARTERS, 6)), 97),
            new MarketLevel(20, new Resource(3000, 3000, 2000, 8), Duration.of(5, HOURS), Set.of(new Requirement(HEADQUARTERS, 6)), 110),

            new MarketLevel(21, new Resource(3000, 3000, 2000, 9), Duration.of(6, HOURS), Set.of(new Requirement(HEADQUARTERS, 6)), 131),
            new MarketLevel(22, new Resource(4000, 5000, 3000, 10), Duration.of(7, HOURS), Set.of(new Requirement(HEADQUARTERS, 6)), 154),
            new MarketLevel(23, new Resource(5000, 6000, 4000, 12), Duration.of(8, HOURS), Set.of(new Requirement(HEADQUARTERS, 6)), 179),
            new MarketLevel(24, new Resource(6000, 8000, 5000, 14), Duration.of(9, HOURS), Set.of(new Requirement(HEADQUARTERS, 6)), 206),
            new MarketLevel(25, new Resource(8000, 10000, 6000, 16), Duration.of(10, HOURS), Set.of(new Requirement(HEADQUARTERS, 6)), 235)
    );
    private final Set<RallyPointLevel> rallyPointLevels = Set.of(
            new RallyPointLevel(1, new Resource(50, 100, 150, 1), Duration.of(30, SECONDS), new HashSet<>(), 50_000),
            new RallyPointLevel(2, new Resource(100, 150, 150, 1), Duration.of(5, MINUTES), new HashSet<>(), 100_000),
            new RallyPointLevel(3, new Resource(2500, 2500, 2500, 1), Duration.of(15, MINUTES), new HashSet<>(), 150_000),
            new RallyPointLevel(4, new Resource(6000, 9000, 11000, 1), Duration.of(5, HOURS), new HashSet<>(), 200_000),
            new RallyPointLevel(5, new Resource(13000, 18000, 25000, 1), Duration.of(8, HOURS), new HashSet<>(), 250_000)
    );
    private final Set<StatueLevel> statueLevels = Set.of(
            new StatueLevel(1, new Resource(50, 100, 150, 1), Duration.of(30, SECONDS), new HashSet<>()),
            new StatueLevel(2, new Resource(100, 150, 150, 1), Duration.of(5, MINUTES), new HashSet<>()),
            new StatueLevel(3, new Resource(2500, 2500, 2500, 1), Duration.of(15, MINUTES), new HashSet<>()),
            new StatueLevel(4, new Resource(6000, 9000, 11000, 1), Duration.of(5, HOURS), new HashSet<>()),
            new StatueLevel(5, new Resource(13000, 18000, 25000, 1), Duration.of(8, HOURS), new HashSet<>())
    );
    private final Set<TavernLevel> tavernLevels = Set.of(
            new TavernLevel(1, new Resource(30, 50, 40, 1), Duration.of(30, SECONDS), Set.of(new Requirement(HEADQUARTERS, 8)), 1),
            new TavernLevel(2, new Resource(40, 70, 60, 1), Duration.of(1, MINUTES), Set.of(new Requirement(HEADQUARTERS, 8)), 1),
            new TavernLevel(3, new Resource(50, 90, 70, 1), Duration.of(1, MINUTES).plus(30, SECONDS), Set.of(new Requirement(HEADQUARTERS, 8)), 2),
            new TavernLevel(4, new Resource(60, 110, 80, 1), Duration.of(2, MINUTES), Set.of(new Requirement(HEADQUARTERS, 8)), 2),
            new TavernLevel(5, new Resource(80, 150, 90, 1), Duration.of(2, MINUTES).plus(30, SECONDS), Set.of(new Requirement(HEADQUARTERS, 8)), 2),

            new TavernLevel(6, new Resource(100, 200, 100, 1), Duration.of(3, MINUTES).plus(30, SECONDS), Set.of(new Requirement(HEADQUARTERS, 8)), 3),
            new TavernLevel(7, new Resource(200, 200, 100, 1), Duration.of(5, MINUTES), Set.of(new Requirement(HEADQUARTERS, 8)), 3),
            new TavernLevel(8, new Resource(200, 200, 100, 2), Duration.of(10, MINUTES), Set.of(new Requirement(HEADQUARTERS, 8)), 3),
            new TavernLevel(9, new Resource(300, 400, 200, 3), Duration.of(20, MINUTES), Set.of(new Requirement(HEADQUARTERS, 8)), 4),
            new TavernLevel(10, new Resource(600, 700, 400, 4), Duration.of(40, MINUTES), Set.of(new Requirement(HEADQUARTERS, 8)), 4),

            new TavernLevel(11, new Resource(1000, 1000, 1000, 6), Duration.of(1, HOURS), Set.of(new Requirement(HEADQUARTERS, 8)), 4),
            new TavernLevel(12, new Resource(2000, 2000, 1000, 7), Duration.of(3, HOURS), Set.of(new Requirement(HEADQUARTERS, 8)), 4),
            new TavernLevel(13, new Resource(3000, 3000, 2000, 8), Duration.of(5, HOURS), Set.of(new Requirement(HEADQUARTERS, 8)), 5),
            new TavernLevel(14, new Resource(4000, 5000, 3000, 10), Duration.of(7, HOURS), Set.of(new Requirement(HEADQUARTERS, 8)), 5),
            new TavernLevel(15, new Resource(8000, 10000, 6000, 16), Duration.of(10, HOURS), Set.of(new Requirement(HEADQUARTERS, 8)), 5)
    );
    private final Set<ResourceLevel> timberCampLevels = Set.of(
            new ResourceLevel(1, new Resource(30, 50, 40, 1), Duration.of(5, SECONDS), new HashSet<>(), 120),
            new ResourceLevel(2, new Resource(40, 70, 60, 1), Duration.of(1, MINUTES), new HashSet<>(), 132),
            new ResourceLevel(3, new Resource(50, 90, 70, 1), Duration.of(1, MINUTES).plus(30, SECONDS), new HashSet<>(), 146),
            new ResourceLevel(4, new Resource(60, 110, 80, 1), Duration.of(2, MINUTES), new HashSet<>(), 162),
            new ResourceLevel(5, new Resource(80, 150, 90, 1), Duration.of(2, MINUTES).plus(30, SECONDS), new HashSet<>(), 180),

            new ResourceLevel(6, new Resource(100, 200, 100, 1), Duration.of(3, MINUTES).plus(30, SECONDS), new HashSet<>(), 200),
            new ResourceLevel(7, new Resource(200, 200, 100, 1), Duration.of(5, MINUTES), new HashSet<>(), 222),
            new ResourceLevel(8, new Resource(200, 200, 100, 2), Duration.of(10, MINUTES), new HashSet<>(), 248),
            new ResourceLevel(9, new Resource(200, 300, 200, 2), Duration.of(15, MINUTES), new HashSet<>(), 276),
            new ResourceLevel(10, new Resource(300, 400, 200, 3), Duration.of(20, MINUTES), new HashSet<>(), 308),

            new ResourceLevel(11, new Resource(400, 500, 300, 3), Duration.of(25, MINUTES), new HashSet<>(), 343),
            new ResourceLevel(12, new Resource(600, 500, 300, 4), Duration.of(30, MINUTES), new HashSet<>(), 383),
            new ResourceLevel(13, new Resource(600, 700, 400, 4), Duration.of(40, MINUTES), new HashSet<>(), 429),
            new ResourceLevel(14, new Resource(700, 900, 500, 5), Duration.of(50, MINUTES), new HashSet<>(), 480),
            new ResourceLevel(15, new Resource(900, 1000, 700, 5), Duration.of(60, MINUTES), new HashSet<>(), 536),

            new ResourceLevel(16, new Resource(1000, 1000, 1000, 6), Duration.of(1, HOURS), new HashSet<>(), 600),
            new ResourceLevel(17, new Resource(1000, 2000, 1000, 6), Duration.of(2, HOURS), new HashSet<>(), 673),
            new ResourceLevel(18, new Resource(2000, 2000, 1000, 7), Duration.of(3, HOURS), new HashSet<>(), 756),
            new ResourceLevel(19, new Resource(2000, 3000, 2000, 7), Duration.of(4, HOURS), new HashSet<>(), 846),
            new ResourceLevel(20, new Resource(3000, 3000, 2000, 8), Duration.of(5, HOURS), new HashSet<>(), 948),

            new ResourceLevel(21, new Resource(3000, 3000, 2000, 9), Duration.of(6, HOURS), new HashSet<>(), 1064),
            new ResourceLevel(22, new Resource(4000, 5000, 3000, 10), Duration.of(7, HOURS), new HashSet<>(), 1194),
            new ResourceLevel(23, new Resource(5000, 6000, 4000, 12), Duration.of(8, HOURS), new HashSet<>(), 1340),
            new ResourceLevel(24, new Resource(6000, 8000, 5000, 14), Duration.of(9, HOURS), new HashSet<>(), 1504),
            new ResourceLevel(25, new Resource(8000, 10000, 6000, 16), Duration.of(10, HOURS), new HashSet<>(), 1690),

            new ResourceLevel(26, new Resource(10000, 12000, 7000, 18), Duration.of(12, HOURS), new HashSet<>(), 1890),
            new ResourceLevel(27, new Resource(12000, 16000, 9000, 20), Duration.of(14, HOURS), new HashSet<>(), 2130),
            new ResourceLevel(28, new Resource(16000, 19000, 12000, 22), Duration.of(16, HOURS), new HashSet<>(), 2390),
            new ResourceLevel(29, new Resource(19000, 24000, 15000, 24), Duration.of(18, HOURS), new HashSet<>(), 2688),
            new ResourceLevel(30, new Resource(24000, 30000, 18000, 26), Duration.of(20, HOURS), new HashSet<>(), 3020)
    );
    private final Set<WallLevel> wallLevels = Set.of(
            new WallLevel(1, new Resource(30, 50, 40, 1), Duration.of(30, SECONDS), Set.of(new Requirement(HEADQUARTERS, 3)), 5000),
            new WallLevel(2, new Resource(40, 70, 60, 1), Duration.of(1, MINUTES), Set.of(new Requirement(HEADQUARTERS, 3)), 10000),
            new WallLevel(3, new Resource(50, 90, 70, 1), Duration.of(1, MINUTES).plus(30, SECONDS), Set.of(new Requirement(HEADQUARTERS, 3)), 15000),
            new WallLevel(4, new Resource(60, 110, 80, 1), Duration.of(2, MINUTES), Set.of(new Requirement(HEADQUARTERS, 3)), 20000),
            new WallLevel(5, new Resource(80, 150, 90, 1), Duration.of(2, MINUTES).plus(30, SECONDS), Set.of(new Requirement(HEADQUARTERS, 3)), 25000),

            new WallLevel(6, new Resource(200, 200, 100, 1), Duration.of(5, MINUTES), Set.of(new Requirement(HEADQUARTERS, 3)), 30000),
            new WallLevel(7, new Resource(200, 200, 100, 2), Duration.of(10, MINUTES), Set.of(new Requirement(HEADQUARTERS, 3)), 35000),
            new WallLevel(8, new Resource(200, 300, 200, 2), Duration.of(15, MINUTES), Set.of(new Requirement(HEADQUARTERS, 3)), 40000),
            new WallLevel(9, new Resource(600, 500, 300, 4), Duration.of(30, MINUTES), Set.of(new Requirement(HEADQUARTERS, 3)), 45000),
            new WallLevel(10, new Resource(900, 1000, 700, 5), Duration.of(60, MINUTES), Set.of(new Requirement(HEADQUARTERS, 3)), 50000),

            new WallLevel(11, new Resource(1000, 2000, 1000, 6), Duration.of(2, HOURS), Set.of(new Requirement(HEADQUARTERS, 3)), 55000),
            new WallLevel(12, new Resource(2000, 3000, 2000, 7), Duration.of(4, HOURS), Set.of(new Requirement(HEADQUARTERS, 3)), 60000),
            new WallLevel(13, new Resource(3000, 3000, 2000, 8), Duration.of(5, HOURS), Set.of(new Requirement(HEADQUARTERS, 3)), 65000),
            new WallLevel(14, new Resource(4000, 5000, 3000, 10), Duration.of(7, HOURS), Set.of(new Requirement(HEADQUARTERS, 3)), 70000),
            new WallLevel(15, new Resource(6000, 8000, 5000, 14), Duration.of(9, HOURS), Set.of(new Requirement(HEADQUARTERS, 3)), 78000),

            new WallLevel(16, new Resource(8000, 10000, 6000, 16), Duration.of(10, HOURS), Set.of(new Requirement(HEADQUARTERS, 3)), 80000),
            new WallLevel(17, new Resource(12000, 16000, 9000, 20), Duration.of(14, HOURS), Set.of(new Requirement(HEADQUARTERS, 3)), 85000),
            new WallLevel(18, new Resource(16000, 19000, 12000, 22), Duration.of(16, HOURS), Set.of(new Requirement(HEADQUARTERS, 3)), 90000),
            new WallLevel(19, new Resource(19000, 24000, 15000, 24), Duration.of(18, HOURS), Set.of(new Requirement(HEADQUARTERS, 3)), 95000),
            new WallLevel(20, new Resource(24000, 30000, 18000, 26), Duration.of(20, HOURS), Set.of(new Requirement(HEADQUARTERS, 3)), 100000)
    );
    private final Set<WarehouseLevel> warehouseLevels = Set.of(
            new WarehouseLevel(1, new Resource(30, 50, 40, 1), Duration.of(30, SECONDS), new HashSet<>(), 1000),
            new WarehouseLevel(2, new Resource(40, 70, 60, 1), Duration.of(1, MINUTES), new HashSet<>(), 2000),
            new WarehouseLevel(3, new Resource(50, 90, 70, 1), Duration.of(1, MINUTES).plus(30, SECONDS), new HashSet<>(), 3000),
            new WarehouseLevel(4, new Resource(60, 110, 80, 1), Duration.of(2, MINUTES), new HashSet<>(), 4000),
            new WarehouseLevel(5, new Resource(80, 150, 90, 1), Duration.of(2, MINUTES).plus(30, SECONDS), new HashSet<>(), 5000),

            new WarehouseLevel(6, new Resource(200, 200, 100, 1), Duration.of(5, MINUTES), new HashSet<>(), 6000),
            new WarehouseLevel(7, new Resource(200, 200, 100, 2), Duration.of(10, MINUTES), new HashSet<>(), 7000),
            new WarehouseLevel(8, new Resource(200, 300, 200, 2), Duration.of(15, MINUTES), new HashSet<>(), 8000),
            new WarehouseLevel(9, new Resource(600, 500, 300, 4), Duration.of(30, MINUTES), new HashSet<>(), 9000),
            new WarehouseLevel(10, new Resource(900, 1000, 700, 5), Duration.of(60, MINUTES), new HashSet<>(), 10000),

            new WarehouseLevel(11, new Resource(1000, 2000, 1000, 6), Duration.of(2, HOURS), new HashSet<>(), 12000),
            new WarehouseLevel(12, new Resource(2000, 3000, 2000, 7), Duration.of(4, HOURS), new HashSet<>(), 20000),
            new WarehouseLevel(13, new Resource(3000, 3000, 2000, 8), Duration.of(5, HOURS), new HashSet<>(), 30000),
            new WarehouseLevel(14, new Resource(4000, 5000, 3000, 10), Duration.of(7, HOURS), new HashSet<>(), 50000),
            new WarehouseLevel(15, new Resource(6000, 8000, 5000, 14), Duration.of(9, HOURS), new HashSet<>(), 80000),

            new WarehouseLevel(16, new Resource(8000, 10000, 6000, 16), Duration.of(10, HOURS), new HashSet<>(), 130000),
            new WarehouseLevel(17, new Resource(12000, 16000, 9000, 20), Duration.of(14, HOURS), new HashSet<>(), 210000),
            new WarehouseLevel(18, new Resource(16000, 19000, 12000, 22), Duration.of(16, HOURS), new HashSet<>(), 270000),
            new WarehouseLevel(19, new Resource(19000, 24000, 15000, 24), Duration.of(18, HOURS), new HashSet<>(), 320000),
            new WarehouseLevel(20, new Resource(24000, 30000, 18000, 26), Duration.of(20, HOURS), new HashSet<>(), 400000)
    );

    private final Map<BuildingType, Map<Integer, ? extends BuildingLevel>> map = new HashMap<>() {{
        put(HEADQUARTERS, headquartersLevels.stream().collect(Collectors.toMap(BuildingLevel::getLevel, e -> e)));
        put(TIMBER_CAMP, timberCampLevels.stream().collect(Collectors.toMap(BuildingLevel::getLevel, e -> e)));
        put(CLAY_PIT, clayPitLevels.stream().collect(Collectors.toMap(BuildingLevel::getLevel, e -> e)));
        put(IRON_MINE, ironMineLevels.stream().collect(Collectors.toMap(BuildingLevel::getLevel, e -> e)));
        put(FARM, farmLevels.stream().collect(Collectors.toMap(BuildingLevel::getLevel, e -> e)));
        put(WAREHOUSE, warehouseLevels.stream().collect(Collectors.toMap(BuildingLevel::getLevel, e -> e)));
        put(CHAPEL, chapelLevels.stream().collect(Collectors.toMap(BuildingLevel::getLevel, e -> e)));
        put(CHURCH, churchLevels.stream().collect(Collectors.toMap(BuildingLevel::getLevel, e -> e)));
        put(RALLY_POINT, rallyPointLevels.stream().collect(Collectors.toMap(BuildingLevel::getLevel, e -> e)));
        put(BARRACKS, barrackLevels.stream().collect(Collectors.toMap(BuildingLevel::getLevel, e -> e)));
        put(STATUE, statueLevels.stream().collect(Collectors.toMap(BuildingLevel::getLevel, e -> e)));
        put(WALL, wallLevels.stream().collect(Collectors.toMap(BuildingLevel::getLevel, e -> e)));
        put(HOSPITAL, hospitalLevels.stream().collect(Collectors.toMap(BuildingLevel::getLevel, e -> e)));
        put(MARKET, marketLevels.stream().collect(Collectors.toMap(BuildingLevel::getLevel, e -> e)));
        put(TAVERN, tavernLevels.stream().collect(Collectors.toMap(BuildingLevel::getLevel, e -> e)));
        put(ACADEMY, academyLevels.stream().collect(Collectors.toMap(BuildingLevel::getLevel, e -> e)));
    }};
    private static final Integer DIVIDER = 100;
    private final Map<BuildingType, Map<Integer, ? extends BuildingLevel>> mapWithIncreasedSpeed = map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                        return entry.getValue().entrySet().stream().collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> {
                                    BuildingLevel bl = e.getValue();
                                    bl.setDuration(bl.getDuration().dividedBy(DIVIDER));
                                    return bl;
                                }
                        ));
                    }));

    @Override
    public Mono<Map<Integer, ? extends BuildingLevel>> fetchAll(BuildingType type) {
        return Mono.just(map.get(type));
    }

    @Override
    public Mono<? extends BuildingLevel> fetch(BuildingType type, Integer level) {
        return fetchAll(type).map(levels -> levels.get(level));
    }
}
