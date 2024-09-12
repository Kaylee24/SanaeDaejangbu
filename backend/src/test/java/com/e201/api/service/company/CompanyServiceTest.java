package com.e201.api.service.company;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.e201.domain.entity.company.Company;
import com.e201.domain.entity.company.CompanyInfo;
import com.e201.domain.repository.company.CompanyInfoRepository;
import com.e201.domain.repository.company.CompanyRepository;

@SpringBootTest
class CompanyServiceTest {

	@Autowired
	CompanyInfoRepository companyInfoRepository;

	@Autowired
	CompanyRepository companyRepository;

	@Autowired
	CompanyService sut;

	@Transactional
	@DisplayName("기업 계정(엔티티)을 조회한다.")
	@Test
	void find_company_entity_success() {
		// given
		CompanyInfo companyInfo = createCompanyInfo("사업자 등록증 번호");
		companyInfoRepository.save(companyInfo);

		Company company = createCompany("company@test.com", "12341234", companyInfo);
		companyRepository.save(company);

		// when
		Company findCompany = sut.findDomain(company.getId());

		//then
		assertThatCompanyMatchExactly(findCompany);
		assertThatCompanyInfoMatchExactly(findCompany.getCompanyInfo());
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

	private void assertThatCompanyInfoMatchExactly(CompanyInfo companyInfo) {
		assertThat(companyInfo)
			.extracting("name", "phone", "businessAddress", "businessType", "representativeName", "registerNumber")
			.containsExactly("사업장 이름", "사업장 연락처", "사업장 주소", "사업 유형", "사업자 대표 이름", "사업자 등록증 번호");
	}

	private void assertThatCompanyMatchExactly(Company findCompany) {
		assertThat(findCompany)
			.extracting("email", "password")
			.containsExactly("company@test.com", "12341234");
	}
}