package com.acore.scanner.repository;

import com.acore.scanner.entity.ScanResultEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

/**
 * 扫描结果 Mapper
 *
 * @author acore
 */
@Mapper
public interface ScanResultMapper extends BaseMapper<ScanResultEntity> {

    default Optional<ScanResultEntity> findLatestByPullRequestId(Long pullRequestId) {
        return Optional.ofNullable(
                selectOne(new LambdaQueryWrapper<ScanResultEntity>()
                        .eq(ScanResultEntity::getPullRequestId, pullRequestId)
                        .orderByDesc(ScanResultEntity::getCreatedAt)
                        .last("LIMIT 1")));
    }

    default List<ScanResultEntity> findByPullRequestId(Long pullRequestId) {
        return selectList(new LambdaQueryWrapper<ScanResultEntity>()
                .eq(ScanResultEntity::getPullRequestId, pullRequestId)
                .orderByDesc(ScanResultEntity::getCreatedAt));
    }
}