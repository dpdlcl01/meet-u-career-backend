package com.highfive.meetu.domain.resume.personal.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeWriteRequestDTO {

    private ProfileDTO profile;
    private ResumeDTO resume;
    private List<ResumeContentDTO> resumeContents;

    // Multipart 전용
    private MultipartFile profileImage;
    private MultipartFile resumeFile;
    private List<MultipartFile> contentFiles;
}
