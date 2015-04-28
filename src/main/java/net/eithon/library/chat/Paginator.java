package net.eithon.library.chat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.bukkit.ChatColor;

/**
 * The ChatPaginator takes a raw string of arbitrary length and breaks it down
 * into an array of strings appropriate for displaying on the Minecraft player
 * console.
 */
public class Paginator {
	public static final int UNBOUNDED_PAGE_WIDTH = Integer.MAX_VALUE;
	public static final int OPEN_CHAT_PAGE_HEIGHT = 20; // The height of an expanded chat window
	public static final int CLOSED_CHAT_PAGE_HEIGHT = 10; // The height of the default chat window
	public static final int UNBOUNDED_PAGE_HEIGHT = Integer.MAX_VALUE;

	public static final char LINE_BREAK = '\n';
	public static final char PAGE_BREAK = (char) 12;
	public static final char SOFT_HYPHEN = (char) 31;
	public static final char HARD_HYPHEN = '-';
	public static final char SPACE = ' ';
	/**
	 * Breaks a raw string up into pages using the default width and height.
	 *
	 * @param unpaginatedString The raw string to break.
	 * @param pageNumber The page number to fetch.
	 * @return A single chat page.
	 */
	public static Page[] paginate(String[] inputLines, int pageWidth) {
		return  paginate(inputLines, 
				pageWidth, CLOSED_CHAT_PAGE_HEIGHT);
	}

	/**
	 * Breaks a raw string up into pages using a provided width and height.
	 *
	 * @param unpaginatedString The raw string to break.
	 * @param pageNumber The page number to fetch.
	 * @param lineLength The desired width of a chat line.
	 * @param pageHeight The desired number of lines in a page.
	 * @return A single chat page.
	 */
	public static Page[] paginate(String[] inputLines, int lineLength, int pageHeight) {
		final int hardHyphenPixels = FontPixels.get().pixelWidth(HARD_HYPHEN);
		List<Page> pages = new ArrayList<Page>();
		List<String> outputLines = new LinkedList<String>();
		for (String inputLine : inputLines) {
			char[] rawChars = (inputLine + ' ').toCharArray(); // add a trailing space to trigger pagination
			StringBuilder word = new StringBuilder();
			StringBuilder outputLine = new StringBuilder();
			int outputLlineInPixels = 0;
			int wordInPixels = 0;
			int outputLineVisibleCharacters = 0;
			int wordVisibleCharacters = 0;
			boolean lastWordHadSoftHyphenAfterIt = false;
			String activeChatColorCharacters = "";
			for (int i = 0; i < rawChars.length; i++) {
				char c = rawChars[i];
				boolean lastBreakWasAutoBreak = false;

				if (c == ChatColor.COLOR_CHAR) {
					word.append(c);
					i++;
					c = rawChars[i];
					word.append(c);
					continue;
				}

				int characterInPixels = FontPixels.get().pixelWidth(c);
				if (c == SOFT_HYPHEN) {
					characterInPixels = hardHyphenPixels;
				}
				int newLineLength = outputLlineInPixels + wordInPixels + characterInPixels;

				// Time for a line break?
				boolean timeToBreak = false;
				if (isHardBreak(c)) {
					// A "hard" break, such as LINE_BREAK or PAGE_BREAK
					timeToBreak = true;
					lastBreakWasAutoBreak = false;				
				} else if (newLineLength > lineLength) {
					timeToBreak = true;
					lastBreakWasAutoBreak = true;
				}

				if (timeToBreak) {
					if (!lastBreakWasAutoBreak || (c == SPACE)) {
						if (word.length() > 0) {
							activeChatColorCharacters = updateChatColors(activeChatColorCharacters, word.toString());
							outputLine.append(word);
							outputLlineInPixels += wordInPixels;
							outputLineVisibleCharacters += wordVisibleCharacters;
							word = new StringBuilder();
							wordInPixels = 0;
							wordVisibleCharacters = 0;
							lastWordHadSoftHyphenAfterIt = false;
						}
					} else if (lastWordHadSoftHyphenAfterIt) {
						outputLine.append(HARD_HYPHEN);
						outputLlineInPixels += hardHyphenPixels;
						outputLineVisibleCharacters += 1;
						lastWordHadSoftHyphenAfterIt = false;
					}
					outputLines.add(outputLine.toString());
					if ((outputLines.size() >= pageHeight) || (c == PAGE_BREAK)) {
						pages.add(new Page(outputLines.toArray(new String[outputLines.size()]), 1, 1));
						outputLines = new LinkedList<String>();
					}
					outputLine = new StringBuilder();
					outputLlineInPixels = 0;
					outputLineVisibleCharacters = 0;
					if (lastBreakWasAutoBreak) {
						// Add all active color codes
						for (char chatColor : activeChatColorCharacters.toCharArray()) {
							outputLine.append(ChatColor.COLOR_CHAR);
							outputLine.append(chatColor);
						}
					} else {
						activeChatColorCharacters = "";
					}
				}

				switch (c) {
				case LINE_BREAK:
				case PAGE_BREAK:
					// Already taken care of
					break;
				case HARD_HYPHEN:
				case SPACE:
					if (wordVisibleCharacters > 0) {
						// This word brake is after an earlier word
						word.append(c);
						wordInPixels += characterInPixels;
						activeChatColorCharacters = updateChatColors(activeChatColorCharacters, word.toString());
						outputLine.append(word);
						outputLlineInPixels += wordInPixels;
						outputLineVisibleCharacters += wordVisibleCharacters;
						word = new StringBuilder();
						wordInPixels = 0;
						wordVisibleCharacters = 0;
						lastWordHadSoftHyphenAfterIt = false;
					} else if (!lastBreakWasAutoBreak) {
						// This is an indenting space/hyphen
						outputLine.append(c);
						outputLlineInPixels += characterInPixels;
					} else {
						// This is a space in the beginning of the line, after an auto break
						// Ignore the space
					}
					break;
				case SOFT_HYPHEN:
					if (wordVisibleCharacters > 0) {
						activeChatColorCharacters = updateChatColors(activeChatColorCharacters, word.toString());
						outputLine.append(word);
						outputLlineInPixels += wordInPixels;
						outputLineVisibleCharacters += wordVisibleCharacters;
						word = new StringBuilder();
						wordInPixels = 0;
						wordVisibleCharacters = 0;
						lastWordHadSoftHyphenAfterIt = true;
					}
					break;
				default:
					word.append(c);
					wordVisibleCharacters++;
					wordInPixels += characterInPixels;
					break;
				}
			}
			if (word.length() > 0) {
				activeChatColorCharacters = updateChatColors(activeChatColorCharacters, word.toString());
				outputLine.append(word);
				outputLlineInPixels += wordInPixels;
				outputLineVisibleCharacters += wordVisibleCharacters;
				word = new StringBuilder();
				wordInPixels = 0;
				wordVisibleCharacters = 0;
				lastWordHadSoftHyphenAfterIt = false;
			}
			outputLines.add(outputLine.toString());
			if (outputLines.size() >= pageHeight) {
				pages.add(new Page(outputLines.toArray(new String[outputLines.size()]), 1, 1));
				outputLines = new LinkedList<String>();
			}
			outputLine = new StringBuilder();
			outputLlineInPixels = 0;
			outputLineVisibleCharacters = 0;
			if (outputLineVisibleCharacters > 0) outputLines.add(outputLine.toString());
		}

		if (outputLines.size() > 0) {
			pages.add(new Page(outputLines.toArray(new String[outputLines.size()]), 1, 1));
		}

		return pages.toArray(new Page[pages.size()]);
	}

	private static String updateChatColors(String activeChatColorCharacters,
			String word) {
		String chatColors = "";

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
		for (char c : activeChatColorCharacters.toCharArray()) {
			colorStack.push(c);
		}
		
		while (!colorStack.isEmpty()) {
			Character c = colorStack.pop();
			if (isOverridden(c, chatColors)) continue;
			chatColors = Character.toString(c) + chatColors;
		}
		return chatColors;
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

	private static boolean isWordBreak(char c) {
		return (c == SPACE) || (c == HARD_HYPHEN) || (c == SOFT_HYPHEN);
	}

	private static boolean isHardBreak(char c) {
		return (c == LINE_BREAK) || (c == PAGE_BREAK);
	}
}
