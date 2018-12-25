package com.redmount.template.util;

import org.springframework.util.Assert;

import java.lang.reflect.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ReflectUtil {
    public static boolean containsProperty(Class cls, String propertyName) {
        try {
            cls.getDeclaredField(propertyName);
        } catch (NoSuchFieldException ex) {
            return false;
        }
        return true;
    }

    public static Field getField(Class cls, String propertyName) {
        Field field = null;
        try {
            field = cls.getDeclaredField(propertyName);
        } catch (NoSuchFieldException ex) {
        } finally {
            return field;
        }
    }

    /**
     * 对象相同属性copy
     *
     * @param obj
     * @param toResult
     * @return
     * @throws Exception 转换报错
     */
    public static <T> T cloneObj(Object obj, Class<T> toResult) {
        if (obj == null) {
            return null;
        }
        try {
            T t = toResult.newInstance();
            List<Field> fields = getFieldList(toResult);
            for (Field field : fields) {
                field.setAccessible(true);//修改访问权限
                if (Modifier.isFinal(field.getModifiers()))
                    continue;
                if (isWrapType(field)) {
                    String firstLetter = field.getName().substring(0, 1).toUpperCase(); // 首字母大写
                    String getMethodName = "get" + firstLetter + field.getName().substring(1);
                    String setMethodName = "set" + firstLetter + field.getName().substring(1);
                    Method getMethod = obj.getClass().getMethod(getMethodName);   //从源对象获取get方法
                    Method setMethod = toResult.getMethod(setMethodName, new Class[]{field.getType()}); //从目标对象获取set方法

                    //如果get 和 set方法都从一个对象中获取会出现object is not an instance of declaring class这个错误
                    //like: User{name} People{name}
                    //因为如果从源对象中获取，在setMethod.invoke调用的时候，虽然名字相同，会由于类的不同，导致
                    //调用的方法却不是目标对象中应有的方法。实际就是:getMethod = com.package.User.getName();setMethod = com.package.User.setName();
                    //而setMethod.invoke调用的结果就变成 People.setName() == People.(com.package.User.setName())
                    //这里的setName却不是People该有的，so 报错了
                    //同理,如果从目标对象中获取，在getMethod.invoke调用的时候也会出错。
                    //因此，对于getMethod和setMethod的获取应该根据源对象和目标对象区别对待。

                    //当然如果只是进行单独的对象复制，就不用担心会出现调用不属于本身的方法，也就不用区分对象get和set

                    Object value = getMethod.invoke(obj); // get 获取的是源对象的值
                    setMethod.invoke(t, new Object[]{value}); // set 设置的是目标对象的值
                }
            }
            return t;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 是否是基本类型、包装类型、String类型
     */
    private static boolean isWrapType(Field field) {
        String[] types = {"java.lang.Integer", "java.lang.Double", "java.lang.Float", "java.lang.Long",
                "java.lang.Short", "java.lang.Byte", "java.lang.Boolean", "java.lang.Char", "java.lang.String", "int",
                "double", "long", "short", "byte", "boolean", "char", "float", "java.util.Date"};
        List<String> typeList = Arrays.asList(types);
        return typeList.contains(field.getType().getName()) ? true : false;
    }

    /**
     * 取类型中所有的属性,(包含基类以及基类的基类,一直到Object级别)
     *
     * @param clazz 类型
     * @return 属性列表
     */
    public static List<Field> getFieldList(Class<?> clazz) {
        if (null == clazz) {
            return null;
        }
        List<Field> fieldList = new LinkedList<Field>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            /** 过滤静态属性**/
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            /** 过滤transient 关键字修饰的属性**/
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            fieldList.add(field);
        }
        /** 处理父类字段**/
        Class<?> superClass = clazz.getSuperclass();
        if (superClass.equals(Object.class)) {
            return fieldList;
        }
        fieldList.addAll(getFieldList(superClass));
        return fieldList;
    }

    public static List<String> getFieldList(Class<?> clazz, String relations) {
        List<String> propertyNameList = Arrays.asList(relations.split(","));
        List<Field> fieldList = getFieldList(clazz);
        List<String> fieldNameList = new ArrayList<>();
        for (Field field : fieldList) {
            if (!isWrapType(field)) {
                fieldNameList.add(field.getName());
            }
        }
        fieldNameList.retainAll(propertyNameList);

        return fieldNameList;
    }
}
