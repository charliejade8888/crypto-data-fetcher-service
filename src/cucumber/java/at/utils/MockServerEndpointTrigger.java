package at.utils;

import org.springframework.core.env.Environment;

public interface MockServerEndpointTrigger {
    MockServerParameterObject createMockServerParameterObject(Environment env, String stubResponseFile);
}
