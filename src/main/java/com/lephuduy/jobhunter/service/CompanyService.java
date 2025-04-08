package com.lephuduy.jobhunter.service;

import java.util.Optional;

import com.lephuduy.jobhunter.domain.Company;
import com.lephuduy.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.lephuduy.jobhunter.repository.CompanyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleSaveCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public void handleDeleteCompany(long id) {
        this.companyRepository.deleteById(id);
        ;

    }

    public ResultPaginationDTO fetchAllCompany(Specification<Company> spec, Pageable pageable) {
        Page<Company> companyPage = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(companyPage.getTotalPages());
        mt.setTotal(companyPage.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(companyPage.getContent());
        return rs;
    }

    public Company handleUpdateCompany(Company company) {
        Optional<Company> newCompany = this.companyRepository.findById(company.getId());
        if (newCompany.isPresent()) {
            Company currentCompany = newCompany.get();
            currentCompany.setName(company.getName());
            currentCompany.setAddress(company.getAddress());
            currentCompany.setLogo(company.getLogo());
            currentCompany.setDescription(company.getDescription());
            return this.companyRepository.save(currentCompany);
        }
        return null;
    }

    public Optional<Company> fetchById(long id) {
        return this.companyRepository.findById(id);
    }
}

