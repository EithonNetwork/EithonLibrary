package net.eithon.library.exceptions;

public class FatalException extends EithonException {
	public FatalException(Exception e) {
		super(e);
	}

	public FatalException(String message, Exception e) {
		super(message, e);
	}

	public FatalException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;
}
