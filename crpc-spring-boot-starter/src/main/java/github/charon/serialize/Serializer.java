package github.charon.serialize;

import github.charon.common.extension.SPI;

@SPI
public interface Serializer {
    byte[] serialize(Object obj);

    <T> T deserialize(byte[] bytes, Class<T> klass);
}
