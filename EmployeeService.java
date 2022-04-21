package com.example.rqchallenge.service;

import com.example.rqchallenge.constants.AppConstants;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.model.EmployeeData;
import com.example.rqchallenge.model.EmployeeResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeService {

    @Autowired
    private RestTemplate restTemplate;

    public List<Employee> getAllEmployees() throws IOException {
        ResponseEntity<EmployeeData> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(
                    new URI(AppConstants.GET_EMPLOYEE_URL),
                    HttpMethod.GET,
                    null,
                    EmployeeData.class
            );
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        log.info("Response of Request :{} ", responseEntity.getBody().getData());
        return responseEntity.getBody().getData();
    }


    public List<Employee> getEmployeesByNameSearch(String searchString) {
        List<Employee> employeeList = null;
        try {
            employeeList = getAllEmployees();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return employeeList.stream()
                .filter(employee -> employee.getEmployee_name().contains(searchString))
                .collect(Collectors.toList());
    }


    public Employee getEmployeeById(String id) {
        ResponseEntity<EmployeeResponseData> responseEntity = restTemplate.exchange(
                AppConstants.GET_EMPLOYEE_ID_URL,
                HttpMethod.GET,
                null,
                EmployeeResponseData.class,
                id);

        log.info("Response of Request :{} ", responseEntity.getBody().getData());
        return responseEntity.getBody().getData();
    }

    public Integer getHighestSalaryOfEmployees() {
        List<Employee> employeeList = null;
        try {
            employeeList = getAllEmployees();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return employeeList.stream()
                .max(Comparator.comparing(Employee::getEmployee_salary))
                .get().getEmployee_salary();
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {
        List<Employee> employeeList = null;
        try {
            employeeList = getAllEmployees();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return employeeList.stream().sorted(Comparator.comparing(Employee::getEmployee_salary).reversed())
                .limit(10)
                .map(Employee::getEmployee_name)
                .collect(Collectors.toList());
    }

    public Employee createEmployee(String name, String salary, String age) {
        Employee employee = Employee.builder()
                .employee_name(name)
                .employee_salary(Integer.parseInt(salary))
                .employee_age(Integer.parseInt(age))
                .build();

        ResponseEntity<EmployeeResponseData> employeeResponseResponseEntity = restTemplate.exchange(
                AppConstants.CREATE_EMPLOYEE_URL,
                HttpMethod.POST,
                new HttpEntity<>(employee),
                EmployeeResponseData.class);

        log.info("Response of Request :{} ", employeeResponseResponseEntity.getBody().getData());

        return employeeResponseResponseEntity.getBody().getData();
    }

    public String deleteEmployee(String id) {

        Employee employee = getEmployeeById(id);

        ResponseEntity<EmployeeResponseData> employeeResponseResponseEntity = restTemplate.exchange(
                AppConstants.DELETE_EMPLOYEE_URL,
                HttpMethod.DELETE,
                null,
                EmployeeResponseData.class,
                id);

        if (employeeResponseResponseEntity.getStatusCode() == HttpStatus.OK)
            return employee.getEmployee_name();

        return "Employee does not exist";
    }
}
