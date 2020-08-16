package com.tyrell.replicant.crypto.data.fetcher.service.controller;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.tyrell.replicant.crypto.data.fetcher.service.Application;
import com.tyrell.replicant.crypto.data.fetcher.service.ICryptoDataFetcherService;
import com.tyrell.replicant.crypto.data.fetcher.service.stubs.StubCryptoDataFetcherService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@Import(CryptoDataFetcherControllerTest.Config.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CryptoDataFetcherControllerTest {

     @TestConfiguration
     static class Config {
        @Bean
        CrypoDailyDataFetcherController crypoDailyDataFetcherController() {
            return new CrypoDailyDataFetcherController(new StubCryptoDataFetcherService());
        } //OKay in spring context... spring is habdling DI here
    }
    //TODO try contrsuctor inj with static final ConcreteType var
    //TODO consder sonmez nomocks approach!!

    //TODO try a setter injection example with??? Environment??/Other
    // I guess your bean will constructor then run setter on it!!
    // setter takes autowired private too???  - uses static final?? required=false on setters
    // also update concrete vs interface autowire
    // look for example of this in sia book, may need latest look on mannings website

    @LocalServerPort
    private int port;

    //TODO download all and put in DB!!!!

    @Test
    public void testGetCryptoDailyData() {
        // given
        int expectedStatusCode = 200;
        Map<String, String> params = new HashMap();
        params.put("basecurrency","");
        params.put("startdate", "");
        params.put("enddate","");

        // when
        Response response = RestAssured.given().port(port).when().params(params).get("/dailydata");

        // then
        assertEquals(expectedStatusCode, response.getStatusCode());
    }
    //TODO add mockito tests?
}