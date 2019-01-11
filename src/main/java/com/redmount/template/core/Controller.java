package com.redmount.template.core;

import java.util.List;

public interface Controller<T> {
    Result<T> saveAutomatic(T model);

    Result<List<T>> listAutomatic(String keywords, String condition, String relations, String orderBy,int page,int size);

    Result<T> getAutomatic(String pk, String relations);

    void init();
}
