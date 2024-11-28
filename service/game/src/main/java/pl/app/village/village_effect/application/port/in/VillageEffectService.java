package pl.app.village.village_effect.application.port.in;

import pl.app.village.village_effect.application.domain.VillageEffect;
import reactor.core.publisher.Mono;

public interface VillageEffectService {
    Mono<VillageEffect> crate(VillageEffectCommand.CreateVillageEffectCommand command);

    Mono<VillageEffect> start(VillageEffectCommand.StartEffectCommand command);

    Mono<VillageEffect> remove(VillageEffectCommand.RemoveInvalidEffectsCommand command);

    Mono<VillageEffect> reject(VillageEffectCommand.RejectAllEffectsCommand command);

}
