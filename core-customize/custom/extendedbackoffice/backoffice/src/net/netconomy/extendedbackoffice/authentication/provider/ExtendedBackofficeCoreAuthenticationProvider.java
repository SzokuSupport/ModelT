package net.netconomy.extendedbackoffice.authentication.provider;

import net.netconomy.extendedbackoffice.authentication.checks.UserAuthenticationChecker;

import com.hybris.backoffice.spring.security.BackofficeAuthenticationProvider;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * @author tim.ollmann@netconomy.net
 */
public class ExtendedBackofficeCoreAuthenticationProvider extends BackofficeAuthenticationProvider {

    private List<UserAuthenticationChecker> additionalChecks;

    @Override
    protected void additionalAuthenticationChecks(final UserDetails details, final AbstractAuthenticationToken authentication) {
        if (CollectionUtils.isNotEmpty(getAdditionalChecks())) {
            getAdditionalChecks().forEach(additionalCheck -> additionalCheck.check(details, authentication));
        }
    }

    protected List<UserAuthenticationChecker> getAdditionalChecks() {
        return additionalChecks;
    }

    @Required
    public void setAdditionalChecks(List<UserAuthenticationChecker> additionalChecks) {
        this.additionalChecks = additionalChecks;
    }
}
