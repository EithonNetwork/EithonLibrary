package net.eithon.library.command.syntax;

import java.util.StringTokenizer;

import net.eithon.library.command.syntax.ParameterSyntax.ParameterType;

import org.apache.commons.lang.NotImplementedException;

// Examples
// eithonfixes restart <after>
// eithonfixes restart (after) <time span>
// cp (from) <file> (to) <file>
// cp <from file> <to file>
// eithonfixes rc set [<variable>=<value> ...]
// eithonfixes rc add <name> <command>

public class CommandSyntaxParser {

	public static CommandSyntax parse(String command) {
		CommandSyntaxParser parser = new CommandSyntaxParser(command);
		return parser.getCommandSyntax();
	}

	private CommandSyntax getCommandSyntax() { return this._rootCommand; }

	private StringTokenizer _stringTokenizer;
	private CommandSyntax _rootCommand;
	private CommandSyntax _currentCommand;
	private boolean _optional;
	private String _lastItem;
	boolean _lastItemWasParameter;
	private String _parameterNamePart;

	private CommandSyntaxParser(String command) {
		this._stringTokenizer = new StringTokenizer(command, " <>[]=", true);
		this._optional = false;
		this._lastItem = null;
		this._lastItemWasParameter = false;
		this._parameterNamePart = null;
		boolean partComplete = false;
		while (this._stringTokenizer.hasMoreElements()) {		
			String token = this._stringTokenizer.nextToken();
			if (token.equals(" ")) {
				partComplete = true;
			} else if (token.equals("<")) {
				this._lastItem = parseParameter();
				this._lastItemWasParameter = true;
			} else if (token.equals("[")) {
				if (this._optional) throw new NotImplementedException("We can't yet handle nested '['.");
				this._optional = true;
			} else if (token.equals("]")) {
				if (!this._optional) throw new IllegalArgumentException("Found a '] without a matching '[' before.");
				this._optional = false;
				partComplete = true;
			} else if (token.equals("=")) {
				if (!this._lastItemWasParameter) throw new IllegalArgumentException("Found a '=' not proceeded by a parameter.");
				this._parameterNamePart = this._lastItem;
				this._lastItem = null;
			} else {
				this._lastItem = token;
				this._lastItemWasParameter = false;
				partComplete = true;
			}
			if (partComplete) {
				addCommandOrParameter();
			}
		}
		addCommandOrParameter();
	}

	public void addCommandOrParameter() {
		if (this._lastItem == null) {
			if (this._parameterNamePart != null) {
				throw new IllegalArgumentException("Missing value inside '<' and '>' after '='");
			}
			return;
		}
		if (this._lastItemWasParameter) {
			addParameter();
			this._lastItemWasParameter = false;
		}
		else addCommand(this._lastItem);
		this._lastItem = null;
	}

	private void addCommand(String lastItem) {
		if ((this._currentCommand != null) && (this._currentCommand.hasParameters())) {
			throw new NotImplementedException("Sub commands after parameters is not yet supported.");
		}
		if (this._rootCommand == null) {
			this._rootCommand = new CommandSyntax(lastItem);
			this._currentCommand = this._rootCommand;
		} else {
			this._currentCommand = this._currentCommand.addCommand(lastItem);
		}
	}

	private void addParameter() {
		if (this._parameterNamePart != null) {
			this._currentCommand.addNamedParameter(this._lastItem);
			this._parameterNamePart = null;
		} else {
			this._currentCommand.addParameter(this._lastItem);
		}
	}

	private String parseParameter() {
		StringBuilder sb = new StringBuilder("");
		boolean endFound = false;
		while (this._stringTokenizer.hasMoreElements()) {		
			String token = this._stringTokenizer.nextToken();
			if ("<[]=".contains(token)) {
				throw new IllegalArgumentException(String.format("You must not have a '%s' within a '<'.", token));
			} else if (token.equals(">")) {
				endFound = true;
				break;
			} 
			sb.append(token);
		}
		if (!endFound) throw new IllegalArgumentException("Missing final '>'");
		return sb.toString();
	}
}
