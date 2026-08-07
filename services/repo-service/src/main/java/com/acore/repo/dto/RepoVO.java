package com.acore.repo.dto;

import com.acore.repo.entity.RepositoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 仓库视图对象
 *
 * @author acore
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepoVO {

    private Long id;
    private Long userId;
    private String platform;
    private String repoName;
    private String repoUrl;
    private Integer isActive;
    private LocalDateTime createdAt;

    public static RepoVO fromEntity(RepositoryEntity entity) {
        return RepoVO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .platform(entity.getPlatform())
                .repoName(entity.getRepoName())
                .repoUrl(entity.getRepoUrl())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}