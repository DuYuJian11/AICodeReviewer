package com.acore.report.repository;

import com.acore.report.entity.ReviewReport;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

/**
 * 报告 Mapper
 *
 * @author acore
 */
@Mapper
public interface ReportMapper extends BaseMapper<ReviewReport> {

    default Optional<ReviewReport> findByPullRequestId(Long pullRequestId) {
        return Optional.ofNullable(
                selectOne(new LambdaQueryWrapper<ReviewReport>()
                        .eq(ReviewReport::getPullRequestId, pullRequestId)
                        .orderByDesc(ReviewReport::getCreatedAt)
                        .last("LIMIT 1")));
    }

    default List<ReviewReport> listByUserId(Long userId) {
        // 实际项目会关联查询，此处简化
        return selectList(new LambdaQueryWrapper<ReviewReport>()
                .orderByDesc(ReviewReport::getCreatedAt));
    }
}