package net.netconomy.extendedbackoffice.authentication.checks;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author tim.ollmann@netconomy.net
 */
public interface UserAuthenticationChecker {

    void check(UserDetails userDetails, AbstractAuthenticationToken authenticationToken);

}
