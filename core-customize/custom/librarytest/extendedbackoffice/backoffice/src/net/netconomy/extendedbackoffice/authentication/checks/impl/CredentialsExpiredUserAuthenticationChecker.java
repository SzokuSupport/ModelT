package net.netconomy.extendedbackoffice.authentication.checks.impl;

import net.netconomy.extendedbackoffice.authentication.checks.AbstractUserAuthenticationChecker;
import net.netconomy.extendedbackoffice.authentication.details.ExtendedBackofficeAuthenticationDetails;

import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

import static net.netconomy.extendedbackoffice.constants.ExtendedbackofficeConstants.Configuration.EMPLOYEE_CREDENTIALS_EXPIRATION_DAYS;

/**
 * @author tim.ollmann@netconomy.net
 */
public class CredentialsExpiredUserAuthenticationChecker extends AbstractUserAuthenticationChecker {

    private ConfigurationService configurationService;

    @Override
    public void check(final UserDetails userDetails, final AbstractAuthenticationToken authentication) {
        if (authentication.getDetails() instanceof ExtendedBackofficeAuthenticationDetails) {
            final EmployeeModel employee = getUserService().getUserForUID(userDetails.getUsername(), EmployeeModel.class);
            if (isCredentialsExpired(employee)) {
                final Locale currentLocale = getI18NService().getCurrentLocale();
                throw new CredentialsExpiredException(
                    getMessageSource().getMessage("CoreAuthenticationProvider.credentialsExpired", null, currentLocale));
            }
        }
    }

    private boolean isCredentialsExpired(final EmployeeModel employeeModel) {
        if (BooleanUtils.isTrue(employeeModel.getInitialPassword())) {
            return true;
        }
        final Integer expirationDays = getConfigurationService().getConfiguration().getInteger(EMPLOYEE_CREDENTIALS_EXPIRATION_DAYS, 0);
        if (expirationDays != null && expirationDays > 0) {
            Date lastPasswordChange = employeeModel.getLastPasswordChange();
            if (lastPasswordChange != null) {
                final LocalDate dateFrom = lastPasswordChange.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                final LocalDate dateTo = LocalDate.now(ZoneId.systemDefault());
                final Period intervalPeriod = Period.between(dateFrom, dateTo);
                return intervalPeriod.getDays() > expirationDays;
            } else {
                return true;
            }
        }
        return false;
    }

    protected ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Required
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
