/**
 * 
 */
package org.irods.jargon.jcommands.core.components;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.jcommands.core.ShellContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

/**
 * Irods file system commands (ils, ipwd, icd)
 * 
 * @author mconway
 *
 */
@Component
public class IrodsFileCommands implements CommandMarker {

	public static final Logger log = LoggerFactory.getLogger(IrodsFileCommands.class);

	@Autowired
	private IRODSAccessObjectFactory irodsAccessObjectFactory;

	@Autowired
	private ShellContext shellContext;

	public ShellContext getShellContext() {
		return shellContext;
	}

	public void setShellContext(ShellContext shellContext) {
		this.shellContext = shellContext;
	}

	@CliCommand(value = "ipwd", help = "display current irods directory")
	public String pwdCommand() {
		return shellContext.getCurrentIrodsPath();
	}

	@CliCommand(value = "ils", help = "display directory contents under current directory")
	public String lsCommand() {

		if (shellContext.getCurrentIrodsAccount() == null) {
			log.warn("no iinit done");
			return "Error: no iinit done";
		}

		try {
			IRODSFile parent = irodsAccessObjectFactory.getIRODSFileFactory(shellContext.getCurrentIrodsAccount())
					.instanceIRODSFile(shellContext.getCurrentIrodsPath());
			StringBuilder sb = new StringBuilder();
			for (String child : parent.list()) {
				sb.append("\n");
				sb.append("\t");
				sb.append(child);
			}
			return sb.toString();
		} catch (JargonException e) {
			log.error("jargon exception getting file", e);
			return "Error:" + e.getMessage();
		} finally {
			irodsAccessObjectFactory.closeSessionAndEatExceptions();
		}

	}

	@CliCommand(value = "icd", help = "display directory contents under current irods directory")
	public String cdCommand(@CliOption(key = { "", "text" }) String cdVal) {

		if (shellContext.getCurrentIrodsAccount() == null) {
			log.warn("no iinit done");
			return "Error: no iinit done";
		}

		try {
			IRODSFile currFile = irodsAccessObjectFactory.getIRODSFileFactory(shellContext.getCurrentIrodsAccount())
					.instanceIRODSFile(shellContext.getCurrentIrodsPath());
			if (cdVal.startsWith("/")) {
				currFile = irodsAccessObjectFactory.getIRODSFileFactory(shellContext.getCurrentIrodsAccount())
						.instanceIRODSFile("/");
			} else if (cdVal.equals("..")) {
				currFile.getParentFile();

			} else {
				currFile = irodsAccessObjectFactory.getIRODSFileFactory(shellContext.getCurrentIrodsAccount())
						.instanceIRODSFile(currFile.getAbsolutePath(), cdVal);
			}

			if (!currFile.exists()) {
				return "Error: path does not exist";
			}
			if (!currFile.isDirectory()) {
				return "Error: not a directory";
			}

			shellContext.setCurrentIrodsPath(currFile.getAbsolutePath());
			return shellContext.getCurrentIrodsPath();

		} catch (JargonException e) {
			log.error("jargon exception getting file", e);
			return "Error:" + e.getMessage();
		} finally {
			irodsAccessObjectFactory.closeSessionAndEatExceptions();
		}

	}

	/**
	 * @return the irodsAccessObjectFactory
	 */
	public IRODSAccessObjectFactory getIrodsAccessObjectFactory() {
		return irodsAccessObjectFactory;
	}

	/**
	 * @param irodsAccessObjectFactory
	 *            the irodsAccessObjectFactory to set
	 */
	public void setIrodsAccessObjectFactory(IRODSAccessObjectFactory irodsAccessObjectFactory) {
		this.irodsAccessObjectFactory = irodsAccessObjectFactory;
	}

}
