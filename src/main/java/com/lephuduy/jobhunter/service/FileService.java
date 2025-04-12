package com.lephuduy.jobhunter.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileService {

    @Value("${lephuduy.upload-file.base-uri}")
    private String baseUri;

    public void createUploadFolder(String folder) throws URISyntaxException {
        URI uri = new URI(folder);
        Path path = Paths.get(uri);
        File tmpDir = new File(path.toString());

        if (!tmpDir.isDirectory()) {
            try {
                Files.createDirectory(tmpDir.toPath());
                System.out.println(">>> CREATE NEW DIRECTORY SUCCESSFUL, PATH = " + folder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(">>> SKIP MAKING DIRECTORY, ALREADY EXISTS");
        }

//        Path path = Paths.get(folder);

//        File path = new File(folder);
//
//        if (!path.exists()) {
//            try {
//                Files.createDirectory(Paths.get(folder));
//                System.out.println(">>> CREATE NEW DIRECTORY SUCCESSFUL, PATH = " + folder);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println(">>> SKIP MAKING DIRECTORY, ALREADY EXISTS");
//        }
    }

    public String uploadFile(MultipartFile file, String folder) throws URISyntaxException {

        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();

        URI uri = new URI(baseUri + folder + "/" + fileName);
        Path path = Paths.get(uri);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fileName;
    }

    public long getFileLength(String file, String folder) throws URISyntaxException {
        URI uri = new URI(baseUri + folder + "/" + file);
        Path path = Paths.get(uri);
        File tmpFile = new File(path.toString());

        if(!tmpFile.exists() || tmpFile.isDirectory()){
            return 0;
        }

        return tmpFile.length();
    }

    public InputStreamResource getResource(String file, String folder) throws URISyntaxException, FileNotFoundException {
        URI uri = new URI(baseUri + folder + "/" + file);
        Path path = Paths.get(uri);
        File tmpFile = new File(path.toString());
        return new InputStreamResource(new FileInputStream(tmpFile));
    }
}
