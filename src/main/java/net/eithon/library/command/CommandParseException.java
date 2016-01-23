package net.eithon.library.command;

public class CommandParseException extends Exception {
	private static final long serialVersionUID = 1L;
	private String _syntaxDocumentation;
	public CommandParseException(String syntaxDocumentation, String message) { 
		super(message);
		this._syntaxDocumentation = syntaxDocumentation;
	}
	
	public String getSyntaxDocumentation() { return this._syntaxDocumentation; }
}
