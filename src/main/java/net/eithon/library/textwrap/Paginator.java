package net.eithon.library.textwrap;

import java.util.LinkedList;
import java.util.List;

import net.eithon.library.plugin.Logger;

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

	/**
	 * Breaks a raw string up into pages using the default width and height.
	 *
	 * @param unpaginatedString The raw string to break.
	 * @param pageNumber The page number to fetch.
	 * @return A single chat page.
	 */
	public static ChatPage paginate(String unpaginatedString, int pageNumber, String ignoreCharacters, String ignoreAlsoFollowingCharacter, int pageWidth) {
		return  paginate(unpaginatedString, pageNumber, ignoreCharacters, ignoreAlsoFollowingCharacter, 
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
	public static ChatPage paginate(String unpaginatedString, int pageNumber, String ignoreCharacters, String ignoreAlsoFollowingCharacter, int lineLength, int pageHeight) {
		String[] lines = wordWrap(unpaginatedString, lineLength, ignoreCharacters, ignoreAlsoFollowingCharacter, pageHeight);

		int totalPages = lines.length / pageHeight + (lines.length % pageHeight == 0 ? 0 : 1);
		int actualPageNumber = pageNumber <= totalPages ? pageNumber : totalPages;

		int from = (actualPageNumber - 1) * pageHeight;
		int to = from + pageHeight <= lines.length  ? from + pageHeight : lines.length;
		String[] selectedLines = new String[to-from];
		for (int i = from; i < to; i++) {
			selectedLines[i-from] = lines[i];
		}

		return new ChatPage(selectedLines, actualPageNumber, totalPages);
	}

	/**
	 * Breaks a raw string up into a series of lines. Words are wrapped using
	 * spaces as separators and the newline character is respected.
	 *
	 * @param rawString The raw string to break.
	 * @param lineLength The length of a line of text.
	 * @param pageHeight 
	 * @return An array of word-wrapped lines.
	 */
	public static String[] wordWrap(
			String rawString, 
			int lineLength, 
			String ignoreCharacters,
			String ignoreAlsoFollowingCharacter,
			int pageHeight) {
		// A null string is a single line
		if (rawString == null) {
			return new String[] {""};
		}

		char[] rawChars = (rawString + ' ').toCharArray(); // add a trailing space to trigger pagination
		StringBuilder word = new StringBuilder();
		StringBuilder line = new StringBuilder();
		List<String> lines = new LinkedList<String>();
		int pageLineCount = 0;
		int lineInPixels = 0;
		int wordInPixels = 0;
		int lineVisibleCharacters = 0;
		int wordVisibleCharacters = 0;
		for (int i = 0; i < rawChars.length; i++) {
			char c = rawChars[i];
			boolean lastBreakWasAutoBreak = false;

			if (ignoreAlsoFollowingCharacter.contains(Character.toString(c))) {
				word.append(c);
				i++;
				c = rawChars[i];
				word.append(c);
				continue;
			}
			if (ignoreCharacters.contains(Character.toString(c))) {
				word.append(c);
				continue;
			}

			int characterInPixels = FontPixels.get().pixelWidth(c);

			// Time for a line break?
			boolean timeToBreak = false;
			if (c == '\n') {
				// A "hard" break
				timeToBreak = true;
				lastBreakWasAutoBreak = false;
			} else if (lineInPixels + wordInPixels + characterInPixels > lineLength) {
				timeToBreak = true;
				lastBreakWasAutoBreak = true;
			}

			if (timeToBreak) {
				if (!lastBreakWasAutoBreak || (c == ' ')) {
					line.append(word);
					lineInPixels += wordInPixels;
					lineVisibleCharacters += wordVisibleCharacters;
					word = new StringBuilder();
					wordInPixels = 0;
					wordVisibleCharacters = 0;
				}
				String lineToAdd = line.toString();
				line = new StringBuilder();
				
				if (lineToAdd.endsWith("[break]") && (lineVisibleCharacters == 7))  {
					while (pageLineCount < pageHeight) {
						lines.add("");
						pageLineCount++;
					}
					pageLineCount = 0;
				} else {
					if ((pageLineCount > 0) || (lineVisibleCharacters > 0)) {
						lines.add(lineToAdd);
						pageLineCount++;
						if (pageLineCount >= pageHeight) pageLineCount = 0;
					}
				}
				lineInPixels = 0;
				lineVisibleCharacters = 0;
			}

			switch (c) {
			case '\n':
				// Already taken care of
				break;
			case ' ':
				if (wordVisibleCharacters > 0) {
					// This space is after an earlier word
					word.append(c);
					wordInPixels += characterInPixels;
					line.append(word);
					lineInPixels += wordInPixels;
					lineVisibleCharacters += wordVisibleCharacters;
					word = new StringBuilder();
					wordInPixels = 0;
					wordVisibleCharacters = 0;
				} else if (!lastBreakWasAutoBreak) {
					// This is an indenting space
					line.append(c);
					lineInPixels += characterInPixels;
				} else {
					// This is a space in the beginning of the line, after an auto break
					// Ignore the space
				}
				break;
			default:
				word.append(c);
				wordVisibleCharacters++;
				wordInPixels += characterInPixels;
				break;
			}
		}

		if (wordVisibleCharacters > 0) {
			line.append(word);
			lineVisibleCharacters += wordVisibleCharacters;
		}
		if (lineVisibleCharacters > 0) lines.add(line.toString());

        // Iterate over the wrapped lines, applying the last color from one line to the beginning of the next
        if (lines.get(0).length() == 0 || lines.get(0).charAt(0) != ChatColor.COLOR_CHAR) {
            lines.set(0, ChatColor.GRAY + lines.get(0));
        }
        for (int i = 1; i < lines.size(); i++) {
            final String pLine = lines.get(i-1);
            final String subLine = lines.get(i);

            char color = pLine.charAt(pLine.lastIndexOf(ChatColor.COLOR_CHAR) + 1);
            if (subLine.length() == 0 || subLine.charAt(0) != ChatColor.COLOR_CHAR) {
                lines.set(i, ChatColor.getByChar(color) + subLine);
            }
        }
		
		return lines.toArray(new String[lines.size()]);
	}
}
