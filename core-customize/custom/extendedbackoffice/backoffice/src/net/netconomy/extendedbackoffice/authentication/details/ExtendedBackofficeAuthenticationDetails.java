package net.netconomy.extendedbackoffice.authentication.details;

import net.netconomy.extendedbackoffice.constants.ExtendedbackofficeWebConstants;

import com.hybris.cockpitng.util.web.authentication.BackofficeAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * @author tim.ollmann@netconomy.net
 */
public class ExtendedBackofficeAuthenticationDetails extends BackofficeAuthenticationDetails {

    private final String newPassword;
    private final String newPasswordConfirm;

    public ExtendedBackofficeAuthenticationDetails(HttpServletRequest request, String localeRequestParameter) {
        super(request, localeRequestParameter);
        this.newPassword = request.getParameter(ExtendedbackofficeWebConstants.RequestParameter.NEW_PASS);
        this.newPasswordConfirm = request.getParameter(ExtendedbackofficeWebConstants.RequestParameter.NEW_PASS_CONFIRM);
    }

    public String getNewPassword() {
        return this.newPassword;
    }

    public String getNewPasswordConfirm() {
        return this.newPasswordConfirm;
    }

}
