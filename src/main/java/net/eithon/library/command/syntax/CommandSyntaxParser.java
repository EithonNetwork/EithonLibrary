package net.eithon.library.command.syntax;

import java.util.ArrayList;
import java.util.List;
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

	public static CommandSyntax parseCommand(String command) {
		CommandSyntaxParser parser = new CommandSyntaxParser(command);
		return parser.getCommandSyntax();
	}

	private CommandSyntax getCommandSyntax() { return this._rootCommand; }

	private StringTokenizer _stringTokenizer;
	private CommandSyntax _rootCommand;
	private CommandSyntax _currentCommand;
	private String _lastItem;
	boolean _lastItemWasParameter;
	private String _leftHandName;
	private String _defaultValue;
	private List<String> _parameterValues;
	private ParameterType _type;

	private CommandSyntaxParser(String command) {
		this._stringTokenizer = new StringTokenizer(command, " <:>{,}=()", true);
		parse();
	}

	private void parse() {
		this._lastItem = null;
		this._lastItemWasParameter = false;
		this._leftHandName = null;
		this._defaultValue = null;
		this._parameterValues = null;
		this._type = ParameterType.STRING;
		boolean partComplete = false;
		while (this._stringTokenizer.hasMoreElements()) {		
			String token = this._stringTokenizer.nextToken();
			if (":>}()".contains(token)) {
				throw new IllegalArgumentException(String.format("Unexpectedly found a '%s'", token));
			} else if (token.equals(" ")) {
				partComplete = true;
			} else if (token.equals("<")) {
				this._lastItem = parseParameter();
				this._lastItemWasParameter = true;
			} else if (token.equals("{")) {
				if (this._lastItemWasParameter) {
					throw new IllegalArgumentException("Unexpectedly found a '{' directly after a parameter.");
				}
				parseCommandList();
			} else if (token.equals("=")) {
				if (this._lastItemWasParameter) throw new IllegalArgumentException("Found a '=' proceeded by a parameter.");
				this._lastItemWasParameter = true;
				this._leftHandName = this._lastItem;
				this._lastItem = null;
			} else  {
				this._lastItem = token;
				this._lastItemWasParameter = false;
			}
			if (partComplete) {
				addCommandOrParameter();
				partComplete = false;
			}
		}
		addCommandOrParameter();
	}

	private void parseCommandList() {
		throw new NotImplementedException("List of sub commands is not yet implemented.");
	}

	public void addCommandOrParameter() {
		if (this._lastItem == null) {
			if (this._leftHandName != null) {
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
		ParameterSyntax parameter = null;
		if (this._leftHandName != null) {
			parameter = this._currentCommand.addNamedParameter(this._type, this._lastItem, this._leftHandName);
			this._leftHandName = null;
		} else {
			parameter = this._currentCommand.addParameter(this._type, this._lastItem);
		}
		if (this._defaultValue != null) {
			parameter.setDefault(this._defaultValue);
			this._defaultValue = null;
		}
		if (this._parameterValues != null) {
			parameter.setValues(this._parameterValues);
			this._parameterValues = null;
		}
		this._type = ParameterType.STRING;
	}

	private String parseParameter() {
		String parameter = null;
		StringBuilder sb = new StringBuilder("");
		boolean typeFound = false;
		boolean endFound = false;
		while (this._stringTokenizer.hasMoreElements()) {		
			String token = this._stringTokenizer.nextToken();
			if ("<[]=})".contains(token)) {
				throw new IllegalArgumentException(String.format("You must not have a '%s' within a '<'.", token));
			} else if (token.equals(">")) {
				if (typeFound) {
					String type = sb.toString();
					try {
					this._type = ParameterType.valueOf(type);
					} catch (Exception e) {
						throw new IllegalArgumentException(String.format("Parameter %s has unknown type; %s", parameter, type));
					}
				}
				else parameter = sb.toString();
				endFound = true;
				break;
			} else if (token.equals("(")) {
				this._defaultValue = parseDefaultValue();
			} else if (token.equals(":")) {
				parameter = sb.toString();
				sb = new StringBuilder("");
				typeFound = true;
			} else if (token.equals("{")) {
				this._parameterValues = parseParameterValues();
			} else {
				sb.append(token);
			}
		}
		if (!endFound) throw new IllegalArgumentException("Missing final '>'");
		return parameter;
	}

	private List<String> parseParameterValues() {
		ArrayList<String> values = new ArrayList<String>();
		StringBuilder sb = new StringBuilder("");
		boolean endFound = false;
		while (this._stringTokenizer.hasMoreElements()) {		
			String token = this._stringTokenizer.nextToken();
			if ("<[]={)".contains(token)) {
				throw new IllegalArgumentException(String.format("You must not have a '%s' within a '{'.", token));
			} else if (token.equals("}")) {
				endFound = true;
				break;
			} else if (token.equals(",")) {
				values.add(sb.toString());
				sb = new StringBuilder("");
			} else {
				sb.append(token);
			}
		}
		if (!endFound) throw new IllegalArgumentException("Missing final '>'");
		values.add(sb.toString());
		return values;
	}

	private String parseDefaultValue() {
		StringBuilder sb = new StringBuilder("");
		boolean endFound = false;
		while (this._stringTokenizer.hasMoreElements()) {		
			String token = this._stringTokenizer.nextToken();
			if ("<([]={}".contains(token)) {
				throw new IllegalArgumentException(String.format("You must not have a '%s' within a '('.", token));
			} else if (token.equals(")")) {
				endFound = true;
				break;
			} else {
				sb.append(token);
			}
		}
		if (!endFound) throw new IllegalArgumentException("Missing final '>'");
		return sb.toString();
	}
}
