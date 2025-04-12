package com.lephuduy.jobhunter.controller;

import com.lephuduy.jobhunter.domain.dto.response.file.ResUploadFileDTO;
import com.lephuduy.jobhunter.service.FileService;
import com.lephuduy.jobhunter.util.anotaton.ApiMessage;
import com.lephuduy.jobhunter.util.error.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class FileController {

    @Value("${lephuduy.upload-file.base-uri}")
    private String baseUri;

    private final FileService fileService;

    public FileController (FileService fileService){
        this.fileService = fileService;
    }



    @PostMapping("/files")
    @ApiMessage("upload single file")
    public ResponseEntity<ResUploadFileDTO> uploadFile(
            @RequestParam(name = "file", required = false)MultipartFile file,
            @RequestParam("folder") String folder) throws URISyntaxException, StorageException {

        //validate ...
        if(file == null ||file.isEmpty()){
            throw new StorageException("file is empty. Please upload file to continue");
        }

        String fileOriginalFilename = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValidFile = allowedExtensions.stream().anyMatch(ext -> fileOriginalFilename.endsWith(ext));

        if(!isValidFile){
            throw new StorageException("Invalid file extension. Only allow " + allowedExtensions.toString());
        }

        //create directory
        this.fileService.createUploadFolder(baseUri + folder);

        //strore file
        String fileName = this.fileService.uploadFile(file, folder);

        ResUploadFileDTO res = new ResUploadFileDTO(fileName, Instant.now());

        return ResponseEntity.ok().body(res);

    }

    @GetMapping("/files")
    @ApiMessage("download a file")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam(name = "fileName", required = false) String fileName,
            @RequestParam(name = "folder", required = false) String folder
    ) throws IOException, StorageException, URISyntaxException {

        if(fileName == null || folder == null){
            throw new StorageException("Missing file or folder.");
        }

        long fileLength = this.fileService.getFileLength(fileName, folder);

        if(fileLength == 0){
            throw new StorageException("File with name = " + fileName + " not found");
        }

        InputStreamResource resource = this.fileService.getResource(fileName, folder);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
