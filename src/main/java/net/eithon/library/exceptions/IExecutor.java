package net.eithon.library.exceptions;


public interface IExecutor {
	public void doIt() throws TryAgainException, FatalException, PlayerException, EithonException;

}
