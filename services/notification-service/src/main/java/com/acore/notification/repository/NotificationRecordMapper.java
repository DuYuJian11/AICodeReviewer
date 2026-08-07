package com.acore.notification.repository;

import com.acore.notification.entity.NotificationRecordEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 通知记录 Mapper
 *
 * @author acore
 */
@Mapper
public interface NotificationRecordMapper extends BaseMapper<NotificationRecordEntity> {

    default List<NotificationRecordEntity> findByPullRequestId(Long pullRequestId) {
        return selectList(new LambdaQueryWrapper<NotificationRecordEntity>()
                .eq(NotificationRecordEntity::getPullRequestId, pullRequestId)
                .orderByDesc(NotificationRecordEntity::getCreatedAt));
    }
}