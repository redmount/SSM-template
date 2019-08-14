package com.redmount.template.util;

import com.redmount.template.core.BaseDO;
import com.redmount.template.core.annotation.Keywords;
import com.redmount.template.core.annotation.RelationData;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ReflectUtil {

    public static Field getRelationDataField(Class clazz, String baseDOName) {
        Field[] fields = clazz.getDeclaredFields();
        RelationData annotation;
        for (Field field : fields) {
            annotation = field.getAnnotation(RelationData.class);
            if (annotation != null) {
                if (annotation.isRelation() && annotation.baseDOTypeName().equals(baseDOName)) {
                    return field;
                }
            }
        }
        return null;
    }

    public static boolean containsProperty(Class clazz, String propertyName) {
        if (null == clazz) {
            return false;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(propertyName)) {
                return true;
            }
        }
        // 处理父类字段
        Class<?> superClass = clazz.getSuperclass();
        if (superClass.equals(Object.class)) {
            return false;
        }
        return containsProperty(superClass, propertyName);
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
     */
    public static <T> T cloneObj(Object obj, Class<T> toResult) {
        if (obj == null) {
            return null;
        }
        try {
            T t = toResult.newInstance();
            List<Field> fields = getFieldList(obj.getClass());
            for (Field field : fields) {
                field.setAccessible(true);//修改访问权限
                if (Modifier.isFinal(field.getModifiers()))
                    continue;
                if (isWrapType(field)) {
                    String firstLetter = field.getName().substring(0, 1).toUpperCase(); // 首字母大写
                    String getMethodName = "get" + firstLetter + field.getName().substring(1);
                    String setMethodName = "set" + firstLetter + field.getName().substring(1);
                    Method getMethod = obj.getClass().getMethod(getMethodName);   //从源对象获取get方法
                    Method setMethod = toResult.getMethod(setMethodName, field.getType()); //从目标对象获取set方法

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
                    setMethod.invoke(t, value); // set 设置的是目标对象的值
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
    public static boolean isWrapType(Field field) {
        String[] types = {"java.lang.Integer", "java.lang.Double", "java.lang.Float", "java.lang.Long",
                "java.lang.Short", "java.lang.Byte", "java.lang.Boolean", "java.lang.Char", "java.lang.String", "int",
                "double", "long", "short", "byte", "boolean", "char", "float", "java.util.Date", "java.math.BigDecimal"};
        List<String> typeList = Arrays.asList(types);
        return typeList.contains(field.getType().getName());
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
            // 过滤静态属性
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            // 过滤transient 关键字修饰的属性
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            fieldList.add(field);
        }
        // 处理父类字段
        Class<?> superClass = clazz.getSuperclass();
        if (superClass.equals(Object.class)) {
            return fieldList;
        }
        fieldList.addAll(getFieldList(superClass));
        return fieldList;
    }

    public static List<String> getFieldListNamesList(Class<?> clazz) {
        List<String> retList = new ArrayList<>();
        if (null == clazz) {
            return retList;
        }
        // List<Field> fieldList = new LinkedList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 过滤静态属性
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            // 过滤transient 关键字修饰的属性
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            retList.add(field.getName());
        }
        // 处理父类字段
        Class<?> superClass = clazz.getSuperclass();
        if (superClass.equals(Object.class)) {
            return retList;
        }
        retList.addAll(getFieldListNamesList(superClass));
        return retList;
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

    public static Object getValueByName(Object source, String propertyName) {
        String firstLetter = propertyName.substring(0, 1).toUpperCase(); // 首字母大写
        String getMethodName = "get" + firstLetter + propertyName.substring(1);
        Object value = null;
        try {
            Method getMethod = source.getClass().getDeclaredMethod(getMethodName);
            getMethod.setAccessible(true);
            value = getMethod.invoke(source); // get 获取的是源对象的值
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            return value;
        }
    }

    public static void setValueByName(Object target, String propertyName, Object value) {
        String firstLetter = propertyName.substring(0, 1).toUpperCase(); // 首字母大写
        String setMethodName = "set" + firstLetter + propertyName.substring(1);
        try {
            Method setMethod = target.getClass().getDeclaredMethod(setMethodName);
            setMethod.setAccessible(true);
            setMethod.invoke(target, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 循环向上转型, 获取对象的 DeclaredMethod
     *
     * @param object         : 子类对象
     * @param methodName     : 父类中的方法名
     * @param parameterTypes : 父类中的方法参数类型
     * @return 父类中的方法对象
     */
    private static Method getDeclaredMethod(Object object, String methodName, Class<?>... parameterTypes) {
        Method method;
        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                method = clazz.getDeclaredMethod(methodName, parameterTypes);
                return method;
            } catch (Exception e) {
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
            }
        }
        return null;
    }

    /**
     * 直接调用对象方法, 而忽略修饰符(private, protected, default)
     *
     * @param object         : 子类对象
     * @param methodName     : 父类中的方法名
     * @param parameterTypes : 父类中的方法参数类型
     * @param parameters     : 父类中的方法参数
     * @return 父类中方法的执行结果
     */
    public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes,
                                      Object[] parameters) {
        //根据 对象、方法名和对应的方法参数 通过反射 调用上面的方法获取 Method 对象
        Method method = getDeclaredMethod(object, methodName, parameterTypes);

        //抑制Java对方法进行检查,主要是针对私有方法而言
        assert method != null;
        method.setAccessible(true);

        try {
            //调用object 的 method 所代表的方法，其方法的参数是 parameters
            return method.invoke(object, parameters);
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 循环向上转型, 获取对象的 DeclaredField
     *
     * @param object    : 子类对象
     * @param fieldName : 父类中的属性名
     * @return 父类中的属性对象
     */
    public static Field getDeclaredField(Object object, String fieldName) {
        Field field;
        Class<?> clazz = object.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                return field;
            } catch (Exception e) {
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
            }
        }
        return null;
    }

    /**
     * 直接设置对象属性值, 忽略 private/protected 修饰符, 也不经过 setter
     *
     * @param object    : 子类对象
     * @param fieldName : 父类中的属性名
     * @param value     : 将要设置的值
     */
    public static void setFieldValue(Object object, String fieldName, Object value) {
        //根据 对象和属性名通过反射 调用上面的方法获取 Field对象
        Field field = getDeclaredField(object, fieldName);
        //抑制Java对其的检查
        assert field != null;
        field.setAccessible(true);
        try {
            //将 object 中 field 所代表的值 设置为 value
            field.set(object, value);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 直接读取对象的属性值, 忽略 private/protected 修饰符, 也不经过 getter
     *
     * @param object    : 子类对象
     * @param fieldName : 父类中的属性名
     * @return : 父类中的属性值
     */
    public static Object getFieldValue(Object object, String fieldName) {

        //根据 对象和属性名通过反射 调用上面的方法获取 Field对象
        Field field = getDeclaredField(object, fieldName);
        //抑制Java对其的检查
        assert field != null;
        field.setAccessible(true);
        try {
            //获取 object 中 field 所代表的属性值
            return field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isRelationMap(Class<?> clazz, String propertyName) {
        List<Field> fieldList = getFieldList(clazz);
        for (Field field : fieldList) {
            if (field.getName().equals(propertyName)) {
                if (field.getType().getName().startsWith("java.util.Map")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<Field> getRelationFields(Object object) {
        Class clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(RelationData.class) != null) {
                fieldList.add(field);
            }

        }
        return fieldList;
    }

    public static <T extends BaseDO> List<Field> getKeywordsFields(Class<T> modelClass) {
        Field[] fields = modelClass.getDeclaredFields();
        List<Field> ret = new ArrayList<>();
        Keywords keywordsAnnotation;
        for (Field field : fields) {
            keywordsAnnotation = field.getAnnotation(Keywords.class);
            if (keywordsAnnotation != null) {
                if (keywordsAnnotation.value()) {
                    ret.add(field);
                }
            }
        }
        return ret;
    }
}
