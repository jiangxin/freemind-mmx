/*
 * KeyEventWorkaround.java - Works around bugs in Java event handling
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2004 Slava Pestov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package freemind.preferences.layout;

//{{{ Imports
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
//import org.gjt.sp.jedit.Debug;
//}}}

/**
 * Various hacks to get keyboard event handling to behave in a consistent manner
 * across Java implementations.
 *
 * @author Slava Pestov
 * @version $Id: KeyEventWorkaround.java,v 1.1.4.2 2005-05-31 20:24:06 dpolivaev Exp $
 */
public class KeyEventWorkaround
{
	public static final boolean ALT_KEY_PRESSED_DISABLED = false;
	public static final boolean ALTERNATIVE_DISPATCHER = false;
	//{{{ processKeyEvent() method
	public static KeyEvent processKeyEvent(KeyEvent evt)
	{
		int keyCode = evt.getKeyCode();
		char ch = evt.getKeyChar();

		switch(evt.getID())
		{
		//{{{ KEY_PRESSED...
		case KeyEvent.KEY_PRESSED:
			lastKeyTime = evt.getWhen();
			// get rid of keys we never need to handle
			switch(keyCode)
			{
			case KeyEvent.VK_DEAD_GRAVE:
			case KeyEvent.VK_DEAD_ACUTE:
			case KeyEvent.VK_DEAD_CIRCUMFLEX:
			case KeyEvent.VK_DEAD_TILDE:
			case KeyEvent.VK_DEAD_MACRON:
			case KeyEvent.VK_DEAD_BREVE:
			case KeyEvent.VK_DEAD_ABOVEDOT:
			case KeyEvent.VK_DEAD_DIAERESIS:
			case KeyEvent.VK_DEAD_ABOVERING:
			case KeyEvent.VK_DEAD_DOUBLEACUTE:
			case KeyEvent.VK_DEAD_CARON:
			case KeyEvent.VK_DEAD_CEDILLA:
			case KeyEvent.VK_DEAD_OGONEK:
			case KeyEvent.VK_DEAD_IOTA:
			case KeyEvent.VK_DEAD_VOICED_SOUND:
			case KeyEvent.VK_DEAD_SEMIVOICED_SOUND:
			case '\0':
				return null;
			case KeyEvent.VK_ALT:
				modifiers |= InputEvent.ALT_MASK;
				return null;
			case KeyEvent.VK_ALT_GRAPH:
				modifiers |= InputEvent.ALT_GRAPH_MASK;
				return null;
			case KeyEvent.VK_CONTROL:
				modifiers |= InputEvent.CTRL_MASK;
				return null;
			case KeyEvent.VK_SHIFT:
				modifiers |= InputEvent.SHIFT_MASK;
				return null;
			case KeyEvent.VK_META:
				modifiers |= InputEvent.META_MASK;
				return null;
			default:
				if(!evt.isMetaDown())
				{
					if(evt.isControlDown()
						&& evt.isAltDown())
					{
						lastKeyTime = 0L;
					}
					else if(!evt.isControlDown()
						&& !evt.isAltDown())
					{
						lastKeyTime = 0L;

						if(keyCode >= KeyEvent.VK_0
							&& keyCode <= KeyEvent.VK_9)
						{
							return null;
						}

						if(keyCode >= KeyEvent.VK_A
							&& keyCode <= KeyEvent.VK_Z)
						{
							return null;
						}
					}
				}

				if(ALT_KEY_PRESSED_DISABLED)
				{
					/* we don't handle key pressed A+ */
					/* they're too troublesome */
					if((modifiers & InputEvent.ALT_MASK) != 0)
						return null;
				}

				switch(keyCode)
				{
				case KeyEvent.VK_NUMPAD0:
				case KeyEvent.VK_NUMPAD1:
				case KeyEvent.VK_NUMPAD2:
				case KeyEvent.VK_NUMPAD3:
				case KeyEvent.VK_NUMPAD4:
				case KeyEvent.VK_NUMPAD5:
				case KeyEvent.VK_NUMPAD6:
				case KeyEvent.VK_NUMPAD7:
				case KeyEvent.VK_NUMPAD8:
				case KeyEvent.VK_NUMPAD9:
				case KeyEvent.VK_MULTIPLY:
				case KeyEvent.VK_ADD:
				/* case KeyEvent.VK_SEPARATOR: */
				case KeyEvent.VK_SUBTRACT:
				case KeyEvent.VK_DECIMAL:
				case KeyEvent.VK_DIVIDE:
					last = LAST_NUMKEYPAD;
					break;
				default:
					last = LAST_NOTHING;
					break;
				}

				return evt;
			}
		//}}}
		//{{{ KEY_TYPED...
		case KeyEvent.KEY_TYPED:
			// need to let \b through so that backspace will work
			// in HistoryTextFields
			if((ch < 0x20 || ch == 0x7f || ch == 0xff)
				&& ch != '\b' && ch != '\t' && ch != '\n')
			{
				return null;
			}

			if(evt.getWhen() - lastKeyTime < 750)
			{
				if(! ALTERNATIVE_DISPATCHER)
				{
					if(((modifiers & InputEvent.CTRL_MASK) != 0
						^ (modifiers & InputEvent.ALT_MASK) != 0)
						|| (modifiers & InputEvent.META_MASK) != 0)
					{
						return null;
					}
				}

				// if the last key was a numeric keypad key
				// and NumLock is off, filter it out
				if(last == LAST_NUMKEYPAD)
				{
					last = LAST_NOTHING;
					if((ch >= '0' && ch <= '9') || ch == '.'
						|| ch == '/' || ch == '*'
						|| ch == '-' || ch == '+')
					{
						return null;
					}
				}
				// Windows JDK workaround
				else if(last == LAST_ALT)
				{
					last = LAST_NOTHING;
					switch(ch)
					{
					case 'B':
					case 'M':
					case 'X':
					case 'c':
					case '!':
					case ',':
					case '?':
						return null;
					}
				}
			}
			else
			{
				if((modifiers & InputEvent.SHIFT_MASK) != 0)
				{
					switch(ch)
					{
					case '\n':
					case '\t':
						return null;
					}
				}
				modifiers = 0;
			}

			return evt;
		//}}}
		//{{{ KEY_RELEASED...
		case KeyEvent.KEY_RELEASED:
			switch(keyCode)
			{
			case KeyEvent.VK_ALT:
				modifiers &= ~InputEvent.ALT_MASK;
				lastKeyTime = evt.getWhen();
				// we consume this to work around the bug
				// where A+TAB window switching activates
				// the menu bar on Windows.
				evt.consume();
				return null;
			case KeyEvent.VK_ALT_GRAPH:
				modifiers &= ~InputEvent.ALT_GRAPH_MASK;
				return null;
			case KeyEvent.VK_CONTROL:
				modifiers &= ~InputEvent.CTRL_MASK;
				return null;
			case KeyEvent.VK_SHIFT:
				modifiers &= ~InputEvent.SHIFT_MASK;
				return null;
			case KeyEvent.VK_META:
				modifiers &= ~InputEvent.META_MASK;
				return null;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_PAGE_UP:
			case KeyEvent.VK_PAGE_DOWN:
			case KeyEvent.VK_END:
			case KeyEvent.VK_HOME:
				/* workaround for A+keys producing
				 * garbage on Windows */
				if(modifiers == InputEvent.ALT_MASK)
					last = LAST_ALT;
				break;
			}
			return evt;
		//}}}
		default:
			return evt;
		}
	} //}}}

	//{{{ numericKeypadKey() method
	/**
	 * A workaround for non-working NumLock status in some Java versions.
	 * @since jEdit 4.0pre8
	 */
	public static void numericKeypadKey()
	{
		last = LAST_NOTHING;
	} //}}}

	//{{{ Package-private members
	static long lastKeyTime;
	static int modifiers;
	//}}}

	//{{{ Private members
	private static int last;
	private static final int LAST_NOTHING = 0;
	private static final int LAST_NUMKEYPAD = 1;
	private static final int LAST_ALT = 2;
	//}}}
}