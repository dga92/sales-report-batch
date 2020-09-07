package com.salesreport.model;


import com.salesreport.converter.InputData;
import com.salesreport.converter.InputDataType;
import com.salesreport.converter.TransformedInputData;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class Sales extends TransformedInputData {

    private String id;
    private String saleId;
    private String salesmanName;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SalesItem> items;

    public Sales() {
    }

    public Sales(InputData input) {
        String[] data = input.getValues();

        this.id = InputDataType.SALES.getCode();
        this.saleId = data[1];
        this.items = createSaleItems(data[2]);
        this.salesmanName = data[3];
    }

    private List<SalesItem> createSaleItems(String data) {
        String[] rawDataItems = data.replaceAll("[\\[|\\]]", "").split(",");
        List<SalesItem> items = Stream.of(rawDataItems).map(SalesItem::new).peek(sale -> sale.setSales(this)).collect(Collectors.toList());

        return items;
    }


    public String getSalesmanName() {
        return salesmanName;
    }

    public void setSalesmanName(String salesmanName) {
        this.salesmanName = salesmanName;
    }

    public String getSaleId() {
        return saleId;
    }

    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Sales{" +
                "id='" + id + '\'' +
                ", saleId='" + saleId + '\'' +
                ", salesmanName='" + salesmanName + '\'' +
                ", items=" + items +
                '}';
    }
}
