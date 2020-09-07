package com.salesreport.model;

import com.salesreport.model.abstracts.AbstractObjectIdentifier;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
public class SalesItem extends AbstractObjectIdentifier {

    private Integer id;
    private Integer quantity;
    private BigDecimal value;

    @ManyToOne
    private Sales sales;

    public SalesItem() {
    }

    public SalesItem(String input) {
        final String[] data = input.split("-");

        this.id = new Integer(data[0]);
        this.quantity = new Integer(data[1]);
        this.value = new BigDecimal(data[2]);
    }


    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Sales getSales() {
        return sales;
    }

    public void setSales(Sales sales) {
        this.sales = sales;
    }

    @Override
    public String toString() {
        return "SalesItem{" +
                ", id=" + id +
                ", quantity=" + quantity +
                ", value=" + value +
                '}';
    }
}
