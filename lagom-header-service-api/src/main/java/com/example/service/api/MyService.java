package com.example.service.api;

import akka.NotUsed;
import com.example.service.model.ColorData;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import static com.lightbend.lagom.javadsl.api.Service.named;

public interface MyService extends Service {

    ServiceCall<NotUsed, ColorData> readServiceCall(String color);
    ServiceCall<NotUsed, ColorData> readServerServiceCall(String color);
    ServiceCall<NotUsed, ColorData> readHeaderServiceCall(String color);

    @Override
    default Descriptor descriptor() {
        return named("myservice").withCalls(
                Service.restCall(Method.GET, "/read/data/serviceCall?color", this::readServiceCall),
                Service.restCall(Method.GET, "/read/data/serverServiceCall?color", this::readServerServiceCall),
                Service.restCall(Method.GET, "/read/data/headerServiceCall?color", this::readHeaderServiceCall)
        ).withAutoAcl(true);
    }
}
