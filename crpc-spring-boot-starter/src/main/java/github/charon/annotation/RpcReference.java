package github.charon.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RpcReference {
    String version() default "";

    /**
     * Service group, default value is empty string
     */
    String group() default "";
}
