package com.example.cursosms.service.impl;

import com.example.cursosms.controller.AlunoController;
import com.example.cursosms.controller.CursoController;
import com.example.cursosms.controller.ProfessorController;
import com.example.cursosms.exception.CursoPatchException;
import com.example.cursosms.mapper.CursoMapper;
import com.example.cursosms.model.Aluno;
import com.example.cursosms.model.Curso;
import com.example.cursosms.model.Professor;
import com.example.cursosms.model.requests.CursoRequest;
import com.example.cursosms.model.resources.CursoResource;
import com.example.cursosms.repository.AlunoRepository;
import com.example.cursosms.repository.CursoRepository;
import com.example.cursosms.repository.ProfessorRepository;
import com.example.cursosms.service.CursoService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@AllArgsConstructor
public class CursoServiceImpl implements CursoService {

    private CursoRepository cursoRepository;
    private CursoMapper cursoMapper;
    private AlunoRepository alunoRepository;
    private ProfessorRepository professorRepository;

    @Transactional
    public CursoResource save(CursoRequest cursoDto) {
        Curso curso = cursoMapper.map(cursoDto);
        Professor professor = professorRepository.findByUsuarioId(cursoDto.professor()).orElseThrow();
        curso.setProfessor(professor);

        Curso cursoSalvo = cursoRepository.save(curso);

        CursoResource cursoResource = cursoMapper.map(cursoSalvo);

        cursoResource.add(linkTo(methodOn(CursoController.class).registrarCurso(cursoDto)).withSelfRel());
        cursoResource.add(linkTo(methodOn(ProfessorController.class).buscarProfessorPorId(cursoDto.professor())).withRel("professor"));
        cursoResource.add(linkTo(methodOn(AlunoController.class).buscarAlunosPorCursoId(cursoSalvo.getId(), Pageable.unpaged())).withRel("alunos"));

        return cursoResource;
    }

    public CursoResource findById(Long id) {
        Curso curso = cursoRepository.findById(id).orElseThrow();

        CursoResource cursoResource = cursoMapper.map(curso);

        cursoResource.add(linkTo(methodOn(CursoController.class).buscarCursoPorId(id)).withSelfRel());
        cursoResource.add(linkTo(methodOn(ProfessorController.class).buscarProfessorPorId(curso.getProfessor().getUsuarioId())).withRel("professor"));
        cursoResource.add(linkTo(methodOn(AlunoController.class).buscarAlunosPorCursoId(curso.getId(), Pageable.unpaged())).withRel("alunos"));

        return cursoResource;
    }

    public Page<CursoResource> findByProfessorId(UUID professorId, Pageable pageable) {
        Page<Curso> cursos = cursoRepository.findCursosByProfessorId(professorId, pageable);

        return cursos
                .map(curso ->
                        cursoMapper
                                .map(curso)
                                .add(linkTo(methodOn(CursoController.class)
                                        .buscarCursoPorId(curso.getId()))
                                        .withSelfRel())
                                .add(linkTo(methodOn(ProfessorController.class)
                                        .buscarProfessorPorId(professorId))
                                        .withRel("professor"))
                                .add(linkTo(methodOn(AlunoController.class)
                                        .buscarAlunosPorCursoId(curso.getId(), Pageable.unpaged()))
                                        .withRel("alunos")));
    }

    public Page<CursoResource> findByAlunoId(UUID alunoId, Pageable pageable) {
        Page<Curso> cursos = cursoRepository.findCursosByAlunoId(alunoId, pageable);

        return cursos
                .map(curso ->
                        cursoMapper
                                .map(curso)
                                .add(linkTo(methodOn(CursoController.class)
                                        .buscarCursoPorId(curso.getId()))
                                        .withSelfRel())
                                .add(linkTo(methodOn(ProfessorController.class)
                                    .buscarProfessorPorId(curso.getProfessor().getUsuarioId()))
                                        .withRel("professor"))
                                .add(linkTo(methodOn(AlunoController.class)
                                        .buscarAlunosPorCursoId(curso.getId(), Pageable.unpaged()))
                                        .withRel("alunos")));
    }

    public Page<CursoResource> findAll(Pageable pageable) {
        Page<Curso> cursos = cursoRepository.findAll(pageable);

        return cursos
                .map(curso ->
                        cursoMapper
                                .map(curso)
                                .add(linkTo(methodOn(CursoController.class)
                                        .buscarCursoPorId(curso.getId()))
                                        .withSelfRel())
                                .add(linkTo(methodOn(ProfessorController.class)
                                        .buscarProfessorPorId(curso.getProfessor().getUsuarioId()))
                                        .withRel("professor"))
                                .add(linkTo(methodOn(AlunoController.class)
                                        .buscarAlunosPorCursoId(curso.getId(), Pageable.unpaged()))
                                        .withRel("alunos")));
    }

    @Transactional
    public CursoResource update(Long id, CursoRequest cursoDto) {
        Curso cursoSalvo = cursoRepository.findById(id).orElseThrow();

        Curso curso = cursoMapper.map(cursoDto);
        Professor professor = professorRepository.findByUsuarioId(cursoDto.professor()).orElseThrow();

        curso.setProfessor(professor);
        curso.setId(cursoSalvo.getId());

        Curso cursoAtualizado = cursoRepository.save(curso);

        CursoResource cursoResource = cursoMapper.map(cursoAtualizado);

        cursoResource.add(linkTo(methodOn(CursoController.class).atualizarCurso(id, cursoDto)).withSelfRel());
        cursoResource.add(linkTo(methodOn(ProfessorController.class).buscarProfessorPorId(cursoAtualizado.getProfessor().getUsuarioId())).withRel("professor"));
        cursoResource.add(linkTo(methodOn(AlunoController.class).buscarAlunosPorCursoId(cursoAtualizado.getId(), Pageable.unpaged())).withRel("alunos"));

        return cursoResource;
    }

    @Transactional
    public CursoResource addAluno(Long id, UUID alunoId) {
        Curso curso = cursoRepository.findById(id).orElseThrow();
        Aluno aluno = alunoRepository.findByUsuarioId(alunoId).orElseThrow();

        ArrayList<Aluno> alunos =  new ArrayList<>(curso.getAlunos());

        if (alunos.contains(aluno)) {
            throw new CursoPatchException("Aluno já foi cadastrado no curso");
        }

        alunos.add(aluno);

        curso.setAlunos(alunos);

        Curso cursoAtualizado = cursoRepository.save(curso);

        CursoResource cursoResource = cursoMapper.map(cursoAtualizado);

        cursoResource.add(linkTo(methodOn(CursoController.class).adicionarAluno(id, alunoId)).withSelfRel());
        cursoResource.add(linkTo(methodOn(ProfessorController.class).buscarProfessorPorId(cursoAtualizado.getProfessor().getUsuarioId())).withRel("professor"));
        cursoResource.add(linkTo(methodOn(AlunoController.class).buscarAlunosPorCursoId(cursoAtualizado.getId(), Pageable.unpaged())).withRel("alunos"));

        return cursoResource;
    }

    @Transactional
    public CursoResource removeAluno(Long id, UUID alunoId) {
        Curso curso = cursoRepository.findById(id).orElseThrow();
        Aluno aluno = alunoRepository.findByUsuarioId(alunoId).orElseThrow();

        ArrayList<Aluno> alunos = new ArrayList<>(curso.getAlunos());

        if (!alunos.contains(aluno)) {
            throw new CursoPatchException("Aluno não está cadastrado neste curso.");
        }

        alunos.remove(aluno);

        curso.setAlunos(alunos);

        Curso cursoAtualizado = cursoRepository.save(curso);

        CursoResource cursoResource = cursoMapper.map(cursoAtualizado);

        cursoResource.add(linkTo(methodOn(CursoController.class).adicionarAluno(id, alunoId)).withSelfRel());
        cursoResource.add(linkTo(methodOn(ProfessorController.class).buscarProfessorPorId(cursoAtualizado.getProfessor().getUsuarioId())).withRel("professor"));
        cursoResource.add(linkTo(methodOn(AlunoController.class).buscarAlunosPorCursoId(cursoAtualizado.getId(), Pageable.unpaged())).withRel("alunos"));

        return cursoResource;
    }

    @Transactional
    public CursoResource delete(Long id){
        Curso curso = cursoRepository.findById(id).orElseThrow();

        cursoRepository.delete(curso);

        CursoResource cursoResource = cursoMapper.map(curso);

        cursoResource.add(linkTo(methodOn(CursoController.class).deletarCurso(id)).withSelfRel());

        return cursoResource;
    }
}
