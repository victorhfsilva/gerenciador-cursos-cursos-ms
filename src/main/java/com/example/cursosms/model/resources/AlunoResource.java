package com.example.cursosms.model.resources;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AlunoResource extends RepresentationModel<AlunoResource> {
    private UUID usuarioId;
}
