package pl.app.battle.application.domain;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.Money;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class CharacterResult {
    private ObjectId characterId;
    private ObjectId godId;
    private Progress progress;
    @Setter
    private Loot loot;

    @SuppressWarnings("unused")
    public CharacterResult() {
    }

    public CharacterResult(ObjectId characterId, ObjectId godId) {
        this.characterId = characterId;
        this.godId = godId;
        this.progress = new Progress();
        this.loot = new Loot();
    }

    public CharacterResult(ObjectId characterId, ObjectId godId, Progress progress, Loot loot) {
        this.characterId = characterId;
        this.godId = godId;
        this.progress = progress;
        this.loot = loot;
    }

    public void setExp(Long exp) {
        progress.setExp(exp);
    }

    public CharacterResult add(CharacterResult characterResult) {
        return new CharacterResult(this.characterId, this.godId, this.progress.add(characterResult.getProgress()),
                this.loot.add(characterResult.getLoot()));
    }

    @Getter
    @Setter
    public static class Progress {
        private Long exp;

        public Progress() {
            this.exp = 0L;
        }

        public Progress(Long exp) {
            this.exp = exp;
        }

        public Progress add(Progress progress) {
            if (progress == null) {
                return new Progress(this.exp);
            }
            return new Progress(this.exp + progress.getExp());
        }
    }

    @Getter
    @Setter
    public static class Loot {
        private Money money;
        private Set<LootItem> items;

        public Loot() {
            this.money = new Money();
            this.items = Collections.unmodifiableSet(new LinkedHashSet<>());
        }

        public Loot(Money money, Set<LootItem> items) {
            this.money = money;
            this.items = Collections.unmodifiableSet(new LinkedHashSet<>(items));
        }

        public Loot addMoney(Money moneyToAdd) {
            return new Loot(this.money.addMoney(moneyToAdd), this.items);
        }

        public Loot addItem(LootItem item) {
            Set<LootItem> newItems = new LinkedHashSet<>(this.items);
            newItems.add(item);
            return new Loot(this.money, newItems);
        }

        public Loot add(Loot loot) {
            if (loot == null) {
                return new Loot(this.money, this.items);
            }
            Set<LootItem> combinedItems = Stream.of(new LinkedHashSet<>(this.items), loot.getItems())
                    .flatMap(Set::stream)
                    .collect(Collectors.groupingBy(LootItem::getItemTemplateId))
                    .values().stream().map(list -> list.stream().reduce((i, i2) -> i.addAmount(i2.getAmount())))
                    .filter(Optional::isPresent).map(Optional::get)
                    .collect(Collectors.toSet());
            return new Loot(this.money.addMoney(loot.getMoney()), combinedItems);
        }

        @Getter
        public static class LootItem implements Serializable {
            private final ObjectId itemTemplateId;
            private final Integer amount;
            private final Integer level;

            public LootItem(ObjectId itemTemplateId, Integer amount, Integer level) {
                this.itemTemplateId = itemTemplateId;
                this.amount = amount;
                this.level = level;
            }

            public LootItem addAmount(int amountToAdd) {
                return new LootItem(this.itemTemplateId, this.amount + amountToAdd, this.level);
            }
        }
    }
}

