# springBoot web 脚手架
## 使用方法
- 引入依赖
```xml
        <dependency>
            <groupId>com.caomu</groupId>
            <artifactId>bootstrap</artifactId>
            <version>0.0.1</version>
        </dependency>
```
- 启动类上添加注解`@CommonWeb`
## web统一设置
### 返回值的统一包装 
- 包装对象`com.caomu.bootstrap.domain.Result`
- 排除注解`@IgnoreFormattedReturn`
### 返回值和入参的转换
- LocalDate
- LocalTime
- LocalDateTime
### 全局异常的捕获
- `BusinessRuntimeException` 业务异常
- `ConstraintViolationException`和`MethodArgumentNotValidException` 参数校验的异常
- `Exception` 未捕获的其他异常
### 参数打印
包括请求的唯一ID
## token鉴权 jwt
### token拦截
- headers中的Key`token`
- `cao-mu:token-exclude-url:` 设置排除token拦截的路径
### token工具类`TokenUtil`
- 使用时，通过spring注入。设置泛型，传入泛型的class
```java
    @Configuration
    public class BeanConfig {
        @Bean
        public TokenUtil<DemoUser> demoUserTokenUtil() {
            return new TokenUtil<>(DemoUser.class);
        }
    }
```
- 可以单独设置token的秘钥和过期时间
- 生成token`generateToken`
- 解析token`resolveToken`
## 持久化框架 MySQL mybatis-plus
### mybatis-plus的全局配置
- 删除字段
- id类型
### 重写方法 填充创建和更新 ID
- save
- saveBatch
- saveOrUpdate
- saveOrUpdateBatch
- updateBatchById
- updateById
### 新增分页；排序；检索方法
- pageAndSearch
### 集成Druid
- 定时打印监控信息