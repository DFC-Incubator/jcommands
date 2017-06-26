/**
 * 
 */
package org.irods.jargon.jcommands.core;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Various utils for path and parameter munging
 * 
 * @author mcc
 *
 */
@Component
public class CoreUtils {

	@Autowired
	private ShellContext shellContext;

	@Autowired
	private IRODSAccessObjectFactory irodsAccessObjectFactory;

	/**
	 * 
	 */
	public CoreUtils() {
	}

	/**
	 * Is that thing true or false?
	 * 
	 * @param boolString
	 *            <code>String</code> param value
	 * @return <code>String</code> with boolean interpretation
	 */
	public boolean resolveBoolValue(final String boolString) {

		if (boolString == null || boolString.isEmpty()) {
			return false;
		} else {
			return Boolean.valueOf(boolString);
		}
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

	/**
	 * Given a local path fragment (relative or absolute) resolve in the context
	 * of the current local path
	 * 
	 * @param dir
	 *            <code>String</code> with the path fragment
	 * @return {@link File} that is the resolution of the path fragment against
	 *         the current local path
	 */
	public File resolveLocalPathToFile(String dir) {
		if (dir == null || dir.isEmpty()) {
			return new File(shellContext.getCurrentLocalPath());
		}

		Path path;
		if (dir.startsWith("/")) {
			path = Paths.get(dir);
		} else {
			path = Paths.get(shellContext.getCurrentLocalPath()).resolve(dir);
		}

		return path.toFile();

	}
}
