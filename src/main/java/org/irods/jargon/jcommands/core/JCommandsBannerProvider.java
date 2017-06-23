/**
 * 
 */
package org.irods.jargon.jcommands.core;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.exception.JargonRuntimeException;
import org.irods.jargon.core.utils.LocalFileUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.stereotype.Component;

/**
 * Banner
 * 
 * @author mconway
 *
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JCommandsBannerProvider extends DefaultBannerProvider {

	@Override
	public String getBanner() {
		try {
			return LocalFileUtils.getClasspathResourceFileAsString("/banner.txt");
		} catch (JargonException e) {
			throw new JargonRuntimeException("banner missing");
		}

	}

	@Override
	public String getWelcomeMessage() {

		return "Welcome to jCommands";
	}

}
