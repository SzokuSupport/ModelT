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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.crm;

import de.hybris.platform.sap.core.bol.backend.jco.BackendBusinessObjectBaseJCo;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.message.MessageList;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.interf.FreeGoodSupportBackend;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sap.conn.jco.JCoTable;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.BackendState;


/**
 * Superclass of R3Lrd documents, containing common functionality
 *
 */
public abstract class IsaBackendBusinessObjectBaseSalesCRM extends BackendBusinessObjectBaseJCo implements BackendState
{

	static final private Log4JWrapper sapLogger = Log4JWrapper.getInstance(IsaBackendBusinessObjectBaseSalesCRM.class.getName());

	/**
	 * Map of price attribute maps for items
	 */
	protected Map<String, Map<String, String>> itemsPriceAttribMap = null;

	/**
	 * Map of price attributes for the header
	 */
	protected Map<String, String> headerPriceAttribs = null;

	/**
	 * Map of price properties for items
	 */
	protected Map<String, String> itemsPropMap = null;

	/**
	 * Map of price properties for the header
	 */
	protected Map<String, String> headerPropMap = null;

	/**
	 * store configuration changeable info for configurable itemSalesDocs
	 */
	protected Map<String, Boolean> itemConfigChangeableMap = null;

	/**
	 * flag if price attributes are needed, to show IPC prices for configurable products
	 */
	protected boolean setIpcPriceAttributes = false;

	/**
	 * store shipTo info for itemSalesDocs
	 */
	protected Map<String, ShipTo> shipToMap = new HashMap<String, ShipTo>(0);

	// store shipTo info of the predecessor item, e.g. template

	/**
	 *
	 */
	protected Map<Object, ShipTo> predecessorShipTo = new HashMap<Object, ShipTo>(0);

	// Lord Load state
	/**
	 *
	 */
	protected LoadOperation loadState = new LoadOperation();



	/**
	 * List of saved items of the order. In order to be able to distinguish between saved items and new items in case of
	 * order change, the handles of the saved items are added to this list when the order is read the first time from the
	 * backend * *
	 */
	protected Set<String> savedItemsMap = new HashSet<String>(0);

	/**
	 * Due to performance reasons the docflow of header and items should only be read once. Therefore the docflow data
	 * are cached after the first call .
	 */
	protected boolean docflowRead = false;

	/**
	 * JCO table: item document flow
	 */
	protected JCoTable itemDocFlow;

	/**
	 * JCO table: header
	 */
	protected JCoTable headerDocFlow;

	/**
	 * Map of messages.
	 */
	protected Map<String, MessageList> messageBufferMap = new HashMap<String, MessageList>(1);

	@Override
	public Set<String> getSavedItemsMap()
	{
		return savedItemsMap;
	}

	@Override
	public void initBackendObject() throws BackendException
	{

		loadState.setLoadOperation(LoadOperation.DISPLAY);
		super.initBackendObject();
	}

	@Override
	public Map<String, String> getHeaderPriceAttribs()
	{

		sapLogger.entering("getHeaderPriceAttribs");

		if (headerPriceAttribs == null)
		{
			sapLogger.debug("Create Header price Attribute Map");
			headerPriceAttribs = new HashMap<String, String>(5);
		}

		sapLogger.exiting();

		return headerPriceAttribs;
	}

	@Override
	public Map<String, Map<String, String>> getItemsPriceAttribMap()
	{

		sapLogger.entering("getItemsPriceAttribMap");

		if (itemsPriceAttribMap == null)
		{
			sapLogger.debug("Create Item price Attribute Map");
			itemsPriceAttribMap = new HashMap<String, Map<String, String>>(5);
		}

		sapLogger.exiting();
		return itemsPriceAttribMap;
	}

	@Override
	public Map<String, String> getHeaderPropMap()
	{

		sapLogger.entering("getHeaderPropMap");

		if (headerPropMap == null)
		{
			sapLogger.debug("Create Header property Map");
			headerPropMap = new HashMap<String, String>(2);
		}

		sapLogger.exiting();
		return headerPropMap;
	}

	@Override
	public Map<String, String> getItemsPropMap()
	{

		sapLogger.entering("getItemsPropMap");

		if (itemsPropMap == null)
		{
			sapLogger.debug("Create Item property Map");
			itemsPropMap = new HashMap<String, String>(1);
		}

		sapLogger.exiting();
		return itemsPropMap;
	}

	@Override
	public abstract Map<String, String> getItemVariantMap();

	@Override
	public Map<String, ShipTo> getShipToMap()
	{
		return shipToMap;
	}

	/**
	 * sets the map, which stores the header resp. item vs. shipToKey relation
	 *
	 * @param map
	 *           shipTo map
	 */
	public void setShipToMap(final Map<String, ShipTo> map)
	{
		this.shipToMap = map;
	}

	/**
	 * Adjusts the free good related sub items. Forwards to {@link FreeGoodSupportBackend}.
	 *
	 * @param salesDoc
	 *           the sales document
	 * @param transConf
	 *           Configuration for SAP synchronous order management
	 * @return was there an inclusive FG item?
	 * @throws BackendException
	 *            exception from parsing etc.
	 */

	protected boolean adjustFreeGoods(final SalesDocument salesDoc, final TransactionConfiguration transConf)
			throws BackendException
	{
		if (transConf == null)
		{
			final BackendException ex = new BackendException("adjustFreeGoods: parameter 'transConf' is null");
			sapLogger.throwing(ex);
			throw ex;
		}

		return FreeGoodSupportBackend.adjustSalesDocument(salesDoc);
	}

	@Override
	public boolean isDocflowRead()
	{
		return docflowRead;
	}

	@Override
	public void setDocflowRead(final boolean docflowread)
	{
		docflowRead = docflowread;
	}

	@Override
	public JCoTable getHeaderDocFlow()
	{

		return headerDocFlow;
	}

	@Override
	public JCoTable getItemDocFlow()
	{
		return itemDocFlow;
	}

	@Override
	public void setHeaderDocFlow(final JCoTable table)
	{
		headerDocFlow = table;
	}

	@Override
	public void setItemDocFlow(final JCoTable table)
	{
		itemDocFlow = table;
	}

	@Override
	public MessageList getMessageList(final TechKey key)
	{

		MessageList msgList = null;
		String keyStr = "DEFAULT";

		if (key != null && key.getIdAsString().length() > 0)
		{
			keyStr = key.getIdAsString();
		}

		msgList = messageBufferMap.get(keyStr);

		return msgList;
	}

	@Override
	public MessageList getOrCreateMessageList(final TechKey key)
	{

		MessageList msgList = null;
		String keyStr = "DEFAULT";

		if (key != null && key.getIdAsString().length() > 0)
		{
			keyStr = key.getIdAsString();
		}
		if (messageBufferMap != null)
		{
			msgList = messageBufferMap.get(key.getIdAsString());
		}
		if (msgList == null)
		{
			msgList = new MessageList();
			messageBufferMap.put(keyStr, msgList);
		}

		return msgList;
	}

	@Override
	public void removeMessageFromMessageList(final TechKey key, final String resourceKey)
	{

		MessageList msgList = null;
		String keyStr = "DEFAULT";

		if (key != null && key.getIdAsString().length() > 0)
		{
			keyStr = key.getIdAsString();
		}

		msgList = messageBufferMap.get(keyStr);
		if (msgList != null && resourceKey != null)
		{
			msgList.remove(resourceKey);
		}
	}

	@Override
	public LoadOperation getLoadState()
	{
		return loadState;
	}

}