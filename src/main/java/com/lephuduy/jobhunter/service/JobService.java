package com.lephuduy.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.lephuduy.jobhunter.domain.Company;
import com.lephuduy.jobhunter.domain.Job;
import com.lephuduy.jobhunter.domain.Skill;
import com.lephuduy.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.lephuduy.jobhunter.domain.dto.response.job.ResCreateJobDTO;
import com.lephuduy.jobhunter.domain.dto.response.job.ResUpdateJobDTO;
import com.lephuduy.jobhunter.repository.CompanyRepository;
import com.lephuduy.jobhunter.repository.JobRepository;
import com.lephuduy.jobhunter.service.mapper.MapperJob;
import org.hibernate.query.named.ResultMappingMementoNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;

@Service
public class JobService {

    private final SkillService skillService;

    private final JobRepository jobRepository;

    private final CompanyRepository companyRepository;

    private final MapperJob mapperJob;

    public JobService(SkillService skillService, JobRepository jobRepository, CompanyRepository companyRepository, MapperJob mapperJob) {
        this.skillService = skillService;
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.mapperJob = mapperJob;
    }

    public ResCreateJobDTO handleSaveJob(Job job) {
        if (job.getSkills() != null) {

            List<Long> skills = job.getSkills()
                    .stream().map(s -> s.getId())
                    .collect(Collectors.toList());

            List<Skill> skillAvailible = this.skillService.getIdIn(skills);
            job.setSkills(skillAvailible);

        }

         if (job.getCompany() != null) {
             Optional<Company> comOptional = this.companyRepository.findById(job.getCompany().getId());
             if (comOptional.isPresent()) {
                job.setCompany(comOptional.get());
             } else {
                 job.setCompany(null);
             }
         }

        this.jobRepository.save(job);

         ResCreateJobDTO res = this.mapperJob.convertToResCreateJobDTO(job);

        return res;
    }

    public ResUpdateJobDTO handleUpdateJob(Job job, Job jobInDB) {
        if (job.getSkills() != null) {

            List<Long> skills = job.getSkills()
                    .stream().map(s -> s.getId())
                    .collect(Collectors.toList());

            List<Skill> skillAvailible = this.skillService.getIdIn(skills);

            jobInDB.setSkills(skillAvailible);

        }

        if (job.getCompany() != null) {
            Optional<Company> comOptional = this.companyRepository.findById(job.getCompany().getId());
            if (comOptional.isPresent()) {
                jobInDB.setCompany(comOptional.get());
            } else {
                job.setCompany(null);
            }
        }

        jobInDB.setId(job.getId());
        jobInDB.setName(job.getName());
        jobInDB.setSalary(job.getSalary());
        jobInDB.setQuantity(job.getQuantity());
        jobInDB.setLocation(job.getLocation());
        jobInDB.setLevel(job.getLevel());
        jobInDB.setStartDate(job.getStartDate());
        jobInDB.setEndDate(job.getEndDate());
        jobInDB.setActive(job.isActive());

        Job currentJob = this.jobRepository.save(jobInDB);

        ResUpdateJobDTO res = this.mapperJob.convertToResUpdateJobDTO(currentJob);
        return res;
    }

    public Optional<Job> findById(long id) {
        return this.jobRepository.findById(id);
    }

    public void handleDeleteJob(long id) {
        this.jobRepository.deleteById(id);
    }

    public boolean isExistJob(long id) {
        return this.jobRepository.existsById(id);
    }

    public Optional<Job> getJobById(Long id) {
        return this.jobRepository.findById(id);
    }

    public ResultPaginationDTO getAllJob(Specification<Job> spec, Pageable pageable) {
        Page<Job> jobPage = this.jobRepository.findAll(spec, pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(jobPage.getTotalPages());
        meta.setTotal(jobPage.getTotalElements());

        result.setResult(jobPage.getContent());
        result.setMeta(meta);

        return result;
    }

    public List<Job> findAllJob() {
        return this.jobRepository.findAll();
    }
}