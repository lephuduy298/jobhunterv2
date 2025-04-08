package com.lephuduy.jobhunter.service.mapper;

import com.lephuduy.jobhunter.domain.Job;
import com.lephuduy.jobhunter.domain.dto.response.job.ResCreateJobDTO;
import com.lephuduy.jobhunter.domain.dto.response.job.ResUpdateJobDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MapperJob {
    public ResCreateJobDTO convertToResCreateJobDTO(Job job){
        ResCreateJobDTO res = new ResCreateJobDTO();
        res.setId(job.getId());
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setQuantity(job.getQuantity());
        res.setLocation(job.getLocation());
        res.setLevel(job.getLevel());
        res.setStartDate(job.getStartDate());
        res.setEndDate(job.getEndDate());
        res.setActive(job.isActive());
        res.setCreatedAt(job.getCreatedAt());
        res.setCreatedBy(job.getCreatedBy());

        if (job.getSkills() != null) {
            List<String> skills = job.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());

            res.setSkills(skills);
        }
        return res;
    }

    public ResUpdateJobDTO convertToResUpdateJobDTO(Job currentJob){
        ResUpdateJobDTO res = new ResUpdateJobDTO();
        // convert response
        res.setId(currentJob.getId());
        res.setName(currentJob.getName());
        res.setSalary(currentJob.getSalary());
        res.setQuantity(currentJob.getQuantity());
        res.setLocation(currentJob.getLocation());
        res.setLevel(currentJob.getLevel());
        res.setStartDate(currentJob.getStartDate());
        res.setEndDate(currentJob.getEndDate());
        res.setActive(currentJob.isActive());
        res.setUpdatedAt(currentJob.getUpdatedAt());
        res.setUpdatedBy(currentJob.getUpdatedBy());
        
        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            res.setSkills(skills);
        }
        
        return res;
    }
}
