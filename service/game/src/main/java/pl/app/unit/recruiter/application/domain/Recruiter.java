package pl.app.unit.recruiter.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.resource.resource.application.domain.Resource;
import pl.app.unit.unit.application.domain.Unit;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Document(collection = "recruiter")
@NoArgsConstructor
public class Recruiter {
    @Id
    private ObjectId villageId;
    private Integer constructNumberMax;
    private Set<Request> requests;

    public Recruiter(ObjectId villageId, Integer constructNumberMax) {
        this.villageId = villageId;
        this.constructNumberMax = constructNumberMax;
        this.requests = new LinkedHashSet<>();
    }

    public Request addRequest(Unit unit, Integer amount) {
        if (requests.size() >= constructNumberMax) {
            throw new RecruiterException.ReachedMaxRecruitRequestNumberException();
        }
        var recruitTime = unit.getTrainingTime().multipliedBy(amount);
        var recruitCost = unit.getCost().multiply(amount);
        Optional<Request> lastConstruct = getLastRequest();
        if (lastConstruct.isPresent()) {
            Instant lastConstructToDate = lastConstruct.get().getTo();
            var newRequest = new Request(unit, amount, lastConstructToDate, lastConstructToDate.plus(recruitTime), recruitCost);
            requests.add(newRequest);
            return newRequest;
        } else {
            Instant now = Instant.now();
            var newRequest = new Request(unit, amount, now, now.plus(recruitTime), recruitCost);
            requests.add(newRequest);
            return newRequest;
        }
    }

    public Optional<Request> removeRequest() {
        Optional<Request> lastRequest = getLastRequest();
        if (lastRequest.isPresent()) {
            this.requests.remove(lastRequest.get());
            return lastRequest;
        }
        return Optional.empty();
    }

    public Optional<Request> finishRequest() {
        Optional<Request> firstRequest = getFirstRequest();
        if (firstRequest.isPresent() && firstRequest.get().getTo().isBefore(Instant.now())) {
            this.requests.remove(firstRequest.get());
            return firstRequest;
        }
        return Optional.empty();
    }

    private Optional<Request> getLastRequest() {
        return requests.stream().max(Comparator.comparing(Request::getTo));
    }

    public Optional<Request> getFirstRequest() {
        return requests.stream().min(Comparator.comparing(Request::getTo));
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Unit unit;
        private Integer amount;
        private Instant from;
        private Instant to;
        private Resource cost;
    }
}
