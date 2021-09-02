package com.hepiplant.backend.validator;

import com.hepiplant.backend.exception.InvalidBeanException;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Component
public class BeanValidator {

    private final Validator validator;

    public BeanValidator(Validator validator) {
        this.validator = validator;
    }

    public <T> void validate(T bean) {
        Set<ConstraintViolation<T>> violations = validator.validate(bean);
        if(!violations.isEmpty()){
            StringBuilder sb = new StringBuilder("Provided " + bean.getClass().getSimpleName() + " is not valid: ");
            for (ConstraintViolation<T> constraintViolation : violations) {
                sb.append("\n\t")
                    .append(constraintViolation.getPropertyPath())
                    .append(": ")
                    .append(constraintViolation.getMessage());
            }
            throw new InvalidBeanException(sb.toString());
        }
    }

}
