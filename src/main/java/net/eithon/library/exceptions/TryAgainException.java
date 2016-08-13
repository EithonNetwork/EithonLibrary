package net.eithon.library.exceptions;

public class TryAgainException extends EithonException{
	public TryAgainException(Exception e) {
		super(e);
	}

	public TryAgainException(String message, Exception e) {
		super(message, e);
	}

	public TryAgainException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;
}
