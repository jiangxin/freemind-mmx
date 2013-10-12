/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2006  Christian Foltin <christianfoltin@users.sourceforge.net>
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
/*$Id: AllTests.java,v 1.1.2.5 2008/04/18 21:18:27 christianfoltin Exp $*/

package tests.freemind;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import tests.freemind.findreplace.FindTextTests;

/** */
public class AllTests {

	public static void main(String[] args) {
		TestResult result = junit.textui.TestRunner.run(suite());
		if (!result.wasSuccessful())
			System.exit(1);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AllTests");
		// $JUnit-BEGIN$
		suite.addTest(new TestSuite(ScriptEditorPanelTest.class));
		suite.addTest(new TestSuite(SplashTests.class));
		suite.addTest(new TestSuite(Base64Tests.class));
		suite.addTest(new TestSuite(FindTextTests.class));
		suite.addTest(new TestSuite(HtmlConversionTests.class));
		suite.addTest(new TestSuite(TransformTest.class));
		suite.addTest(new TestSuite(MarshallerTests.class));
		suite.addTest(new TestSuite(SignedScriptTests.class));
		suite.addTest(new TestSuite(LastStorageManagementTests.class));
		suite.addTest(new TestSuite(ToolsTests.class));
		suite.addTest(new TestSuite(ExportTests.class));
		suite.addTest(new TestSuite(LayoutTests.class));
		suite.addTest(new TestSuite(LastOpenedTests.class));
		// $JUnit-END$
		return suite;
	}

}
