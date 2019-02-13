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

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
        startMockServer(env, YESTERDAY, "src/test/resources/crypto_compare_daily_BTC_one_day_sample_response.json");
    }

    //TODO move to utils
    private static String createExpectation(String time) throws IOException, TemplateException {
        String response;
        Configuration cfg = new Configuration(new Version("2.3.23"));
        cfg.setClassForTemplateLoading(CryptoDailyDataFetcherServiceTest.class, "/");
        cfg.setDefaultEncoding("UTF-8");
        Template template = cfg.getTemplate("crypto_data_fetcher_daily_BTC_one_day_sample_response_expectation.ftl");
        Map<String, Object> templateData = new HashMap<>();
        if(!time.isEmpty()) { templateData.put("time", time);}
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
        String yesterday = "2018-06-20";
        CryptoDataFetcherRestParameterObject cryptoDataFetcherRestParameterObject = new CryptoDataFetcherRestParameterObject.Builder()
                .baseCurrency("BTC")
                .quoteCurency(env.getProperty("quote.currency"))
                .startDate(yesterday)
                .endDate(yesterday)
                .build(); //put in helper

        // and
        JSONArray expected = new JSONArray(createExpectation(""));

        Stream.of(expected).parallel().forEach(System.out::println);

        // when
        JSONArray actual = new JSONArray(cryptoDailyDataFetcherService.getDailyData(cryptoDataFetcherRestParameterObject));

        System.err.println(expected.toString(5));
        System.err.println(actual.toString(5));

        // then
        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    public void streamsAndLambdasScratchPad_operateOnAJSONArray() throws IOException, TemplateException {
        // given
        JSONArray input = new JSONArray("[{\n" +
                "    \"high\": \"6817.9\",\n" +
                "    \"low\": \"6569.96\",\n" +
                "    \"volumeto\": \"400530201.24\",\n" +
                "    \"volumefrom\": \"59674.47\",\n" +
                "    \"time\": \"1529452800\",\n" +
                "    \"close\": \"6761.27\",\n" +
                "    \"open\": \"6741.28\"\n" +
                "}]");

        input.put(input.get(0));
        JSONArray output = new JSONArray();

        // and
        List<JSONObject> jsonObjects = convertToListOfJsonObjectsUsingStreamsLambdas(input);
        Stream<JSONObject> jsonObjectStream = jsonObjects.parallelStream().map(jsonObject -> {
            JSONObject processed = process(jsonObject);
            return processed; //tODO just return function!! hmmm process meyhod is a muator!! it shoudldnt FIXME!!!
        });

        // also
        List<JSONObject> jsonObjects1 = jsonObjectStream.collect(Collectors.toList());

        JSONArray jsonArray2 = new JSONArray(jsonObjects);

        System.err.println(jsonArray2.toString(5));


    }

    //TODO use parallelism
    private List<JSONObject> convertToListOfJsonObjectsUsingStreamsLambdas(JSONArray array) {
        return arrayToStream(array)
                .map(JSONObject.class::cast).collect(Collectors.toList());
    }

    @Nonnull
    private static Stream<Object> arrayToStream(JSONArray array) {
        return StreamSupport.stream(array.spliterator(), true);
    }

    private JSONObject process(JSONObject jsonObject) {
        JSONObject processed = new JSONObject(jsonObject.toString());
        String volumeToKey = "volumeto";
        String volumeFromKey = "volumefrom";
        String quoteCurrencyVolumeInUnits = processed.get(volumeToKey).toString();
        String baseCurrencyVolumeInUnits = processed.get(volumeFromKey).toString();
        processed.remove(volumeToKey);
        processed.remove(volumeFromKey);
        processed.put("baseCurrencyVolumeInUnits", baseCurrencyVolumeInUnits);
        processed.put("quoteCurrencyVolumeInUnits", quoteCurrencyVolumeInUnits);
        return processed;
    }

}