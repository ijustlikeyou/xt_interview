package com.xt.interview.service.impl;

import com.xt.interview.dto.DepartmentDTO;
import com.xt.interview.entity.Department;
import com.xt.interview.repository.DepartmentRepository;
import com.xt.interview.service.DepartmentService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private static final Integer NOT_DELETED = 0;
    private static final Integer DELETED = 1;

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public DepartmentDTO createDepartment(DepartmentDTO dto) {
        String code = dto.getCode().trim();
        if (departmentRepository.existsByCode(code)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "部门编码已存在");
        }

        Department department = new Department();
        department.setName(dto.getName().trim());
        department.setCode(code);
        department.setDescription(dto.getDescription());
        department.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        department.setDeleted(NOT_DELETED);
        return convertToDTO(saveDepartment(department));
    }

    @Override
    public DepartmentDTO updateDepartment(Long id, DepartmentDTO dto) {
        Department department = findDepartment(id);
        String code = dto.getCode().trim();
        if (departmentRepository.existsByCodeAndIdNot(code, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "部门编码已存在");
        }

        department.setName(dto.getName().trim());
        department.setCode(code);
        department.setDescription(dto.getDescription());
        if (dto.getStatus() != null) {
            department.setStatus(dto.getStatus());
        }
        return convertToDTO(saveDepartment(department));
    }

    @Override
    public void deleteDepartment(Long id) {
        Department department = findDepartment(id);
        department.setDeleted(DELETED);
        departmentRepository.save(department);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentDTO getDepartmentById(Long id) {
        return convertToDTO(findDepartment(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findByDeleted(NOT_DELETED).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findByIdAndDeleted(id, NOT_DELETED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "部门不存在"));
    }

    private Department saveDepartment(Department department) {
        try {
            return departmentRepository.saveAndFlush(department);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "部门编码已存在", e);
        }
    }

    private DepartmentDTO convertToDTO(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setCode(department.getCode());
        dto.setDescription(department.getDescription());
        dto.setStatus(department.getStatus());
        dto.setCreateTime(department.getCreateTime());
        dto.setUpdateTime(department.getUpdateTime());
        return dto;
    }
}
