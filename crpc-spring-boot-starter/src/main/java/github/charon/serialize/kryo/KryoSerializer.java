package github.charon.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import github.charon.common.exception.SerializeException;
import github.charon.remote.dto.RpcRequest;
import github.charon.remote.dto.RpcResponse;
import github.charon.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer implements Serializer {

    /**
     * 非线程安全，使用threadlocal存储kryo队形
     */
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {
            Kryo kryo = kryoThreadLocal.get();
//            RpcRequest r = (RpcRequest) obj;
//            KryoRegister(r.getParamTypes(), kryo);
            // Object->byte:将对象序列化为byte数组
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
//            e.printStackTrace();
//            throw new SerializeException("Serialization failed");
            throw new SerializeException(e.getMessage());
        }
    }

    // kryo5.2.0版本及以上使用
    private void KryoRegister(Class<?>[] paramTypes, Kryo kryo) {
        kryo.register(java.lang.Class[].class);
        kryo.register(java.lang.Class.class);
        kryo.register(java.lang.Object[].class);
        for (Class<?> paramType : paramTypes) {
            kryo.register(paramType);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> klass) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
//            Kryo kryo = new Kryo();
//            kryo.register(RpcResponse.class);
//            kryo.register(RpcRequest.class);
            Kryo kryo = kryoThreadLocal.get();
            // byte->Object:从byte数组中反序列化出对对象
            Object o = kryo.readObject(input, klass);
            kryoThreadLocal.remove();
            return klass.cast(o);
        } catch (Exception e) {
            throw new SerializeException("Deserialization failed");
        }
    }
}
