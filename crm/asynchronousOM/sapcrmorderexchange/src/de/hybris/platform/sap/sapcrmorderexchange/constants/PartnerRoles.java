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
package de.hybris.platform.sap.sapcrmorderexchange.constants;

/**
 * 
 * Partner roles for sales order processing as defined in SAP ERP. Corresponds to SAP defined entries of table TPAR
 * 
 */
public enum PartnerRoles
{

	@SuppressWarnings("javadoc")
	SOLD_TO("00000001"), @SuppressWarnings("javadoc")
	CONTACT("00000015"), @SuppressWarnings("javadoc")
	SHIP_TO("00000002"), @SuppressWarnings("javadoc")
	BILL_TO("00000003"), @SuppressWarnings("javadoc")
	PLACED_BY("00000012");

	private String code;

	private PartnerRoles(final String code)
	{
		this.code = code;
	}

	@SuppressWarnings("javadoc")
	public String getCode()
	{
		return code;
	}
}
