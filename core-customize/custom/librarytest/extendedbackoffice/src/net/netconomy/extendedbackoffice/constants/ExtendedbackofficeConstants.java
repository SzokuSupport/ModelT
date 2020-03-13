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
public final class ExtendedbackofficeConstants extends GeneratedExtendedbackofficeConstants {

    public static final String EXTENSIONNAME = "extendedbackoffice";

    private ExtendedbackofficeConstants() {
        //empty to avoid instantiating this constant class
    }

    public static final class Configuration {

        public static final String EMPLOYEE_CREDENTIALS_EXPIRATION_DAYS = "employee.credentials.expiration.days";

        private Configuration() {
            //empty to avoid instantiating this constant class
        }
    }

}
