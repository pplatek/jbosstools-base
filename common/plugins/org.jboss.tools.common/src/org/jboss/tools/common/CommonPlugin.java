/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.common.log.BaseUIPlugin;
import org.jboss.tools.common.log.IPluginLog;
import org.jboss.tools.common.util.DirtyEditorTracker;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class CommonPlugin extends BaseUIPlugin {

	public static final String PLUGIN_ID = "org.jboss.tools.common"; //$NON-NLS-1$
	protected static CommonPlugin instance;
	private static String environment;

	public CommonPlugin() {
		super();
		instance = this;
	}

	public static CommonPlugin getInstance() {
	    return instance;
	}

    public void start(BundleContext context) throws Exception {
        super.start(context);
        Display.getDefault().asyncExec(new Runnable() {
        	public void run() {
        		DirtyEditorTracker.getInstance();
        	}
        });
	}
	/**
	 * Gets message from plugin.properties
	 * @param key
	 * @return
	 */
	public static String getMessage(String key)	{
		return Platform.getResourceString(instance.getBundle(), key);
	}

	/**
	 * @return Studio environment.
	 */
	public static String getEnvironment() {
		if(environment == null) {
			String osName = System.getProperty("os.name"); //$NON-NLS-1$
			String javaVersion = System.getProperty("java.version"); //$NON-NLS-1$
			String studioName = "unknown"; //$NON-NLS-1$
			String studioVersion = "unknown"; //$NON-NLS-1$
			String eclipseVersion = "unknown"; //$NON-NLS-1$
			String eclipseBuildId = "unknown"; //$NON-NLS-1$

			Bundle studio = Platform.getBundle("org.jboss.tools.common"); //$NON-NLS-1$
			if(studio!=null) {
				Dictionary studioDic = studio.getHeaders();
				studioName = (String)studioDic.get("Bundle-Name"); //$NON-NLS-1$
				studioVersion = (String)studioDic.get("Bundle-Version"); //$NON-NLS-1$
			}

			Bundle eclipse = Platform.getBundle("org.eclipse.platform"); //$NON-NLS-1$
			if(eclipse!=null) {
				Dictionary eclipseDic = eclipse.getHeaders();
				eclipseVersion = (String)eclipseDic.get("Bundle-Version"); //$NON-NLS-1$
				FileInputStream fis = null;
				try {
					String path = FileLocator.resolve(eclipse.getEntry("/")).getPath(); //$NON-NLS-1$
					if(path!=null) {
						File aboutMappings = new File(path, "about.mappings"); //$NON-NLS-1$
						if(aboutMappings.exists()) {
							Properties properties = new Properties();
							fis = new FileInputStream(aboutMappings);
							properties.load(fis);
							String buildId = properties.getProperty("0"); //$NON-NLS-1$
							if(buildId!=null && buildId.length()>0) {
								eclipseBuildId = buildId;
							}
						}
					}
				} catch (IOException e) {
					getPluginLog().logError("Error in getting environment info: " + e.getMessage()); //$NON-NLS-1$
				} finally {
					if(fis!=null) {
						try {
							fis.close();
						} catch (IOException e) {
							// ignore
						}
					}
				}
			}
			StringBuffer result = new StringBuffer(studioName).append(" ").append(studioVersion). //$NON-NLS-1$
				append(", Eclipse ").append(eclipseVersion).append(" "). //$NON-NLS-1$ //$NON-NLS-2$
				append(eclipseBuildId).append(", Java ").append(javaVersion). //$NON-NLS-1$
				append(", ").append(osName); //$NON-NLS-1$
			environment = result.toString();
		}
		return environment;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static CommonPlugin getDefault() {
		return instance;
	}

	/**
	 * @return IPluginLog object
	 */
	public static IPluginLog getPluginLog() {
		return getDefault();
	}
}