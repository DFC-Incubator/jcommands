/**
 * 
 */
package org.irods.jargon.jcommands.core.components;

import java.io.File;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.packinstr.TransferOptions.ForceOption;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.transfer.TransferControlBlock;
import org.irods.jargon.core.utils.MiscIRODSUtils;
import org.irods.jargon.jcommands.core.ShellContext;
import org.irods.jargon.jcommands.core.components.transfer.ConsoleCallbackListener;
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

	@CliCommand(value = "iput", help = "transfer a file from local to irods")
	public String iPut(
			@CliOption(key = { "local" }, mandatory = true, help = "local file, relative or absolute") String localFile,
			@CliOption(key = {
					"remote" }, help = "irods file, relative or absolute, omit for current dir") String irodsFile,
			@CliOption(key = {
					"overwrite" }, help = "indicates overwrite", specifiedDefaultValue = "false") String overwrite) {
		log.info("iput()");
		log.info("localFile:{}", localFile);
		log.info("irodsFile:{}", irodsFile);
		log.info("overwrite:{}", overwrite);

		if (shellContext.getCurrentIrodsAccount() == null) {
			log.warn("no iinit done");
			return "Error: no iinit done";
		}

		try {
			TransferControlBlock tcb = irodsAccessObjectFactory
					.buildDefaultTransferControlBlockBasedOnJargonProperties();

			if (overwrite == null || overwrite.isEmpty()) {
				log.info("no overwrite");

			} else if (Boolean.valueOf(overwrite) == true) {
				log.info("setting overwrite");
				tcb.getTransferOptions().setForceOption(ForceOption.USE_FORCE);
			}

			DataTransferOperations dto = irodsAccessObjectFactory
					.getDataTransferOperations(shellContext.getCurrentIrodsAccount());

			ConsoleCallbackListener listener = new ConsoleCallbackListener();

			File sourceFile;
			if (localFile.startsWith("/")) {
				log.info("local file absolute");
				sourceFile = new File(localFile);
			} else {
				log.info("relative path for local file");
				sourceFile = new File(shellContext.getCurrentLocalPath(), localFile);
			}

			log.info("checking sourceFile:{}", sourceFile);
			if (!sourceFile.exists()) {
				log.error("local file does not exist:{}", sourceFile);
				return "Error: local file does not exist";
			}

			if (!sourceFile.isFile()) {
				log.error("local file is a directory:{}", sourceFile);
				return "Error: local file is a directory, recursive not yet supported";
			}

			IRODSFile targetFile;
			if (irodsFile == null || irodsFile.isEmpty()) {
				log.info("use current dir");
				targetFile = irodsAccessObjectFactory.getIRODSFileFactory(shellContext.getCurrentIrodsAccount())
						.instanceIRODSFile(shellContext.getCurrentIrodsPath());
			} else if (irodsFile.startsWith("/")) {
				log.info("irods file absolute");
				targetFile = irodsAccessObjectFactory.getIRODSFileFactory(shellContext.getCurrentIrodsAccount())
						.instanceIRODSFile(irodsFile);
			} else {
				log.info("relative path for irods file");
				targetFile = irodsAccessObjectFactory.getIRODSFileFactory(shellContext.getCurrentIrodsAccount())
						.instanceIRODSFile(shellContext.getCurrentIrodsPath(), irodsFile);
			}

			log.info("checking targetFile:{}", targetFile);
			if (!targetFile.exists()) {
				log.error("irods file does not exist:{}", targetFile);
				return "Error: target file does not exist";
			}

			if (!targetFile.isDirectory()) {
				log.error("target file is a directory:{}", targetFile);
				return "Error: target file is a directory";
			}

			long startTime = System.currentTimeMillis();
			dto.putOperation(sourceFile, targetFile, listener, tcb);
			long endTime = System.currentTimeMillis();

			System.out.println("transfer of " + MiscIRODSUtils.humanReadableByteCount(sourceFile.length()));
			System.out.println("completed in " + (endTime - startTime) + " milliseconds");
			return "Transfer complete";
		} catch (JargonException e) {
			log.error("exception in transfer", e);
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
