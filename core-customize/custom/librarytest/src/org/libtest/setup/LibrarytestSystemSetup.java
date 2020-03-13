/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package org.libtest.setup;

import static org.libtest.constants.LibrarytestConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import org.libtest.constants.LibrarytestConstants;
import org.libtest.service.LibrarytestService;


@SystemSetup(extension = LibrarytestConstants.EXTENSIONNAME)
public class LibrarytestSystemSetup
{
	private final LibrarytestService librarytestService;

	public LibrarytestSystemSetup(final LibrarytestService librarytestService)
	{
		this.librarytestService = librarytestService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		librarytestService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return LibrarytestSystemSetup.class.getResourceAsStream("/librarytest/sap-hybris-platform.png");
	}
}
