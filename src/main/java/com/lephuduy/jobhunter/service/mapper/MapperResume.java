package com.lephuduy.jobhunter.service.mapper;

import com.lephuduy.jobhunter.domain.Resume;
import com.lephuduy.jobhunter.domain.dto.response.resume.ResCreateResumeDTO;
import com.lephuduy.jobhunter.domain.dto.response.resume.ResFetchResumeDTO;
import com.lephuduy.jobhunter.domain.dto.response.resume.ResUpdateResumeDTO;
import com.lephuduy.jobhunter.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class MapperResume {

    public ResFetchResumeDTO convertToFetchResumeDTO(Resume resume) {
        ResFetchResumeDTO res = new ResFetchResumeDTO();
        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setStatus(resume.getStatus());
        res.setUrl(resume.getUrl());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());

        if (resume.getJob() != null) {
            res.setCompanyName(resume.getJob().getCompany().getName());
        }

        ResFetchResumeDTO.ResumeUser resUser = new ResFetchResumeDTO.ResumeUser();
        resUser.setId(resume.getUser().getId());
        resUser.setName(resume.getUser().getName());
        res.setUser(resUser);

        ResFetchResumeDTO.ResumeJob resJob = new ResFetchResumeDTO.ResumeJob();
        resJob.setId(resume.getJob().getId());
        resJob.setName(resume.getJob().getName());
        res.setJob(resJob);

        return res;
    }

    public ResCreateResumeDTO convertToResCreateResumeDTO(Resume resume) {
        ResCreateResumeDTO res = new ResCreateResumeDTO();
        res.setId(resume.getId());
        res.setCreateAt(resume.getCreatedAt());
        res.setCreateBy(resume.getCreatedBy());
        return res;
    }

    public ResUpdateResumeDTO convertToResUpdateResumeDTO(Resume resume) {
        ResUpdateResumeDTO res = new ResUpdateResumeDTO();
        res.setUpdatedAt(Instant.now());
        res.setUpdatedBy(SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                : "");
        return res;
    }

}
