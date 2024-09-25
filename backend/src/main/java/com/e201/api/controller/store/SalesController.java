package com.e201.api.controller.store;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.e201.api.controller.store.request.SalesCreateRequest;
import com.e201.api.controller.store.response.SalesCreateResponse;
import com.e201.api.service.store.SalesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SalesController {
	private final SalesService salesService;

	@PostMapping("/stores/sales")
	public ResponseEntity<SalesCreateResponse> create(@RequestBody SalesCreateRequest salesRequest){
		SalesCreateResponse response = salesService.create(salesRequest);
		return ResponseEntity.status(CREATED).body(response);
	}
}