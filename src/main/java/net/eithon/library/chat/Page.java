package net.eithon.library.chat;

public class Page {

    private String[] _lines;
    private int _pageNumber;

    public Page(String[] lines, int pageNumber) {
        this._lines = lines;
        this._pageNumber = pageNumber;
    }

    public int getPageNumber() {
        return this._pageNumber;
    }

    public String[] getLines() {

        return this._lines;
    }
}