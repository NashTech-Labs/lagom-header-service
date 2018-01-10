package com.example.service.module;

import com.example.service.api.MyService;
import com.google.inject.AbstractModule;
import com.example.service.impl.MyServiceImpl;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

public class MyServiceModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        bindService(MyService.class, MyServiceImpl.class);
    }
}
