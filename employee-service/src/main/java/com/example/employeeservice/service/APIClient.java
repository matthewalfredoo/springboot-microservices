package com.example.employeeservice.service;

import com.example.employeeservice.dto.DepartmentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "DEPARTMENT-SERVICE")
public interface APIClient {

    @GetMapping("/api/departments/{departmentCode}")
    DepartmentDto getDepartment(
            @PathVariable(name = "departmentCode")
            String departmentCode
    );

}
