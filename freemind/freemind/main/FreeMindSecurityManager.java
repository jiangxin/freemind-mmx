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
/*$Id: FreeMindSecurityManager.java,v 1.1.2.1 2008/03/14 21:15:22 christianfoltin Exp $*/

package freemind.main;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;

/**
 * By default, everything is allowed. But you can install a different security
 * controller once, until you install it again. Thus, the code executed in
 * between is securely controlled by that different security manager. Moreover,
 * only by double registering the manager is removed. So, no malicious code can
 * remove the active security manager.
 * 
 * @author foltin
 * 
 */
public final class FreeMindSecurityManager extends SecurityManager {

	private SecurityManager mFinalSecurityManager = null;

	public FreeMindSecurityManager() {
	}

	/**
	 * @param pFinalSecurityManager
	 *            set twice the same to remove it.
	 * 
	 */
	public void setFinalSecurityManager(SecurityManager pFinalSecurityManager) {
		if (pFinalSecurityManager == mFinalSecurityManager) {
			mFinalSecurityManager = null;
			return;
		}
		if (mFinalSecurityManager != null) {
			throw new SecurityException(
					"There is a SecurityManager installed already.");
		}
		mFinalSecurityManager = pFinalSecurityManager;
	}

	public void checkAccept(String pHost, int pPort) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkAccept(pHost, pPort);
	}

	public void checkAccess(Thread pT) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkAccess(pT);
	}

	public void checkAccess(ThreadGroup pG) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkAccess(pG);
	}

	public void checkAwtEventQueueAccess() {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkAwtEventQueueAccess();
	}

	public void checkConnect(String pHost, int pPort, Object pContext) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkConnect(pHost, pPort, pContext);
	}

	public void checkConnect(String pHost, int pPort) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkConnect(pHost, pPort);
	}

	public void checkCreateClassLoader() {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkCreateClassLoader();
	}

	public void checkDelete(String pFile) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkDelete(pFile);
	}

	public void checkExec(String pCmd) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkExec(pCmd);
	}

	public void checkExit(int pStatus) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkExit(pStatus);
	}

	public void checkLink(String pLib) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkLink(pLib);
	}

	public void checkListen(int pPort) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkListen(pPort);
	}

	public void checkMemberAccess(Class arg0, int arg1) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkMemberAccess(arg0, arg1);
	}

	public void checkMulticast(InetAddress pMaddr, byte pTtl) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkMulticast(pMaddr, pTtl);
	}

	public void checkMulticast(InetAddress pMaddr) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkMulticast(pMaddr);
	}

	public void checkPackageAccess(String pPkg) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkPackageAccess(pPkg);
	}

	public void checkPackageDefinition(String pPkg) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkPackageDefinition(pPkg);
	}

	public void checkPermission(Permission pPerm, Object pContext) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkPermission(pPerm, pContext);
	}

	public void checkPermission(Permission pPerm) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkPermission(pPerm);
	}

	public void checkPrintJobAccess() {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkPrintJobAccess();
	}

	public void checkPropertiesAccess() {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkPropertiesAccess();
	}

	public void checkPropertyAccess(String pKey) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkPropertyAccess(pKey);
	}

	public void checkRead(FileDescriptor pFd) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkRead(pFd);
	}

	public void checkRead(String pFile, Object pContext) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkRead(pFile, pContext);
	}

	public void checkRead(String pFile) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkRead(pFile);
	}

	public void checkSecurityAccess(String pTarget) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkSecurityAccess(pTarget);
	}

	public void checkSetFactory() {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkSetFactory();
	}

	public void checkSystemClipboardAccess() {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkSystemClipboardAccess();
	}

	public boolean checkTopLevelWindow(Object pWindow) {
		if (mFinalSecurityManager == null)
			return true;
		return mFinalSecurityManager.checkTopLevelWindow(pWindow);
	}

	public void checkWrite(FileDescriptor pFd) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkWrite(pFd);
	}

	public void checkWrite(String pFile) {
		if (mFinalSecurityManager == null)
			return;
		mFinalSecurityManager.checkWrite(pFile);
	}

	public Object getSecurityContext() {
		if (mFinalSecurityManager == null)
			return super.getSecurityContext();
		return mFinalSecurityManager.getSecurityContext();
	}

}
