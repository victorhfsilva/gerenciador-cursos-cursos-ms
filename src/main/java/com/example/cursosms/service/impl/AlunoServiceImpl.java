package com.example.cursosms.service.impl;

import com.example.cursosms.controller.AlunoController;
import com.example.cursosms.controller.CursoController;
import com.example.cursosms.mapper.AlunoMapper;
import com.example.cursosms.model.Aluno;
import com.example.cursosms.model.requests.AlunoRequest;
import com.example.cursosms.model.resources.AlunoResource;
import com.example.cursosms.repository.AlunoRepository;
import com.example.cursosms.service.AlunoService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@AllArgsConstructor
public class AlunoServiceImpl implements AlunoService {

    private AlunoRepository alunoRepository;
    private AlunoMapper alunoMapper;

    @Transactional
    public AlunoResource save(AlunoRequest alunoDto) {
        Aluno aluno = alunoMapper.map(alunoDto);
        Aluno alunoSalvo = alunoRepository.save(aluno);

        AlunoResource alunoResource = alunoMapper.map(alunoSalvo);

        alunoResource.add(linkTo(methodOn(AlunoController.class).registrarAluno(alunoDto)).withSelfRel());
        alunoResource.add(linkTo(methodOn(CursoController.class).buscarCursosPorAlunoId(alunoDto.usuarioId(), Pageable.unpaged())).withRel("cursos"));

        return alunoResource;
    }

    public AlunoResource findByUsuarioId(UUID usuarioId) {
        Aluno aluno = alunoRepository.findByUsuarioId(usuarioId).orElseThrow();

        AlunoResource alunoResource = alunoMapper.map(aluno);

        alunoResource.add(linkTo(methodOn(AlunoController.class).buscarAlunoPorId(usuarioId)).withSelfRel());
        alunoResource.add(linkTo(methodOn(CursoController.class).buscarCursosPorAlunoId(aluno.getUsuarioId(), Pageable.unpaged())).withRel("cursos"));

        return alunoResource;
    }

    public Page<AlunoResource> findAlunosByCursoId(Long cursoId, Pageable pageable) {
        Page<Aluno> alunos = alunoRepository.findAlunosByCursoId(cursoId, pageable);

        return alunos
                .map(aluno ->
                        alunoMapper
                                .map(aluno)
                                .add(linkTo(methodOn(AlunoController.class)
                                        .buscarAlunoPorId(aluno.getUsuarioId()))
                                        .withSelfRel())
                                .add(linkTo(methodOn(CursoController.class)
                                        .buscarCursosPorAlunoId(aluno.getUsuarioId(), Pageable.unpaged()))
                                        .withRel("curso")));
    }

    public Page<AlunoResource> findAll(Pageable pageable) {
        Page<Aluno> alunos = alunoRepository.findAll(pageable);

        return alunos
                .map(aluno ->
                        alunoMapper
                                .map(aluno)
                                .add(linkTo(methodOn(AlunoController.class)
                                        .buscarAlunoPorId(aluno.getUsuarioId()))
                                        .withSelfRel())
                                .add(linkTo(methodOn(CursoController.class)
                                        .buscarCursosPorAlunoId(aluno.getUsuarioId(), Pageable.unpaged()))
                                        .withRel("curso")));

    }

    @Transactional
    public AlunoResource delete(UUID id){
        Aluno aluno = alunoRepository.findByUsuarioId(id).orElseThrow();

        alunoRepository.delete(aluno);

        AlunoResource alunoResource = alunoMapper.map(aluno);

        alunoResource.add(linkTo(methodOn(AlunoController.class).deletarAluno(id)).withSelfRel());

        return alunoResource;
    }
}
