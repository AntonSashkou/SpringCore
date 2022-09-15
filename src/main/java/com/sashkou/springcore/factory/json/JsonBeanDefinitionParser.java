package com.sashkou.springcore.factory.json;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
public class JsonBeanDefinitionParser {

    private static final String CLASS_ATTRIBUTE = "class";
    private static final String ID_ATTRIBUTE = "id";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String ID_PREFIX = "json_";

    private final JsonBeanDefinitionDelegate delegate = new JsonBeanDefinitionDelegate();
    private final Set<String> usedNames = new HashSet<>();

    public BeanDefinitionHolder parseBeanDefinitionElement(JsonObject ele, @Nullable BeanDefinition containingBean, BeanDefinitionRegistry registry) {
        String id = ele.get(ID_ATTRIBUTE).getAsString();
        String nameAttr = ele.get(NAME_ATTRIBUTE).getAsString();
        List<String> aliases = new ArrayList<>();
        if (StringUtils.hasLength(nameAttr)) {
            String[] nameArr = StringUtils.tokenizeToStringArray(nameAttr, ",; ");
            aliases.addAll(Arrays.asList(nameArr));
        }

        String beanName = id;
        if (!StringUtils.hasText(id) && !aliases.isEmpty()) {
            beanName = aliases.remove(0);
            log.warn("No json 'id' specified - using '" + beanName + "' as bean name and " + aliases + " as aliases");
        }

        if (containingBean == null) {
            this.checkNameUniqueness(beanName, aliases, ele);
        }

        AbstractBeanDefinition beanDefinition = this.parseBeanDefinitionElement(ele);
        if (beanDefinition != null) {
            if (!StringUtils.hasText(beanName)) {
                try {
                    if (containingBean != null) {
                        beanName = delegate.generateBeanName(beanDefinition, registry);
                    } else {
                        beanName = ID_PREFIX + (beanDefinition);
                        String beanClassName = beanDefinition.getBeanClassName();
                        if (beanClassName != null && beanName.startsWith(beanClassName) && beanName.length() > beanClassName.length() && !registry.isBeanNameInUse(beanClassName)) {
                            aliases.add(beanClassName);
                        }
                    }

                    log.error("Neither json 'id' nor 'name' specified - using generated bean name [" + beanName + "]");
                } catch (Exception e) {
                    throw new BeanCreationException("Can't create bean");
                }
            }

            String[] aliasesArray = StringUtils.toStringArray(aliases);
            return new BeanDefinitionHolder(beanDefinition, beanName, aliasesArray);
        } else {
            throw new BeanCreationException("Can't create bean");
        }
    }

    private void checkNameUniqueness(String beanName, List<String> aliases, JsonObject beanElement) {
        String foundName = null;
        if (StringUtils.hasText(beanName) && this.usedNames.contains(beanName)) {
            foundName = beanName;
        }

        if (foundName == null) {
            foundName = CollectionUtils.findFirstMatch(this.usedNames, aliases);
        }

        if (foundName != null) {
            log.warn("Bean name '" + foundName + "' is already used in this <beans> element");
        }

        this.usedNames.add(beanName);
        this.usedNames.addAll(aliases);
    }

    private AbstractBeanDefinition parseBeanDefinitionElement(JsonObject ele) {
        String className = null;
        if (ele.get(CLASS_ATTRIBUTE) != null) {
            className = ele.get("class").getAsString().trim();
        }

        try {
            return delegate.createBeanDefinition(className);
        } catch (Exception e) {
            throw new BeanCreationException("Can't create bean");
        }
    }
}
