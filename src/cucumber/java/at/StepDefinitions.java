package at;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.tyrell.replicant.crypto.data.fetcher.service.Application;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.json.JSONArray;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static at.utils.CucumberUtils.*;
import static at.utils.MockServerEndPointTriggerCriteria.LAST_TWO_DAYS;
import static at.utils.MockServerEndPointTriggerCriteria.YESTERDAY;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {Application.class, CucumberTestConfig.class})
@ActiveProfiles("test")
@PropertySource("classpath:crypto-compare.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StepDefinitions {

    @LocalServerPort
    private int port;

    @Autowired
    private Environment env;

    private static Response lastResponse;

    @Given("^the following daily data is available for bitcoin yesterday:$")
    public void the_following_daily_data_is_available_for_bitcoin_yesterday(DataTable dataTable) throws IOException, InterruptedException {
        String stubby = "cryptocompare_daily_BTC_one_day_sample_response.json";
        makeStubFromDataTable(stubby, dataTable, "nothing to ignore here");
        startMockServer(env, YESTERDAY, "/tmp/" + stubby);
    }

    @Given("^the following daily data is available for bitcoin over the last two days:$")
    public void the_following_daily_data_is_available_for_bitcoin_over_the_last_two_days(DataTable dataTable) throws Throwable {
        makeStubFromDataTable("daily_BTC_last_two_days.json", dataTable, "nothing to ignore here");
        startMockServer(env, LAST_TWO_DAYS, "/tmp/daily_BTC_last_two_days.json");
    }

    @When("^I make a request for daily data \"([^\"]*)\" from \"([^\"]*)\" to \"([^\"]*)\"$")
    public void i_make_a_request_for_daily_data(String baseCurrency, String from, String to) {
        lastResponse = RestAssured
                .given()
                .param("basecurrency",baseCurrency)
                .param("startdate", from)
                .param("enddate", to)
                .port(port)
                .when()
                .get(env.getProperty("daily.path"));
        assertEquals(200, lastResponse.getStatusCode());
    }

    @Then("^the following data should be returned:$")
    public void the_following_data_should_be_returned(DataTable dataTable) throws Throwable {
        JSONArray expected = convertDataTableToJSONArray(dataTable, "nothing to ignore here");
        JSONArray actual = new JSONArray(lastResponse.getBody().asString());
        JSONAssert.assertEquals(expected, actual, false);
    }

}