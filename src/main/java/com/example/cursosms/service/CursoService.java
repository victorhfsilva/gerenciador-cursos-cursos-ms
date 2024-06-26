package com.example.cursosms.service;

import com.example.cursosms.model.requests.CursoRequest;
import com.example.cursosms.model.resources.CursoResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CursoService {
    CursoResource save(CursoRequest cursoDto);

    CursoResource findById(Long id);

    Page<CursoResource> findByProfessorId(UUID professorId, Pageable pageable);

    Page<CursoResource> findByAlunoId(UUID alunoId, Pageable pageable);

    Page<CursoResource> findAll(Pageable pageable);

    CursoResource update(Long id, CursoRequest cursoDto);

    CursoResource addAluno(Long id, UUID alunoId);

    CursoResource removeAluno(Long id, UUID alunoId);

    CursoResource delete(Long id);
}
