package com.tyrell.replicant.crypto.data.fetcher.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpMethod.GET;

@PropertySource("classpath:crypto-compare.properties")
@PropertySource("classpath:application.properties")
@Component("cryptoDailyDataFetcherService")
public class CryptoDailyDataFetcherService implements ICryptoDataFetcherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoDailyDataFetcherService.class);

    public static final String FYSM_PARAM_KEY = "fsym"; // from symbol
    public static final String TYSM_PARAM_KEY = "tsym"; // to symbol
    public static final String TOTS_PARAM_KEY = "toTs"; // end date in past
    public static final String LIMIT_PARAM_KEY = "limit"; // num of data points

    @Autowired
    Environment env;

    @Override
    public String getDailyData(CryptoDataFetcherRestParameterObject cryptoDataFetcherRestParameterObject) {
        String url = new StringBuilder()
                .append(env.getProperty("crypto.compare.host"))
                .append(":")
                .append(env.getProperty("crypto.compare.port"))
                .append(env.getProperty("crypto.compare.path.daily"))
                .toString();
        return restGetPrettifiedCryptoCompareResponse(url, cryptoDataFetcherRestParameterObject);
    }

    private static String restGetPrettifiedCryptoCompareResponse(String url, CryptoDataFetcherRestParameterObject ccrpo) {
        String originalCryptCompareResponseBody = restGet(url,ccrpo).getBody();
        JSONArray input = new JSONArray(new JSONObject(originalCryptCompareResponseBody).get("Data").toString());
        JSONArray output = new JSONArray();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd"); //could add hh mm ss

       //TODO move to method with name that highlights it makes hr date and hr volume props
        for(int i=0; i<input.length(); i++) { //parallelise??
            JSONObject jsonObject = new JSONObject(input.get(i).toString());
            long epochTime = Long.valueOf(jsonObject.get("time").toString());
            String date = sdf.format(new Date(epochTime*1000));
            String volumeToKey = "volumeto";
            String volumeFromKey = "volumefrom";
            String quoteCurrencyVolumeInUnits = jsonObject.get(volumeToKey).toString();
            String baseCurrencyVolumeInUnits = jsonObject.get(volumeFromKey).toString();
            jsonObject.remove(volumeToKey);
            jsonObject.remove(volumeFromKey);
            jsonObject.put("baseCurrencyVolumeInUnits", baseCurrencyVolumeInUnits);
            jsonObject.put("quoteCurrencyVolumeInUnits", quoteCurrencyVolumeInUnits);
            jsonObject.put("time", date);
            output.put(jsonObject);
        }
        String nicelyFormattedResponseBody = output.toString(4);
        LOGGER.info("response body::" + nicelyFormattedResponseBody);
        return nicelyFormattedResponseBody;
    }

    private static ResponseEntity<String> restGet(String url, CryptoDataFetcherRestParameterObject ccrpo) {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam(FYSM_PARAM_KEY, ccrpo.getBaseCurrency())
                .queryParam(TOTS_PARAM_KEY, convertDateStringToEpoch(ccrpo.getEndDate()))
                .queryParam(LIMIT_PARAM_KEY, getDaysBetweenStartDateAndEndDate(ccrpo))
                .queryParam(TYSM_PARAM_KEY, ccrpo.getQuoteCurency()); //TODO if size<=1 only ret first in input
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        //TODO need to throw exception here if non-200!
        return restTemplate.exchange(builder.toUriString(), GET, entity, String.class);
    }

    private static long getDaysBetweenStartDateAndEndDate(CryptoDataFetcherRestParameterObject ccrpo) {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        long daysBetween = 0L;
        try {
            Date dateFrom = myFormat.parse(ccrpo.getStartDate());
            Date dateTo = myFormat.parse(ccrpo.getEndDate());
            daysBetween = getDifferenceDays(dateFrom, dateTo);
        } catch (ParseException e) {
            LOGGER.error("cannot parse dates - start date: " + ccrpo.getStartDate() + " end date: " + ccrpo.getEndDate(), e); //does this print stacktrace too?
        }
        return daysBetween;
    }

    public static long getDifferenceDays(Date startDate, Date endDate) {
        long diff = endDate.getTime() - startDate.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    //TODO gradle jib
    public static long convertDateStringToEpoch(String strDate) {
        long epochSecond = 0L;
        long oneDay = 1 * 24 * 60 * 60;
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd").parse(strDate);
            epochSecond = d.toInstant().plusSeconds(oneDay).atOffset(ZoneOffset.UTC).toEpochSecond();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return epochSecond;
    }

}