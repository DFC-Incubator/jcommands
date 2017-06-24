/**
 * 
 */
package org.irods.jargon.jcommands.core.components;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.utils.MiscIRODSUtils;
import org.irods.jargon.jcommands.core.ShellContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

/**
 * Configuration commands
 * 
 * @author mcc
 *
 */
@Component
public class ConfigCommands implements CommandMarker {

	public static final Logger log = LoggerFactory.getLogger(ConfigCommands.class);
	@Autowired
	private ShellContext shellContext;
	@Autowired
	IRODSAccessObjectFactory irodsAccessObjectFactory;

	/**
	 * 
	 */
	public ConfigCommands() {
	}

	@CliCommand(value = "iinit", help = "Configure account settings")
	public String cdCommand(@CliOption(key = { "host" }, mandatory = true, help = "irods host") String host,
			@CliOption(key = { "zone" }, mandatory = true, help = "irods zone") String zone,
			@CliOption(key = { "port" }, mandatory = true, help = "irods port") int port,
			@CliOption(key = { "user" }, mandatory = true, help = "the user id") String user,
			@CliOption(key = { "resc" }, help = "optional default resource") String resource,
			@CliOption(key = { "pwd" }, mandatory = true, help = "irods password") String password) {

		String myresc;
		if (resource == null) {
			myresc = "";
		} else {
			myresc = resource;
		}

		try {
			IRODSAccount irodsAccount = IRODSAccount.instance(host, port, user, password, "", zone, myresc);
			irodsAccessObjectFactory.authenticateIRODSAccount(irodsAccount);
			log.info("authenticated");
			shellContext.setCurrentIrodsAccount(irodsAccount);
			String homeDir = MiscIRODSUtils.computeHomeDirectoryForIRODSAccount(irodsAccount);
			shellContext.setCurrentIrodsPath(homeDir);
			return "logged in!";

		} catch (JargonException e) {
			log.error("error creating irods account", e);
			shellContext.setCurrentIrodsAccount(null);
			return ("error!  unable to create irods account:" + e.getMessage());
		} finally {
			irodsAccessObjectFactory.closeSessionAndEatExceptions();
		}

	}

	@CliCommand(value = "iexit", help = "clear connection")
	public String cdCommand() {

		shellContext.setCurrentIrodsAccount(null);
		return "logged out!";

	}

	/**
	 * @return the shellContext
	 */
	public ShellContext getShellContext() {
		return shellContext;
	}

	/**
	 * @param shellContext
	 *            the shellContext to set
	 */
	public void setShellContext(ShellContext shellContext) {
		this.shellContext = shellContext;
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
