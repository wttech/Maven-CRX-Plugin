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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.exc.UnrecognizedPropertyException;
import org.junit.Test;

public class CrxResponseParserTest {

	@Test
	public void parseDefaultResponseTest() throws JsonParseException,
			JsonMappingException, IOException {
		String json = "{\"success\":true,\"msg\":\"Package installed\"}";

		CrxResponse defaultResponse = CrxResponse.parseCrxJsonResponse(json);
		assertEquals(defaultResponse.isSuccess(), true);
		assertEquals(defaultResponse.getMsg(), "Package installed");
		assertEquals(defaultResponse.getPath(), null);
	}

	@Test
	public void parseFullResponseTest() throws JsonParseException,
			JsonMappingException, IOException {
		String json = "{\"success\":true,\"msg\":\"Package created\",\"path\":\"/etc/packages/day/testpackage.zip\"}";

		CrxResponse defaultResponse = CrxResponse.parseCrxJsonResponse(json);
		assertEquals(defaultResponse.isSuccess(), true);
		assertEquals(defaultResponse.getMsg(), "Package created");
		assertEquals(defaultResponse.getPath(),
				"/etc/packages/day/testpackage.zip");
	}

	@Test(expected = UnrecognizedPropertyException.class)
	public void wrongJsonMappingTest() throws JsonParseException,
			JsonMappingException, IOException {
		String json = "{\"x\":true,\"msg\":\"Package installed\"}";

		CrxResponse.parseCrxJsonResponse(json);
	}

}
