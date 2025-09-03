package org.example.aston.service;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.aston.DTO.ProjectDTO;
import org.example.aston.entity.ProjectEntity;
import org.example.aston.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public void createProject(ProjectDTO projectDTO) {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setName(projectDTO.getName());
        projectEntity.setSerialNumber(projectDTO.getSerialNumber());
        projectRepository.save(projectEntity);
    }


    public List<ProjectDTO> getAllProjects() {
        List<ProjectEntity> projectEntities = projectRepository.findAll();
        List<ProjectDTO> projectDTOList = new ArrayList<>();
        for (ProjectEntity projectEntity : projectEntities) {
            ProjectDTO projectDTO = new ProjectDTO();
            projectDTO.setId(projectEntity.getId());
            projectDTO.setName(projectEntity.getName());
            projectDTO.setSerialNumber(projectEntity.getSerialNumber());
            projectDTOList.add(projectDTO);
        }
        return projectDTOList;
    }

    @Transactional
    public void remove(Long projectId) {
        projectRepository.deleteById(projectId);
    }

    @Transactional
    public void update(Long projectId, ProjectDTO projectDTO) {
        Optional<ProjectEntity> projectEntity = projectRepository.findById(projectId);
        if (projectEntity.isEmpty()) {
            ProjectEntity newProjectEntity = new ProjectEntity();
            newProjectEntity.setId(projectId);
            newProjectEntity.setName(projectDTO.getName());
            newProjectEntity.setSerialNumber(projectDTO.getSerialNumber());
            projectRepository.save(newProjectEntity);
        }else {
            projectEntity.get().setName(projectDTO.getName());
            projectEntity.get().setSerialNumber(projectDTO.getSerialNumber());
            projectRepository.save(projectEntity.get());
        }
    }
}
