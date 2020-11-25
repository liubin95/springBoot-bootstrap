package com.caomu.bootstrap.service;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caomu.bootstrap.constant.CommonConstant;
import com.caomu.bootstrap.domain.BaseEntity;
import com.caomu.bootstrap.domain.Page;
import com.caomu.bootstrap.token.TokenUtil;

/**
 * 统一的service；更新和保存之前，保存用户信息
 *
 * @author 刘斌
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseEntity> extends ServiceImpl<M, T> implements BaseService<T> {

    @Resource
    private TokenUtil<BaseEntity> baseEntityTokenUtil;


    /**
     * 获取request的token
     *
     * @return token
     */
    protected String getToken() {
        final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        final HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        return request.getHeader(CommonConstant.HEADER_TOKEN_KEY);
    }

    /**
     * 获取token中的用户
     *
     * @return token中的用户
     */
    private BaseEntity getTokenUser() {
        return baseEntityTokenUtil.resolveToken(getToken());
    }

    @Override
    public boolean save(T entity) {
        final BaseEntity tokenUser = getTokenUser();
        entity.setUpdaterId(tokenUser.getId());
        entity.setCreatorId(tokenUser.getId());
        return super.save(entity);
    }

    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        final BaseEntity tokenUser = getTokenUser();
        entityList.forEach(item -> {
            item.setUpdaterId(tokenUser.getId());
            item.setCreatorId(tokenUser.getId());
        });
        return super.saveBatch(entityList, batchSize);
    }

    @Override
    public boolean saveOrUpdate(T entity) {
        final BaseEntity tokenUser = getTokenUser();
        entity.setUpdaterId(tokenUser.getId());
        if (entity.getId() == null) {
            entity.setCreatorId(tokenUser.getId());
        }
        return super.saveOrUpdate(entity);
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        final BaseEntity tokenUser = getTokenUser();
        entityList.forEach(item -> {
            item.setUpdaterId(tokenUser.getId());
            if (item.getId() == null) {
                item.setCreatorId(tokenUser.getId());
            }
        });
        return super.saveOrUpdateBatch(entityList, batchSize);
    }

    @Override
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        final BaseEntity tokenUser = getTokenUser();
        entityList.forEach(item -> item.setUpdaterId(tokenUser.getId()));
        return super.updateBatchById(entityList, batchSize);
    }

    @Override
    public boolean updateById(T entity) {
        final BaseEntity tokenUser = getTokenUser();
        entity.setUpdaterId(tokenUser.getId());
        return super.updateById(entity);
    }

    @Override
    public IPage<T> pageAndSearch(Page<T> page) {
        /* page参数；orders 排序字段;searchMap 模糊查询字段和值
            {
                "current": 1,
                "size": 5,
                "searchMap": {
                    "player_id_number": "12",
                    "player_name": "好"
                },
                "orders": [
                    {
                        "column": "player_id_number",
                        "asc": true
                    }
                ]
            }
         */
        final QueryWrapper<T> wrapper = new QueryWrapper<>();
        final Map<String, String> searchMap = page.getSearchMap();
        searchMap.forEach((key, value) -> {
            if (StringUtils.isNotBlank(value)) {
                wrapper.likeRight(key, value);
            }
        });
        return this.page(page, wrapper);
    }

}
