package net.eithon.library.chat;

class Word {
	private StringBuilder _word;
	private int _wordInPixels;
	private int _wordVisibleCharacters;

	Word()
	{
		reset();
	}
	
	void reset()
	{
		this._word = new StringBuilder();
		this._wordInPixels = 0;
		this._wordVisibleCharacters = 0;
	}
	
	@Override
	public
	String toString() { return this._word.toString(); }

	int getWidthInPixels() { return this._wordInPixels; }

	int getVisibleCharacters() { return this._wordVisibleCharacters; }

	boolean hasContent() { return this._wordVisibleCharacters > 0; }

	void add(char c, int characterInPixels, boolean isVisibleCharacter) {
		this._word.append(c);
		this._wordInPixels += characterInPixels;
		if (isVisibleCharacter) this._wordVisibleCharacters++;
	}

	void add(char c) { add(c, 0, false); }
}
