package com.example.rqchallenge.service;

import com.example.rqchallenge.constants.AppConstants;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.model.EmployeeData;
import com.example.rqchallenge.model.EmployeeResponseData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class EmployeeServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmployeeService employeeService = new EmployeeService();

    private List<Employee> employeeList = new ArrayList<>();

    @BeforeEach
    public void setup() {
        employeeList.add(Employee.builder().id(1).employee_name("Deepak").employee_age(23).employee_salary(1000).build());
        employeeList.add(Employee.builder().id(2).employee_name("Bradley").employee_age(25).employee_salary(1002).build());
        employeeList.add(Employee.builder().id(3).employee_name("Tiger").employee_age(26).employee_salary(1004).build());
        employeeList.add(Employee.builder().id(4).employee_name("Nixon").employee_age(27).employee_salary(100001).build());
        employeeList.add(Employee.builder().id(5).employee_name("Kennedy").employee_age(27).employee_salary(100006).build());
        employeeList.add(Employee.builder().id(6).employee_name("Haley").employee_age(27).employee_salary(10000).build());
        employeeList.add(Employee.builder().id(7).employee_name("Doris").employee_age(27).employee_salary(10001).build());
        employeeList.add(Employee.builder().id(8).employee_name("Vance").employee_age(27).employee_salary(10002).build());
        employeeList.add(Employee.builder().id(9).employee_name("Caesar").employee_age(27).employee_salary(10003).build());
        employeeList.add(Employee.builder().id(10).employee_name("Yuri").employee_age(27).employee_salary(10004).build());
        employeeList.add(Employee.builder().id(11).employee_name("Jenette").employee_age(27).employee_salary(10005).build());
    }

    @Test
    public void testGetAllEmployees() throws URISyntaxException, IOException {
        getAllEmployee();

        List<Employee> allEmployeesList = employeeService.getAllEmployees();

        assertEquals(allEmployeesList.size(), employeeList.size());
        assertEquals(allEmployeesList, employeeList);

    }

    @Test
    public void testGetEmployeesByNameSearch() throws URISyntaxException, IOException {
        getAllEmployee();

        List<Employee> allEmployeesList = employeeService.getEmployeesByNameSearch("Dee");

        assertEquals(allEmployeesList.get(0).getEmployee_name(), "Deepak");
    }

    @Test
    public void testGetEmployeeById() throws URISyntaxException, IOException {
        EmployeeResponseData employeeResponseData = getEmployeeByID();

        Employee employee = employeeService.getEmployeeById("1");

        assertEquals(employeeResponseData.getData(), employee);
    }

    private EmployeeResponseData getEmployeeByID() {
        String id = "1";
        EmployeeResponseData employeeResponseData = EmployeeResponseData.builder()
                .data(Employee.builder().id(1)
                        .employee_name("Deepak")
                        .employee_age(23)
                        .employee_salary(1000)
                        .build())
                .build();

        when(restTemplate.exchange(
                AppConstants.GET_EMPLOYEE_ID_URL,
                HttpMethod.GET,
                null,
                EmployeeResponseData.class,
                id))
                .thenReturn(new ResponseEntity(employeeResponseData, HttpStatus.OK));
        return employeeResponseData;
    }

    @Test
    public void testGetHighestSalaryOfEmployees() throws URISyntaxException, IOException {
        getAllEmployee();

        Integer highestSalaryOfEmployee = employeeService.getHighestSalaryOfEmployees();

        assertEquals(highestSalaryOfEmployee, Integer.valueOf(100006));
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNames() throws URISyntaxException, IOException {
        getAllEmployee();

        List<String> topTenHighestEarningEmployeeNames = employeeService.getTopTenHighestEarningEmployeeNames();

        assertEquals(topTenHighestEarningEmployeeNames.contains("Deepak"), false);
        assertEquals(topTenHighestEarningEmployeeNames.contains("Jenette"), true);
        assertEquals(topTenHighestEarningEmployeeNames.size(), 10);
    }

    @Test
    public void testCreateEmployee() throws URISyntaxException, IOException {
        Employee employee = Employee.builder()
                .employee_name("Byrd")
                .employee_salary(1004)
                .employee_age(29)
                .build();

        EmployeeResponseData employeeResponseData = EmployeeResponseData.builder().data(employee).status("Success").build();

        when(restTemplate.exchange(
                AppConstants.CREATE_EMPLOYEE_URL,
                HttpMethod.POST,
                new HttpEntity<>(employee),
                EmployeeResponseData.class))
                .thenReturn(new ResponseEntity(employeeResponseData, HttpStatus.OK));

        Employee serviceEmployee = employeeService.createEmployee("Byrd", "1004", "29");
        assertEquals(serviceEmployee, employee);
        assertEquals(serviceEmployee, employee);

    }

    @Test
    public void testDeleteEmployee() throws URISyntaxException, IOException {
        String id = "1";
        EmployeeResponseData employeeResponseData = getEmployeeByID();

        when(restTemplate.exchange(
                AppConstants.DELETE_EMPLOYEE_URL,
                HttpMethod.DELETE,
                null,
                EmployeeResponseData.class,
                id))
                .thenReturn(new ResponseEntity(employeeResponseData, HttpStatus.OK));

        String employeeName = employeeService.deleteEmployee("1");

        assertEquals(employeeName, "Deepak");

    }

    private void getAllEmployee() throws URISyntaxException {
        EmployeeData employeeData = new EmployeeData(employeeList);
        when(restTemplate.exchange(
                new URI(AppConstants.GET_EMPLOYEE_URL),
                HttpMethod.GET,
                null,
                EmployeeData.class))
                .thenReturn(new ResponseEntity(employeeData, HttpStatus.OK));
    }
}
