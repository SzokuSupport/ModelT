package net.netconomy.extendedbackoffice.renderer;

import com.hybris.cockpitng.composer.DefaultLoginInfoRenderer;
import com.hybris.cockpitng.core.util.impl.TypedSettingsMap;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Textbox;

import static net.netconomy.extendedbackoffice.constants.ExtendedbackofficeWebConstants.ClientAttibute.Key.AUTCOMPLETE;
import static net.netconomy.extendedbackoffice.constants.ExtendedbackofficeWebConstants.ClientAttibute.Value.OFF;


/**
 * @author tim.ollmann@netconomy.net
 */
public class ExtendedBackofficeLoginInfoRenderer extends DefaultLoginInfoRenderer {

    private static final String SPRING_LIKE_NEWPASS = "newPassword";
    private static final String SPRING_LIKE_NEWPASS_CONFIRM = "newPasswordConfirm";
    private static final String TEXTBOX_TYPE_PASS = "password";

    @Override
    protected Component renderStringInput(TypedSettingsMap widgetSettings, Object settingValue, String settingKey, boolean hasConnections) {
        final Textbox textBox = (Textbox) super.renderStringInput(widgetSettings, settingValue, settingKey, hasConnections);
        if (SPRING_LIKE_NEWPASS.equals(settingKey) || SPRING_LIKE_NEWPASS_CONFIRM.equals(settingKey)) {
            textBox.setType(TEXTBOX_TYPE_PASS);
            textBox.setClientAttribute(AUTCOMPLETE, OFF);
        }
        return textBox;
    }

}
