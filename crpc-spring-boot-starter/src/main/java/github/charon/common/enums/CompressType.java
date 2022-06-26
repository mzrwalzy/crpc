package github.charon.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CompressType {

    GZIP((byte) 0x01, "gzip");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (CompressType c : CompressType.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }


}
