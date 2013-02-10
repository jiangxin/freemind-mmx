/*
 * Copyright (C) 2009 Archie L. Cobbs. All rights reserved.
 * Provided as is, as stated in http://jira.codehaus.org/browse/JIBX-346
 */

package de.foltin;

/**
 * Encodes/decodes XML-invalid characters in Java strings so they may be
 * included as XML text.
 */
public final class StringEncoder {

	private static final String HEXDIGITS = "0123456789abcdef";

	private StringEncoder() {
	}

	/**
	 * Encode a string, escaping any invalid XML characters.
	 * 
	 * <p>
	 * Invalid characters are escaped using <code>&#92;uNNNN</code> notation
	 * like Java unicode characters, e.g., <code>0x001f</code> would appear in
	 * the encoded string as <code>&#92;u001f</code>. Backslash characters are
	 * themselves encoded with a double backslash.
	 * 
	 * @param value
	 *            string to encode (possibly null)
	 * @return the encoded version of {@code value}, or {@code null} if
	 *         {@code value} was {@code null}
	 * @see #decode
	 */
	public static String encode(String value) {
		if (value == null)
			return value;
		StringBuilder buf = new StringBuilder(value.length() + 4);
		final int limit = value.length();
		for (int i = 0; i < limit; i++) {
			final char ch = value.charAt(i);

			// Handle escape character
			if (ch == '\\') {
				buf.append('\\');
				buf.append('\\');
				continue;
			}

			// If character is an otherwise valid XML character, pass it through
			// unchanged
			if (isValidXMLChar(ch)) {
				buf.append(ch);
				continue;
			}

			// Escape it
			buf.append('\\');
			buf.append('u');
			for (int shift = 12; shift >= 0; shift -= 4)
				buf.append(HEXDIGITS.charAt((ch >> shift) & 0x0f));
		}
		return buf.toString();
	}

	/**
	 * Decode a string encoded by {@link #encode}.
	 * 
	 * <p>
	 * The parsing is strict; any ill-formed backslash escape sequence (i.e.,
	 * not of the form <code>&#92;uNNNN</code> or <code>\\</code>) will cause an
	 * exception to be thrown.
	 * 
	 * @param text
	 *            string to decode (possibly null)
	 * @return the decoded version of {@code text}, or {@code null} if
	 *         {@code text} was {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code text} contains an invalid escape sequence
	 * @see #encode
	 */
	public static String decode(String text) {
		if (text == null)
			return null;
		StringBuilder buf = new StringBuilder(text.length());
		final int limit = text.length();
		for (int i = 0; i < limit; i++) {
			char ch = text.charAt(i);

			// Handle unescaped characters
			if (ch != '\\') {
				buf.append(ch);
				continue;
			}

			// Get next char
			if (++i >= limit)
				throw new IllegalArgumentException(
						"illegal trailing '\\' in encoded string");
			ch = text.charAt(i);

			// Check for backslash escape
			if (ch == '\\') {
				buf.append(ch);
				continue;
			}

			// Must be unicode escape
			if (ch != 'u')
				throw new IllegalArgumentException(
						"illegal escape sequence '\\" + ch
								+ "' in encoded string");

			// Decode hex value
			int value = 0;
			for (int j = 0; j < 4; j++) {
				if (++i >= limit)
					throw new IllegalArgumentException(
							"illegal truncated '\\u' escape sequence in encoded string");
				int nibble = Character.digit(text.charAt(i), 16);
				if (nibble == -1) {
					throw new IllegalArgumentException(
							"illegal escape sequence '"
									+ text.substring(i - j - 2, i - j + 4)
									+ "' in encoded string");
				}
				// assert nibble >= 0 && nibble <= 0xf;
				value = (value << 4) | nibble;
			}

			// Append decodec character
			buf.append((char) value);
		}
		return buf.toString();
	}

	/**
	 * Determine if the given character is a valid XML character according to
	 * the XML 1.0 specification.
	 * 
	 * @see <a href="http://www.w3.org/TR/REC-xml/#charsets">The XML 1.0
	 *      Specification</a>
	 */
	public static boolean isValidXMLChar(char ch) {
		return (ch >= '\u0020' && ch <= '\ud7ff') || (ch >= '\ue000' && ch <= '\ufffd');
	}
}
