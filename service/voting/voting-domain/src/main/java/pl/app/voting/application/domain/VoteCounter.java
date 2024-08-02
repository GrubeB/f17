package pl.app.voting.application.domain;

import lombok.Getter;

@Getter
public class VoteCounter {
    private String type;
    private Long number;
    @SuppressWarnings("unused")
    public VoteCounter() {
    }
    public VoteCounter(String type) {
        this.type = type;
        this.number = 0L;
    }

    public void increment() {
        this.number++;
    }

    public void decrement() {
        this.number--;
    }
}
