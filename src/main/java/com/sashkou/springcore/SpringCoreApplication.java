package com.sashkou.springcore;

import com.sashkou.springcore.factory.json.ClassPathJsonApplicationContext;
import com.sashkou.springcore.service.Service;
import org.springframework.context.ApplicationContext;

public class Application {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathJsonApplicationContext("spring.json");
        String[] beanNamesForType = context.getBeanNamesForType(Service.class);
        Service service = context.getBean("service", Service.class);
        service.serve();
    }
}
