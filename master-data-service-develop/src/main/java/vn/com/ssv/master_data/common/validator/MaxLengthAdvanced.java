package vn.com.ssv.master_data.common.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MaxLengthAdvancedValidator.class)
@Documented
public @interface MaxLengthAdvanced {
    int value();                    // max length

    String fieldName() default "";  // tên field hiển thị trong message

    String message() default "{fieldName} không đươc quá {value} kí tự"; // dùng i18n key

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}