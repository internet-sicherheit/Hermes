package net.ifis.ites.hermes.utils.exception

import org.springframework.validation.FieldError

/**
 * Class to create an invalid entity exception to store errors or an error message.
 *
 * @author Andreas Sekulski
 */
class InvalidEntityException extends Exception {

    /** An list from all field errors **/
    private List<FieldError> errors

    /**
     * Constructor to create an invalid entity exception with an given error list.
     *
     * @param errors - List from all field errors
     */
    public InvalidEntityException(List<FieldError> errors) {
        super()
        this.errors = errors
    }

    /**
     * Constructor to create an invalid entity exception with an given message.
     *
     * @param message - Error message text
     */
    public InvalidEntityException(String message) {
        super(message)
    }

    /**
     * Gets an error list if exists.
     * @return An list from all errors or null if not init.
     */
    public List<FieldError> getErrors() {
        return errors
    }

    @Override
    public String getMessage() {
        return super.getMessage()
    }
}