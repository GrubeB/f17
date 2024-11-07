package pl.app.village.village_effect.application.port.in;

import pl.app.village.village_effect.application.domain.VillageEffect;
import reactor.core.publisher.Mono;

public interface VillageEffectService {
    Mono<VillageEffect> crate(VillageEffectCommand.CreateVillageEffectCommand command);

    Mono<VillageEffect> add(VillageEffectCommand.AddEffectCommand command);

    Mono<VillageEffect> remove(VillageEffectCommand.RemoveInvalidEffectsCommand command);

}
