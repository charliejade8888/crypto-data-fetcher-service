package com.tyrell.replicant.crypto.data.fetcher.service;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

//tODO will disappear when lomboij done
//TODO chek all with sonar lint
public class CryptoDataFetcherRestParameterObjectTest {

    private static final String EXPECTED_BASE_CURRENCY = "BTC";
    private static final String EXPECTED_QUOTE_CURRENCY = "JPY";
    private static final String EXPECTED_START_DATE = "2018-06-24";
    private static final String EXPECTED_END_DATE = "2018-06-25";

    private static CryptoDataFetcherRestParameterObject underTest;

    @BeforeClass
    public static void setUp() {
        underTest = CryptoDataFetcherRestParameterObject.builder().baseCurrency(EXPECTED_BASE_CURRENCY)
                .quoteCurency(EXPECTED_QUOTE_CURRENCY)
                .startDate(EXPECTED_START_DATE)
                .endDate(EXPECTED_END_DATE)
                .build();
    }

    @Test
    public void testGetBaseCurrency() {
        assertEquals(EXPECTED_BASE_CURRENCY, underTest.getBaseCurrency());
    }

    @Test
    public void testGetQuoteCurrency() {
        assertEquals(EXPECTED_QUOTE_CURRENCY, underTest.getQuoteCurency());
    }

    @Test
    public void testGetStartDate() {
        assertEquals(EXPECTED_START_DATE, underTest.getStartDate());
    }

    @Test
    public void testGetEndDate() {
        assertEquals(EXPECTED_END_DATE, underTest.getEndDate());
    }

}