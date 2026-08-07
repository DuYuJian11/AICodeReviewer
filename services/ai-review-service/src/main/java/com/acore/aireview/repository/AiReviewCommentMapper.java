package com.acore.aireview.repository;

import com.acore.aireview.entity.AiReviewCommentEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI 审查评论 Mapper
 *
 * @author acore
 */
@Mapper
public interface AiReviewCommentMapper extends BaseMapper<AiReviewCommentEntity> {

    default List<AiReviewCommentEntity> findByPullRequestId(Long pullRequestId) {
        return selectList(new LambdaQueryWrapper<AiReviewCommentEntity>()
                .eq(AiReviewCommentEntity::getPullRequestId, pullRequestId)
                .orderByDesc(AiReviewCommentEntity::getCreatedAt));
    }

    default List<AiReviewCommentEntity> findByPullRequestIdAndFile(Long pullRequestId, String filePath) {
        return selectList(new LambdaQueryWrapper<AiReviewCommentEntity>()
                .eq(AiReviewCommentEntity::getPullRequestId, pullRequestId)
                .eq(AiReviewCommentEntity::getFilePath, filePath));
    }

    default long countByPullRequestId(Long pullRequestId) {
        return selectCount(new LambdaQueryWrapper<AiReviewCommentEntity>()
                .eq(AiReviewCommentEntity::getPullRequestId, pullRequestId));
    }
}