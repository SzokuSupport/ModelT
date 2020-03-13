package net.netconomy.extendedbackoffice.composer;

import net.netconomy.extendedbackoffice.authentication.exception.PasswordsDoNotMatchException;

import com.hybris.cockpitng.composer.LoginFormComposer;
import com.hybris.cockpitng.handler.login.LoginInformationConfigData;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.impl.InputElement;

import static net.netconomy.extendedbackoffice.constants.ExtendedbackofficeWebConstants.RequestParameter.NEW_PASS;
import static net.netconomy.extendedbackoffice.constants.ExtendedbackofficeWebConstants.RequestParameter.NEW_PASS_CONFIRM;

/**
 * @author tim.ollmann@netconomy.net
 */
public class ExtendedBackofficeLoginFormComposer extends LoginFormComposer {

    private static final String SPRING_SECURITY_LAST_EXCEPTION = "SPRING_SECURITY_LAST_EXCEPTION";

    @Override
    protected void renderConfiguredFields(boolean updateFocus) {
        boolean shouldSetFocus = updateFocus;
        getConfiguredFieldPanel().getChildren().clear();
        if (CollectionUtils.isNotEmpty(this.getLoginInformationHandler().getConfiguration())) {
            final boolean showNewPasswordFields = showNewPasswordFields();
            for (LoginInformationConfigData c : getLoginInformationHandler().getConfiguration()) {
                if ((!NEW_PASS.equals(c.getKey()) && !NEW_PASS_CONFIRM.equals(c.getKey())) || showNewPasswordFields) {
                    shouldSetFocus = renderConfiguredField(shouldSetFocus, c);
                }
            }
        }
    }

    @Override
    protected void updateLabels() {
        getLanguageLabel().setValue(Labels.getLabel("login.language.label"));
        getAccessdenied().setValue(Labels.getLabel("login.accessdenied.message"));
        getLoginButton().setLabel(Labels.getLabel("login.button.label"));

        final Exception e = getLastException();
        final String errorMessage = (e != null && StringUtils.isNotEmpty(e.getLocalizedMessage()))
                                    ? e.getLocalizedMessage() : Labels.getLabel("login.error.message");
        getStatus().setValue(errorMessage);
    }

    private boolean showNewPasswordFields() {
        final Exception lastException = getLastException();
        return lastException instanceof CredentialsExpiredException
            || lastException instanceof PasswordsDoNotMatchException;
    }

    private Exception getLastException() {
        return (Exception) Executions.getCurrent().getSession().getAttribute(SPRING_SECURITY_LAST_EXCEPTION);
    }

    private boolean renderConfiguredField(boolean shouldSetFocus, LoginInformationConfigData c) {
        final Hbox hbox = new Hbox();
        final Div labelDiv = new Div();
        labelDiv.setSclass("labelRowCnt");
        final Div compDiv = new Div();
        compDiv.setSclass("compRowCnt");
        hbox.appendChild(labelDiv);
        hbox.appendChild(compDiv);
        getConfiguredFieldPanel().appendChild(hbox);
        final Label label = new Label(Labels.getLabel(c.getName()));
        label.setAttribute("labelKey", c.getName());
        labelDiv.appendChild(label);
        final Component view = this.getLoginInfoRenderer()
                                   .renderSetting(this.getLoginInformationHandler().getLoginInformation(), c.getKey(), false);
        if (view != null) {
            compDiv.appendChild(view);
            if (shouldSetFocus && view instanceof InputElement) {
                ((InputElement) view).setFocus(true);
                shouldSetFocus = false;
            }
        }
        return shouldSetFocus;
    }

}
