package org.example.aston.repository;

import org.example.aston.DTO.ProjectDTO;
import org.example.aston.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

}
