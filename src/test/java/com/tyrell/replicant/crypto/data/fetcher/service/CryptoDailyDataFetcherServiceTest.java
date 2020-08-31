package com.tyrell.replicant.crypto.data.fetcher.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import org.json.JSONArray;
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
import java.util.stream.Stream;

import static at.utils.CucumberUtils.startMockServer;
import static at.utils.MockServerEndPointTriggerCriteria.YESTERDAY;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@PropertySource("classpath:crypto-compare.properties")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS) //start this test class with a fresh spring context so no oither tests interfere here!!
@SpringBootTest
public class CryptoDailyDataFetcherServiceTest {

  // TODO stub restopersations?? inject in classes=... highlight needs service real in there too
  // ..try with/out @SpringBootTest
  // try junit 5!!
  // try junit 5!!
  // https://docs.gradle.org/current/userguide/java_library_plugin.html
  //
  // For monoliths really where each module is a library.
  //
  // Google/be aware of multimodule gradle projects as these come before microservices!
  //  DDD many ways to get crude wifevtest to pass if SO I rest then build model and have get serve
  // up one scenario!!
  // try jacoco!
  // google checkstyle plugin used
  // put all udseful here in TODOs/README.md!

  @Autowired ICryptoDataFetcherService cryptoDailyDataFetcherService;

  @Autowired private Environment env;

  @Before
  public void setUp() throws IOException {
    startMockServer(
        env, YESTERDAY, "src/test/resources/crypto_compare_daily_BTC_one_day_sample_response.json");
  }

  @Test
  public void testgetDailyData() throws IOException, TemplateException {
    // given
    String yesterday = "2018-06-20";
    CryptoDataFetcherRestParameterObject cryptoDataFetcherRestParameterObject =
        CryptoDataFetcherRestParameterObject.builder()
            .baseCurrency("BTC")
            .quoteCurency(env.getProperty("quote.currency"))
            .startDate(yesterday)
            .endDate(yesterday)
            .build(); // put in helper

    // and
    JSONArray expected = new JSONArray(createExpectation(""));

    Stream.of(expected).parallel().forEach(System.out::println);

    // when
    JSONArray actual =
        new JSONArray(
            cryptoDailyDataFetcherService.getDailyData(cryptoDataFetcherRestParameterObject));

    // then
    JSONAssert.assertEquals(expected, actual, false);
  }

  private static String createExpectation(String time) throws IOException, TemplateException {
    String response;
    Configuration cfg = new Configuration(new Version("2.3.23"));
    cfg.setClassForTemplateLoading(CryptoDailyDataFetcherServiceTest.class, "/");
    cfg.setDefaultEncoding("UTF-8");
    Template template =
        cfg.getTemplate("crypto_data_fetcher_daily_BTC_one_day_sample_response_expectation.ftl");
    Map<String, Object> templateData = new HashMap<>();
    if (!time.isEmpty()) {
      templateData.put("time", time);
    }
    try (StringWriter out = new StringWriter()) {
      template.process(templateData, out);
      response = out.getBuffer().toString();
      out.flush();
    }
    return response;
  }
}
