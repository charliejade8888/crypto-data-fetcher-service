package com.tyrell.replicant.crypto.data.fetcher.service.controller;

import com.tyrell.replicant.crypto.data.fetcher.service.CryptoDataFetcherRestParameterObject;
import com.tyrell.replicant.crypto.data.fetcher.service.ICryptoDataFetcherService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CrypoDailyDataFetcherController implements ICrypoDataFetcherController {

    @Value("${quote.currency}")
    private String quoteCurrency;

    @Autowired
    private ICryptoDataFetcherService cryptoDailyDataFetcherService;

    @CrossOrigin(origins = "http://localhost:8080")
    @ApiOperation(value = "Returns historical daily data for basecurrency specified over given date range.")
    @RequestMapping(value = "/dailydata", method = RequestMethod.GET)
//    @RequestMapping(value = "{$daily.path}", method = RequestMethod.GET)
    @Override
    public ResponseEntity<String> getOHLCVData(
            @RequestParam("basecurrency") String baseCurrency,
            @RequestParam("startdate") String startDate,
            @RequestParam("enddate") String endDate) {

        String result = cryptoDailyDataFetcherService.getDailyData(CryptoDataFetcherRestParameterObject.builder()
                .endDate(endDate)
                .startDate(startDate)
                .baseCurrency(baseCurrency)
                .quoteCurency(quoteCurrency)
                .build());

        return new ResponseEntity<>(result, HttpStatus.OK);//TODO not always 200!!!!
    }


}
