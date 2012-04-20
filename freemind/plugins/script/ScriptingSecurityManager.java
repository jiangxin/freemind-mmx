/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2008 Christian Foltin and others.
 *
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
 *
 * Created on 06.03.2008
 */
/*$Id: ScriptingSecurityManager.java,v 1.1.2.5 2008/04/02 20:02:37 christianfoltin Exp $*/

package plugins.script;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;
import java.util.HashSet;

import freemind.main.Resources;

/**
 * @author foltin
 * 
 */
public class ScriptingSecurityManager extends SecurityManager {

	private final boolean mWithoutFileRestriction;
	private final boolean mWithoutNetworkRestriction;
	private final boolean mWithoutExecRestriction;

	private static final int PERM_Accept = 0;
	private static final int PERM_Connect = 1;
	private static final int PERM_Listen = 2;
	private static final int PERM_Multicast = 3;
	private static final int PERM_SetFactory = 4;
	private static final int PERM_Exec = 5;
	private static final int PERM_Link = 6;
	private static final int PERM_Delete = 7;
	private static final int PERM_Read = 8;
	private static final int PERM_Write = 9;

	private static final int PERM_GROUP_FILE = 0;
	private static final int PERM_GROUP_NETWORK = 1;
	private static final int PERM_GROUP_EXEC = 2;
	private static java.util.logging.Logger logger = null;

	public ScriptingSecurityManager(boolean pWithoutFileRestriction,
			boolean pWithoutNetworkRestriction, boolean pWithoutExecRestriction) {
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		mWithoutFileRestriction = pWithoutFileRestriction;
		mWithoutNetworkRestriction = pWithoutNetworkRestriction;
		mWithoutExecRestriction = pWithoutExecRestriction;
	}

	public void checkAccept(String pHost, int pPort) {
		if (mWithoutNetworkRestriction)
			return;
		throw getException(PERM_GROUP_NETWORK, PERM_Accept);
	}

	public void checkConnect(String pHost, int pPort, Object pContext) {
		if (mWithoutNetworkRestriction)
			return;
		throw getException(PERM_GROUP_NETWORK, PERM_Connect);
	}

	public void checkConnect(String pHost, int pPort) {
		if (mWithoutNetworkRestriction)
			return;
		throw getException(PERM_GROUP_NETWORK, PERM_Connect);
	}

	public void checkListen(int pPort) {
		if (mWithoutNetworkRestriction)
			return;
		throw getException(PERM_GROUP_NETWORK, PERM_Listen);
	}

	public void checkMulticast(InetAddress pMaddr, byte pTtl) {
		if (mWithoutNetworkRestriction)
			return;
		throw getException(PERM_GROUP_NETWORK, PERM_Multicast);
	}

	public void checkMulticast(InetAddress pMaddr) {
		if (mWithoutNetworkRestriction)
			return;
		throw getException(PERM_GROUP_NETWORK, PERM_Multicast);
	}

	public void checkSetFactory() {
		if (mWithoutNetworkRestriction)
			return;
		throw getException(PERM_GROUP_NETWORK, PERM_SetFactory);
	}

	public void checkExec(String pCmd) {
		if (mWithoutExecRestriction)
			return;
		throw getException(PERM_GROUP_EXEC, PERM_Exec);
	}

	public void checkLink(String pLib) {
		/*
		 * This should permit system libraries to be loaded.
		 */
		HashSet set = new HashSet();
		set.add("awt");
		set.add("net");
		set.add("jpeg");
		set.add("fontmanager");
		if (mWithoutExecRestriction || set.contains(pLib))
			return;
		throw getException(PERM_GROUP_EXEC, PERM_Link);
	}

	public void checkDelete(String pFile) {
		if (mWithoutFileRestriction)
			return;
		throw getException(PERM_GROUP_FILE, PERM_Delete);
	}

	public void checkRead(FileDescriptor pFd) {
		if (mWithoutFileRestriction)
			return;
		throw getException(PERM_GROUP_FILE, PERM_Read);
	}

	public void checkRead(String pFile, Object pContext) {
		if (mWithoutFileRestriction)
			return;
		throw getException(PERM_GROUP_FILE, PERM_Read);
	}

	public void checkRead(String pFile) {
		if (mWithoutFileRestriction)
			return;
		throw getException(PERM_GROUP_FILE, PERM_Read);
	}

	public void checkWrite(FileDescriptor pFd) {
		if (mWithoutFileRestriction)
			return;
		throw getException(PERM_GROUP_FILE, PERM_Write);
	}

	public void checkWrite(String pFile) {
		if (mWithoutFileRestriction)
			return;
		throw getException(PERM_GROUP_FILE, PERM_Write);
	}

	private SecurityException getException(int pPermissionGroup, int pPermission) {
		return new SecurityException(Resources.getInstance().format(
				"plugins/ScriptEditor.FORBIDDEN_ACTION",
				new Integer[] { new Integer(pPermissionGroup),
						new Integer(pPermission) }));
	}

	public void checkAccess(Thread pT) {
	}

	public void checkAccess(ThreadGroup pG) {
	}

	public void checkAwtEventQueueAccess() {
	}

	public void checkCreateClassLoader() {
	}

	public void checkExit(int pStatus) {
	}

	public void checkMemberAccess(Class arg0, int arg1) {
	}

	public void checkPackageAccess(String pPkg) {
	}

	public void checkPackageDefinition(String pPkg) {
	}

	public void checkPermission(Permission pPerm, Object pContext) {
		logger.fine("Check Permission with Context: " + pPerm.getClass());
	}

	public void checkPermission(Permission pPerm) {
		logger.fine("Check Permission: " + pPerm.getClass());
	}

	public void checkPrintJobAccess() {
	}

	public void checkPropertiesAccess() {
	}

	public void checkPropertyAccess(String pKey) {
	}

	public void checkSecurityAccess(String pTarget) {
	}

	public void checkSystemClipboardAccess() {
	}

	public boolean checkTopLevelWindow(Object pWindow) {
		return true;
	}

}
