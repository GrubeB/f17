package pl.app.comment.application.port.in.command;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentContainerCommand implements
        Serializable {
    @NotBlank
    private String domainObjectId;
    @NotBlank
    private String domainObjectType;
}