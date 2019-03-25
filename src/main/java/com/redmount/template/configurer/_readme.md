# 系统配置项
* ApplicationStartup
> 系统启动之后马上进行的操作,在这里写.目前执行的是将数据库中的业务异常读取到内存中

* AsyncConfigurer
> 异步配置类,包含总线程池等参数,提供异步方法支持.

* AuthenticationInterceptor
> 权限验证拦截器,实际是实现了请求的拦截接口,在preHandler方法中增加了验证身份的方法.

* FastJsonPropertyPreFilter
> FastJson的输出拦截器,目前将"created","updated",以"Pk"结尾的字段和值为"null"的实体排除在外,不向外输出.

* MybatisConfigurer
> MyBatis的配置器

* ScheduledTaskConfigurer
> 定时任务配置器,所有的定时任务可以在此类中进行配置,使用Spring的定时任务定义.

* Swagger2Configurer
> Swagger2的配置类

* WebMvcConfigurer
> SpringMVC的配置器

