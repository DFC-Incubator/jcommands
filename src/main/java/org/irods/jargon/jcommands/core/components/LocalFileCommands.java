/**
 * 
 */
package org.irods.jargon.jcommands.core.components;

import java.io.File;

import org.irods.jargon.jcommands.core.ShellContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

/**
 * Local file system commands (ls, mkdir, etc)
 * 
 * @author mconway
 *
 */
@Component
public class LocalFileCommands implements CommandMarker {

	@Autowired
	private ShellContext shellContext;

	public ShellContext getShellContext() {
		return shellContext;
	}

	public void setShellContext(ShellContext shellContext) {
		this.shellContext = shellContext;
	}

	@CliCommand(value = "pwd", help = "display current local directory")
	public String pwdCommand() {
		return shellContext.getCurrentLocalPath();
	}

	@CliCommand(value = "ls", help = "display directory contents under current directory")
	public String lsCommand() {

		File currFile = new File(shellContext.getCurrentLocalPath());
		StringBuilder sb = new StringBuilder();
		for (String child : currFile.list()) {
			sb.append("\n");
			sb.append("\t");
			sb.append(child);
		}
		return sb.toString();

	}

	@CliCommand(value = "cd", help = "display directory contents under current directory")
	public String cdCommand(@CliOption(key = { "", "text" }) String cdVal) {
		File currFile;
		if (cdVal.startsWith("/")) {
			currFile = new File(cdVal);
		} else if (cdVal.equals("..")) {
			currFile = new File(shellContext.getCurrentLocalPath()).getParentFile();
		} else {
			currFile = new File(shellContext.getCurrentLocalPath(), cdVal);
		}

		if (!currFile.exists()) {
			return "Error: path does not exist";
		}
		if (!currFile.isDirectory()) {
			return "Error: not a directory";
		}

		shellContext.setCurrentLocalPath(currFile.getAbsolutePath());
		return shellContext.getCurrentLocalPath();

	}

}
