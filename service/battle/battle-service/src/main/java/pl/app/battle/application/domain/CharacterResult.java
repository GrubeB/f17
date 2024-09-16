package pl.app.battle.application.domain;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.Money;

@Getter
@Setter
public class CharacterResult {
    private ObjectId characterId;
    private ObjectId godId;
    private Progress progress;
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

    public CharacterResult(ObjectId characterId, ObjectId godId,Progress progress, Loot loot) {
        this.characterId = characterId;
        this.godId = godId;
        this.progress = progress;
        this.loot = loot;
    }

    public void setExp(Long exp) {
        progress.setExp(exp);
    }

    public void setMoney(Money money) {
        loot.setMoney(money);
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
            if(progress == null){
                return new Progress(this.exp);
            }
            return new Progress(this.exp + progress.getExp());
        }
    }

    @Getter
    @Setter
    public static class Loot {
        private Money money;

        public Loot() {
            this.money = new Money();
        }

        public Loot(Money money) {
            this.money = money;
        }

        public Loot add(Loot loot) {
            if(loot == null){
                return new Loot(this.money);
            }
            return new Loot( money.addMoney(loot.getMoney()));
        }
    }
}

