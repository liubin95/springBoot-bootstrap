package com.caomu.bootstrap.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义分页
 *
 * @author 刘斌
 */
public class Page<T> extends com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> {

    /**
     * 模糊查询Map，Key为列名，value为值
     */
    private Map<String, String> searchMap = new HashMap<>();

    public Map<String, String> getSearchMap() {
        return searchMap;
    }

    public void setSearchMap(Map<String, String> searchMap) {
        this.searchMap = searchMap;
    }
}
