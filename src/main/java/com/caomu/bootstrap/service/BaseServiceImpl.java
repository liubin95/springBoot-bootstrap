package com.caomu.bootstrap.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caomu.bootstrap.domain.BaseEntity;
import com.caomu.bootstrap.domain.Page;
import com.caomu.bootstrap.token.TokenUtil;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;

/**
 * 统一的service；更新和保存之前，保存用户信息
 *
 * @author 刘斌
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseEntity> extends ServiceImpl<M, T> implements BaseService<T> {

    @Resource
    private TokenUtil<BaseEntity> baseEntityTokenUtil;


    @Override
    public boolean save(T entity) {

        entity.setUpdaterId(baseEntityTokenUtil.userIdFromSecurity());
        entity.setCreatorId(baseEntityTokenUtil.userIdFromSecurity());
        return super.save(entity);
    }

    @Override
    public boolean saveBatch(Collection<T> entityList,
                             int batchSize) {

        entityList.forEach(item -> {
            item.setUpdaterId(baseEntityTokenUtil.userIdFromSecurity());
            item.setCreatorId(baseEntityTokenUtil.userIdFromSecurity());
        });
        return super.saveBatch(entityList, batchSize);
    }

    @Override
    public boolean saveOrUpdate(T entity) {

        entity.setUpdaterId(baseEntityTokenUtil.userIdFromSecurity());
        if (entity.getId() == null) {
            entity.setCreatorId(baseEntityTokenUtil.userIdFromSecurity());
        }
        return super.saveOrUpdate(entity);
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList,
                                     int batchSize) {

        entityList.forEach(item -> {
            item.setUpdaterId(baseEntityTokenUtil.userIdFromSecurity());
            if (item.getId() == null) {
                item.setCreatorId(baseEntityTokenUtil.userIdFromSecurity());
            }
        });
        return super.saveOrUpdateBatch(entityList, batchSize);
    }

    @Override
    public boolean updateBatchById(Collection<T> entityList,
                                   int batchSize) {

        entityList.forEach(item -> item.setUpdaterId(baseEntityTokenUtil.userIdFromSecurity()));
        return super.updateBatchById(entityList, batchSize);
    }

    @Override
    public boolean updateById(T entity) {

        entity.setUpdaterId(baseEntityTokenUtil.userIdFromSecurity());
        return super.updateById(entity);
    }

    @Override
    public IPage<T> pageAndSearchAndFilter(Page<T> page) {
        /* page参数；orders 排序字段;searchMap 模糊查询字段和值,filterMap过滤，全等筛选
            {
                "current": 1,
                "size": 5,
                "searchMap": {
                    "player_id_number": "12",
                    "player_name": "好"
                },
                "filterMap":{
                    "player_id_number":"123456789"
                }
                "orders": [
                    {
                        "column": "player_id_number",
                        "asc": true
                    }
                ]
            }
         */
        final QueryWrapper<T>     wrapper   = new QueryWrapper<>();
        final Map<String, String> searchMap = page.getSearchMap();
        searchMap.forEach((key, value) -> {
            if (StringUtils.isNotBlank(value)) {
                wrapper.likeRight(key, value);
            }
        });
        final Map<String, String> filterMap = page.getFilterMap();
        filterMap.forEach((key, value) -> {
            if (StringUtils.isNotBlank(value)) {
                wrapper.eq(key, value);
            }
        });
        return this.page(page, wrapper);
    }

}
