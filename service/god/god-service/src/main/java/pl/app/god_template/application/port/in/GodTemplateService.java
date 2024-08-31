package pl.app.god_template.application.port.in;

import pl.app.god.application.domain.God;
import pl.app.god.application.port.in.GodCommand;
import pl.app.god_template.application.domain.GodTemplate;
import reactor.core.publisher.Mono;


public interface GodTemplateService {
    Mono<GodTemplate> create(GodTemplateCommand.CreateGodTemplateCommand command);
    Mono<GodTemplate> update(GodTemplateCommand.UpdateGodTemplateCommand command);
    Mono<GodTemplate> delete(GodTemplateCommand.DeleteGodTemplateCommand command);
}
