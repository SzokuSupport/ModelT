/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */

package net.netconomy.extendedbackoffice.constants;

/**
 * Global class for all Ybackoffice constants. You can add global constants for your extension into this class.
 */
public final class ExtendedbackofficeWebConstants {

    private ExtendedbackofficeWebConstants() {
        //empty to avoid instantiating this constant class
    }

    public static final class ClientAttibute {

        private ClientAttibute() {
            //empty to avoid instantiating this constant class
        }

        public static final class Key {

            public static final String AUTCOMPLETE = "autocomplete";

            private Key() {
                //empty to avoid instantiating this constant class
            }

        }

        public static final class Value {

            public static final String OFF = "off";

            private Value() {
                //empty to avoid instantiating this constant class
            }
        }
    }

    public static final class RequestParameter {

        public static final String NEW_PASS = "newPassword";
        public static final String NEW_PASS_CONFIRM = "newPasswordConfirm";

        private RequestParameter() {
            //empty to avoid instantiating this constant class
        }

    }

}
