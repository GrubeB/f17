package pl.app.tribe.application.port.in;

import pl.app.tribe.application.domain.Tribe;
import reactor.core.publisher.Mono;

public interface TribeService {
    Mono<Tribe> create(TribeCommand.CreateTribeCommand command);

    Mono<Tribe> addMember(TribeCommand.AddMemberCommand command);

    Mono<Tribe> removeMember(TribeCommand.RemoveMemberCommand command);

    Mono<Tribe> changeMemberType(TribeCommand.ChangeMemberTypeCommand command);

    Mono<Tribe> addDiplomacy(TribeCommand.AddDiplomacyCommand command);

    Mono<Tribe> removeDiplomacy(TribeCommand.RemoveDiplomacyCommand command);
}
