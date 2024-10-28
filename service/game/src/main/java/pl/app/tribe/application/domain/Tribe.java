package pl.app.tribe.application.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
@Document(collection = "tribe")
@NoArgsConstructor
public class Tribe {
    @Id
    private ObjectId id;
    private String name;
    private String abbreviation;
    private List<Member> members;
    private Diplomacy diplomacy;

    public Tribe(String name, String abbreviation) {
        this.id = ObjectId.get();
        this.name = name;
        this.abbreviation = abbreviation;
        this.members = new LinkedList<>();
        this.diplomacy = new Diplomacy();
    }

    public void changeMemberType(ObjectId playerId, MemberType memberType) {
        Optional<Member> memberOpt = getMemberByPlayerId(playerId);
        if (memberOpt.isPresent()) {
            memberOpt.get().setType(memberType);
        }
    }

    public void removeMember(ObjectId playerId) {
        Optional<Member> memberOpt = getMemberByPlayerId(playerId);
        if (memberOpt.isPresent()) {
            members.remove(memberOpt.get());
        }
    }

    public void addMember(ObjectId playerId) {
        Optional<Member> memberOpt = getMemberByPlayerId(playerId);
        if (memberOpt.isEmpty()) {
            members.add(new Member(playerId, MemberType.DEFAULT));
        }
    }

    public Optional<Member> getMemberByPlayerId(ObjectId playerId) {
        return members.stream().filter(e -> Objects.equals(e.getPlayerId(), playerId)).findAny();
    }

    @Getter
    @NoArgsConstructor
    public static class Member {
        private ObjectId playerId;
        @Setter
        private MemberType type;

        public Member(ObjectId playerId, MemberType type) {
            this.playerId = playerId;
            this.type = type;
        }
    }

    public enum MemberType {
        DEFAULT,
        TRUSTED,
        FOUNDER
    }
}
