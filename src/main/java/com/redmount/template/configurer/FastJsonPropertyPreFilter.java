package com.redmount.template.configurer;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;


public class FastJsonPropertyPreFilter implements PropertyPreFilter {
    @Override
    public boolean apply(JSONSerializer serializer, Object object, String name) {
        if (name.endsWith("Pk") || name.equals("created") || name.equals("updated") || name.equals("deleted")) {
            return false;
        }
        return true;
    }
}
