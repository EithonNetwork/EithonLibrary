package net.eithon.library.chat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
	public static Page[] paginate(String[] inputLines, int lineWidthInPixels) {
		return  paginate(inputLines, 
				lineWidthInPixels, CLOSED_CHAT_PAGE_HEIGHT);
	}

	/**
	 * Breaks a raw string up into pages using a provided width and height.
	 *
	 * @param unpaginatedString The raw string to break.
	 * @param pageNumber The page number to fetch.
	 * @param lineWidthInPixels The desired width of a chat line.
	 * @param pageHeightInLines The desired number of lines in a page.
	 * @return A single chat page.
	 */
	public static Page[] paginate(String[] inputLines, int lineWidthInPixels, int pageHeightInLines) {
		List<Page> pages = new LinkedList<Page>();
		ArrayList<String> pageLines = new ArrayList<String>();
		String pageBreak = Character.toString(LineWrapper.PAGE_BREAK);
		int pageNumber = 1;
		for (String inputLine : inputLines) {
			LineWrapper lineWrapper = new LineWrapper(inputLine, lineWidthInPixels);
			String[] outputLines = lineWrapper.getOutputLines();
			for (String outputLine : outputLines) {
				if ((pageLines.size() == 0) && (outputLine.length() == 0)) continue;
				boolean newPage = false;
				if (outputLine.equalsIgnoreCase(pageBreak)) newPage = true;
				else {
					pageLines.add(outputLine);
					if (pageLines.size() >= pageHeightInLines) newPage = true;
				}
				if (newPage) {
					trimEmptyLines(pageLines);
					Page page = new Page(pageLines.toArray(new String[0]), pageNumber);
					pages.add(page);
					pageNumber++;
					pageLines = new ArrayList<String>();
				}
			}
		}
		if (pageLines.size() > 0) {
			trimEmptyLines(pageLines);
			Page page = new Page(pageLines.toArray(new String[0]), pageNumber);
			pages.add(page);
			pageNumber++;
		}
		return pages.toArray(new Page[0]);
	}

	private static void trimEmptyLines(ArrayList<String> pageLines) {
		for (int i = pageLines.size()-1; i >= 0; i--) {
			if (!pageLines.get(i).isEmpty()) return;
			pageLines.remove(i);
		}
	}
}
