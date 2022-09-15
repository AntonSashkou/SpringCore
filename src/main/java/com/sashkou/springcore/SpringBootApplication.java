package com.sashkou.springcore;

import com.sashkou.springcore.factory.json.ClassPathJsonApplicationContext;
import com.sashkou.springcore.service.Service;
import org.springframework.context.ApplicationContext;

public class SpringBootApplication {

    public static void main(String[] args) {
        //ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        ApplicationContext context = new ClassPathJsonApplicationContext("spring.json");
        Service service = context.getBean("service", Service.class);
        service.serve();
    }
}
