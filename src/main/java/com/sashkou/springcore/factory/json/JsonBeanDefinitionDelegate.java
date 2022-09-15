package com.sashkou.springcore.factory.json;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.lang.Nullable;

public class JsonBeanDefinitionDelegate {

    public AbstractBeanDefinition createBeanDefinition(@Nullable String className) throws ClassNotFoundException {
        return BeanDefinitionReaderUtils.createBeanDefinition(null, className, null);
    }


    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        return BeanDefinitionReaderUtils.generateBeanName(definition, registry, true);
    }

    public int registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
        int countBefore = registry.getBeanDefinitionCount();
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
        return registry.getBeanDefinitionCount() - countBefore;
    }
}
