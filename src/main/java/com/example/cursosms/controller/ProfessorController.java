package com.example.cursosms.controller;

import com.example.cursosms.model.Professor;
import com.example.cursosms.model.dto.ProfessorRequest;
import com.example.cursosms.model.resource.ProfessorResource;
import com.example.cursosms.service.IProfessorService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/professores")
@AllArgsConstructor
public class ProfessorController {

    private IProfessorService professorService;
    private PagedResourcesAssembler<ProfessorResource> pagedResourceAssembler;

    @PostMapping
    public ResponseEntity<EntityModel<ProfessorResource>> registrarProfessor(
            @RequestBody @Valid ProfessorRequest professorRequest){

        ProfessorResource professorResource = professorService.save(professorRequest);
        return ResponseEntity.created(null).body(EntityModel.of(professorResource));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProfessorResource>> buscarProfessorPorId(@PathVariable UUID id){
        ProfessorResource professorResource = professorService.findByUsuarioId(id);
        return ResponseEntity.ok().body(EntityModel.of(professorResource));
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ProfessorResource>>> buscarTodosProfessores(Pageable pageable){
        Page<ProfessorResource> professors = professorService.findAll(pageable);

        PagedModel<EntityModel<ProfessorResource>> pagedModel = pagedResourceAssembler
                .toModel(professors, linkTo(methodOn(ProfessorController.class)
                        .buscarTodosProfessores(pageable))
                        .withSelfRel());

        return ResponseEntity.ok().body(pagedModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EntityModel<ProfessorResource>> deletarProfessor(@PathVariable UUID id){
        ProfessorResource professorResource = professorService.delete(id);
        return ResponseEntity.ok().body(EntityModel.of(professorResource));
    }
}