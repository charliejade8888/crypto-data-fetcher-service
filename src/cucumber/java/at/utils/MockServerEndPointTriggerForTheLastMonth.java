package at.utils;

import org.springframework.core.env.Environment;

import static java.lang.Integer.valueOf;

public class MockServerEndPointTriggerForTheLastMonth implements MockServerEndpointTrigger {

    //TODO not so diff this method make a common one and just pass ub linitParam??
    @Override
    public MockServerParameterObject createMockServerParameterObject(Environment env, String stubResponseFile) {
        String returnOneMonthsWorthOfDataPlease = "30";
        return new MockServerParameterObject.Builder()
                .host(env.getProperty("crypto.compare.host"))
                .path(env.getProperty("crypto.compare.path.daily"))
                .port(valueOf(env.getProperty("crypto.compare.port")))
                .fsymParam("BTC")
                .limitParam(returnOneMonthsWorthOfDataPlease)
                .tsymParam("USD")
                .stubResponseFile(stubResponseFile).build();
    }

}