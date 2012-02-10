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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.plexus.util.StringUtils;

public class HtmlParser {

	private final Pattern errorPattern = Pattern.compile("<span class=\"E\"><b>E</b>&nbsp;(.+)</span>");

	private final Pattern processingErrorPattern = Pattern
			.compile("<span class=\"error\">(.+)</span><br><code><pre>([\\s\\S]+)</pre>");

	private final String installedSuccessfullyInfo = "<span class=\"Package imported.\">";

	private final String installedWithErrorsInfo = "<span class=\"Package imported (with errors";

	private final String rawHtml;

	private List<String> errors;

	private InstallationStatus status;

	public HtmlParser(String rawHtml) {
		this.rawHtml = rawHtml;
	}

	public void parse() {
		this.errors = new ArrayList<String>();
		findErrorsByPattern(processingErrorPattern, true);
		findErrorsByPattern(errorPattern, false);
		checkStatus();
	}

	private void findErrorsByPattern(Pattern pattern, boolean printStacktrace) {
		Matcher matcher = pattern.matcher(this.rawHtml);
		while (matcher.find()) {
			this.errors.add(matcher.group(1));
			if (printStacktrace) {
				if (matcher.groupCount() > 1) {
					String secondGroup = matcher.group(2);
					if (StringUtils.isNotBlank(secondGroup)) {
						this.errors.add(secondGroup);
					}
				}
			}
		}
	}

	private void checkStatus() {
		if (this.rawHtml.contains(installedSuccessfullyInfo)) {
			status = InstallationStatus.SUCCESS;
		} else if (this.rawHtml.contains(installedWithErrorsInfo)) {
			status = InstallationStatus.SUCCESS_WITH_ERRORS;
		} else {
			status = InstallationStatus.FAIL;
		}
	}

	public List<String> getErrors() {
		if (this.errors == null) {
			throw new IllegalStateException(
					"Cannot return error messages before parsing html content. Invoke parse() method first.");
		}

		return Collections.unmodifiableList(errors);
	}

	public Pattern getProcessingErrorPattern() {
		return processingErrorPattern;
	}

	public InstallationStatus getStatus() {
		return status;
	}
}
