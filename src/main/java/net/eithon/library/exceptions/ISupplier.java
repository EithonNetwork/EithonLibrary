package net.eithon.library.exceptions;


public interface ISupplier<T> {
	public T doIt() throws TryAgainException, FatalException, PlayerException, EithonException;
}
