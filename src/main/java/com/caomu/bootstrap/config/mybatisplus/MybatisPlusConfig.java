package com.caomu.bootstrap.config.mybatisplus;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.caomu.bootstrap.domain.BaseEntity;

/**
 * MybatisPlus 配置类
 *
 * @author 刘斌
 */
@Configuration
public class MybatisPlusConfig implements MybatisPlusPropertiesCustomizer {

    @Resource
    private IdentifierGenerator idGenerator;


    @Override
    public void customize(MybatisPlusProperties properties) {
        final GlobalConfig globalConfig = new GlobalConfig();
        final GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();

        globalConfig.setBanner(false);
        globalConfig.setIdentifierGenerator(idGenerator);

        dbConfig.setLogicDeleteField(BaseEntity.DELETED);
        dbConfig.setIdType(IdType.ASSIGN_ID);

        globalConfig.setDbConfig(dbConfig);
        properties.setGlobalConfig(globalConfig);
    }
}
