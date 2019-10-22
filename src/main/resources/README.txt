

请及时关注注意事项 README.txt
一,
    1.建表,表名规则
        T_xxx_NN
        T   业务固有前缀
        xxx 表名
        NN  个人工号

        前缀包含 I,S,P,C,M,F,T,O
        I 例如部门角色,用户
        S 系统类型,比如公告,日志,权限
        P 参数类型,比如字典,码表,前端反馈请求结果描述
        C 配置类型,比如定义一些配置参数,导入模版
        M 消息类型
        F 流程相关
        T 业务表
        O 其它类型

    2,基础字段
        主键:兼容String,Integer 两种类型,需要继承不同的基础类
            com.xajiusuo.jpa.config.BaseEntity, (ID 类型为 String需继承)
            com.xajiusuo.jpa.config.BaseIntEntity (ID 类型为 Integer需继承)
        基础字段
            protected Date createTime;          /** 创建时间 */
            protected Date lastModifyTime;      /** 最后更新时间 */
            protected Integer createUID;        /** 创建人 */
            protected Integer lastModifyUID;    /** 修改人 */
            protected String createName;        /** 创建人名称 */
            protected Boolean delFlag;          /** 删除标记 */