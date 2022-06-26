package github.charon.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public enum SerializationType {

    KRYO((byte) 0x01, "kryo"),
    PROTOSTUFF((byte) 0x02, "protostuff"),
    HESSIAN((byte) 0X03, "hessian");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (SerializationType c : SerializationType.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

    public static byte getCode(String name) {
        for (SerializationType c : SerializationType.values()) {
            if (Objects.equals(c.getName(), name)) {
                return c.code;
            }
        }
        return (byte) 0x00;
    }

}
