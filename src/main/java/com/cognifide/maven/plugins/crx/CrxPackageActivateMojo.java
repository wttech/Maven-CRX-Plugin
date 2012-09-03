/*
 * Copyright 2012 Cognifide Limited
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.cognifide.maven.plugins.crx;

import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.StringUtils;

/**
 * Activates a CRX package (replicates the package to publish instances).
 * 
 * @goal activate
 * @description Activates a CRX package (replicates the package to publish instances).
 * 
 * @execute goal='install'
 */
public class CrxPackageActivateMojo extends CrxPackageAbstractMojo {

	/**
	 * @parameter expression="${packagePath}"
	 * 
	 * @readonly
	 */
	private String packagePath;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (runOnlyAtExecutionRoot && !isThisTheExecutionRoot()) {
			getLog().info("Skipping package activation in this project because it's not the Execution Root");
			return;
		}

		if (this.skip) {
			getLog().info("Skipping package activation as instructed");
			return;
		}

		String uploadedPackagePath = StringUtils.defaultString(mavenSession.getUserProperties().get(
				UPLOADED_PACKAGE_PATH_PROPERTY));

		if (StringUtils.isEmpty(uploadedPackagePath)) {
			uploadedPackagePath = packagePath;
		}

		if (StringUtils.isEmpty(uploadedPackagePath)) {
			getLog().error("Uploading goal hasn't set up path for package!");
			return;
		}

		String targetURL = getActivationURL(uploadedPackagePath);

		getLog().info("Activating package using command: " + targetURL);
		String response = post(targetURL);

		CrxResponse parsedJson;
		try {
			parsedJson = CrxResponse.parseCrxJsonResponse(response);
		} catch (IOException e) {
			getLog().error("Malformed JSON response", e);
			throw new MojoExecutionException("Package activation failed");
		}

		if (parsedJson.isSuccess()) {
			getLog().info("Package activated");
		} else {
			getLog().error("Package activation failed: + " + parsedJson.getMsg());
			throw new MojoExecutionException(parsedJson.getMsg());
		}
	}

	protected String getActivationURL(String path) {
		return getJsonTargetURL() + path + "/?cmd=replicate";
	}
}