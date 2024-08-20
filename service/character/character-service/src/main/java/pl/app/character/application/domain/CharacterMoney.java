package pl.app.character.application.domain;

public class CharacterMoney {
    private Long amount;

    @SuppressWarnings("unused")
    public CharacterMoney() {
        this.amount = 0L;
    }

    public CharacterMoney(Long amount) {
        this.amount = amount;
    }


    public Long getBalance() {
        return amount;
    }

    public void addMoney(Long amount) {
        this.amount = this.amount + amount;
    }

    public void subtractMoney(Long amount) {
        if (amount < 0) {
            throw new CharacterException.InvalidAmountException();
        }
        if (this.amount - amount < 0) {
            throw new CharacterException.InsufficientMoneyException();
        }
        this.amount = this.amount - amount;
    }
}
