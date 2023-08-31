package com.example.employeeservice.service.impl;

import com.example.employeeservice.dto.APIResponseDto;
import com.example.employeeservice.dto.DepartmentDto;
import com.example.employeeservice.dto.EmployeeDto;
import com.example.employeeservice.entity.Employee;
import com.example.employeeservice.exception.ResourceNotFoundException;
import com.example.employeeservice.repository.EmployeeRepository;
import com.example.employeeservice.service.APIClient;
import com.example.employeeservice.service.EmployeeService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private EmployeeRepository employeeRepository;

    private ModelMapper modelMapper;

    private RestTemplate restTemplate;

    private WebClient webClient;

    private APIClient apiClient;

    @Override
    public EmployeeDto saveEmployee(EmployeeDto employeeDto) {
        /*Employee employee = new Employee(
                employeeDto.getId(),
                employeeDto.getFirstName(),
                employeeDto.getLastName(),
                employeeDto.getEmail()
        );*/
        /** ModelMapper **/
        Employee employee = modelMapper.map(employeeDto, Employee.class);
        /** MapStruct **/
        // Employee employee = AutoUserMapper.MAPPER.mapToEmployee(employeeDto);

        Employee savedEmployee = employeeRepository.save(employee);

        /*EmployeeDto savedEmployeeDto = new EmployeeDto(
                savedEmployee.getId(),
                savedEmployee.getFirstName(),
                savedEmployee.getLastName(),
                savedEmployee.getEmail()
        );*/
        /** ModelMapper **/
        EmployeeDto savedEmployeeDto = modelMapper.map(savedEmployee, EmployeeDto.class);
        /** MapStruct **/
        // EmployeeDto savedEmployeeDto = AutoUserMapper.MAPPER.mapToEmployeeDto(savedEmployee);

        return savedEmployeeDto;
    }

    // @CircuitBreaker(name = "${spring.application.name}", fallbackMethod = "getDefaultDepartment")
    @Retry(name = "${spring.application.name}", fallbackMethod = "getDefaultDepartment")
    @Override
    public APIResponseDto getEmployeeById(Long id) {
        LOGGER.info("inside getEmployeeById() method");

        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> {
                    return new ResourceNotFoundException("Employee", "id", id);
                }
        );

        // get Department from the Department Service based on the the Department code stored in employee object
        /*ResponseEntity<DepartmentDto> responseEntity = restTemplate.getForEntity(
                "http://localhost:8080/api/departments/" + employee.getDepartmentCode(),
                DepartmentDto.class
        );

        DepartmentDto departmentDto = responseEntity.getBody();*/

        DepartmentDto departmentDto = webClient.get()
                .uri("http://localhost:8080/api/departments/" + employee.getDepartmentCode())
                .retrieve()
                .bodyToMono(DepartmentDto.class)
                .block();

        // DepartmentDto departmentDto = apiClient.getDepartment(employee.getDepartmentCode());

        /*EmployeeDto employeeDto = new EmployeeDto(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail()
        );*/
        /** ModelMapper **/
        EmployeeDto employeeDto = modelMapper.map(employee, EmployeeDto.class);
        /** MapStruct **/
        // EmployeeDto employeeDto = AutoUserMapper.MAPPER.mapToEmployeeDto(employee);

        APIResponseDto apiResponseDto = new APIResponseDto();
        apiResponseDto.setEmployee(employeeDto);
        apiResponseDto.setDepartment(departmentDto);

        return apiResponseDto;
    }

    public APIResponseDto getDefaultDepartment(Long id, Exception exception) {
        LOGGER.info("inside getDefaultDepartment() method");

        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> {
                    return new ResourceNotFoundException("Employee", "id", id);
                }
        );

        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setId(1L);
        departmentDto.setDepartmentName("R&D Department");
        departmentDto.setDepartmentCode("RD001");
        departmentDto.setDepartmentDescription("Research and Development Department");

        EmployeeDto employeeDto = modelMapper.map(employee, EmployeeDto.class);

        APIResponseDto apiResponseDto = new APIResponseDto();
        apiResponseDto.setEmployee(employeeDto);
        apiResponseDto.setDepartment(departmentDto);

        return apiResponseDto;
    }

}
