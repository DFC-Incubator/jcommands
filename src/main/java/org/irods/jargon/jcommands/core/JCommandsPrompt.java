/**
 * 
 */
package org.irods.jargon.jcommands.core;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultPromptProvider;
import org.springframework.stereotype.Component;

/**
 * @author mconway
 *
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JCommandsPrompt extends DefaultPromptProvider {

	@Override
	public String getPrompt() {
		return "iRODS>";
	}
}
