package pl.app.comment.application.domain;

import lombok.Getter;

@Getter
public class VoteCounter {
    private final String type;
    private Long number;

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
