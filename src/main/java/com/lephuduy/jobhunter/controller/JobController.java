package com.lephuduy.jobhunter.controller;

import java.util.List;
import java.util.Optional;


import com.lephuduy.jobhunter.domain.Job;
import com.lephuduy.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.lephuduy.jobhunter.domain.dto.response.job.ResCreateJobDTO;
import com.lephuduy.jobhunter.domain.dto.response.job.ResUpdateJobDTO;
import com.lephuduy.jobhunter.service.JobService;
import com.lephuduy.jobhunter.util.anotaton.ApiMessage;
import com.lephuduy.jobhunter.util.error.IdInvalidException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class JobController {

    private final AuthController authController;

    private final JobService jobService;

    public JobController(JobService jobService, AuthController authController) {
        this.jobService = jobService;
        this.authController = authController;
    }

    @PostMapping("/jobs")
    @ApiMessage("create a job")
    public ResponseEntity<ResCreateJobDTO> createAJob(@Valid @RequestBody Job job) {
        ResCreateJobDTO resCreateJobDTO = this.jobService.handleSaveJob(job);
        return ResponseEntity.created(null).body(resCreateJobDTO);
    }

    @PutMapping("/jobs")
    @ApiMessage("update a job")
    public ResponseEntity<ResUpdateJobDTO> updateJob(@Valid @RequestBody Job job) throws IdInvalidException {
        boolean isExistJob = this.jobService.isExistJob(job.getId());
        if (!isExistJob) {
            throw new IdInvalidException("Job với id=" + job.getId() + " không tồn tại");
        }

        Optional<Job> jobDBOptional = this.jobService.findById(job.getId());
        return ResponseEntity.ok().body(this.jobService.handleUpdateJob(job, jobDBOptional.get()));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("delete a job")
    public ResponseEntity<Job> deleteAJob(@Valid @PathVariable("id") Long id) throws IdInvalidException {
        boolean isExistJob = this.jobService.isExistJob(id);
        if (!isExistJob) {
            throw new IdInvalidException("Job với id=" + id + " không tồn tại");
        }
        this.jobService.handleDeleteJob(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("get a job")
    public ResponseEntity<Job> getAJob(@Valid @PathVariable("id") Long id) throws IdInvalidException {
        boolean isExistJob = this.jobService.isExistJob(id);
        if (!isExistJob) {
            throw new IdInvalidException("Job với id=" + id + " không tồn tại");
        }
        Optional<Job> newJobOptional = this.jobService.getJobById(id);
        if (newJobOptional.isPresent()) {
            Job newJob = newJobOptional.get();
            return ResponseEntity.ok().body(newJob);
        }
        return null;
    }

    @GetMapping("/jobs")
    @ApiMessage("get all job")
    public ResponseEntity<ResultPaginationDTO> fetchAllJob(@Filter Specification<Job> spec,
                                                           Pageable pageable) {

        return ResponseEntity.ok().body(this.jobService.getAllJob(spec, pageable));
    }
}