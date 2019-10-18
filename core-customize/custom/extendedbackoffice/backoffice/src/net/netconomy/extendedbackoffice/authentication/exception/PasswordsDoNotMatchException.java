package net.netconomy.extendedbackoffice.authentication.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author tim.ollmann@netconomy.net
 */
public class PasswordsDoNotMatchException extends AuthenticationException {

    public PasswordsDoNotMatchException(String msg) {
        super(msg);
    }

}
