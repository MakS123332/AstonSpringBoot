package org.example.aston;

import org.example.aston.DTO.ProjectDTO;
import org.example.aston.entity.ProjectEntity;
import org.example.aston.repository.ProjectRepository;
import org.example.aston.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void createProject_ShouldSaveProject() {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName("Test Project");
        projectDTO.setSerialNumber("SN-001");

        ProjectEntity savedEntity = new ProjectEntity();
        savedEntity.setId(1L);
        savedEntity.setName("Test Project");
        savedEntity.setSerialNumber("SN-001");

        when(projectRepository.save(any(ProjectEntity.class))).thenReturn(savedEntity);

        projectService.createProject(projectDTO);

        verify(projectRepository, times(1)).save(any(ProjectEntity.class));
    }

    @Test
    void getAllProjects_ShouldReturnListOfProjects() {
        ProjectEntity entity1 = new ProjectEntity();
        entity1.setId(1L);
        entity1.setName("Project 1");
        entity1.setSerialNumber("SN-001");

        ProjectEntity entity2 = new ProjectEntity();
        entity2.setId(2L);
        entity2.setName("Project 2");
        entity2.setSerialNumber("SN-002");

        when(projectRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));

        List<ProjectDTO> result = projectService.getAllProjects();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Project 1", result.get(0).getName());
        assertEquals("SN-002", result.get(1).getSerialNumber());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void remove_ShouldDeleteProject() {
        Long projectId = 1L;
        doNothing().when(projectRepository).deleteById(projectId);

        projectService.remove(projectId);

        verify(projectRepository, times(1)).deleteById(projectId);
    }

    @Test
    void update_WhenProjectExists_ShouldUpdateProject() {
        Long projectId = 1L;
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName("Updated Name");
        projectDTO.setSerialNumber("Updated-SN");

        ProjectEntity existingEntity = new ProjectEntity();
        existingEntity.setId(projectId);
        existingEntity.setName("Original Name");
        existingEntity.setSerialNumber("Original-SN");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingEntity));
        when(projectRepository.save(any(ProjectEntity.class))).thenReturn(existingEntity);

        projectService.update(projectId, projectDTO);

        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, times(1)).save(existingEntity);
        assertEquals("Updated Name", existingEntity.getName());
        assertEquals("Updated-SN", existingEntity.getSerialNumber());
    }

    @Test
    void update_WhenProjectNotExists_ShouldCreateNewProject() {

        Long projectId = 999L;
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName("New Project");
        projectDTO.setSerialNumber("NEW-SN");

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        when(projectRepository.save(any(ProjectEntity.class))).thenAnswer(invocation -> {
            ProjectEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        projectService.update(projectId, projectDTO);

        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, times(1)).save(any(ProjectEntity.class));

        ArgumentCaptor<ProjectEntity> projectCaptor = ArgumentCaptor.forClass(ProjectEntity.class);
        verify(projectRepository).save(projectCaptor.capture());

        ProjectEntity savedProject = projectCaptor.getValue();
        assertNotNull(savedProject);
        assertEquals("New Project", savedProject.getName());
        assertEquals("NEW-SN", savedProject.getSerialNumber());
    }
}