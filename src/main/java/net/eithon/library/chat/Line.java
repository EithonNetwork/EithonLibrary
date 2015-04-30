package net.eithon.library.chat;

import java.util.Stack;

import org.bukkit.ChatColor;

class Line {
	private StringBuilder _content;
	private int _widthInPixels;
	private int _numberOfVisibleCharacters;
	private boolean _hasPendingSoftHyphen;
	private String _activeChatColorCharacters;
	private boolean _isFirstLine;

	Line()  {
		this._activeChatColorCharacters = "";
		reset();
		this._isFirstLine = true;
	}

	boolean hasPendingSoftHyphen() { return this._hasPendingSoftHyphen; }

	void setHasPendingSoftHyphen(boolean value) { this._hasPendingSoftHyphen = value; }

	boolean hasContent() { return this._numberOfVisibleCharacters > 0; }

	int getWidthInPixels() { return this._widthInPixels; }	

	public boolean hasBeenWrapped() { return !this._isFirstLine; }
	
	@Override
	public String toString() { return this._content.toString(); }

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
		updateChatColors(word.toString());
		this._content.append(word.toString());
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
		for (char c : this._activeChatColorCharacters.toCharArray()) {
			colorStack.push(c);
		}

		while (!colorStack.isEmpty()) {
			Character c = colorStack.pop();
			if (isOverridden(c, newActiveChatColorCharacters)) continue;
			newActiveChatColorCharacters = Character.toString(c) + newActiveChatColorCharacters;
		}
		this._activeChatColorCharacters = newActiveChatColorCharacters;
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
