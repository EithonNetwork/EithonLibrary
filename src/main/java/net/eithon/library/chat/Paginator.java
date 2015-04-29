package net.eithon.library.chat;

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
		List<Page> pages = new LinkedList<Page>();
		List<String> pageLines = new LinkedList<String>();
		String pageBreak = Character.toString(LineWrapper.PAGE_BREAK);
		int pageNumber = 1;
		int lineCount = 0;
		for (String inputLine : inputLines) {
			LineWrapper lineWrapper = new LineWrapper(inputLine);
			String[] outputLines = lineWrapper.getOutputLines();
			for (String outputLine : outputLines) {
				boolean newPage = false;
				if (outputLine.equalsIgnoreCase(pageBreak)) newPage = true;
				else {
					pageLines.add(outputLine);
					lineCount++;
					if (lineCount >= pageHeight) newPage = true;
				}
				if (newPage) {
					Page page = new Page(pageLines.toArray(new String[0]), pageNumber);
					pages.add(page);
					pageNumber++;
					lineCount = 0;
				}
			}
		}
		return pages.toArray(new Page[0]);
	}
}
