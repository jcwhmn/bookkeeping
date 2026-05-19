package com.bookkeeping.common;

public interface Auditable {
    Long getCreatedBy();
    void setCreatedBy(Long createdBy);
    Long getModifiedBy();
    void setModifiedBy(Long modifiedBy);
}