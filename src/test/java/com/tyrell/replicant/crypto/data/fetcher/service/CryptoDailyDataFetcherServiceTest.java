package com.tyrell.replicant.crypto.data.fetcher.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static at.utils.CucumberUtils.startMockServer;
import static at.utils.MockServerEndPointTriggerCriteria.YESTERDAY;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@PropertySource("classpath:crypto-compare.properties")
@SpringBootTest
public class CryptoDailyDataFetcherServiceTest {

    @Autowired
    ICryptoDataFetcherService cryptoDailyDataFetcherService;

    @Autowired
    private Environment env;

    @Before
    public void setUp() throws IOException {
        startMockServer(env, YESTERDAY, "src/test/resources/daily_BTC_one_day_sample_response.json");//TODO have a generic default criteria
    }

    public static String createStubResponse(String time) throws IOException, TemplateException {
        String response;
        Configuration cfg = new Configuration(new Version("2.3.23"));
        cfg.setClassForTemplateLoading(CryptoDailyDataFetcherServiceTest.class, "/");
        cfg.setDefaultEncoding("UTF-8");
        Template template = cfg.getTemplate("daily_BTC_one_day_sample_response.ftl");
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("time", time);
        try (StringWriter out = new StringWriter()) {
            template.process(templateData, out);
            response = out.getBuffer().toString();
            out.flush();
        }
        return response;
    }

    @Test
    public void testgetDailyData() throws IOException, TemplateException {
        // given
        String yesterday = "2018-01-01";
        CryptoDataFetcherRestParameterObject cryptoDataFetcherRestParameterObject = new CryptoDataFetcherRestParameterObject.Builder()
                .baseCurrency("BTC")
                .quoteCurency(env.getProperty("quote.currency"))
                .startDate(yesterday)
                .endDate(yesterday)
                .build(); //put in helper

        // and
        JSONArray expected = new JSONArray(new JSONObject(createStubResponse("2018/06/20")).get("Data").toString());

        // when
        JSONArray actual = new JSONArray(cryptoDailyDataFetcherService.getDailyData(cryptoDataFetcherRestParameterObject));
        //TODO convert time to string date!!

        System.err.println(expected.toString(5));
        System.err.println(actual.toString(5));
        // then
        JSONAssert.assertEquals(expected, actual, false);
    }

    //TODO 1 clean up service
    //TODO 2 test precise dates
}
