/*
 * Copyright 2011 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cognifide.maven.plugins.crx;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Installs an CRX package to a running CRX instance.
 * 
 * @goal install
 * @description an CRX package to a running CRX instance.
 * @execute goal='upload'
 */
public class CrxPackageInstallMojo extends CrxPackageAbstractMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (this.skip) {
			getLog().info("Skipping package installation as instructed");
			return;
		}

		String uploadedPackagePath = System.getProperty(UPLOADED_PACKAGE_NAME_PROPERTY);
		if (uploadedPackagePath != null) {
			System.clearProperty(UPLOADED_PACKAGE_NAME_PROPERTY);
			String targetURL = getInstallURL(uploadedPackagePath);
			
			getLog().info("Installing package using command: " + targetURL);
			String response = post(targetURL);

			HtmlParser parser = new HtmlParser(response);
			parser.parse();
			if (parser.getErrors().size() > 0) {
				for (String s : parser.getErrors()) {
					getLog().error("CRX: " + s);
				}
				throw new MojoExecutionException("Installation completed with errors!");
			} else {
				getLog().info("Package sucesfully installed.");
			}
		} else {
			getLog().error("Uploading goal hasn't set up path for package!");
		}
		return;
	}

	protected String getInstallURL(String path) {
		return getHtmlTargetURL() + path + "/?cmd=install";
	}

}