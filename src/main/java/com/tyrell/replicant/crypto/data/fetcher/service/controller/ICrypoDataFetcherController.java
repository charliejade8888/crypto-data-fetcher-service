package com.tyrell.replicant.crypto.data.fetcher.service.controller;

import org.springframework.http.ResponseEntity;

public interface ICrypoDataFetcherController {

    ResponseEntity<String> getOHLCVData(String baseCurrency, String startDate, String endDate);

}
