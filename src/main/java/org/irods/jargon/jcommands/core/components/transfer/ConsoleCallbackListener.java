/**
 * 
 */
package org.irods.jargon.jcommands.core.components.transfer;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.transfer.TransferStatus;
import org.irods.jargon.core.transfer.TransferStatusCallbackListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mcc
 *
 */
public class ConsoleCallbackListener implements TransferStatusCallbackListener {

	public static final Logger log = LoggerFactory.getLogger(ConsoleCallbackListener.class);

	/**
	 * 
	 */
	public ConsoleCallbackListener() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.irods.jargon.core.transfer.TransferStatusCallbackListener#
	 * statusCallback(org.irods.jargon.core.transfer.TransferStatus)
	 */
	@Override
	public FileStatusCallbackResponse statusCallback(TransferStatus transferStatus) throws JargonException {
		System.out.println(transferStatus.toString());
		return FileStatusCallbackResponse.CONTINUE;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.irods.jargon.core.transfer.TransferStatusCallbackListener#
	 * overallStatusCallback(org.irods.jargon.core.transfer.TransferStatus)
	 */
	@Override
	public void overallStatusCallback(TransferStatus transferStatus) throws JargonException {
		System.out.println(transferStatus.toString());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.irods.jargon.core.transfer.TransferStatusCallbackListener#
	 * transferAsksWhetherToForceOperation(java.lang.String, boolean)
	 */
	@Override
	public CallbackResponse transferAsksWhetherToForceOperation(String irodsAbsolutePath, boolean isCollection) {
		System.out.println("file exists, specify overwrite=true:" + irodsAbsolutePath);
		return CallbackResponse.CANCEL;
	}

}
