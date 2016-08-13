package net.eithon.library.exceptions;

public class ProgrammersErrorException extends FatalException {
	public ProgrammersErrorException(Exception e) {
		super(e);
	}

	public ProgrammersErrorException(String message, Exception e) {
		super(message, e);
	}

	public ProgrammersErrorException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;
}
