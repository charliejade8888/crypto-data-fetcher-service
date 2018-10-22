package com.tyrell.replicant.crypto.data.fetcher.service;

import org.json.JSONArray;
import org.json.JSONObject;
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
        return restGet(url, cryptoDataFetcherRestParameterObject);
    }

    //TODO clean this one!!! too big!!!!
    private static String restGet(String url, CryptoDataFetcherRestParameterObject ccrpo) {
        String http = "http";
        String doubleForwardSlash = "//";
        String colon = ":";
        String httpUrlPart = http + colon + doubleForwardSlash;
        RestTemplate restTemplate = new RestTemplate();
        if (!url.startsWith(http)) {
            url = httpUrlPart + url;
        }

        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");

        long daysBetween = 0L;
        long oneDay = 1 * 24 * 60 * 60 * 1000;

        try {
            Date dateFrom = myFormat.parse(ccrpo.getStartDate());
            Date dateTo = myFormat.parse(ccrpo.getEndDate());
            daysBetween = getDifferenceDays(dateFrom, dateTo);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //needs helper to name converting api param to cryptocompare param
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam(FYSM_PARAM_KEY, ccrpo.getBaseCurrency())
                .queryParam(TOTS_PARAM_KEY, convertDateStringToEpoch(ccrpo.getEndDate()))
                .queryParam(LIMIT_PARAM_KEY, daysBetween)
                .queryParam(TYSM_PARAM_KEY, ccrpo.getQuoteCurency()); //TODO if size<=1 only ret first in input
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), GET, entity, String.class);
        //remove first element?? or if

        JSONArray input = new JSONArray(new JSONObject(response.getBody()).get("Data").toString());
        JSONArray output = new JSONArray();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");//TODO add hh mm ss
        for(int i=0; i<input.length(); i++) {
            JSONObject jsonObject = new JSONObject(input.get(i).toString());
            long epochTime = Long.valueOf(jsonObject.get("time").toString());//FIXME!!
            String date = sdf.format(new Date(epochTime*1000));
            jsonObject.put("time", date);
            //input.remove(i);
            output.put(jsonObject);


            //TODO convert epoch time prop to string datetime?EOD
            //volTo is num of USDs traded
            //volFrom is num of BTCs traded ...not number of transactions, but rather number of units!

        }
        String nicelyFormattedResponseBody = output.toString(4);

        return nicelyFormattedResponseBody;
    }

    public static long getDifferenceDays(Date startDate, Date endDate) {
        long diff = endDate.getTime() - startDate.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

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