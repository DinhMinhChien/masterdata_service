package vn.com.ssv.master_data.common.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinOneMaxDigitsValidator.class)
@Documented
public @interface MinOneMaxDigits {

    int max(); // số chữ số tối đa

    String fieldName(); // tên trường để tự sinh message

    String message() default ""; // sẽ bỏ qua, tự generate trong validator

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
