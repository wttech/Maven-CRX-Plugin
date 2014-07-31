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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProxySelector;
import java.net.URL;
import java.text.MessageFormat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.settings.MavenSettingsBuilder;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;

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
	 * Builder for the user or global settings.
	 * 
	 * @component
	 * @required
	 * @readonly
	 */
	private MavenSettingsBuilder mavenSettingsBuilder;

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
	protected CloseableHttpClient getHttpClient() {
		HttpClientBuilder httpClientBuilder = HttpClients.custom();

		httpClientBuilder.useSystemProperties(); // support proxies, ssl etc
		httpClientBuilder.setDefaultRequestConfig(getRequestConfig());

		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		Credentials credentials = new UsernamePasswordCredentials(user, password);
		credentialsProvider.setCredentials(AuthScope.ANY, credentials);
		httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);

		return httpClientBuilder.build();
	}

	private void configureRoutePlanner(HttpClientBuilder httpClientBuilder) throws MojoExecutionException {
		Settings settings = null;
		try {
			settings = mavenSettingsBuilder.buildSettings();
		} catch (Exception e) {
			getLog().warn("Cannot build Settings object");
			throw new MojoExecutionException("Cannot build Settings object: " + e.getMessage(), e);
		}
		if (settings != null) {
			Proxy proxy = settings.getActiveProxy();
			if (proxy != null) {
				SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(
						ProxySelector.getDefault());
				httpClientBuilder.setRoutePlanner(routePlanner);
				// httpClient.getHostConfiguration().setProxyHost(new
				// ProxyHost(proxy.getHost(), proxy.getPort()));
				// if (StringUtils.isNotBlank(proxy.getUsername()) ||
				// StringUtils.isNotBlank(proxy.getPassword())) {
				// httpClient.getState().setProxyCredentials(new
				// AuthScope(proxy.getHost(), proxy.getPort()),
				// new UsernamePasswordCredentials(proxy.getUsername(),
				// proxy.getPassword()));
				// }
			}
		}
	}

	private RequestConfig getRequestConfig() {
		Builder requestConfigBuilder = RequestConfig.custom();
		requestConfigBuilder.setConnectTimeout(HTTP_CONNECTION_TIMEOUT);
		return requestConfigBuilder.build();
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
		return post(targetURL, null);
	}

	/**
	 * Performs post request to given URL with given parameters provided as a part lists.
	 * 
	 * @param targetUrl Place where post action should be submitted
	 * @param partList Parameters of post action
	 * @return Response body
	 * @throws MojoExecutionException
	 */
	protected String post(String targetUrl, HttpEntity postEntity) throws MojoExecutionException {

		URL url;
		try {
			url = new URL(targetUrl);
		} catch (MalformedURLException e) {
			getLog().warn(e.getMessage());
			throw new MojoExecutionException("Malformed URL: " + targetUrl, e);
		}
		HttpClientContext context = getAuthenticatedContext(url);

		HttpPost post = new HttpPost(targetUrl);

		if (null != postEntity) {
			post.setEntity(postEntity);
		}

		CloseableHttpClient httpClient = getHttpClient();

		try {
			CloseableHttpResponse response = httpClient.execute(post, context);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			String responseBody = EntityUtils.toString(response.getEntity());
			if (HttpStatus.SC_OK == statusCode) {
				return responseBody;
			} else {
				getLog().warn(responseBody);
				String message = MessageFormat.format("Request to the repository failed, "
						+ "cause: {0} (check URL, user and password)", statusLine.getReasonPhrase());
				throw new MojoExecutionException(message);
			}
		} catch (IOException e) {
			throw new MojoExecutionException("Request to the repository failed, cause: " + e.getMessage(), e);
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				// silently fail - at least we tried
			}
		}

	}

	private HttpClientContext getAuthenticatedContext(URL url) {
		Credentials credentials = new UsernamePasswordCredentials(user, password);
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, credentials);

		AuthCache authCache = new BasicAuthCache();
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(new HttpHost(url.getHost(), url.getPort(), url.getProtocol()), basicAuth);

		HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(credentialsProvider);
		context.setAuthCache(authCache);
		return context;
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
