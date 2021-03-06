package org.cobbzilla.wizard.validation;

import org.joda.time.format.DateTimeFormat;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static org.cobbzilla.util.daemon.ZillaRuntime.empty;
import static org.cobbzilla.util.daemon.ZillaRuntime.now;

public class AgeMinValidator implements ConstraintValidator<AgeMin, Object> {

    private long min;
    private String format;
    private boolean emptyOk;

    @Override public void initialize(AgeMin constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.format = constraintAnnotation.format();
        this.emptyOk = constraintAnnotation.emptyOk();
    }

    @Override public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (empty(value)) return emptyOk;
        if (!empty(format) && value instanceof String) {
            return now() - DateTimeFormat.forPattern(format).parseMillis(value.toString()) >= min;
        } else if (value instanceof Number) {
            return now() - ((Number) value).longValue() >= min;
        } else {
            return isValid(Long.parseLong(value.toString()), context);
        }
    }
}
