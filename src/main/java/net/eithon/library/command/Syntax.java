package net.eithon.library.command;

class Syntax {
	private String _name;
	private boolean _useHints;
	
	public Syntax(String name) {
		this._name = name;
	}
	
	public String getName() { return this._name; }
	public boolean useHints() { return this._useHints; }
	public Syntax useHints(boolean useHints) { this._useHints = useHints; return this; }
}
