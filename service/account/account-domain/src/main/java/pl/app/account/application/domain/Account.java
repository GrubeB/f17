package pl.app.account.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "accounts")
public class Account {
    @Id
    private ObjectId id;
    private String nickname;

    @SuppressWarnings("unused")
    public Account() {
    }

    public Account(String nickname) {
        this.id = ObjectId.get();
        this.nickname = nickname;
    }
}
