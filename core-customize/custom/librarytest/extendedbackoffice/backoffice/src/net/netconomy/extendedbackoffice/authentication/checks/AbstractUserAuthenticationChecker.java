package net.netconomy.extendedbackoffice.authentication.checks;

import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;

/**
 * @author tim.ollmann@netconomy.net
 */
public abstract class AbstractUserAuthenticationChecker implements UserAuthenticationChecker {

    private UserService userService;
    private MessageSource messageSource;
    private I18NService i18NService;

    protected UserService getUserService() {
        return userService;
    }

    @Required
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    protected MessageSource getMessageSource() {
        return messageSource;
    }

    @Required
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    protected I18NService getI18NService() {
        return i18NService;
    }

    @Required
    public void setI18NService(I18NService i18NService) {
        this.i18NService = i18NService;
    }

}
