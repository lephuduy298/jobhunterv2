package com.lephuduy.jobhunter.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.lephuduy.jobhunter.domain.Resume;
import com.lephuduy.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.lephuduy.jobhunter.domain.dto.response.resume.ResCreateResumeDTO;
import com.lephuduy.jobhunter.domain.dto.response.resume.ResFetchResumeDTO;
import com.lephuduy.jobhunter.domain.dto.response.resume.ResUpdateResumeDTO;
import com.lephuduy.jobhunter.repository.JobRepository;
import com.lephuduy.jobhunter.repository.ResumeRepository;
import com.lephuduy.jobhunter.repository.UserRepository;
import com.lephuduy.jobhunter.service.mapper.MapperResume;
import com.lephuduy.jobhunter.util.SecurityUtil;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

import jakarta.validation.Valid;

@Service
public class ResumeService {

    @Autowired
    private FilterParser filterParser;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;

    private final ResumeRepository resumeRepository;

    private final UserRepository userRepository;

    private final JobRepository jobRepository;

    private final MapperResume mapperResume;

    public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository,
                         JobRepository jobRepository, MapperResume mapperResume) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.mapperResume = mapperResume;
    }

    public void handleSave(Resume resume) {
        this.resumeRepository.save(resume);
    }


    public boolean isExistResume(long id) {
        return this.resumeRepository.existsById(id);
    }

    public Optional<Resume> findResumeById(long id) {
        return this.resumeRepository.findById(id);
    }

    public void handleUpdateResume(Resume updateResume) {
        this.resumeRepository.save(updateResume);
    }



    public void deleteResume(long id) {
        this.resumeRepository.deleteById(id);
    }


    public ResultPaginationDTO getAllResume(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> resumePage = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(resumePage.getNumber() + 1);
        meta.setPageSize(resumePage.getSize());
        meta.setPages(resumePage.getTotalPages());
        meta.setTotal(resumePage.getTotalElements());

        result.setMeta(meta);
        result.setResult(resumePage.stream()
                .map(res -> this.mapperResume.convertToFetchResumeDTO(res)).collect(Collectors.toList()).stream());

        return result;
    }

    public boolean checkValidUserAndJob(Resume resume) {
        if (resume.getUser() == null || resume.getJob() == null) {
            return false;
        }

        boolean existUser = this.userRepository.existsById(resume.getUser().getId());

        boolean existJob = this.jobRepository.existsById(resume.getJob().getId());

        if (!existUser || !existJob) {
            return false;
        }

        return true;
    }

    public ResultPaginationDTO getResumeByUser(Pageable pageable) {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        FilterNode node = filterParser.parse("email=" + "'" + email + "'");

        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageResume.getTotalPages());
        mt.setTotal(pageResume.getTotalElements());

        rs.setMeta(mt);

        List<ResFetchResumeDTO> res = pageResume.getContent()
                .stream().map(item -> this.mapperResume.convertToFetchResumeDTO(item))
                .collect(Collectors.toList());

        rs.setResult(res);

        return rs;
    }

}
