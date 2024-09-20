package com.e201.api.service.company;

import static com.e201.global.auth.RoleType.*;
import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.e201.api.controller.company.request.employee.EmployeeAuthRequest;
import com.e201.api.controller.company.request.employee.EmployeeCreateRequest;
import com.e201.api.controller.company.request.manager.ManagerAuthRequest;
import com.e201.api.controller.company.response.employee.EmployeeCreateResponse;
import com.e201.domain.entity.company.Company;
import com.e201.domain.entity.company.CompanyInfo;
import com.e201.domain.entity.company.Department;
import com.e201.domain.entity.company.Employee;
import com.e201.domain.entity.company.Manager;
import com.e201.domain.repository.company.CompanyInfoRepository;
import com.e201.domain.repository.company.CompanyRepository;
import com.e201.domain.repository.company.DepartmentRepository;
import com.e201.domain.repository.company.EmployeeRepository;
import com.e201.global.auth.response.AuthResponse;

@SpringBootTest
@Transactional
class EmployeeServiceTest {

	@Autowired
	CompanyRepository companyRepository;

	@Autowired
	CompanyInfoRepository companyInfoRepository;

	@Autowired
	DepartmentRepository departmentRepository;

	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	EmployeeService sut;

	Company company;

	CompanyInfo companyInfo;

	Department department;

	@BeforeEach
	void setUp() {
		companyInfo = createCompanyInfo("사업자 등록증 번호");
		companyInfoRepository.save(companyInfo);

		company = createCompany("company@test.com", "12341234", companyInfo);
		companyRepository.save(company);

		department = createDepartment(company);
		departmentRepository.save(department);
	}

	@DisplayName("직원 계정을 저장한다.")
	@Test
	void create_employee_success() {
		// given
		EmployeeCreateRequest request = createEmployeeCreateRequest(department.getId());

		// when
		EmployeeCreateResponse actual = sut.create(request);

		//then
		assertThat(actual.getId()).isNotNull();
	}

	@DisplayName("인증에 성공한다.")
	@Test
	void check_password_success() {
		// given
		Employee employee = createEmployee(department);
		employeeRepository.save(employee);
		EmployeeAuthRequest request = createEmployeeAuthRequest("직원코드", "12341234");

		// when
		AuthResponse actual = sut.checkPassword(request);

		//then
		assertThat(actual).extracting("id", "roleType").containsExactly(employee.getId(), EMPLOYEE);
	}

	@DisplayName("존재하지 않는 코드로 인증을 시도하면 예외가 발생한다.")
	@Test
	void check_password_fail_by_not_found_code() {
		// given
		Employee employee = createEmployee(department);
		employeeRepository.save(employee);
		EmployeeAuthRequest request = createEmployeeAuthRequest("존재하지 않는 코드", "12341234");

		// when //then
		assertThatThrownBy(() ->sut.checkPassword(request)).isInstanceOf(RuntimeException.class);
	}

	@DisplayName("요청 비밀번호와 실제 비밀번호가 다르면 인증에 실패한다.")
	@Test
	void check_password_fail() {
		// given
		Employee employee = createEmployee(department);
		employeeRepository.save(employee);
		EmployeeAuthRequest request = createEmployeeAuthRequest("직원코드", "invalid_password");

		// when //then
		assertThatThrownBy(() ->sut.checkPassword(request)).isInstanceOf(RuntimeException.class);
	}

	@DisplayName("직원(엔티티)를 조회한다.")
	@Test
	void find_employee_entity_success() {
		// given
		Employee employee = createEmployee(department);
		employeeRepository.save(employee);

		// when
		Employee actual = sut.findEntity(employee.getId());

		//then
		assertThatEmployeeMatchExactly(actual);
	}

	@DisplayName("존재하지 않는 직원(엔티티)을 조회하면 예외가 발생한다.")
	@Test
	void find_employee_entity_fail() {
		// expected
		assertThatThrownBy(() -> sut.findEntity(UUID.randomUUID())).isInstanceOf(RuntimeException.class);
	}

	private EmployeeCreateRequest createEmployeeCreateRequest(UUID departmentId) {
		return EmployeeCreateRequest.builder()
			.departmentId(departmentId)
			.code("직원코드")
			.password("12341234")
			.build();
	}

	private Employee createEmployee(Department department) {
		return Employee.builder()
			.department(department)
			.code("직원코드")
			.password("12341234")
			.build();
	}

	private Department createDepartment(Company company) {
		return Department.builder()
			.company(company)
			.code("부서코드")
			.name("부서이름")
			.build();
	}

	private Company createCompany(String email, String password, CompanyInfo companyInfo) {
		return Company.builder()
			.email(email)
			.password(password)
			.companyInfo(companyInfo)
			.build();
	}

	private CompanyInfo createCompanyInfo(String registerNumber) {
		return CompanyInfo.builder()
			.name("사업장 이름")
			.phone("사업장 연락처")
			.businessAddress("사업장 주소")
			.businessType("사업 유형")
			.representativeName("사업자 대표 이름")
			.registerNumber(registerNumber)
			.build();
	}

	private EmployeeAuthRequest createEmployeeAuthRequest(String code, String password) {
		return EmployeeAuthRequest.builder()
			.code(code)
			.password(password)
			.build();
	}

	private void assertThatEmployeeMatchExactly(Employee employee) {
		assertThat(employee)
			.extracting("code", "password")
			.containsExactly("직원코드", "12341234");
	}
}