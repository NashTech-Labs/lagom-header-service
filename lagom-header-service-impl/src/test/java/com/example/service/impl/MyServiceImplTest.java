package com.example.service.impl;

import akka.NotUsed;
import akka.japi.Pair;
import com.example.service.model.ColorData;
import com.lightbend.lagom.javadsl.api.transport.RequestHeader;
import com.lightbend.lagom.javadsl.api.transport.ResponseHeader;
import mockit.Tested;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;

public class MyServiceImplTest {
    private static final String AUTHORIZATION_KEY = "authorization_key";
    private static final String AUTHORIZATION_VALUE = "7208015cfb39d49fd39b1339f4627281";

    @Tested
    MyServiceImpl myService;

    private static final String COLOR = "ORANGE";
    private static final String COLOR_CODE = "#FF5500";
    private static final String RGB_VALUES = "255,85,0";
    @Test
    public void serviceCallTest() throws Exception {
        ColorData colorData = myService.readServiceCall(COLOR).invoke()
                .toCompletableFuture().get(10, TimeUnit.SECONDS);

        assertEquals("color code does not match", COLOR_CODE, colorData.getColorCode());
        assertEquals("RGB values do not match", RGB_VALUES, colorData.getRgbValues());
    }

    @Test
    public void serverServiceCallTest() throws Exception {
        ColorData colorData = myService.readServerServiceCall(COLOR).invoke()
                .toCompletableFuture().get(10, TimeUnit.SECONDS);

        assertEquals("color code does not match", COLOR_CODE, colorData.getColorCode());
        assertEquals("RGB values do not match", RGB_VALUES, colorData.getRgbValues());
    }

    @Test
    public void headerServiceCallTest() throws Exception{
        Pair<ResponseHeader, ColorData> data = myService.readHeaderServiceCall(COLOR)
                .invokeWithHeaders(RequestHeader.DEFAULT
                        .withHeader(AUTHORIZATION_KEY, AUTHORIZATION_VALUE), NotUsed.getInstance())
                .toCompletableFuture().get(10, TimeUnit.SECONDS);
        ColorData colorData = data.second();

        assertEquals("color code does not match", COLOR_CODE, colorData.getColorCode());
        assertEquals("RGB values do not match", RGB_VALUES, colorData.getRgbValues());
    }

}
