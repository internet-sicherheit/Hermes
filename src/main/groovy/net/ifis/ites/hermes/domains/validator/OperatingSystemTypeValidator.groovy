package net.ifis.ites.hermes.domains.validator

import net.ifis.ites.hermes.domains.management.OperatingSystemType
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.ValidationUtils
import org.springframework.validation.Validator

/**
 * Validation class for an OperatingSystemType entity.
 *
 * @author Andreas Sekulski
 */
@Component
class OperatingSystemTypeValidator implements Validator {

    @Override
    boolean supports(Class clazz) {
        return OperatingSystemType.class.isAssignableFrom(clazz)
    }

    @Override
    void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "empty.string")
    }
}