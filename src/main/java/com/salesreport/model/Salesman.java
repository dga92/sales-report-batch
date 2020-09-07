package com.salesreport.model;

import com.salesreport.converter.InputDataType;
import com.salesreport.converter.TransformedInputData;
import com.salesreport.converter.InputData;

import javax.persistence.Entity;

@Entity
public class Salesman extends TransformedInputData {

    private String id;
    private String cnpj;
    private String name;
    private String salary;

    public Salesman() {
    }

    public Salesman(InputData input) {
        final String[] data = input.getValues();

        this.id = InputDataType.SALESMAN.getCode();
        this.cnpj = data[1];
        this.name = data[2];
        this.salary = data[3];
    }


    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Salesman{" +
                "id='" + id + '\'' +
                ", cnpj='" + cnpj + '\'' +
                ", name='" + name + '\'' +
                ", salary='" + salary + '\'' +
                '}';
    }
}
