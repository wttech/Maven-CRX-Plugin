/*
 * Copyright 2012 Cognifide Limited
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

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

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

	@Test
	public void testHugeError() {
		HtmlParser parser = new HtmlParser("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\">\n" +
				"<html><head>\n" +
				"    <style type=\"text/css\">\n" +
				"        body {\n" +
				"            background-color: white;\n" +
				"            font-family: verdana, arial, sans-serif;\n" +
				"            font-size: 12px;\n" +
				"            white-space: nowrap;\n" +
				"        }\n" +
				"        div {\n" +
				"            font-family: courier, monospace;\n" +
				"            font-size: 11px;\n" +
				"        }\n" +
				"    </style>\n" +
				"    <script type=\"text/javascript\">\n" +
				"        function onStatus(pid, stat, p, max, msg) {\n" +
				"            window.parent.cqPackageShareClient.onStatus(pid, stat, p, max, msg);\n" +
				"            window.scrollTo(0, 1000000);\n" +
				"        }\n" +
				"    </script></head>\n" +
				"<body><h2>Installing content</h2><div><span class=\"Collecting import information...\"><b>Collecting import information...</b>&nbsp;</span><br>\n" +
				"<script>\n" +
				"window.scrollTo(0, 1000000);\n" +
				"</script>\n" +
				"<span class=\"Installing node types...\"><b>Installing node types...</b>&nbsp;</span><br>\n" +
				"<span class=\"Installing privileges...\"><b>Installing privileges...</b>&nbsp;</span><br>\n" +
				"<span class=\"Importing content...\"><b>Importing content...</b>&nbsp;</span><br>\n" +
				"<span class=\"E\"><b>E</b>&nbsp;/content (org.xml.sax.SAXException\n" +
				"java.lang.NullPointerException)</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/data</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/data/apps</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/data/apps/particulieren</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/data/apps/particulieren/i18n</span><br>\n" +
				"<span class=\"E\"><b>E</b>&nbsp;/content/migration/data/content (org.xml.sax.SAXException\n" +
				"java.lang.NullPointerException)</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/data/content/particulieren</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/data/content/particulieren/jcr:content</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/data/content/particulieren/nl</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/data/content/particulieren/nl/jcr:content</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/data/content/particulieren/nl/jcr:content/parsys</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/data/etc</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/data/etc/siteConfig</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/data/etc/siteConfig/particulieren</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/data/etc/siteConfig/particulieren/jcr:content</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/data/etc/siteConfig/particulieren/fr</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/data/etc/siteConfig/particulieren/fr/jcr:content</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/data/etc/siteConfig/particulieren/nl</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/data/etc/siteConfig/particulieren/nl/jcr:content</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/rulebundle</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/rulebundle/0000_create_particulieren_apps_dir</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/rulebundle/0001_create_particulieren_content_dir</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/rulebundle/0002_create_particulieren_siteConfig_dir</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/rulebundle/0003_copy_base_pages_pa</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/rulebundle/0004_move_textimage_to_common</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/rulebundle/0005_copy_i18n_folder</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/rulebundle/0006_add_label_header_search</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/rulebundle/0007_add_label_header_menu</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/rulebundle/0008_add_label_header_logo_seo</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/rulebundle/0009_add_label_header_touch</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/rulebundle/0010_copy_intial_siteConfig_fr</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/rulebundle/0011_create_test_particulieren_content_dir</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/rulebundle/0012_create_test_particulieren_siteConfig_dir</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/rulebundle/0013_change_resourcesupertype_page_base_pa</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/rulebundle/0014_change_resourcesupertype_page_base_test_pa</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/rulebundle/0015_delete_base_page_property_pa</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/rulebundle/0016_delete_base_page_property_test_pa</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/rulebundle/0017_delete_base_page_property_pa</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/rulebundle/0018_delete_base_page_property_test_pa</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/rulebundle/0019_move_textimage_to_common_test_sites</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/livesitecontexts</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/livesitecontexts/kbcondernemers</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/livesitecontexts/particulieren</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/testsitecontexts</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/testsitecontexts/test-kbcondernemers</span><br>\n" +
				"<span class=\"-\"><b>-</b>&nbsp;/content/migration/testsitecontexts/test-particulieren</span><br>\n" +
				"<span class=\"saving approx 2 nodes...\"><b>saving approx 2 nodes...</b>&nbsp;</span><br>\n" +
				"<span class=\"Package imported (with errors, check logs!)\"><b>Package imported (with errors, check logs!)</b>&nbsp;</span><br>\n" +
				"</div><br>Package installed in 991ms.<br><script type=\"text/javascript\">\n" +
				"window.scrollTo(0, 1000000);\n" +
				"</script>\n" +
				"</body></html>");
		parser.parse();
		List<String> errors = parser.getErrors();

		Assert.assertEquals(2, errors.size());
		Assert.assertEquals("/content (org.xml.sax.SAXException\n" +
				"java.lang.NullPointerException)", errors.get(0));
	}

}
