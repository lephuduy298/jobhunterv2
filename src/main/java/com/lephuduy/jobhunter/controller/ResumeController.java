package com.lephuduy.jobhunter.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.lephuduy.jobhunter.domain.Company;
import com.lephuduy.jobhunter.domain.Job;
import com.lephuduy.jobhunter.domain.Resume;
import com.lephuduy.jobhunter.domain.User;
import com.lephuduy.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.lephuduy.jobhunter.domain.dto.response.resume.ResCreateResumeDTO;
import com.lephuduy.jobhunter.domain.dto.response.resume.ResFetchResumeDTO;
import com.lephuduy.jobhunter.domain.dto.response.resume.ResUpdateResumeDTO;
import com.lephuduy.jobhunter.repository.ResumeRepository;
import com.lephuduy.jobhunter.service.JobService;
import com.lephuduy.jobhunter.service.ResumeService;
import com.lephuduy.jobhunter.service.UserService;
import com.lephuduy.jobhunter.service.mapper.MapperResume;
import com.lephuduy.jobhunter.util.SecurityUtil;
import com.lephuduy.jobhunter.util.anotaton.ApiMessage;
import com.lephuduy.jobhunter.util.error.IdInvalidException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
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
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.node.FilterNode;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    private final ResumeRepository resumeRepository;

    private final UserService userService;

    private final JobService jobService;

    private final ResumeService resumeService;

    private final FilterSpecificationConverter filterSpecificationConverter;

    private final FilterBuilder filterBuilder;

    private final MapperResume mapperResume;

    public ResumeController(UserService userService, JobService jobService, ResumeService resumeService,
                            ResumeRepository resumeRepository, FilterSpecificationConverter filterSpecificationConverter,
                            FilterBuilder filterBuilder, MapperResume mapperResume) {
        this.userService = userService;
        this.jobService = jobService;
        this.resumeService = resumeService;
        this.resumeRepository = resumeRepository;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
        this.mapperResume = mapperResume;
    }

    @PostMapping("/resumes")
    @ApiMessage("create a resume")
    public ResponseEntity<ResCreateResumeDTO> createResume(@Valid @RequestBody Resume resume)
            throws IdInvalidException {
        // check user
        boolean validResume = this.resumeService.checkValidUserAndJob(resume);
        if (!validResume) {
            throw new IdInvalidException("job or user is not exist. Please try again");
        }

        this.resumeService.handleSave(resume);
        return ResponseEntity.created(null).body(this.mapperResume.convertToResCreateResumeDTO(resume));
    }

    @PutMapping("/resumes")
    @ApiMessage("update a resume")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume resume)
            throws IdInvalidException {
        // check resume
        boolean existResume = this.resumeService.isExistResume(resume.getId());
        if (!existResume) {
            throw new IdInvalidException("Không tồn tại resume với id=" + resume.getId());
        }

        Optional<Resume> resumeOptional = this.resumeService.findResumeById(resume.getId());

        if (resumeOptional.isPresent()) {
            Resume updateResume = resumeOptional.get();
            updateResume.setStatus(resume.getStatus());
            this.resumeService.handleUpdateResume(updateResume);
            return ResponseEntity.ok().body(this.mapperResume.convertToResUpdateResumeDTO(resume));
        }
        return null;
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("delete a resume")
    public void deleteResume(@PathVariable("id") long id)
            throws IdInvalidException {
        // check resume
        boolean existResume = this.resumeService.isExistResume(id);
        if (!existResume) {
            throw new IdInvalidException("Không tồn tại resume với id=" + id);
        }

        this.resumeService.deleteResume(id);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("fetch a resume")
    public ResponseEntity<ResFetchResumeDTO> fetchResume(@PathVariable("id") long id)
            throws IdInvalidException {
        // check resume
        boolean existResume = this.resumeService.isExistResume(id);
        if (!existResume) {
            throw new IdInvalidException("Không tồn tại resume với id=" + id);
        }

        Optional<Resume> resumeOptional = this.resumeService.findResumeById(id);

        if (resumeOptional.isPresent()) {
            Resume resume = resumeOptional.get();
            return ResponseEntity.ok().body(this.mapperResume.convertToFetchResumeDTO(resume));
        }
        return null;
    }

    @GetMapping("/resumes")
    @ApiMessage("fetch all resume")
    public ResponseEntity<ResultPaginationDTO> fetchAllResumeByCompany(
            @Filter Specification<Resume> spec,
            Pageable pageable)
            throws IdInvalidException {
        // fetch all user;
        List<Long> arrJobIds;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User currentUser = this.userService.getUserByUserName(email);
        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && companyJobs.size() > 0) {
                    arrJobIds = companyJobs.stream().map(x -> x.getId())
                            .collect(Collectors.toList());
                } else {
                    arrJobIds = null;
                }
            } else {
                arrJobIds = null;
            }
        } else {
            arrJobIds = null;
        }

//        Specification<Resume> jobInSpec = filterSpecificationConverter.convert(filterBuilder.field("job")
//                .in(filterBuilder.input(arrJobIds)).get());

        Specification<Resume> jobInSpec = (root, query, cb) -> {
//            if (arrJobIds.isEmpty()) {
//                return cb.isFalse(cb.literal(true));
//            }
            return root.get("job").get("id").in(arrJobIds); // Điều chỉnh "job" theo tên field thực tế
        };

        Specification<Resume> finalSpec = jobInSpec.and(spec);

        return ResponseEntity.ok().body(this.resumeService.getAllResume(finalSpec, pageable));

//        FilterNode filterNode = filterBuilder.field("job")
//                .in(filterBuilder.input(listJobIds)).get();
//
//        FilterSpecification<Resume> filterSpec = filterSpecificationConverter.convert(filterNode);
//
//        Specification<Resume> finalSpec = filterSpec.and(spec);
//
//        return ResponseEntity.ok().body(this.resumeService.getAllResume(finalSpec, pageable));
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("get list of resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchAllResumeByUser(
            Pageable pageable) {
        // fetch all user;
        return ResponseEntity.ok().body(this.resumeService.getResumeByUser(pageable));
    }

}
