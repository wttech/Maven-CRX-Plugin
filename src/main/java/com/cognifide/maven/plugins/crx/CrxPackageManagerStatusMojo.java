package com.cognifide.maven.plugins.crx;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.IOException;

/**
 * Checks if CRX Package Manager service is running.
 *
 * @goal statusCheck
 * @description Checking if CRX Package Manager service is running.
 *
 * @execute goal='statusCheck'
 */
public class CrxPackageManagerStatusMojo extends CrxPackageAbstractMojo {

	/**
	 * Defines how many times CRX Package Manager service status will be checked.
	 *
	 * @parameter expression="${crx.retryCount}" default-value="1"
	 */
	private int retryCount;

	/**
	 * Defines interval between each CRX Package Manager service call.
	 *
	 * @parameter expression="${crx.waitingTime}" default-value="2000"
	 */
	private long waitingTime;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			checkStatus(getJspTargetURL());
		} catch (InterruptedException e) {
			getLog().error("Cannot connect to CRX Package Manager service", e);
			throw new MojoExecutionException("CRX Package Manager service connection failed");
		}
	}

	private void checkStatus(String targetURL) throws InterruptedException, MojoExecutionException {
		int count = 1;
		while (count <= retryCount) {
			getLog().info(String.format("Trying connect to CRX Package Manager service (%s)... [%d/%d]",
					targetURL, count, retryCount));
			if (getStatus(targetURL) != HttpStatus.SC_OK) {
				getLog().warn("Cannot connect to CRX Package Manager service");

				if (count++ == retryCount) {
					throw new MojoExecutionException("CRX Package Manager service is not accessible.");
				}

				Thread.sleep(waitingTime);
				continue;
			}
			getLog().info("CRX Package Manager service is up and running.");
			break;
		}
	}

	private int getStatus(String targetURL) throws MojoExecutionException {
		final GetMethod getMethod = new GetMethod(targetURL);
		int status;
		try {
			status = getHttpClient().executeMethod(getMethod);
		} catch (IOException e) {
			throw new MojoExecutionException(
					"Request to the CRX Package Manager service failed, cause: " + e.getMessage(),
					e);
		} finally {
			getMethod.releaseConnection();
		}
		return status;
	}

	private String getJspTargetURL() {
		return this.url + this.packageManagerSuffix + ".jsp";
	}
}
