package com.lephuduy.jobhunter.repository;

import com.lephuduy.jobhunter.domain.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long>, JpaSpecificationExecutor<Skill> {
    List<Skill> findByIdIn(List<Long> skills);

    boolean existsByName(String name);
}
