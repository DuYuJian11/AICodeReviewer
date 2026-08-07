package com.acore.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页响应体
 *
 * @param <T> 数据类型
 * @author acore
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 数据列表 */
    @Builder.Default
    private List<T> records = Collections.emptyList();

    /** 当前页码 */
    private long page;

    /** 每页条数 */
    private long pageSize;

    /** 总条数 */
    private long total;

    /** 总页数 */
    private long pages;

    /**
     * 构建分页结果
     *
     * @param records  数据列表
     * @param page     当前页码
     * @param pageSize 每页条数
     * @param total    总条数
     * @param <T>      数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(List<T> records, long page, long pageSize, long total) {
        long pages = pageSize > 0 ? (total + pageSize - 1) / pageSize : 0;
        return PageResult.<T>builder()
                .records(records)
                .page(page)
                .pageSize(pageSize)
                .total(total)
                .pages(pages)
                .build();
    }

    /**
     * 返回空分页
     *
     * @param <T> 数据类型
     * @return 空分页
     */
    public static <T> PageResult<T> empty() {
        return PageResult.<T>builder()
                .records(Collections.emptyList())
                .page(1)
                .pageSize(20)
                .total(0)
                .pages(0)
                .build();
    }
}