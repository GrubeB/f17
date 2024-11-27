package pl.app.army.recruiter.application.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.army.unit.model.Unit;
import pl.app.resource.share.Resource;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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
            var newRequest = new Request(unit, amount, false, lastConstructToDate, lastConstructToDate.plus(recruitTime), recruitCost);
            requests.add(newRequest);
            return newRequest;
        } else {
            Instant now = Instant.now();
            var newRequest = new Request(unit, amount, false, now, now.plus(recruitTime), recruitCost);
            requests.add(newRequest);
            return newRequest;
        }
    }

    public Request starFirstRequest() {
        Optional<Request> firstRequest = getFirstRequest();
        if (firstRequest.isEmpty()) {
            throw new RecruiterException.FailedToStartRequestException("failed to start recruit request, because there is no requests");
        }
        if (firstRequest.get().getStarted()) {
            throw new RecruiterException.FailedToStartRequestException("failed to start recruit request because first request is processed");
        }
        firstRequest.get().setStarted(true);
        return firstRequest.get();
    }

    public Optional<Request> finishFirstRequest() {
        Optional<Request> firstRequest = getFirstRequest();
        if (firstRequest.isPresent() && firstRequest.get().getStarted() && firstRequest.get().getTo().isBefore(Instant.now())) {
            this.requests.remove(firstRequest.get());
            return firstRequest;
        }
        return Optional.empty();
    }

    public Optional<Request> cancelRequest(ObjectId requestId) {
        Optional<Request> request = getRequestById(requestId);
        if (request.isPresent()) {
            this.requests.remove(request.get());
            rescheduleRequests(Instant.now());
            return request;
        }
        return Optional.empty();
    }

    public Set<Request> rejectAllRequests() {
        Set<Request> rejectedRequests = new HashSet<>(requests);
        this.requests.clear();
        return rejectedRequests;
    }

    private void rescheduleRequests(Instant now) {
        LinkedList<Request> collect = requests.stream()
                .sorted(Comparator.comparing(Request::getFrom))
                .collect(Collectors.toCollection(LinkedList::new));
        for (Request e : collect) {
            Duration difference = Duration.between(e.getFrom(), e.getTo());
            e.setFrom(now);
            e.setTo(now.plus(difference));
            now = now.plus(difference);
        }
    }


    private Optional<Request> getLastRequest() {
        return requests.stream().max(Comparator.comparing(Request::getTo));
    }

    public Optional<Request> getFirstRequest() {
        return requests.stream().min(Comparator.comparing(Request::getTo));
    }

    public Optional<Request> getRequestById(ObjectId requestId) {
        return requests.stream().filter(r -> Objects.equals(r.getRequestId(), requestId)).findAny();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Request {
        private ObjectId requestId;
        private Unit unit;
        private Integer amount;
        private Boolean started;
        private Instant from;
        private Instant to;
        private Resource cost;

        public Request(Unit unit, Integer amount, Boolean started, Instant from, Instant to, Resource cost) {
            this.requestId = ObjectId.get();
            this.unit = unit;
            this.amount = amount;
            this.started = started;
            this.from = from;
            this.to = to;
            this.cost = cost;
        }
    }
}
