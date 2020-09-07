package com.salesreport.converter;

public enum InputDataType {
    SALESMAN("001"),
    CUSTOMER("002"),
    SALES("003"),
    UNKNOWN("000");

    private String code;

    InputDataType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
