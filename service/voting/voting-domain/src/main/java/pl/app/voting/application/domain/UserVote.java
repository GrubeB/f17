package pl.app.voting.application.domain;

import lombok.Getter;

@Getter
public class UserVote {
    private String userId;
    private String type;

    @SuppressWarnings("unused")
    public UserVote() {
    }
    public UserVote(String userId, String type) {
        this.userId = userId;
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
