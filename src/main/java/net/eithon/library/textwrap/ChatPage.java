package net.eithon.library.textwrap;

public class ChatPage {

    private String[] lines;
    private int pageNumber;
    private int totalPages;

    public ChatPage(String[] lines, int pageNumber, int totalPages) {
        this.lines = lines;
        this.pageNumber = pageNumber;
        this.totalPages = totalPages;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public int getTotalPages() {
        return this.totalPages;
    }

    public String[] getLines() {

        return this.lines;
    }
}