package com.acore.repo.repository;

import com.acore.repo.entity.PullRequestEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

/**
 * Pull Request Mapper
 *
 * @author acore
 */
@Mapper
public interface PullRequestMapper extends BaseMapper<PullRequestEntity> {

    default Optional<PullRequestEntity> findByRepoIdAndPrNumber(Long repositoryId, Integer prNumber) {
        return Optional.ofNullable(
                selectOne(new LambdaQueryWrapper<PullRequestEntity>()
                        .eq(PullRequestEntity::getRepositoryId, repositoryId)
                        .eq(PullRequestEntity::getPrNumber, prNumber)));
    }

    default List<PullRequestEntity> findByRepositoryId(Long repositoryId) {
        return selectList(new LambdaQueryWrapper<PullRequestEntity>()
                .eq(PullRequestEntity::getRepositoryId, repositoryId)
                .orderByDesc(PullRequestEntity::getCreatedAt));
    }
}