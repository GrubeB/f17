package pl.app.recruitment.adapter.out;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import pl.app.character.http.GodApplicantCollectionControllerHttpInterface;
import pl.app.god_applicant_collection.application.port.in.GodApplicantCollectionCommand;
import pl.app.recruitment.application.port.out.GodApplicantCreator;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class GodApplicantCreatorImpl implements GodApplicantCreator {
    private final GodApplicantCollectionControllerHttpInterface godApplicantCollectionControllerHttpInterface;

    @Override
    public Mono<Void> create(ObjectId godId, ObjectId characterId) {
        var command = new GodApplicantCollectionCommand.CreateGodApplicantCommand(godId, characterId);
        return godApplicantCollectionControllerHttpInterface.create(godId, command).then();
    }
}
