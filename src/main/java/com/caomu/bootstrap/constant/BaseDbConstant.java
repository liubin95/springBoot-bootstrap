package com.caomu.bootstrap.constant;

/**
 * 所有表都会有的字段。
 * 子项目 继承 该接口，增加自己的字段名
 * 可以在service层使用，
 *
 * @author 刘斌
 */
public interface BaseDbConstant {

    String ID = "id";

    String DELETED = "deleted";

    String CREATOR_ID = "creator_id";

    String CREATE_TIME = "create_time";

    String UPDATER_ID = "updater_id";

    String UPDATE_TIME = "update_time";
}
