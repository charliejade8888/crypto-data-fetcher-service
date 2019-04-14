package com.tyrell.replicant.crypto.data.fetcher.service.controller;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.tyrell.replicant.crypto.data.fetcher.service.ICryptoDataFetcherService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CryptoDataFetcherControllerTest {

    //TODO use @springboottest's classes rathrer than using reflection!!

    @LocalServerPort
    private int port;

    @Autowired
    ICryptoDataFetcherService stubCryptoDataFetcherService;

    @Autowired
    ICrypoDataFetcherController cryptoDailyDataFetcherController;// make Icontroller w 2 versions!! daily + intra

    //TODO download all and put in DB!!!!

    @Before
    public void setUp() {
        String fieldToReplace = "cryptoDailyDataFetcherService";
        setField(cryptoDailyDataFetcherController, fieldToReplace, stubCryptoDataFetcherService);
    }

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