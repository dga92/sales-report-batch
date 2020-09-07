package com.salesreport.factory;

import com.salesreport.converter.InputData;
import com.salesreport.converter.InputDataType;
import com.salesreport.converter.TransformedInputData;
import com.salesreport.model.Customer;
import com.salesreport.model.Sales;
import com.salesreport.model.Salesman;

public class InputDataFactory {

    private InputDataFactory() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static TransformedInputData create(InputData input) throws IllegalAccessException {
        InputDataType type = input.getType();

        switch (type) {
            case SALESMAN: {
                return new Salesman(input);
            }
            case CUSTOMER: {
                return new Customer(input);
            }
            case SALES: {
                return new Sales(input);
            }
            case UNKNOWN:
            default: {
                throw new IllegalAccessException();
            }
        }
    }
}
