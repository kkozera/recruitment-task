package pl.kkozera.recruitment_task.configuration.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AllowedSortFields {
    String[] value();
}