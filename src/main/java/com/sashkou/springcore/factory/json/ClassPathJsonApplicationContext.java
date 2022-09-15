package com.sashkou.springcore.context;

import com.sashkou.springcore.reader.JsonBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;

public class ClassPathJsonApplicationContext extends GenericApplicationContext {

    private final JsonBeanDefinitionReader reader = new JsonBeanDefinitionReader(this);

    public ClassPathJsonApplicationContext(String... resourceLocations) {
        this.load(resourceLocations);
        this.refresh();
    }

    public void load(String... resourceLocations) {
        this.reader.loadBeanDefinitions(resourceLocations);
    }
}
