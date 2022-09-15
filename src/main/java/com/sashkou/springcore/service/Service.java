package com.sashkou.springcore.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Service {

    public Service() {
        log.info("initiating...");
    }

    public void serve() {
        log.info("serving...");
    }
}
