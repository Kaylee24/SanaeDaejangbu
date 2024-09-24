package com.e201.api.controller.company.request.companyinfo;

import com.e201.domain.entity.company.CompanyInfo;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CompanyInfoCreateRequest {

	@NotBlank
	private String registerNumber;

	@NotBlank
	private String name;

	@NotBlank
	private String representativeName;

	@NotBlank
	private String phone;

	@NotBlank
	private String businessType;

	@NotBlank
	private String businessAddress;

	@Builder
	private CompanyInfoCreateRequest(String registerNumber, String name, String representativeName, String phone,
		String businessType, String businessAddress) {
		this.registerNumber = registerNumber;
		this.name = name;
		this.representativeName = representativeName;
		this.phone = phone;
		this.businessType = businessType;
		this.businessAddress = businessAddress;
	}

	public CompanyInfo toEntity() {
		return CompanyInfo.builder()
			.registerNumber(registerNumber)
			.name(name)
			.representativeName(representativeName)
			.phone(phone)
			.businessType(businessType)
			.businessAddress(businessAddress)
			.build();
	}
}
