package com.example.service.impl;

import akka.NotUsed;
import akka.japi.Pair;
import com.example.service.api.MyService;
import com.example.service.model.ColorData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.RequestHeader;
import com.lightbend.lagom.javadsl.api.transport.ResponseHeader;
import com.lightbend.lagom.javadsl.server.HeaderServiceCall;
import com.lightbend.lagom.javadsl.server.ServerServiceCall;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class MyServiceImpl implements MyService {
    private static final Logger logger = LoggerFactory.getLogger(ColorData.class);
    private static final String DATA_MASTER = ConfigFactory.load().getString("data.file");
    private static final int SUCCESS_CODE = 200;
    private static final int NOT_FOUND_CODE = 404;
    private static final int BACKEND_ERROR_CODE = 500;
    private static final int AUTHORIZATION_FAILURE_CODE = 401;
    private static final String NOT_FOUND = "NOT_FOUND";
    private static final String BACKEND_ERROR = "BACKEND_ERROR";
    private static final String AUTHORIZATION_FAILURE = "AUTHORIZATION_FAILURE";
    private static final String AUTHORIZATION_KEY = "authorization_key";
    private static final String AUTHORIZATION_VALUE = "7208015cfb39d49fd39b1339f4627281";

    private static Pair<ResponseHeader, ColorData> concatHeader(ResponseHeader responseHeader, ColorData colorData) {
        return Pair.create(responseHeader, colorData);
    }

    @Override
    public ServiceCall<NotUsed, ColorData> readServiceCall(String color) {
        return request -> readServerServiceCall(color)
                //Manipulating incoming request header
                .handleRequestHeader(requestHeader -> requestHeader.clearPrincipal())
                //Modifying request header that is being passed to ServerServiceCall
                .invokeWithHeaders(RequestHeader.DEFAULT.withHeader(AUTHORIZATION_KEY, AUTHORIZATION_VALUE), request)
                .thenApply(pair -> pair.second())
                .exceptionally(error -> getBackendErrorResponse(color));
    }

    @Override
    public ServerServiceCall<NotUsed, ColorData> readServerServiceCall(String color) {
        return request -> readHeaderServiceCall(color)
                //Manipulating incoming request header
                .handleRequestHeader(requestHeader -> requestHeader.clearPrincipal())
                //Modifying request header that is being passed to ServerServiceCall
                .invokeWithHeaders(RequestHeader.DEFAULT.withHeader(AUTHORIZATION_KEY, AUTHORIZATION_VALUE), request)
                .thenApply(pair -> pair.second())
                .exceptionally(error -> getBackendErrorResponse(color));
    }

    @Override
    public HeaderServiceCall<NotUsed, ColorData> readHeaderServiceCall(String color) {
        return (requestHeader, request) -> {
            if (!checkRequestHeader(requestHeader)) {
                return CompletableFuture.completedFuture(concatHeader(ResponseHeader.OK
                                .withStatus(AUTHORIZATION_FAILURE_CODE),
                        getUnauthorizedResponse(color)));
            }
            try {
                Optional<ColorData> colorData = getData(color);
                if (colorData.isPresent()) {
                    logger.info("Data retrieved successfully for color {} ..", color);
                    return CompletableFuture.completedFuture(concatHeader(ResponseHeader.OK.withStatus(SUCCESS_CODE),
                            colorData.get()));
                } else {

                    return CompletableFuture.completedFuture(concatHeader(ResponseHeader.OK.withStatus(NOT_FOUND_CODE),
                            getNotFoundResponse(color)));
                }
            } catch (IOException e) {
                logger.error("Exception occurred when trying to read json for color {}", color);
                return CompletableFuture.completedFuture(concatHeader(ResponseHeader.OK.withStatus(BACKEND_ERROR_CODE),
                        getBackendErrorResponse(color)));
            }
        };
    }

    private boolean checkRequestHeader(RequestHeader requestHeader){
        if (requestHeader.headers().containsKey(AUTHORIZATION_KEY)
                && requestHeader.getHeader(AUTHORIZATION_KEY).get().equals(AUTHORIZATION_VALUE)) {
            logger.info("Authorized request");
            return true;
        } else {
            logger.info("Unauthorized request");
            return false;
        }
    }

    private ColorData getBackendErrorResponse(String color) {
        logger.info("Getting failure response for color {} ..", color);
        return new ColorData() {
            {
                setColor(color);
                setColorCode(BACKEND_ERROR);
                setRgbValues(BACKEND_ERROR);
            }
        };
    }

    private ColorData getNotFoundResponse(String color) {
        logger.info("Getting failure response for color {} ..", color);
        return new ColorData() {
            {
                setColor(color);
                setColorCode(NOT_FOUND);
                setRgbValues(NOT_FOUND);
            }
        };
    }

    private ColorData getUnauthorizedResponse(String color) {
        logger.info("Getting failure response for color {} ..", color);
        return new ColorData() {
            {
                setColor(color);
                setColorCode(AUTHORIZATION_FAILURE);
                setRgbValues(AUTHORIZATION_FAILURE);
            }
        };
    }

    private Optional<ColorData> getData(String color) throws IOException{
        List<ColorData> colorDataList = readJsonFile(DATA_MASTER);
        if (colorDataList.isEmpty()) {
            return Optional.empty();
        } else {
            Predicate<ColorData> predicate = colorData -> colorData.getColor().equalsIgnoreCase(color);
            if (colorDataList.stream().noneMatch(predicate)) {
                logger.warn("No data found for color {} ..", color);
                return Optional.empty();
            } else {
                return colorDataList.stream().filter(predicate).findFirst();
            }
        }
    }

    private List<ColorData> readJsonFile(String fileName) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        ClassLoader classLoader = getClass().getClassLoader();
        logger.info("Attempting to read {} ..", fileName);
        return mapper.readValue(new File(classLoader.getResource(fileName).getPath()),
                    new TypeReference<List<ColorData>>() {
                    });
    }

}
