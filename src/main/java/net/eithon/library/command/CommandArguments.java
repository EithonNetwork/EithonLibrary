package net.eithon.library.command;

import org.bukkit.command.CommandSender;

public class CommandArguments {
	private String[] _args;
	private int _nextArgument;
	private CommandSender _sender;

	public CommandArguments(CommandSender sender, String[] args) {
		this._args = args;
		this._nextArgument = 0;
		this._sender = sender;
	}

	public CommandSender getSender() { return this._sender; }
	public void setNextArgument(int nextArgument) { this._nextArgument = nextArgument; }
	public boolean hasReachedEnd() { return this._nextArgument >= this._args.length; }
	private String getNextArgument() {
		int position = this._nextArgument++;
		String argument = this._args.length > position ? this._args[position] : null;
		return argument;
	}
	
	public String getString() { return getNextArgument(); }

	public String getStringAsLowercase() {
		String argument = getNextArgument();
		if (argument == null) return argument;
		return argument.toLowerCase();
	}
	
	@Override
	public CommandArguments clone() {
		CommandArguments clone = new CommandArguments(this._sender, this._args);
		clone._nextArgument = this._nextArgument;
		return clone;
	}

	public void goOneArgumentBack() {
		this._nextArgument--;
		if (this._nextArgument < 0) {
			throw new IllegalArgumentException("You can't go back from argument 0");
		}
	}
}
