package tests.freemind;

import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;
import plugins.script.SignedScriptHandler;
import plugins.script.SignedScriptHandler.ScriptContents;

public class SignedScriptTests extends TestCase {
	private static final String SCRIPTS_CONTENT = "test";
	private static final String SCRIPTS_SIGNATURE = "MCwCFCllrN6Xig7V0nRFGmWBLoBauMiGAhQLoYbNRTjVS1c7A2ev3bvJqUqg8Q==";
	private static final String SIGNED_SCRIPT = SCRIPTS_CONTENT + "//SIGN:"
			+ SCRIPTS_SIGNATURE;
	private static final String SIGNED_SCRIPT_OTHER_KEY = SCRIPTS_CONTENT
			+ "//SIGN(mykey):" + SCRIPTS_SIGNATURE;
	private static final String SIGNED_SCRIPT_FREEMIND_KEY = SCRIPTS_CONTENT
			+ "//SIGN(" + SignedScriptHandler.FREEMIND_SCRIPT_KEY_NAME + "):"
			+ SCRIPTS_SIGNATURE;

	public void testSignedInitialization() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		SignedScriptHandler signedScriptHandler = new SignedScriptHandler();
		assertEquals(true,
				signedScriptHandler.isScriptSigned(SIGNED_SCRIPT, out));
		assertEquals(true, signedScriptHandler.isScriptSigned(
				SIGNED_SCRIPT_FREEMIND_KEY, out));
		assertEquals(true, signedScriptHandler.isScriptSigned(SIGNED_SCRIPT
				+ "  \n  ", out));
		assertEquals(false,
				signedScriptHandler.isScriptSigned("2" + SIGNED_SCRIPT, out));
	}

	public void testScriptContents() throws Exception {
		ScriptContents scriptContent = new ScriptContents(SIGNED_SCRIPT);
		assertEquals(SCRIPTS_CONTENT, scriptContent.mScript);
		assertEquals(SCRIPTS_SIGNATURE, scriptContent.mSignature);
		assertEquals(null, scriptContent.mKeyName);
		scriptContent = new ScriptContents(SIGNED_SCRIPT_OTHER_KEY);
		assertEquals(SCRIPTS_CONTENT, scriptContent.mScript);
		assertEquals(SCRIPTS_SIGNATURE, scriptContent.mSignature);
		assertEquals("mykey", scriptContent.mKeyName);
	}
}
