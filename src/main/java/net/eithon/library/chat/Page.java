package net.eithon.library.chat;

public class Page {

    private String[] _lines;
    private int _pageNumber;
    private int _totalPages;

    public Page(String[] lines, int pageNumber, int totalPages) {
        this._lines = lines;
        this._pageNumber = pageNumber;
        this._totalPages = totalPages;
    }

    public int getPageNumber() {
        return this._pageNumber;
    }

    public int getTotalPages() {
        return this._totalPages;
    }

    public String[] getLines() {

        return this._lines;
    }
}