/*
 * Copyright 2012 Wunderman Thompson Technology
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.io.IOUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.MojoExecutionException;

public abstract class CrxPackageAbstractMojo extends AbstractMojo {

	private static final int HTTP_CONNECTION_TIMEOUT = 5000;

	protected static final String UPLOADED_PACKAGE_PATH_PROPERTY = "crx_install-uploaded_package_path";

	/**
	 * The URL of the running CRX instance.
	 * 
	 * @parameter expression="${crx.url}" default-value="http://localhost:5402"
	 */
	protected String url;

	/**
	 * The user name to authenticate at the running CRX instance.
	 * 
	 * @parameter expression="${crx.user}" default-value="admin"
	 */
	protected String user;

	/**
	 * The password to authenticate at the running CRX instance.
	 * 
	 * @parameter expression="${crx.password}" default-value="admin"
	 */
	protected String password;

	/**
	 * Forces to upload package even it already exists in the CRX repository.
	 * 
	 * @parameter expression="${crx.force}" default-value="true"
	 */
	protected boolean force;

	/**
	 * The name of the generated JAR file.
	 * 
	 * @parameter expression="${crx.packageFileName}" default-value=
	 * "${project.build.directory}/${project.build.finalName}.zip"
	 */
	protected String packageFileName;

	/**
	 * An optional url suffix which will be appended to the <code>crx.url</code> for use as the real target
	 * url of package manager.
	 * 
	 * @parameter expression="${crx.packageManagerSuffix}" default-value="/crx/packmgr/service"
	 */
	protected String packageManagerSuffix;

	/**
	 * Whether to skip this step even though it has been configured in the project to be executed. This
	 * property may be set by the <code>package.install.skip</code> comparable to the
	 * <code>maven.test.skip</code> property to prevent running the unit tests.
	 * 
	 * @parameter expression="${crx.skip}" default-value="false"
	 * @required
	 */
	protected boolean skip;
	
	/**
	 * This will cause the upload to run only at the top of a given module tree. That is, run in the project
	 * contained in the same folder where the mvn execution was launched.
	 * 
	 * @parameter expression="${runOnlyAtExecutionRoot}" default-value="false"
	 */
	protected boolean runOnlyAtExecutionRoot;	

	/**
	 * Base directory of the project.
	 * 
	 * @parameter default-value="${basedir}"
	 * @required
	 * @readonly
	 */
	private File basedir;
	
	/**
	 * The Maven Session Object
	 * 
	 * @parameter expression="${session}"
	 * @required
	 */
	protected MavenSession mavenSession;

	/**
	 * Returns the combination of <code>crx.url</code> and <code>jsonPackageManager.urlSuffix}</code>.
	 */
	protected String getJsonTargetURL() {
		return this.url + this.packageManagerSuffix + "/.json";
	}

	/**
	 * Returns the combination of <code>crx.url</code> and <code>htmlPackageManager.urlSuffix}</code>.
	 */
	protected String getHtmlTargetURL() {
		return this.url + this.packageManagerSuffix + "/.html";
	}

	/**
	 * Get the http client
	 */
	protected HttpClient getHttpClient() {
		final HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(HTTP_CONNECTION_TIMEOUT);

		// authentication stuff
		client.getParams().setAuthenticationPreemptive(true);
		Credentials defaultcreds = new UsernamePasswordCredentials(user, password);
		client.getState().setCredentials(AuthScope.ANY, defaultcreds);

		return client;
	}

	/**
	 * Creates {@link File} object for a current package.
	 * 
	 * @return
	 */
	protected File getPackageFile() {
		return new File(this.packageFileName);
	}

	/**
	 * Performs post request to given URL without any parameters.
	 * 
	 * @param targetURL Place where post action should be submitted
	 * @return Response body
	 * @throws MojoExecutionException
	 */
	protected String post(String targetURL) throws MojoExecutionException {
		return post(targetURL, new ArrayList<Part>());
	}

	/**
	 * Performs post request to given URL with given parameters provided as a part lists.
	 * 
	 * @param targetURL Place where post action should be submitted
	 * @param partList Parameters of post action
	 * @return Response body
	 * @throws MojoExecutionException
	 */
	protected String post(String targetURL, List<Part> partList) throws MojoExecutionException {
		PostMethod postMethod = new PostMethod(targetURL);

		try {
			Part[] parts = partList.toArray(new Part[partList.size()]);
			postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));

			int status = getHttpClient().executeMethod(postMethod);

			if (status == HttpStatus.SC_OK) {
				return IOUtils.toString(postMethod.getResponseBodyAsStream());
			} else {
				getLog().warn(postMethod.getResponseBodyAsString());
				throw new MojoExecutionException("Request to the repository failed, cause: "
						+ HttpStatus.getStatusText(status) + " (check URL, user and password)");
			}

		} catch (IOException ex) {
			throw new MojoExecutionException("Request to the repository failed, cause: " + ex.getMessage(),
					ex);
		} finally {
			postMethod.releaseConnection();
		}
	}

	/**
	 * Returns true if the current project is located at the Execution Root Directory (where mvn was launched)
	 */
	protected boolean isThisTheExecutionRoot() {
		Log log = this.getLog();
		log.debug("Root Folder: " + mavenSession.getExecutionRootDirectory());
		log.debug("Current Folder: " + basedir);
		boolean result = mavenSession.getExecutionRootDirectory().equalsIgnoreCase(basedir.toString());
		if (result) {
			log.debug("This is the execution root.");
		} else {
			log.debug("This is NOT the execution root.");
		}

		return result;
	}
}
