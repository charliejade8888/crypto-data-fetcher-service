package com.tyrell.replicant.crypto.data.fetcher.service.stubs;

import com.tyrell.replicant.crypto.data.fetcher.service.CryptoDataFetcherRestParameterObject;
import com.tyrell.replicant.crypto.data.fetcher.service.ICryptoDataFetcherService;
import org.springframework.stereotype.Component;

@Component("stubCryptoDataFetcherService")
public class StubCryptoDataFetcherService implements ICryptoDataFetcherService {

    @Override
    public String getDailyData(CryptoDataFetcherRestParameterObject cryptoDataFetcherRestParameterObject) {
        return "stubbyMcStubFace";
    }

}
