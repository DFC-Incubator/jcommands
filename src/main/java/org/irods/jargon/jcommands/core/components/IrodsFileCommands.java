/**
 * 
 */
package org.irods.jargon.jcommands.core.components;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.packinstr.TransferOptions.ForceOption;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.transfer.TransferControlBlock;
import org.irods.jargon.core.utils.MiscIRODSUtils;
import org.irods.jargon.jcommands.core.CoreUtils;
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
	private CoreUtils coreUtils;

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
			@CliOption(key = {
					"local" }, mandatory = true, help = "local file, relative or absolute") String localFileAbsPath,
			@CliOption(key = {
					"remote" }, help = "irods file, relative or absolute, omit for current dir") String irodsFileAbsPath,
			@CliOption(key = {
					"overwrite" }, help = "indicates overwrite", specifiedDefaultValue = "false") String overwrite) {
		log.info("iput()");
		log.info("localFile:{}", localFileAbsPath);
		log.info("irodsFile:{}", irodsFileAbsPath);
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

			File localFile;
			if (localFileAbsPath.startsWith("/")) {
				log.info("local file absolute");
				localFile = new File(localFileAbsPath);
			} else {
				log.info("relative path for local file");
				Path localPath = Paths.get(shellContext.getCurrentLocalPath()).resolve(localFileAbsPath);
				localFile = localPath.toFile();
			}

			log.info("checking sourceFile:{}", localFile);
			if (!localFile.exists()) {
				log.error("local file does not exist:{}", localFile);
				return "Error: local file does not exist";
			}

			if (!localFile.isFile()) {
				log.error("local file is a directory:{}", localFile);
				return "Error: local file is a directory, recursive not yet supported";
			}

			IRODSFile targetFile;
			if (irodsFileAbsPath == null || irodsFileAbsPath.isEmpty()) {
				log.info("use current dir");
				targetFile = irodsAccessObjectFactory.getIRODSFileFactory(shellContext.getCurrentIrodsAccount())
						.instanceIRODSFile(shellContext.getCurrentIrodsPath());
			} else if (irodsFileAbsPath.startsWith("/")) {
				log.info("irods file absolute");
				targetFile = irodsAccessObjectFactory.getIRODSFileFactory(shellContext.getCurrentIrodsAccount())
						.instanceIRODSFile(irodsFileAbsPath);
			} else {
				Path remotePath = Paths.get(shellContext.getCurrentIrodsPath()).resolve(irodsFileAbsPath);
				targetFile = irodsAccessObjectFactory.getIRODSFileFactory(shellContext.getCurrentIrodsAccount())
						.instanceIRODSFile(remotePath.toString());
			}

			log.info("checking targetFile:{}", targetFile);
			if (!targetFile.exists()) {
				log.error("irods file does not exist:{}", targetFile);
				return "Error: target file does not exist";
			}

			if (!targetFile.isDirectory()) {
				log.error("target file isnt a directory:{}", targetFile);
				return "Error: target file isnt a directory";
			}

			long startTime = System.currentTimeMillis();
			dto.putOperation(localFile, targetFile, listener, tcb);
			long endTime = System.currentTimeMillis();

			System.out.println("transfer of " + MiscIRODSUtils.humanReadableByteCount(localFile.length()));
			System.out.println("completed in " + (endTime - startTime) + " milliseconds");
			return "Transfer complete";
		} catch (JargonException e) {
			log.error("exception in transfer", e);
			return "Error:" + e.getMessage();
		} finally {
			irodsAccessObjectFactory.closeSessionAndEatExceptions();
		}

	}

	@CliCommand(value = "iget", help = "transfer a file from irods to local")
	public String iGet(
			@CliOption(key = {
					"local" }, mandatory = false, help = "local file, relative or absolute, omit for current dir") String localFileAbsPath,
			@CliOption(key = {
					"remote" }, mandatory = true, help = "irods file, relative or absolute") String irodsFileAbsPath,
			@CliOption(key = {
					"overwrite" }, help = "indicates overwrite", specifiedDefaultValue = "false") String overwrite) {
		log.info("iget()");
		log.info("localFile:{}", localFileAbsPath);
		log.info("irodsFile:{}", irodsFileAbsPath);
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

			IRODSFile irodsFile;
			if (irodsFileAbsPath.startsWith("/")) {
				log.info("irods file absolute");
				irodsFile = irodsAccessObjectFactory.getIRODSFileFactory(shellContext.getCurrentIrodsAccount())
						.instanceIRODSFile(irodsFileAbsPath);
			} else {
				log.info("relative path for irods file");
				Path irodsPath = Paths.get(shellContext.getCurrentIrodsPath()).resolve(irodsFileAbsPath);
				irodsFile = irodsAccessObjectFactory.getIRODSFileFactory(shellContext.getCurrentIrodsAccount())
						.instanceIRODSFile(irodsPath.toString());
			}

			log.info("checking irodsFile:{}", irodsFile);
			if (!irodsFile.exists()) {
				log.error("irods file does not exist:{}", irodsFile);
				return "Error: irods file does not exist";
			}

			if (!irodsFile.isFile()) {
				log.error("irods file is a directory:{}", irodsFile);
				return "Error: irods file is a directory, I'm not smart yet";
			}

			File localFile;
			if (localFileAbsPath == null || localFileAbsPath.isEmpty()) {
				localFile = new File(shellContext.getCurrentLocalPath());
			} else if (localFileAbsPath.startsWith("/")) {
				log.info("local file absolute");
				localFile = new File(localFileAbsPath);
			} else {
				Path localPath = Paths.get(shellContext.getCurrentLocalPath()).resolve(localFileAbsPath);
				localFile = localPath.toFile();
			}

			log.info("checking localFile:{}", localFile);

			long startTime = System.currentTimeMillis();
			dto.getOperation(irodsFile, localFile, listener, tcb);
			long endTime = System.currentTimeMillis();

			System.out.println("transfer of " + MiscIRODSUtils.humanReadableByteCount(irodsFile.length()));
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

			IRODSFile currFile = coreUtils.resolveIrodsPathToFile(cdVal);

			if (!currFile.exists()) {
				return "Error: path does not exist";
			}
			if (!currFile.isDirectory()) {
				return "Error: not a directory";
			}

			shellContext.setCurrentIrodsPath(currFile.getAbsolutePath());
			return shellContext.getCurrentIrodsPath();

		} finally {
			irodsAccessObjectFactory.closeSessionAndEatExceptions();
		}

	}

	@CliCommand(value = "irm", help = "delete a file or directory")
	public String irmCommand(@CliOption(key = { "", "file" }, mandatory = true, help = "file to delete") String dir,
			@CliOption(key = {
					"force" }, mandatory = false, specifiedDefaultValue = "false", help = "indicates force, required for directory") String force) {

		if (shellContext.getCurrentIrodsAccount() == null) {
			log.warn("no iinit done");
			return "Error: no iinit done";
		}

		IRODSFile irodsFile = coreUtils.resolveIrodsPathToFile(dir);
		boolean isForce = coreUtils.resolveBoolValue(force);

		if (irodsFile.isDirectory()) {
			if (!isForce) {
				log.warn("delete dir with no force");
				return "Error: delete directory with no force, use --force true flag";
			}
			irodsFile.deleteWithForceOption();
		} else if (isForce) {

			irodsFile.deleteWithForceOption();
		} else {
			irodsFile.delete();
		}

		return "Success!";

	}

	@CliCommand(value = "imkdir", help = "create a subdirectory")
	public String imkdirCommand(@CliOption(key = { "", "dir" }, mandatory = true) String dir,
			@CliOption(key = { "parent" }, mandatory = false, specifiedDefaultValue = "false") String parent) {

		if (shellContext.getCurrentIrodsAccount() == null) {
			log.warn("no iinit done");
			return "Error: no iinit done";
		}

		boolean isMakeParent = coreUtils.resolveBoolValue(parent);

		IRODSFile irodsFile = coreUtils.resolveIrodsPathToFile(dir);

		if (isMakeParent) {
			irodsFile.mkdirs();
		} else {
			irodsFile.mkdir();
		}

		return "Success!";

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

	/**
	 * @return the coreUtils
	 */
	public CoreUtils getCoreUtils() {
		return coreUtils;
	}

	/**
	 * @param coreUtils
	 *            the coreUtils to set
	 */
	public void setCoreUtils(CoreUtils coreUtils) {
		this.coreUtils = coreUtils;
	}

}
