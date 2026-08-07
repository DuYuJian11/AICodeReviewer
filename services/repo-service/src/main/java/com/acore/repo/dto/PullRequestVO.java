package com.acore.repo.dto;

import com.acore.repo.entity.PullRequestEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * PR/MR 视图对象
 *
 * @author acore
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PullRequestVO {

    private Long id;
    private Long repositoryId;
    private Integer prNumber;
    private String title;
    private String description;
    private String branchFrom;
    private String branchTo;
    private String commitSha;
    private String author;
    private String status;
    private LocalDateTime createdAt;

    public static PullRequestVO fromEntity(PullRequestEntity entity) {
        return PullRequestVO.builder()
                .id(entity.getId())
                .repositoryId(entity.getRepositoryId())
                .prNumber(entity.getPrNumber())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .branchFrom(entity.getBranchFrom())
                .branchTo(entity.getBranchTo())
                .commitSha(entity.getCommitSha())
                .author(entity.getAuthor())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}