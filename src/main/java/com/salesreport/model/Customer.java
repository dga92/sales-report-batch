package com.salesreport.model;

import com.salesreport.converter.InputData;
import com.salesreport.converter.InputDataType;
import com.salesreport.converter.TransformedInputData;

import javax.persistence.Entity;

@Entity
public class Customer extends TransformedInputData {

    private String id;
    private String cpf;
    private String name;
    private String businessArea;

    public Customer() {
    }

    public Customer(InputData input) {
        final String[] data = input.getValues();

        this.id = InputDataType.CUSTOMER.getCode();
        this.cpf = data[1];
        this.name = data[2];
        this.businessArea = data[3];
    }


    public String getBusinessArea() {
        return businessArea;
    }

    public void setBusinessArea(String businessArea) {
        this.businessArea = businessArea;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", cpf='" + cpf + '\'' +
                ", name='" + name + '\'' +
                ", businessArea='" + businessArea + '\'' +
                '}';
    }
}
