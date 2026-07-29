package vn.com.ssv.master_data.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;

public class AtLeastOneValidator implements ConstraintValidator<AtLeastOne, Collection<?>> {

    @Override
    public boolean isValid(Collection<?> value, ConstraintValidatorContext context) {
        boolean valid = value != null && !value.isEmpty();

        if (!valid) {
            context.disableDefaultConstraintViolation();

            // Lấy tên field từ context
            String fieldName = context.getDefaultConstraintMessageTemplate();
            // Nếu không có, dùng "Trường này"
            if (fieldName == null || fieldName.isEmpty()) {
                fieldName = "Trường này";
            }

            context.buildConstraintViolationWithTemplate(
                    "Phải có ít nhất 1 " + fieldName
            ).addConstraintViolation();
        }

        return valid;
    }
}
