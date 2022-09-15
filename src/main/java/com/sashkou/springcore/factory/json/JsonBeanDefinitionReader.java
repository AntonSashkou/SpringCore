package com.sashkou.springcore.factory.json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JsonBeanDefinitionReader extends AbstractBeanDefinitionReader {

    private final JsonBeanDefinitionParser parser = new JsonBeanDefinitionParser();
    private final JsonBeanDefinitionDelegate delegate = new JsonBeanDefinitionDelegate();

    public JsonBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
        try {
            String resourceContent = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
            JsonObject object = new Gson().fromJson(resourceContent, JsonObject.class);
            BeanDefinitionHolder beanDefinitionHolder = this.parser.parseBeanDefinitionElement(object, null, this.getRegistry());
            return delegate.registerBeanDefinition(beanDefinitionHolder, this.getRegistry());
        } catch (IOException e) {
            throw new BeanCreationException(e.getMessage());
        }
    }

}
