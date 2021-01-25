/*
 * Copyright 2012 Wunderman Thompson Technology
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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class HtmlParserTest {

	@Test
	public void testPrematureGetError() {
		HtmlParser parser = new HtmlParser("");
		try {
			parser.getErrors();
			Assert.fail("Exception expected: " + IllegalStateException.class);
		} catch (Exception e) {
		}
	}

	@Test
	public void testNoError() {
		HtmlParser parser = new HtmlParser("...abc<span class=\"-\"><b>-</b>&nbsp;Hello world</span>xyz...");
		parser.parse();
		List<String> errors = parser.getErrors();

		Assert.assertEquals(0, errors.size());
	}

	@Test
	public void testSimpleError() {
		HtmlParser parser = new HtmlParser("...abc<span class=\"E\"><b>E</b>&nbsp;Huge Error!</span>xyz...");
		parser.parse();
		List<String> errors = parser.getErrors();

		Assert.assertEquals(1, errors.size());
		Assert.assertEquals("Huge Error!", errors.get(0));
	}

}
