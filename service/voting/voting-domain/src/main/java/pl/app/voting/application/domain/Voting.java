package pl.app.voting.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Document(collection = "voting")
@Getter
public class Voting {
    @Id
    private ObjectId id;
    private String domainObjectType;
    private String domainObjectId;
    private Set<VoteCounter> votes;
    private Set<UserVote> userVotes;

    @SuppressWarnings("unused")
    public Voting() {
    }

    public Voting(String domainObjectId, String domainObjectType) {
        this.id = new ObjectId();
        this.domainObjectType = domainObjectType;
        this.domainObjectId = domainObjectId;
        this.votes = new LinkedHashSet<>();
        this.userVotes = new LinkedHashSet<>();
    }

    public Voting(String domainObjectId, String domainObjectType, ObjectId id) {
        this(domainObjectId, domainObjectType);
        this.id = id;
    }

    public void addUserVote(String userId, String type) {
        Optional<UserVote> userVoteOptional = getUserVoteByUserId(userId);
        if (userVoteOptional.isPresent()) {
            final UserVote userVote = userVoteOptional.get();
            final String currentType = userVote.getType();
            if (currentType.equals(type)) {
                return;
            }
            userVote.setType(type);
            incrementType(type);
        } else {
            UserVote userVote = new UserVote(userId, type);
            this.userVotes.add(userVote);
            incrementType(type);
        }
    }

    public Optional<UserVote> removeUserVote(String userId) {
        Optional<UserVote> userVoteOptional = getUserVoteByUserId(userId);
        if (userVoteOptional.isPresent()) {
            final UserVote userVote = userVoteOptional.get();
            decrementType(userVote.getType());
            removeUserVoteByUserId(userId);
            return Optional.of(userVote);
        }
        return Optional.empty();
    }

    private void incrementType(String type) {
        Optional<VoteCounter> voteCounterOptional = getVoteCounterByType(type);
        if (voteCounterOptional.isPresent()) {
            final VoteCounter voteCounter = voteCounterOptional.get();
            voteCounter.increment();
        } else {
            final VoteCounter voteCounter = new VoteCounter(type);
            voteCounter.increment();
            this.votes.add(voteCounter);
        }
    }

    private void decrementType(String type) {
        Optional<VoteCounter> voteCounterOptional = getVoteCounterByType(type);
        if (voteCounterOptional.isPresent()) {
            final VoteCounter voteCounter = voteCounterOptional.get();
            voteCounter.decrement();
        } else {
            final VoteCounter voteCounter = new VoteCounter(type);
            voteCounter.decrement();
        }
    }

    private Optional<VoteCounter> getVoteCounterByType(String type) {
        return this.votes.stream()
                .filter(v -> v.getType().equals(type))
                .findAny();
    }

    private void removeUserVoteByUserId(String userId) {
        this.userVotes.removeIf(v -> v.getUserId().equals(userId));
    }

    private Optional<UserVote> getUserVoteByUserId(String userId) {
        return this.userVotes.stream()
                .filter(v -> v.getUserId().equals(userId))
                .findAny();
    }
}
