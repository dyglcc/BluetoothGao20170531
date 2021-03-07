package com.xiaobailong_student.widget;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class helloTest {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        LinkedHashMap map = new LinkedHashMap();
        map.put(100,"abc");
        map.put(1,"a");
        map.put(10,"cc");
//        for(Object ent:map.entrySet()){
//            System.out.println(((Map.Entry)ent).getValue());
//        }
        System.out.println(getTailByReflection(map).getValue());
    }

    public static <K, V> Map.Entry<K, V> getTailByReflection(LinkedHashMap<K, V> map)
            throws NoSuchFieldException, IllegalAccessException {
        Field tail = map.getClass().getDeclaredField("tail");
        tail.setAccessible(true);
        return (Map.Entry<K, V>) tail.get(map);
    }
}
