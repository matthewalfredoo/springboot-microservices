package com.example.employeeservice.service.impl;

import com.example.employeeservice.dto.EmployeeDto;
import com.example.employeeservice.entity.Employee;
import com.example.employeeservice.exception.ResourceNotFoundException;
import com.example.employeeservice.mapper.AutoUserMapper;
import com.example.employeeservice.repository.EmployeeRepository;
import com.example.employeeservice.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepository;

    private ModelMapper modelMapper;

    @Override
    public EmployeeDto saveEmployee(EmployeeDto employeeDto) {
        /*Employee employee = new Employee(
                employeeDto.getId(),
                employeeDto.getFirstName(),
                employeeDto.getLastName(),
                employeeDto.getEmail()
        );*/
        /** ModelMapper **/
        //Employee employee = modelMapper.map(employeeDto, Employee.class);
        /** MapStruct **/
        Employee employee = AutoUserMapper.MAPPER.mapToEmployee(employeeDto);

        Employee savedEmployee = employeeRepository.save(employee);

        /*EmployeeDto savedEmployeeDto = new EmployeeDto(
                savedEmployee.getId(),
                savedEmployee.getFirstName(),
                savedEmployee.getLastName(),
                savedEmployee.getEmail()
        );*/
        /** ModelMapper **/
        // EmployeeDto savedEmployeeDto = modelMapper.map(savedEmployee, EmployeeDto.class);
        /** MapStruct **/
        EmployeeDto savedEmployeeDto = AutoUserMapper.MAPPER.mapToEmployeeDto(savedEmployee);

        return savedEmployeeDto;
    }

    @Override
    public EmployeeDto getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> {
                    return new ResourceNotFoundException("Employee", "id", id);
                }
        );

        /*EmployeeDto employeeDto = new EmployeeDto(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail()
        );*/
        /** ModelMapper **/
        // EmployeeDto employeeDto = modelMapper.map(employee, EmployeeDto.class);
        /** MapStruct **/
        EmployeeDto employeeDto = AutoUserMapper.MAPPER.mapToEmployeeDto(employee);

        return employeeDto;
    }

}
