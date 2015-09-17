package net.eithon.library.chat;

import java.util.Stack;

import org.bukkit.ChatColor;

class Line {
	private StringBuilder _content;
	private int _maxWidthInPixels;
	private int _widthInPixels;
	private int _numberOfVisibleCharacters;
	private boolean _hasPendingSoftHyphen;
	private boolean _shouldBeCentered;
	private String _activeChatColorCharacters;
	private boolean _isFirstLine;
	private String _fillPrefix;
	private Character _fillCharacter;
	private String _fillPostfix;
	private String _fillLeft;
	private String _fillRight;

	Line(int maxWidthInPixels)  {
		this._maxWidthInPixels = maxWidthInPixels;
		this._activeChatColorCharacters = "";
		reset();
		this._isFirstLine = true;
	}

	boolean shouldBeCentered() { return this._shouldBeCentered; }

	void setShouldBeCentered(String fillPrefix, Character fillCharacter, String fillPostfix) { 
		this._shouldBeCentered = true;
		this._fillPrefix = fillPrefix;
		this._fillCharacter = fillCharacter;
		this._fillPostfix = fillPostfix;
		this._fillLeft = "";
		this._fillRight = "";
	}

	boolean hasPendingSoftHyphen() { return this._hasPendingSoftHyphen; }

	void setHasPendingSoftHyphen(boolean value) { this._hasPendingSoftHyphen = value; }

	boolean hasContent() { return this._numberOfVisibleCharacters > 0; }

	int getWidthInPixels() { return this._widthInPixels; }	

	public boolean hasBeenWrapped() { return !this._isFirstLine; }
	
	@Override
	public String toString() {
		if (!this.hasContent()) return "";
		calculateFill();
		String result = "";
		if (this._fillLeft != null) result += this._fillLeft;
		result += this._content.toString();
		if (this._fillRight != null) result += this._fillRight;
		return result;
	}
	
	private void calculateFill() {
		if (!shouldBeCentered()) return;
		StringBuilder fillLeft = new StringBuilder();
		StringBuilder fillRight = new StringBuilder();
		long pixelsNeededLeft = (this._maxWidthInPixels-this._widthInPixels)/2;
		long pixelsNeededRight = this._maxWidthInPixels-this._widthInPixels-pixelsNeededLeft;
		long remainingPixels = fillWithSpace(fillLeft, pixelsNeededLeft);
		pixelsNeededRight += remainingPixels;
		fillWithSpace(fillRight, pixelsNeededRight);
		this._fillLeft = fillLeft.toString();
		this._fillRight = fillRight.toString();
	}

	long fillWithSpace(StringBuilder stringToFill, long pixelsNeeded) {
		stringToFill.append(this._fillPrefix);
		int spaceWidth = FontPixels.get().pixelWidth(this._fillCharacter);
		long requiredSpaces = pixelsNeeded/spaceWidth;
		for (long i = 0; i < requiredSpaces; i++) stringToFill.append(this._fillCharacter);
		stringToFill.append(this._fillPostfix);
		long remainingPixels = pixelsNeeded % spaceWidth;
		return remainingPixels;
	}

	void reset() {
		this._content = new StringBuilder();
		this._widthInPixels = 0;
		this._numberOfVisibleCharacters = 0;
		this._hasPendingSoftHyphen = false;
		// Add all active color codes
		for (char chatColor : this._activeChatColorCharacters.toCharArray()) {
			this._content.append(ChatColor.COLOR_CHAR);
			this._content.append(chatColor);
		}
		this._isFirstLine = false;
	}

	void add(Word word) {
		String wordAsString = word.toString();
		updateChatColors(wordAsString);
		this._content.append(wordAsString);
		this._widthInPixels += word.getWidthInPixels();
		this._numberOfVisibleCharacters += word.getVisibleCharacters();
		this._hasPendingSoftHyphen = false;
	}
	
	void addAndResetWord(Word word) {
		add(word);
		word.reset();
	}

	void add(char c, int characterInPixels, boolean isVisibleCharacter) {
		this._content.append(c);
		this._widthInPixels += characterInPixels;
		if (isVisibleCharacter) this._numberOfVisibleCharacters ++;
		this._hasPendingSoftHyphen = false;
	}

	private void updateChatColors(String word) {
		String newActiveChatColorCharacters = "";

		// Get a stack over new color characters in the word
		Stack<Character> colorStack = new Stack<Character>();
		for (char c : this._activeChatColorCharacters.toCharArray()) {
			colorStack.push(c);
		}
		boolean nextCharIsColorChar = false;
		for (char c : word.toCharArray()) {
			if (c == ChatColor.COLOR_CHAR) {
				nextCharIsColorChar = true;
				continue;
			}
			if (nextCharIsColorChar) {
				colorStack.push(c);
				nextCharIsColorChar = false;
			}
		}

		while (!colorStack.isEmpty()) {
			Character c = colorStack.pop();
			if (isOverridden(c, newActiveChatColorCharacters)) continue;
			newActiveChatColorCharacters = Character.toString(c) + newActiveChatColorCharacters;
		}
		if (containsOnlyTheControlCharacter(newActiveChatColorCharacters)) newActiveChatColorCharacters = "";
		this._activeChatColorCharacters = newActiveChatColorCharacters;
	}

	boolean containsOnlyTheControlCharacter(String newActiveChatColorCharacters) {
		if (newActiveChatColorCharacters.length() != 1) return false;
		return newActiveChatColorCharacters.charAt(0) == ChatColor.COLOR_CHAR;
	}

	private static boolean isOverridden(Character c, String chatColors) {
		ChatColor newCharacter = ChatColor.getByChar(c);
		for (char colorCharacter : chatColors.toCharArray()) {
			if (c == colorCharacter) return true;
			if (isOverridden(newCharacter, ChatColor.getByChar(colorCharacter))) return true;
		}
		return false;
	}

	private static boolean isOverridden(ChatColor newCharacter, ChatColor existingCharacter) {
		if (existingCharacter == ChatColor.RESET) return true;
		if (newCharacter.isFormat()) return false;
		if (existingCharacter.isColor()) return true;
		return false;
	}
}
