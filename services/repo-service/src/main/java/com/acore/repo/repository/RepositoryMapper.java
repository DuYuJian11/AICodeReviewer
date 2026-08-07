package com.acore.repo.repository;

import com.acore.repo.entity.RepositoryEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

/**
 * 仓库 Mapper
 *
 * @author acore
 */
@Mapper
public interface RepositoryMapper extends BaseMapper<RepositoryEntity> {

    default Optional<RepositoryEntity> findByIdAndUserId(Long id, Long userId) {
        return Optional.ofNullable(
                selectOne(new LambdaQueryWrapper<RepositoryEntity>()
                        .eq(RepositoryEntity::getId, id)
                        .eq(RepositoryEntity::getUserId, userId)));
    }

    default List<RepositoryEntity> findByUserId(Long userId) {
        return selectList(new LambdaQueryWrapper<RepositoryEntity>()
                .eq(RepositoryEntity::getUserId, userId)
                .orderByDesc(RepositoryEntity::getCreatedAt));
    }

    default boolean existsByRepoUrlAndUserId(String repoUrl, Long userId) {
        return selectCount(new LambdaQueryWrapper<RepositoryEntity>()
                .eq(RepositoryEntity::getRepoUrl, repoUrl)
                .eq(RepositoryEntity::getUserId, userId)) > 0;
    }
}