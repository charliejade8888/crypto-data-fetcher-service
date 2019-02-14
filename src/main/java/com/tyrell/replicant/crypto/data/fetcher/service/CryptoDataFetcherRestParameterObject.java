package com.tyrell.replicant.crypto.data.fetcher.service;

import lombok.*;

@Getter
@Builder
public class CryptoDataFetcherRestParameterObject {

    private String endDate;
    private String startDate;
    private String baseCurrency;
    private String quoteCurency;

}
