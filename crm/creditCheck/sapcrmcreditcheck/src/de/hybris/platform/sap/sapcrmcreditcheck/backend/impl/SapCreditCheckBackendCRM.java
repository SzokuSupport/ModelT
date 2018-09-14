/*
 *
 *  [y] hybris Platform
 *
 *  Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 *  This software is the confidential and proprietary information of SAP
 *  ("Confidential Information"). You shall not disclose such Confidential
 *  Information and shall use it only in accordance with the terms of the
 *  license agreement you entered into with SAP.
 * /
 */

/**
 *
 */
package de.hybris.platform.sap.sapcrmcreditcheck.backend.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.core.bol.backend.jco.BackendBusinessObjectBaseJCo;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.connection.JCoManagedConnectionFactory;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.sapcreditcheck.backend.SapCreditCheckBackend;
import de.hybris.platform.sap.sapcreditcheck.constants.SapcreditcheckConstants;
import de.hybris.platform.b2b.services.B2BUnitService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

/**
 * @author c5230244
 *
 */
@BackendType("CRM")
public class SapCreditCheckBackendCRM extends BackendBusinessObjectBaseJCo implements SapCreditCheckBackend {
    static final private Logger sapLogger = Logger.getLogger(SapCreditCheckBackendCRM.class.getName());
    private JCoManagedConnectionFactory managedConnectionFactory;

    private ModuleConfigurationAccess moduleConfigurationAccess;

    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;

    /**
     * @return the b2BUnitService
     */
    public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2BUnitService() {
        return b2BUnitService;
    }

    /**
     * @param b2bUnitService
     *            the b2BUnitService to set
     */

    public void setB2BUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService) {
        b2BUnitService = b2bUnitService;
    }

    @Override
    public ModuleConfigurationAccess getModuleConfigurationAccess() {
        return moduleConfigurationAccess;
    }

    @Override
    @Required
    public void setModuleConfigurationAccess(final ModuleConfigurationAccess moduleConfigurationAccess) {
        this.moduleConfigurationAccess = moduleConfigurationAccess;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.sap.sapcreditcheck.backend.SapCreditCheckBackend#
     * checkCreditLimitExceeded(java.lang.String)
     */
    @Override
    public boolean checkOrderCreditBlocked(final String orderCode) throws BackendException {

        final JCoConnection connection = getDefaultJCoConnection();
        final JCoFunction function = connection.getFunction(SapcreditcheckConstants.ERP_LORD_GET_ALL);
        final JCoParameterList importParameterList = function.getImportParameterList();

        importParameterList.setValue("IV_VBELN", convertOrderCode(orderCode));
        importParameterList.setValue("IV_TRTYP", SapcreditcheckConstants.IV_TRTYP);

        if (sapLogger.isDebugEnabled()) {
            sapLogger.debug(String.format("Calling the FM: ERP_LORD_GET_ALL to read the credit status for order %s!",
                    orderCode));
            sapLogger.debug("The import parameters are: " + importParameterList.toString());
        }

        connection.execute(function);

        final JCoStructure returnStructure = function.getExportParameterList().getStructure("ES_HEAD_VSTAT_COMV");

        final String creditStatus = returnStructure.getString("CMGST");

        if (!StringUtils.isEmpty(creditStatus)
                && SapcreditcheckConstants.CREDIT_BLOCKED_STATUS.contentEquals(creditStatus)) {

            sapLogger.info(String.format("The order %s has a blocked credit check status in ERP!", orderCode));
            return true;

        } else {
            return false;
        }

    }

    @Override
    public boolean checkCreditLimitExceeded(final AbstractOrderData orderData, final String soldTo)
            throws BackendException {

        final JCoConnection connection = getDefaultJCoConnection();
        final JCoFunction function = connection.getFunction(SapcreditcheckConstants.BAPI_CREDITCHECK);
        final JCoTable inputTable = function.getTableParameterList().getTable("IT_CREDITLINES");

        final String erpCustomerId = getERPCustomerID(soldTo);
        // Fill input table
        inputTable.appendRow();
        inputTable.setValue("CREDITGROUP", SapcreditcheckConstants.CREDIT_GROUP);
        if (!StringUtils.isEmpty(erpCustomerId)) {
            inputTable.setValue("PAYER", convertSoldTo(erpCustomerId));
        }
        inputTable.setValue("SALES_ORG", getProperty("saperp_salesOrganization"));
        inputTable.setValue("DIS_CHANNEL", getProperty("saperp_distributionChannel"));
        inputTable.setValue("DIVISION", getProperty("saperp_division"));
        inputTable.setValue("CURRENCY", orderData.getTotalPrice().getCurrencyIso());
        inputTable.setValue("NET_VALUE", orderData.getTotalPrice().getValue().doubleValue());
        inputTable.setValue("TAX_VALUE", orderData.getTotalTax().getValue().doubleValue());

        if (sapLogger.isDebugEnabled()) {
            sapLogger.debug(String.format(
                    "Calling the FM BAPI_CREDITCHECK to check the credit limit for the order %s placed by the user %s!",
                    orderData.getCode(), soldTo));
            sapLogger.debug("The import parameters are: " + inputTable.toString());
        }

        // Execute
        connection.execute(function);

        // Get output table
        final JCoTable outputTable = function.getTableParameterList().getTable("ET_CREDITLINES");

        // Read output table
        outputTable.firstRow();

        // Initial value assuming the credit limit has not been exceeded
        boolean creditExceeded = false;

        // Credit limit exceeded
        if ("V1".equalsIgnoreCase(outputTable.getString("ID"))
                && ("150".equals(outputTable.getString("NUMBER")) || "152".equals(outputTable.getString("NUMBER")))) {
            /**
             * Based on the dynamic credit check reaction in the transaction
             * OVA8, we can get one of the following error messages: A - Warning
             * B - Error message -> message 150 C - Like A + value by which the
             * credit limit has been exceeded D - Like B + value by which the
             * credit limit has been exceeded -> message 152
             **/
            creditExceeded = true;
            sapLogger.info("Credit limit check failed for order " + orderData.getCode() + "! "
                    + outputTable.getString("MESSAGE"));
        }
        // Getting an error message that blocks the order creation, such as
        // missing mandatory configurations in the ERP back-end
        else if (!outputTable.getString("ERROR").isEmpty()) {
            creditExceeded = true;
            sapLogger.error("Getting an error message while checking the credit limit for order " + orderData.getCode()
                    + "! " + outputTable.getString("MESSAGE"));

        }
        // Getting a warning message that does not block the order creation,
        // such as missing optional configurations in the ERP back-end
        else if (!outputTable.getString("WARNING").isEmpty()) {
            sapLogger.warn("Getting a warning message while checking the credit limit for order " + orderData.getCode()
                    + "! " + outputTable.getString("MESSAGE"));

        }
        // Credit limit check passed successfully
        else {
            sapLogger.info("Credit limit check passed for order " + orderData.getCode());

        }
        return creditExceeded;
    }

    public String getERPCustomerID(final String soldTo) {

        String erpCustomerId = null;
        final B2BUnitModel b2bUnit = getB2BUnitService().getUnitForUid(soldTo);
        if (b2bUnit != null) {
            erpCustomerId = b2bUnit.getErpcustomerid();
        }
        return erpCustomerId;
    }

    private String convertOrderCode(final String code) {
        if (code.matches("\\d+")) {

            return String.format("%010d", Integer.valueOf(code));

        }
        return code;
    }

    // Pad the leading zeros if the soldTo ID is numeric
    private String convertSoldTo(final String soldTo) {
        if (soldTo.matches("\\d+")) {

            return String.format("%010d", Integer.valueOf(soldTo));

        }
        return soldTo;
    }

    public JCoManagedConnectionFactory getManagedConnectionFactory() {
        return managedConnectionFactory;
    }

    @Required
    public void setManagedConnectionFactory(final JCoManagedConnectionFactory managedConnectionFactory) {
        this.managedConnectionFactory = managedConnectionFactory;
    }

    protected String getProperty(final String name) {
        final Object propertyValue = getModuleConfigurationAccess().getProperty(name);
        return (String) propertyValue;
    }

}
