package pl.app.item.application.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.app.common.shared.model.ItemType;
import pl.app.common.shared.model.Money;
import pl.app.item_template.application.domain.OutfitTemplate;

class OutfitTest {

    @Test
    void getQuality() {
        OutfitTemplate template = getTemplate();
        Outfit outfit = new Outfit(template, 10);
        Integer quality = outfit.getQuality();
        Assertions.assertThat(quality).isGreaterThan(75_000);
        Assertions.assertThat(quality).isLessThan(125_000);
    }

    @Test
    void testFormula() {
        int quality = 100_000;
        long persistence = 10L;
        long persistencePercentage = 5_000L;
        int generatedForLevel = 10;
        Long result = quality
                * (persistence + persistence * generatedForLevel * persistencePercentage/ 100_000)
                / 100_000;
        Assertions.assertThat(persistence * generatedForLevel * persistencePercentage/ 100_000).isEqualTo(5);
        Assertions.assertThat(result).isEqualTo(15);
    }

    OutfitTemplate getTemplate() {
        return new OutfitTemplate(
                ItemType.ARMOR, "test", "descripition", "-",
                new Money(Money.Type.BASE, 10_000L), 5_000L,
                10L, 5_000L,
                10L, 5_000L,
                10L, 5_000L,
                10L, 0L,
                10L, 0L,
                10L, 0L,
                10L, 0L,
                10L, 0L
        );
    }
}