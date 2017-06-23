/**
 * 
 */
package org.irods.jargon.jcommands.core;

import org.irods.jargon.core.connection.IRODSAccount;
import org.springframework.stereotype.Component;

/**
 * @author mconway
 *
 */
@Component
public class ShellContext {

	private String currentLocalPath = "/";
	private String currentIrodsPath = "/";
	private IRODSAccount currentIrodsAccount;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ShellContext [");
		if (currentLocalPath != null) {
			builder.append("currentLocalPath=").append(currentLocalPath).append(", ");
		}
		if (currentIrodsPath != null) {
			builder.append("currentIrodsPath=").append(currentIrodsPath).append(", ");
		}
		if (currentIrodsAccount != null) {
			builder.append("currentIrodsAccount=").append(currentIrodsAccount);
		}
		builder.append("]");
		return builder.toString();
	}

	public String getCurrentLocalPath() {
		return currentLocalPath;
	}

	public void setCurrentLocalPath(String currentLocalPath) {
		this.currentLocalPath = currentLocalPath;
	}

	public String getCurrentIrodsPath() {
		return currentIrodsPath;
	}

	public void setCurrentIrodsPath(String currentIrodsPath) {
		this.currentIrodsPath = currentIrodsPath;
	}

	public IRODSAccount getCurrentIrodsAccount() {
		return currentIrodsAccount;
	}

	public void setCurrentIrodsAccount(IRODSAccount currentIrodsAccount) {
		this.currentIrodsAccount = currentIrodsAccount;
	}

}
