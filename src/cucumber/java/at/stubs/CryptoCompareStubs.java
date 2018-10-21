package at.stubs;

import at.utils.CucumberUtils;
import at.utils.MockServerParameterObject;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.model.Parameter;

import java.io.IOException;

import static com.tyrell.replicant.crypto.data.fetcher.service.CryptoDailyDataFetcherService.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class CryptoCompareStubs {

    public static void makeCryptoCompareStub(MockServerParameterObject mspo) throws IOException {
        new MockServerClient(mspo.getHost(), mspo.getPort())
                .when(
                        request()
                                .withMethod(CucumberUtils.GET)
                                .withPath(mspo.getPath())
                                .withQueryStringParameter(new Parameter(FYSM_PARAM_KEY, mspo.getFsymParam()))
                                .withQueryStringParameter(new Parameter(TYSM_PARAM_KEY, mspo.getTsymParam()))
                              //  .withQueryStringParameter(new Parameter(TOTS_PARAM_KEY, mspo.getToTsParam()))
                                .withQueryStringParameter(new Parameter(LIMIT_PARAM_KEY, mspo.getLimitParam()))
                )
                .respond(
                        response()
                                .withBody(CucumberUtils.readFile(mspo.getStubResponseFile()))
                );
    }

}