package pl.app.account.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface AccountEvent {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class AccountCreatedEvent implements Serializable {
        private ObjectId accountId;
        private String nickName;
    }
}
