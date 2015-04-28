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
		inputLines = makeHardBreaksEasier(inputLines);
		List<Page> pages = new ArrayList<Page>();
		List<String> outputLines = new LinkedList<String>();
		for (String inputLine : inputLines) {
		}

		if (outputLines.size() > 0) {
			pages.add(new Page(outputLines.toArray(new String[outputLines.size()]), 1, 1));
		}

		return pages.toArray(new Page[0]);
	}
	
	if ((outputLines.size() >= pageHeight) || (c == PAGE_BREAK)) {
		pages.add(new Page(outputLines.toArray(new String[outputLines.size()]), 1, 1));
		outputLines = new LinkedList<String>();
	}
	if (outputLines.size() >= pageHeight) {
		pages.add(new Page(outputLines.toArray(new String[outputLines.size()]), 1, 1));
		outputLines = new LinkedList<String>();
	}


}
