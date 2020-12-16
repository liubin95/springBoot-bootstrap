package com.caomu.bootstrap.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caomu.bootstrap.domain.Page;

/**
 * 通用拓展service
 *
 * @author 刘斌
 */
public interface BaseService<T> extends IService<T> {

    /**
     * 分页；排序；搜索
     *
     * @param page 分页对象
     * @return 分页对象
     */
    IPage<T> pageAndSearchAndFilter(Page<T> page);

}
