package com.sashkou.springcore.reader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class JsonBeanDefinitionReader extends AbstractBeanDefinitionReader {

    private final Set<String> usedNames = new HashSet();

    public JsonBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
        try {
            String result = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
            String json = String.valueOf(result);
            JsonObject object = new Gson().fromJson(json, JsonObject.class);
            BeanDefinitionHolder beanDefinitionHolder = parseBeanDefinitionElement(object, null);
            BeanDefinitionReaderUtils.registerBeanDefinition(beanDefinitionHolder, this.getRegistry());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return 1;
    }

    @Nullable
    public BeanDefinitionHolder parseBeanDefinitionElement(JsonObject ele, @Nullable BeanDefinition containingBean) {
        String id = ele.get("id").getAsString();
        String nameAttr = ele.get("name").getAsString();
        List<String> aliases = new ArrayList();
        if (StringUtils.hasLength(nameAttr)) {
            String[] nameArr = StringUtils.tokenizeToStringArray(nameAttr, ",; ");
            aliases.addAll(Arrays.asList(nameArr));
        }

        String beanName = id;
        if (!StringUtils.hasText(id) && !aliases.isEmpty()) {
            beanName = (String) aliases.remove(0);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No XML 'id' specified - using '" + beanName + "' as bean name and " + aliases + " as aliases");
            }
        }

        if (containingBean == null) {
            this.checkNameUniqueness(beanName, aliases, ele);
        }

        AbstractBeanDefinition beanDefinition = this.parseBeanDefinitionElement(ele);
        if (beanDefinition != null) {
            if (!StringUtils.hasText(beanName)) {
                try {
                    if (containingBean != null) {
                        beanName = BeanDefinitionReaderUtils.generateBeanName(beanDefinition, this.getRegistry(), true);
                    } else {
                        beanName = "json_" + (beanDefinition);
                        String beanClassName = beanDefinition.getBeanClassName();
                        if (beanClassName != null && beanName.startsWith(beanClassName) && beanName.length() > beanClassName.length() && !this.getRegistry().isBeanNameInUse(beanClassName)) {
                            aliases.add(beanClassName);
                        }
                    }

                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace("Neither XML 'id' nor 'name' specified - using generated bean name [" + beanName + "]");
                    }
                } catch (Exception var9) {
                    //this.error(var9.getMessage(), ele);
                    return null;
                }
            }

            String[] aliasesArray = StringUtils.toStringArray(aliases);
            return new BeanDefinitionHolder(beanDefinition, beanName, aliasesArray);
        } else {
            return null;
        }
    }

    protected void checkNameUniqueness(String beanName, List<String> aliases, JsonObject beanElement) {
        String foundName = null;
        if (StringUtils.hasText(beanName) && this.usedNames.contains(beanName)) {
            foundName = beanName;
        }

        if (foundName == null) {
            foundName = (String) CollectionUtils.findFirstMatch(this.usedNames, aliases);
        }

        if (foundName != null) {
            //this.error("Bean name '" + foundName + "' is already used in this <beans> element", beanElement);
        }

        this.usedNames.add(beanName);
        this.usedNames.addAll(aliases);
    }

    public AbstractBeanDefinition parseBeanDefinitionElement(JsonObject ele) {
        String className = null;
        if (ele.get("class") != null) {
            className = ele.get("class").getAsString().trim();
        }

        try {
            return this.createBeanDefinition(className);
        } catch (Exception e) {
            return null;
        }
    }

    protected AbstractBeanDefinition createBeanDefinition(@Nullable String className) throws ClassNotFoundException {
        return BeanDefinitionReaderUtils.createBeanDefinition(null, className, null);
    }

}
