package net.eithon.library.chat;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;

public class LineWrapper {
	public static final char LINE_BREAK = '\n';
	public static final char HORIZONTAL_TAB = '\t';
	public static final char PAGE_BREAK = (char) 12;
	public static final char SOFT_HYPHEN = (char) 31;
	public static final char HARD_HYPHEN = '-';
	public static final char SPACE = ' ';
	static final int hardHyphenPixels = FontPixels.get().pixelWidth(HARD_HYPHEN);

	private int _chatLineWidthInPixels;
	private Line _line = new Line(this._chatLineWidthInPixels);
	private Word _nextWord = new Word();

	private List<String> _outputLines = new LinkedList<String>();

	public LineWrapper(String inputLine, int chatLineWidthInPixels) {
		this._chatLineWidthInPixels = chatLineWidthInPixels;
		String[] inputLines = makeHardBreaksEasier(inputLine);
		for (String line : inputLines) {
			if ((line.length() < 1) || (line.charAt(0) == PAGE_BREAK)) {
				// Empty lines and page breaks are not considered for wrapping
				this._outputLines.add(line);
				continue;
			}
			wrap(line);
		}
	}
	
	public static String[] wrapLine(String inputLine, int chatLineWidthInPixels)
	{
		return new LineWrapper(inputLine, chatLineWidthInPixels).getOutputLines();
	}

	public String[] getOutputLines() { return this._outputLines.toArray(new String[0]); }

	private void wrap(String inputLine)
	{
		this._line = new Line(this._chatLineWidthInPixels);
		this._nextWord = new Word();	
		final char[] rawChars = inputLine.toCharArray();
		//final char[] rawChars = (inputLine + ' ').toCharArray(); // add a trailing space to trigger wrapping
		for (int i = 0; i < rawChars.length; i++) {
			if (rawChars[i] == ChatColor.COLOR_CHAR) {
				this._nextWord.add(rawChars[i++]);
				this._nextWord.add(rawChars[i]);
				continue;
			}
			final char c = rawChars[i];
			if (c == HORIZONTAL_TAB) {
				String fillPrefix = "";
				i++;
				while (rawChars[i] == ChatColor.COLOR_CHAR) {
					fillPrefix += Character.toString(rawChars[i++]) + Character.toString(rawChars[i++]);
				}
				String fillPostfix = "";
				while (rawChars[i] == ChatColor.COLOR_CHAR) {
					fillPostfix += Character.toString(rawChars[i++]) + Character.toString(rawChars[i]);
				}
				this._line.setShouldBeCentered(fillPrefix, rawChars[i], fillPostfix);
				continue;
			}

			int characterWidthInPixels = characterWidthInPixels(c);
			if (wrapNeeded(characterWidthInPixels)) wrapLine(c);
			if (isWordDelimiter(c))  handleWordDelimiter(c, characterWidthInPixels);
			else this._nextWord.add(c, characterWidthInPixels, true);
		}

		if (this._nextWord.hasContent()) this._line.addAndResetWord(this._nextWord);
		if (this._line.hasContent()) this._outputLines.add(this._line.toString());
	}

	private void wrapLine(char c) {
		if (c == SPACE) {
			// There is enough room for our next word
			this._line.addAndResetWord(this._nextWord);
		} else if (this._line.hasPendingSoftHyphen()) {
			// Before we break the line, add a hyphen
			this._line.add(HARD_HYPHEN, hardHyphenPixels, true);
		}
		this._outputLines.add(this._line.toString());
		this._line.reset();
	}

	private void handleWordDelimiter(char c, int characterWidthInPixels) {
		switch (c) {
		case HARD_HYPHEN:
			this._nextWord.add(c, characterWidthInPixels, false);
			this._line.addAndResetWord(this._nextWord);
			break;
		case SOFT_HYPHEN:
			this._line.addAndResetWord(this._nextWord);
			this._line.setHasPendingSoftHyphen(true);
			break;
		case SPACE:
			// After a wrap, we will ignore leading spaces.
			if (!this._line.hasBeenWrapped() || this._nextWord.hasContent()) {
				// This space is the start of a new line or after an earlier word, so make it count.
				this._nextWord.add(c, characterWidthInPixels, false);
				this._line.addAndResetWord(this._nextWord);
			}
			break;
		default:
			break;
		}
	}

	private boolean isWordDelimiter(char c) {
		return (c == HARD_HYPHEN) ||  (c == SPACE) ||  (c == SOFT_HYPHEN);
	}

	private int characterWidthInPixels(char c) {
		int characterInPixels = FontPixels.get().pixelWidth(c);
		if (c == SOFT_HYPHEN) characterInPixels = hardHyphenPixels;
		return characterInPixels;
	}

	private boolean wrapNeeded(int characterWidthInPixels) {
		final int totalWidthInPixels = this._line.getWidthInPixels() + this._nextWord.getWidthInPixels() + characterWidthInPixels;
		return totalWidthInPixels > this._chatLineWidthInPixels;
	}

	private static String[] makeHardBreaksEasier(String inputLine) {
		List<String> lines = new LinkedList<String>();
		final String pageBreak = Character.toString(PAGE_BREAK);
		while (true) {
			int lineBreakPosition = inputLine.indexOf(LINE_BREAK);
			int pageBreakPosition = inputLine.indexOf(PAGE_BREAK);
			if (pageBreakPosition == lineBreakPosition) break;
			if (pageBreakPosition < 0) pageBreakPosition = Integer.MAX_VALUE;
			if (lineBreakPosition < 0) lineBreakPosition = Integer.MAX_VALUE;
			int breakPosition = Math.min(pageBreakPosition, lineBreakPosition);
			final String firstPart = inputLine.substring(0, breakPosition);
			lines.add(firstPart);
			if (pageBreakPosition < lineBreakPosition) {
				lines.add(pageBreak);
			}
			inputLine = inputLine.substring(breakPosition+1);
		}
		lines.add(inputLine);
		return lines.toArray(new String[0]);
	}

}
