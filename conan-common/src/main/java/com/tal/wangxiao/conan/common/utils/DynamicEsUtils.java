package com.tal.wangxiao.conan.common.utils;

import com.tal.wangxiao.conan.common.SpringContextHolder;
import com.tal.wangxiao.conan.common.config.DynamicEsClientConfig;
import com.tal.wangxiao.conan.config.core.utils.StringUtils;
import com.tal.wangxiao.conan.utils.str.StringHandlerUtils;
import com.tal.wangxiao.conan.utils.str.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author: dengkunnan
 * @date: 2021-01-04 16:00
 * @description:
 **/

@Slf4j
public class DynamicEsUtils {

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static RestHighLevelClient getRestHighLevelClient(String restHighLevelClientBeanName, String host, Integer port, String esUser, String esPasswd) {
        DynamicEsClientConfig dynamicEsClient;
        try {
            Object objBean = SpringContextHolder.getBean(restHighLevelClientBeanName);
            if (!StringHandlerUtils.isNull(objBean)) {
                dynamicEsClient = (DynamicEsClientConfig) objBean;
                return dynamicEsClient.getRestHighLevelClient();
            }
        } catch (Exception e) {
            log.error("e= {}", e);
        }
        ConfigurableApplicationContext
                configurableApplicationContext = (ConfigurableApplicationContext) SpringContextHolder.getApplicationContext();
        // 获取bean工厂并转换为DefaultListableBeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
        // 通过BeanDefinitionBuilder创建bean定义
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DynamicEsClientConfig.class);
        // 注册bean
        defaultListableBeanFactory.registerBeanDefinition(restHighLevelClientBeanName, beanDefinitionBuilder.getRawBeanDefinition());
        try {
            Object objBean = SpringContextHolder.getBean(restHighLevelClientBeanName);
            if (!StringHandlerUtils.isNull(objBean)) {
                dynamicEsClient = (DynamicEsClientConfig) objBean;
                if (StringUtils.isEmpty(esUser) && StringUtils.isEmpty(esPasswd)) {
                    return dynamicEsClient.getRestHighLevelClient(host, port);
                }
                return dynamicEsClient.getRestHighLevelClient(host, port, esUser, esPasswd);
            }
        } catch (Exception e) {
            log.error("e= {}", e);
        }
        return null;
    }


}
