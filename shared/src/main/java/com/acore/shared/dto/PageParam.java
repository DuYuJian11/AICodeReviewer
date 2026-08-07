package com.acore.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 分页请求参数
 *
 * @author acore
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 当前页码 */
    @Builder.Default
    private long page = 1;

    /** 每页条数 */
    @Builder.Default
    private long pageSize = 20;
}