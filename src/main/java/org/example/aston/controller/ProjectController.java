package org.example.aston.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.aston.DTO.ProjectDTO;
import org.example.aston.service.ProjectService;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("create")
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO) {
        projectService.createProject(projectDTO);
        return ResponseEntity.ok(projectDTO);
    }

    @GetMapping("getAllProjects")
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @DeleteMapping("delete/{id}")
    public void deleteProject(@PathVariable Long id) {
           projectService.remove(id);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable("id") Long projectId, @RequestBody ProjectDTO projectDTO) {
        projectService.update(projectId, projectDTO);
        return ResponseEntity.ok(projectDTO);
    }

}
