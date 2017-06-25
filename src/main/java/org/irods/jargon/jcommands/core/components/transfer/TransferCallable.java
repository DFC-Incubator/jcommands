/**
 * 
 */
package org.irods.jargon.jcommands.core.components.transfer;

import java.util.concurrent.Callable;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.transfer.TransferControlBlock;
import org.irods.jargon.core.transfer.TransferStatusCallbackListener;

/**
 * Callable that runs a transfer
 * 
 * @author mcc
 *
 */
public abstract class TransferCallable implements Callable<TransferFuture> {

	private IRODSAccessObjectFactory irodsAccessObjectFactory;
	private IRODSAccount irodsAccount;
	private TransferControlBlock transferControlBlock;
	private TransferStatusCallbackListener transferStatusCallbackListener;

	/**
	 * 
	 */
	public TransferCallable(IRODSAccessObjectFactory irodsAccessObjectFactory, IRODSAccount irodsAccount,
			TransferControlBlock transferControlBlock, TransferStatusCallbackListener transferStatusCallbackListener) {

		if (irodsAccessObjectFactory == null) {
			throw new IllegalArgumentException("null irodsAccessObjectFactory");
		}

		if (irodsAccount == null) {
			throw new IllegalArgumentException("null irodsAccount");
		}

		if (transferControlBlock == null) {
			throw new IllegalArgumentException("null transferControlBlock");
		}

		if (transferStatusCallbackListener == null) {
			throw new IllegalArgumentException("null transferStatusCallbackListener");
		}

		this.irodsAccessObjectFactory = irodsAccessObjectFactory;
		this.irodsAccount = irodsAccount;
		this.transferControlBlock = transferControlBlock;
		this.transferStatusCallbackListener = transferStatusCallbackListener;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public abstract TransferFuture call() throws Exception;

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
	 * @return the irodsAccount
	 */
	public IRODSAccount getIrodsAccount() {
		return irodsAccount;
	}

	/**
	 * @param irodsAccount
	 *            the irodsAccount to set
	 */
	public void setIrodsAccount(IRODSAccount irodsAccount) {
		this.irodsAccount = irodsAccount;
	}

	/**
	 * @return the transferControlBlock
	 */
	public TransferControlBlock getTransferControlBlock() {
		return transferControlBlock;
	}

	/**
	 * @param transferControlBlock
	 *            the transferControlBlock to set
	 */
	public void setTransferControlBlock(TransferControlBlock transferControlBlock) {
		this.transferControlBlock = transferControlBlock;
	}

	/**
	 * @return the transferStatusCallbackListener
	 */
	public TransferStatusCallbackListener getTransferStatusCallbackListener() {
		return transferStatusCallbackListener;
	}

	/**
	 * @param transferStatusCallbackListener
	 *            the transferStatusCallbackListener to set
	 */
	public void setTransferStatusCallbackListener(TransferStatusCallbackListener transferStatusCallbackListener) {
		this.transferStatusCallbackListener = transferStatusCallbackListener;
	}

}
