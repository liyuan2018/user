package com.spark.ims.user.strategy;

import com.spark.ims.auth.strategy.ILoginStrategy;
import com.spark.ims.core.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 22:17
 */
@Component
public class LoginStrategyFactory implements InitializingBean,ApplicationListener<ContextRefreshedEvent> {

    private static Logger logger = LoggerFactory.getLogger(LoginStrategyFactory.class);

    private Map<String,ILoginStrategy> strategyMap = new HashMap<>();

    public ILoginStrategy getStrategy(String key){
        ILoginStrategy strategy = strategyMap.get(key);
        if(strategy==null){
            logger.error("cannot found suitable loginStrategy");
            throw new BusinessException("登录认证配置错误，未匹配到合适的登陆策略！");
        }
        return strategy;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        Map<String,ILoginStrategy> beans = contextRefreshedEvent.getApplicationContext().getBeansOfType(ILoginStrategy.class);
        for(Object bean:beans.values()){
            strategyMap.put(bean.getClass().getName(),(ILoginStrategy)bean);
            logger.info("init loginStrategy:" + bean.getClass().getName());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
