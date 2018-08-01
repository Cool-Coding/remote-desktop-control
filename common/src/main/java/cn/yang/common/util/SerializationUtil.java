package cn.yang.common.util;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: cool coding
 * @date: 2018/1/7
 * Time:21:37
 * Protostuff序列化与反序列化工具
 * Objenesis 来实例化对象，它是比 Java 反射更加强大
 */
public class SerializationUtil {

    private static Map<Class<?>,Schema<?>> cachedSchema=new ConcurrentHashMap<>();
    private static Objenesis objenesis = new ObjenesisStd(true);
    private SerializationUtil() {
    }

    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> cls){
        Schema<T> schema=(Schema<T>)cachedSchema.get(cls);
        if(schema==null){
            schema= RuntimeSchema.createFrom(cls);
            if(schema != null){
                cachedSchema.put(cls,schema);
            }
        }
        return schema;
    }

    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T obj){
        Class<T> cls=(Class<T>)obj.getClass();
        LinkedBuffer buffer= LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try{
            Schema schema=getSchema(cls);
            return ProtostuffIOUtil.toByteArray(obj,schema,buffer);
        }catch(Exception e){
            throw new IllegalStateException(e.getMessage(),e);
        }finally {
            buffer.clear();
        }
    }

    public static <T> byte[] JDKSerialize(T obj) throws IOException{
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        return byteArrayOutputStream.toByteArray();
    }

    public static <T> T deSerialize(byte[] data,Class<T> cls){
        try{
            T message = objenesis.newInstance(cls);
            Schema<T> schema = getSchema(cls);
            ProtostuffIOUtil.mergeFrom(data, message, schema);
            return message;
        }catch (Exception e){
            throw new IllegalStateException(e.getMessage(),e);
        }
    }

    public static <T> T JDKDeSerialize(byte[] data,Class<T> cls) throws IOException,ClassNotFoundException{
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        final ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return (T)objectInputStream.readObject();
    }
}
