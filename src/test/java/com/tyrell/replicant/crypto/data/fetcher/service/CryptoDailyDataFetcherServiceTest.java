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
import static org.junit.Assert.assertTrue;

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

        // Streams and lambdas to process collections::
        // Lambdas and streams really make sense if you use ".parallelStream()" instead of ".stream()".
        // Then your code will act in some parallel way with the Fork-Join-Thread-Pool and your code speeds up.
        // Anything else is Old wine in new bottles.
        // Occasionally lambdas seem to be more expressive.
        // In other situations you will break your leg if you want to fit you algorithm to lambda and you loose expressiveness.

        // good (improved readability - loop is hidden, only operation visible)
        // people.filter( p -> p.age() < 19).collect(toList());
        // list.forEach(System.out::println);

        // bad (reduced readability - lambda abused/used badly here!!) even with method reference version simple for loops slightly easier
        // probably only makes sense if parallelism required
        // see below '// and'

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
        JSONArray output = new JSONArray();

        // and
        List<JSONObject> jsonObjects = convertToListOfJsonObjectsUsingStreamsLambdas(input);
        Stream<JSONObject> jsonObjectStream = jsonObjects.parallelStream().map(jsonObject -> {
            process(jsonObject);
            return jsonObject;
        });
        // or jsonObjects.parallelStream().map(this::apply);
        // private JSONObject apply(JSONObject jsonObject) {
        //   process(jsonObject);
        //   return jsonObject;
        // } suggested by SonarLint plugin

        // when
        jsonObjectStream.parallel().forEach(fruit -> {
            output.put(fruit);
        }); // or jsonObjectStream.parallel().forEach(output::put); suggested by SonarLint plugin

        // then
        assertTrue(output.length()>0);

        // also
        List<JSONObject> jsonObjects1 = jsonObjectStream.collect(Collectors.toList());
    }

    private List<JSONObject> convertToListOfJsonObjectsUsingStreamsLambdas(JSONArray array) {
        return arrayToStream(array)
                .map(JSONObject.class::cast).collect(Collectors.toList());
    }

    @Nonnull
    private static Stream<Object> arrayToStream(JSONArray array) {
        return StreamSupport.stream(array.spliterator(), false);
    }

    private JSONObject process(JSONObject jsonObject) {
        String volumeToKey = "volumeto";
        String volumeFromKey = "volumefrom";
        String quoteCurrencyVolumeInUnits = jsonObject.get(volumeToKey).toString();
        String baseCurrencyVolumeInUnits = jsonObject.get(volumeFromKey).toString();
        jsonObject.remove(volumeToKey);
        jsonObject.remove(volumeFromKey);
        jsonObject.put("baseCurrencyVolumeInUnits", baseCurrencyVolumeInUnits);
        jsonObject.put("quoteCurrencyVolumeInUnits", quoteCurrencyVolumeInUnits);
        System.err.println(jsonObject.toString(5));
        return jsonObject;
    }

}