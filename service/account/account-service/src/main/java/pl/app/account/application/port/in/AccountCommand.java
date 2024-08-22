package pl.app.account.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;


public interface AccountCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateAccountCommand implements Serializable {
        private String nickname;
    }
}
