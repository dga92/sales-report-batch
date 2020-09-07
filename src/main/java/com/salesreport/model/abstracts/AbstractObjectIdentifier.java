package com.salesreport.model.abstracts;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public abstract class AbstractObjectIdentifier implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long objId;

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    @Override
    public String toString() {
        return "ObjectIdentifier{" +
                "objId=" + objId +
                '}';
    }
}
