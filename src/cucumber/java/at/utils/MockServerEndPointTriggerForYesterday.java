package at.utils;

import org.springframework.core.env.Environment;

import static java.lang.Integer.valueOf;

public class MockServerEndPointTriggerForYesterday implements MockServerEndpointTrigger {

    @Override
    public MockServerParameterObject createMockServerParameterObject(Environment env, String stubResponseFile) {
        String return24HoursWorthOfDataPlease = "0";
        return new MockServerParameterObject.Builder()
                .host(env.getProperty("crypto.compare.host"))
                .path(env.getProperty("crypto.compare.path.daily"))
                .port(valueOf(env.getProperty("crypto.compare.port")))
                .fsymParam("BTC")
                .limitParam(return24HoursWorthOfDataPlease)
                .tsymParam("USD")
                .stubResponseFile(stubResponseFile).build();
    }

}