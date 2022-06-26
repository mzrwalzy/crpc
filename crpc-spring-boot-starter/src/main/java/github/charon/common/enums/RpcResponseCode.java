package github.charon.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum RpcResponseCode {
    SUCCESS(0, "remote call successful"),
    FAIL(1, "remote call failed");

    private final int code;
    private final String message;
}
