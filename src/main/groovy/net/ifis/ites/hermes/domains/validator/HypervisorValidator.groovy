package net.ifis.ites.hermes.domains.validator

import net.ifis.ites.hermes.domains.management.Hypervisor
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.ValidationUtils
import org.springframework.validation.Validator

/**
 * Validation class for an Hypervisor entity.
 *
 * @author Andreas Sekulski
 */
@Component
public class HypervisorValidator implements Validator {

    @Override
    public boolean supports(Class clazz) {
        return Hypervisor.class.isAssignableFrom(clazz)
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "empty.string")
    }
}