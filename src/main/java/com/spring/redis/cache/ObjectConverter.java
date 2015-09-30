package com.spring.redis.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 *
 * desc: 对象序列化与反序列化
 */
public class ObjectConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectConverter.class);

    //对象序列化为字符串
    public static String objectSerialization(Object obj){
        String serStr = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            serStr = byteArrayOutputStream.toString("ISO-8859-1");
            serStr = java.net.URLEncoder.encode(serStr, "UTF-8");

            objectOutputStream.close();
            byteArrayOutputStream.close();
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("object serialization UnsupportedEncodingException ERROR",e);
        } catch (IOException e) {
            LOGGER.error("object serialization IOException ERROR", e);
        }

        return serStr;
    }

    //字符串反序列化为对象
    public static Object objectDeserialization(String serStr){
        Object newObj = null;
        try {
            String redStr = java.net.URLDecoder.decode(serStr, "UTF-8");
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            newObj = objectInputStream.readObject();
            objectInputStream.close();
            byteArrayInputStream.close();
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("object deserialization UnsupportedEncodingException ERROR", e);
        } catch (ClassNotFoundException e) {
            LOGGER.error("object deserialization ClassNotFoundException ERROR",e);
        } catch (IOException e) {
            LOGGER.error("object deserialization IOException ERROR",e);
        }
        return newObj;
    }
}
