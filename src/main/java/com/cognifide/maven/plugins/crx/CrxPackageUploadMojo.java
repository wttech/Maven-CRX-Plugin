/*
 * Copyright 2011 Cognifide Limited
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.FilePartSource;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Uploads an CRX package to a running CRX instance.
 * 
 * @goal upload
 * @description Uploads an CRX package to a running CRX instance.
 */
public class CrxPackageUploadMojo extends CrxPackageAbstractMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {		
		if (runOnlyAtExecutionRoot && !isThisTheExecutionRoot()) {
			getLog().info("Skipping upload in this project because it's not the Execution Root");
			return;
		}
		
		if (this.skip) {
			getLog().info("Skipping package uploading as instructed");
			return;
		}

		File packageFile = getPackageFile();

		String targetURL = getUploadURL();
		getLog().info("Uploading package " + packageFileName + " to " + targetURL);
		try {
			String response = post(targetURL, createUploadParameters(packageFile));
			CrxResponse parsedJson = CrxResponse.parseCrxJsonResponse(response);

			if (parsedJson.isSuccess()) {
				getLog().info(parsedJson.getMsg());
				mavenSession.getUserProperties().put(UPLOADED_PACKAGE_PATH_PROPERTY, parsedJson.getPath());
			} else {
				getLog().error(parsedJson.getMsg());
				throw new MojoExecutionException(parsedJson.getMsg());
			}
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException("File '" + packageFile.getName() + "' not found!", e);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	protected String getUploadURL() {
		return getJsonTargetURL() + "/?cmd=upload";
	}

	protected List<Part> createUploadParameters(File file) throws IOException {
		List<Part> partList = new ArrayList<Part>();
		if (file != null) {
			partList.add(new FilePart("package", new FilePartSource(file.getName(), file)));
		}

		partList.add(new StringPart("force", Boolean.toString(this.force)));
		return partList;
	}
}
