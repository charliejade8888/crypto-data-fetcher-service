package com.tyrell.replicant.crypto.data.fetcher.service;

public class CryptoDataFetcherRestParameterObject {

    private String endDate;
    private String startDate;
    private String baseCurrency;
    private String quoteCurency;

    private CryptoDataFetcherRestParameterObject() {
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public String getQuoteCurency() {
        return quoteCurency;
    }

    private CryptoDataFetcherRestParameterObject(Builder builder) {
        endDate = builder.endDate;
        startDate = builder.startDate;
        baseCurrency = builder.baseCurrency;
        quoteCurency = builder.quoteCurency;
    }

    public static final class Builder {
        private String endDate;
        private String startDate;
        private String baseCurrency;
        private String quoteCurency;

        public Builder() {
        }

        public Builder endDate(String val) {
            endDate = val;
            return this;
        }

        public Builder startDate(String val) {
            startDate = val;
            return this;
        }

        public Builder baseCurrency(String val) {
            baseCurrency = val;
            return this;
        }

        public Builder quoteCurency(String val) {
            quoteCurency = val;
            return this;
        }

        public CryptoDataFetcherRestParameterObject build() {
            return new CryptoDataFetcherRestParameterObject(this);
        }
    }
}
