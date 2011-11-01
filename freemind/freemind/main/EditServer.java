/*
 * EditServer.java - FreeMind server
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2003 Slava Pestov
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

package freemind.main;

//{{{ Imports
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import javax.swing.SwingUtilities;

/**
 * Inter-process communication.
 * <p>
 * 
 * The edit server protocol is very simple. <code>$HOME/.jedit/server</code> is
 * an ASCII file containing two lines, the first being the port number, the
 * second being the authorization key.
 * <p>
 * 
 * You connect to that port on the local machine, sending the authorization key
 * as four bytes in network byte order, followed by the length of the BeanShell
 * script as two bytes in network byte order, followed by the script in UTF8
 * encoding. After the socked is closed, the BeanShell script will be executed
 * by FreeMind.
 * <p>
 * 
 * The snippet is executed in the AWT thread. None of the usual BeanShell
 * variables (view, buffer, textArea, editPane) are set so the script has to
 * figure things out by itself.
 * <p>
 * 
 * In most cases, the script will call the static
 * {@link #handleClient(boolean,String,String[])} method, but of course more
 * complicated stuff can be done too.
 * 
 * @author Slava Pestov
 * @version $Id: EditServer.java 19384 2011-02-23 16:50:37Z k_satoda $
 */
public class EditServer extends Thread {
	protected static java.util.logging.Logger logger = null;
	private final FreeMindMain mFrame;

	// {{{ EditServer constructor
	EditServer(String portFile, FreeMindMain pFrame) {
		super("FreeMind server daemon [" + portFile + "]");
		mFrame = pFrame;
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		setDaemon(true);
		this.portFile = portFile;

		try {
			// On Unix, set permissions of port file to rw-------,
			// so that on broken Unices which give everyone read
			// access to user home dirs, people can't see your
			// port file (and hence send arbitriary BeanShell code
			// your way. Nasty.)
			if (Tools.isUnix()) {
				new File(portFile).createNewFile();
				Tools.setPermissions(portFile, 0600);
			}

			// Bind to any port on localhost; accept 2 simultaneous
			// connection attempts before rejecting connections
			socket = new ServerSocket(0, 2, InetAddress.getByName("127.0.0.1"));
			authKey = new Random().nextInt(Integer.MAX_VALUE);
			int port = socket.getLocalPort();

			FileWriter out = new FileWriter(portFile);

			try {
				out.write("b\n");
				out.write(String.valueOf(port));
				out.write("\n");
				out.write(String.valueOf(authKey));
				out.write("\n");
			} finally {
				out.close();
			}

			ok = true;

			logger.info("FreeMind server started on port "
					+ socket.getLocalPort());
			logger.info("Authorization key is " + authKey);
		} catch (IOException io) {
			/*
			 * on some Windows versions, connections to localhost fail if the
			 * network is not running. To avoid confusing newbies with weird
			 * error messages, log errors that occur while starting the server
			 * as NOTICE, not ERROR
			 */
			logger.info("" + io);
		}
	} // }}}

	// {{{ run() method
	public void run() {
		for (;;) {
			if (abort)
				return;

			Socket client = null;
			try {
				client = socket.accept();

				// Stop script kiddies from opening the edit
				// server port and just leaving it open, as a
				// DoS
				client.setSoTimeout(1000);

				logger.info(client + ": connected");

				DataInputStream in = new DataInputStream(
						client.getInputStream());

				if (!handleClient(client, in))
					abort = true;
			} catch (Exception e) {
				if (!abort)
					logger.info("" + e);
				abort = true;
			} finally {
				/*
				 * if(client != null) { try { client.close(); } catch(Exception
				 * e) { logger.info(e); }
				 * 
				 * client = null; }
				 */
			}
		}
	} // }}}

	// {{{ isOK() method
	boolean isOK() {
		return ok;
	} // }}}

	// {{{ getPort method
	public int getPort() {
		return socket.getLocalPort();
	} // }}}

	// {{{ stopServer() method
	void stopServer() {
		abort = true;
		try {
			socket.close();
		} catch (IOException io) {
		}

		new File(portFile).delete();
	} // }}}

	// {{{ Private members

	// {{{ Instance variables
	private String portFile;
	private ServerSocket socket;
	private int authKey;
	private boolean ok;
	private boolean abort;

	// }}}

	// {{{ handleClient() method
	private boolean handleClient(final Socket client, DataInputStream in)
			throws Exception {
		int key = in.readInt();
		if (key != authKey) {
			logger.info(client + ": wrong" + " authorization key (got " + key
					+ ", expected " + authKey + ")");
			in.close();
			client.close();

			return false;
		} else {
			// Reset the timeout
			client.setSoTimeout(0);

			logger.info(client + ": authenticated" + " successfully");

			final String script = in.readUTF();
			logger.info(script);

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						Vector urls = Tools.urlStringToUrls(script);
						for (Iterator it = urls.iterator(); it.hasNext();) {
							URL urli = (URL) it.next();
							mFrame.getController().getModeController()
									.load(urli);
						}
					} catch (MalformedURLException e) {
						freemind.main.Resources.getInstance().logException(e);
					} catch (Exception e) {
						freemind.main.Resources.getInstance().logException(e);
					}
				}
			});
			in.close();
			client.close();

			return true;
		}
	} // }}}

	// }}}
}
