package net.eithon.library.command;

class Syntax {
	private String _name;
	private boolean _displayHint;
	
	public Syntax(String name) {
		this._name = name;
	}
	
	public String getName() { return this._name; }
	public boolean getDisplayHint() { return this._displayHint; }
	public Syntax setDisplayHint(boolean displayHint) { this._displayHint = displayHint; return this; }
	public Syntax inherit(Syntax parent) {
		this.setDisplayHint(parent.getDisplayHint());
		return this;
	}
}
