package com.lephuduy.jobhunter.controller;

import com.lephuduy.jobhunter.domain.Company;
import com.lephuduy.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.lephuduy.jobhunter.service.CompanyService;
import com.lephuduy.jobhunter.util.anotaton.ApiMessage;
import com.lephuduy.jobhunter.util.error.IdInvalidException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    @ApiMessage("create a company")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company) {
        Company newCompany = this.companyService.handleSaveCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
    }

    @PutMapping("/companies")
    @ApiMessage("update company")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company company) {
        Company newCompany = this.companyService.handleUpdateCompany(company);
        return ResponseEntity.status(HttpStatus.OK).body(newCompany);
    }

    @DeleteMapping("/companies/{id}")
    @ApiMessage("delete a company")
    public ResponseEntity<String> deleteCompany(@PathVariable("id") long id) {
        this.companyService.handleDeleteCompany(id);
        return ResponseEntity.status(HttpStatus.OK).body("delete company");
    }

    @GetMapping("/companies")
    @ApiMessage("fetch all company")
    public ResponseEntity<ResultPaginationDTO> fetchCompany(
            @Filter Specification<Company> spec,
            Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(this.companyService.fetchAllCompany(spec, pageable));
    }

    @GetMapping("/companies/{id}")
    @ApiMessage("fetch a company")
    public ResponseEntity<Company> fetchACompany(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Company> comOptional = this.companyService.fetchById(id);
        if (!comOptional.isPresent()) {
            throw new IdInvalidException("Company id không hợp lệ");
        }
        Company com = comOptional.get();
        return ResponseEntity.status(HttpStatus.OK).body(com);
    }
}
