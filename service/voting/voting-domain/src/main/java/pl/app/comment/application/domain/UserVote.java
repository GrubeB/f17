package pl.app.comment.application.domain;

import lombok.Getter;

@Getter
public class UserVote {
    private final String userId;
    private String type;

    public UserVote(String userId, String type) {
        this.userId = userId;
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
