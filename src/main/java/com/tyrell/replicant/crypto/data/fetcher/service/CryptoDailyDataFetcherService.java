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
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.Long.parseLong;
import static org.springframework.http.HttpMethod.GET;

@PropertySource("classpath:crypto-compare.properties")
@PropertySource("classpath:application.properties")
@Component("cryptoDailyDataFetcherService")
public class CryptoDailyDataFetcherService implements ICryptoDataFetcherService {
//todo have an application-test.properties too!!
    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoDailyDataFetcherService.class);

    //TODO put in props
    //TODO address yellow sonar lint issues
    public static final String FYSM_PARAM_KEY = "fsym"; // from symbol
    public static final String TYSM_PARAM_KEY = "tsym"; // to symbol
    public static final String TOTS_PARAM_KEY = "toTs"; // end date in past
    public static final String LIMIT_PARAM_KEY = "limit"; // num of data points

    @Autowired
    private Environment env;

    @Override
    public String getDailyData(CryptoDataFetcherRestParameterObject cryptoDataFetcherRestParameterObject) {
        String url = new StringBuilder()
                .append(env.getProperty("crypto.compare.host"))
                .append(":")
                .append(env.getProperty("crypto.compare.port"))
                .append(env.getProperty("crypto.compare.path.daily"))
                .toString();
        return Helper.restGetPrettifiedCryptoCompareResponse(url, cryptoDataFetcherRestParameterObject);
    }

    private static class Helper {

        private static String restGetPrettifiedCryptoCompareResponse(String url, CryptoDataFetcherRestParameterObject ccrpo) {
            JSONArray data = extractDataSectionFromCryptoCompareResponse(url, ccrpo);

            List<JSONObject> cryptoDataListWithHumanReadableDates = convertToListOfJsonObjectsUsingStreamsLambdas(
                    createResponseWithHumanReadableDate(data));

            Stream<JSONObject> cryptoDataStreamWithHumanReadableDatesAndDescriptiveVolumeKeys = cryptoDataListWithHumanReadableDates
                    .parallelStream().map(CryptoDailyDataFetcherService.Helper::createResponseWithDescriptiveVolumeKeys);

            JSONArray output = new JSONArray(cryptoDataStreamWithHumanReadableDatesAndDescriptiveVolumeKeys.collect(Collectors.toList()));
            String nicelyFormattedResponseBody = output.toString(4);

            LOGGER.info("response body::" + nicelyFormattedResponseBody); //TODO  consider log at entry p exit consider aspects??
            return nicelyFormattedResponseBody;
        }

        private static JSONArray extractDataSectionFromCryptoCompareResponse(String url, CryptoDataFetcherRestParameterObject ccrpo) {
            String originalCryptCompareResponseBody = restGet(url,ccrpo).getBody();
            Object data = new JSONObject(originalCryptCompareResponseBody).get("Data");
            return new JSONArray(data.toString());
        }

        private static List<JSONObject> convertToListOfJsonObjectsUsingStreamsLambdas(JSONArray array) {
            return arrayToStream(array).parallel()
                    .map(JSONObject.class::cast).collect(Collectors.toList());
        }

        //    @Nonnull
        private static Stream<Object> arrayToStream(JSONArray array) {
            return StreamSupport.stream(array.spliterator(), true);
        }

        private static JSONObject convertEpochTimeDateFieldOfJSONObjectToHumanReadableDate(JSONObject jsonObject) {
            long epochTime = parseLong(jsonObject.getString("time"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd"); //could add hh mm ss
            String date = sdf.format(new Date(epochTime*1000));
            JSONObject converted = new JSONObject(jsonObject.toString());
            converted.put("time", date);
            return converted;
        }

        private static JSONArray createResponseWithHumanReadableDate(JSONArray input) {
            JSONArray output;
            List<JSONObject> originalList = convertToListOfJsonObjectsUsingStreamsLambdas(input);
            List<JSONObject> convertedList = originalList.parallelStream()
                    .map(CryptoDailyDataFetcherService.Helper::convertEpochTimeDateFieldOfJSONObjectToHumanReadableDate).collect(Collectors.toList());
            output = new JSONArray(convertedList);
            return output;
        }

        private static JSONObject createResponseWithDescriptiveVolumeKeys(JSONObject jsonObject) {
            JSONObject processed = new JSONObject(jsonObject.toString());
            String volumeToKey = "volumeto";
            String volumeFromKey = "volumefrom";
            String quoteCurrencyVolumeInUnits = processed.get(volumeToKey).toString();
            String baseCurrencyVolumeInUnits = processed.get(volumeFromKey).toString();
            processed.remove(volumeToKey);
            processed.remove(volumeFromKey);
            processed.put("baseCurrencyVolumeInUnits", baseCurrencyVolumeInUnits);
            processed.put("quoteCurrencyVolumeInUnits", quoteCurrencyVolumeInUnits);
            return processed;
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

        private static long getDifferenceDays(Date startDate, Date endDate) {
            long diff = endDate.getTime() - startDate.getTime();
            return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        }

        //TODO gradle jib
        private static long convertDateStringToEpoch(String strDate) {
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

}