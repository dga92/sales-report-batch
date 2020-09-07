package com.salesreport.converter;

import java.util.stream.Stream;

public class InputData {

    private String delimiter = "ç";

    private String raw;

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String[] getValues() {
        String[] strings = this.raw.split(this.getDelimiter());
        return strings;
    }

    public InputDataType getType() {
        InputDataType type = Stream.of(InputDataType.values())
                .filter(value -> value.getCode().equals(this.raw.split(this.getDelimiter())[0]))
                .findFirst().orElse(InputDataType.UNKNOWN);

        return type;
    }
}
