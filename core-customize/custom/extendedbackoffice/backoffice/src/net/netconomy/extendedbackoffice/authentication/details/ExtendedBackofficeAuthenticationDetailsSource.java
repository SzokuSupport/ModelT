package net.netconomy.extendedbackoffice.authentication.details;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.AuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;

/**
 * @author tim.ollmann@netconomy.net
 */
public class ExtendedBackofficeAuthenticationDetailsSource
    implements AuthenticationDetailsSource<HttpServletRequest, ExtendedBackofficeAuthenticationDetails> {

    private String localeRequestParameter;

    public ExtendedBackofficeAuthenticationDetails buildDetails(HttpServletRequest request) {
        return new ExtendedBackofficeAuthenticationDetails(request, this.localeRequestParameter);
    }

    @Required
    public void setLocaleRequestParameter(String localeRequestParameter) {
        this.localeRequestParameter = localeRequestParameter;
    }
}
