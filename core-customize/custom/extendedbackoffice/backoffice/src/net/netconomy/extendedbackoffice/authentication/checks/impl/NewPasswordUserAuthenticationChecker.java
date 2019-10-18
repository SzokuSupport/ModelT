package net.netconomy.extendedbackoffice.authentication.checks.impl;

import net.netconomy.extendedbackoffice.authentication.checks.AbstractUserAuthenticationChecker;
import net.netconomy.extendedbackoffice.authentication.details.ExtendedBackofficeAuthenticationDetails;
import net.netconomy.extendedbackoffice.authentication.exception.PasswordsDoNotMatchException;

import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Locale;

/**
 * @author tim.ollmann@netconomy.net
 */
public class NewPasswordUserAuthenticationChecker extends AbstractUserAuthenticationChecker {

    private ModelService modelService;

    @Override
    public void check(final UserDetails userDetails, final AbstractAuthenticationToken authentication) {
        if (authentication.getDetails() instanceof ExtendedBackofficeAuthenticationDetails) {
            final ExtendedBackofficeAuthenticationDetails details = (ExtendedBackofficeAuthenticationDetails) authentication.getDetails();
            if (StringUtils.isNotBlank(details.getNewPassword())) {
                if (!StringUtils.equals(details.getNewPassword(), details.getNewPasswordConfirm())) {
                    final Locale currentLocale = getI18NService().getCurrentLocale();
                    throw new PasswordsDoNotMatchException(
                        getMessageSource().getMessage("CoreAuthenticationProvider.passwordsDoNotMatch", null, currentLocale));
                }
                final EmployeeModel employee = getUserService().getUserForUID(userDetails.getUsername(), EmployeeModel.class);
                getUserService().setPassword(employee, details.getNewPassword());
                employee.setInitialPassword(Boolean.FALSE);
                getModelService().save(employee);
            }
        }
    }

    protected ModelService getModelService() {
        return modelService;
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

}
