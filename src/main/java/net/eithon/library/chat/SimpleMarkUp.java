package net.eithon.library.chat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

import org.bukkit.ChatColor;

public class SimpleMarkUp {
	private Stack<String> _colorStack = null;
	private boolean _isBold = false;
	private boolean _isStrikeThrough = false;
	private boolean _isUnderline = false;
	private boolean _isItalic = false;
	private boolean _isMagic = false;
	private File _file;
	private String[] _parsedLines;

	public SimpleMarkUp(File file) {
		this._file = file;
		reloadRules();
	}

	public void reloadRules() {
		parseFile();
	}
	
	public String[] getParsedLines() { return this._parsedLines; }

	private void parseFile() {
		boolean firstLine = true;
		List<String> parsedLines = new ArrayList<String>();
		try {
			FileInputStream fis = new FileInputStream(this._file);

			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			String line = null;
			this._colorStack = new Stack<String>();
			String code = convertToColorCode("grey");
			this._colorStack.push(code);
			while ((line = br.readLine()) != null) {
				String parsedLine = parseLine(line, firstLine);
				if (firstLine) firstLine = false;
				parsedLines.add(parsedLine);
			}
			br.close();
		} catch (IOException e) {
			if (firstLine) firstLine = false;
			String parsedLine =String.format("Failed to read the content from \"%s\".", this._file.toString());
			parsedLines.add(parsedLine);
		}
		this._parsedLines = parsedLines.toArray(new String[parsedLines.size()]);
	}

	private String parseLine(String line, boolean firstLine) {
		String parsedLine = "";
		StringTokenizer st = new StringTokenizer(line, "[]", true);
		boolean isInsideBrackets = false;
		boolean firstToken = true;
		boolean hasContent = false;
		while (st.hasMoreElements()) {
			boolean specialCharacter = false;
			String token = st.nextToken();
			if (token.equalsIgnoreCase("[")) {
				isInsideBrackets = true;
				continue;
			} else if (token.equalsIgnoreCase("]")) {
				if (isInsideBrackets) {
					isInsideBrackets = false;
					parsedLine += activeCodes();
					continue;
				}
			}

			if (isInsideBrackets) {
				if (token.startsWith("color="))
				{
					String color = token.substring(6);
					String code = convertToColorCode(color);
					this._colorStack.push(code);
				} else if (token.equalsIgnoreCase("/color")) {
					if (this._colorStack.size() > 1) {
						this._colorStack.pop();
					}
				} else if (token.equalsIgnoreCase("break")) {
					token = Character.toString((char) 12) ; // FF (Form feed), Page break
					specialCharacter = true;
				} else if (token.equalsIgnoreCase("-")) {
					token = Character.toString((char) 31) ; // US (Unit separator), Optional hyphen
					specialCharacter = true;
				} else if (token.equalsIgnoreCase("b")) {
					this._isBold = true;
				} else if (token.equalsIgnoreCase("s")) {
					this._isStrikeThrough = true;
				} else if (token.equalsIgnoreCase("u")) {
					this._isUnderline = true;
				} else if (token.equalsIgnoreCase("i")) {
					this._isItalic = true;
				} else if (token.equalsIgnoreCase("m")) {
					this._isMagic = true;
				} else if (token.equalsIgnoreCase("/b")) {
					this._isBold = false;
				} else if (token.equalsIgnoreCase("/s")) {
					this._isStrikeThrough = false;
				} else if (token.equalsIgnoreCase("/u")) {
					this._isUnderline = false;
				} else if (token.equalsIgnoreCase("/i")) {
					this._isItalic = false;
				} else if (token.equalsIgnoreCase("/m")) {
					this._isMagic = false;
				} else {
					isInsideBrackets = false;
					token = "[" + token;
				}
			}

			if (firstToken) parsedLine += activeCodes();
			if (!isInsideBrackets || specialCharacter) {
				hasContent = true;
				parsedLine += token;
			}
			firstToken = false;
		}

		if (!hasContent) return "";
		return parsedLine;
	}

	private String activeCodes() {
		String activeCodes = this._colorStack.peek();
		if (this._isBold) activeCodes += ChatColor.BOLD;
		if (this._isStrikeThrough) activeCodes += ChatColor.STRIKETHROUGH;
		if (this._isUnderline) activeCodes += ChatColor.UNDERLINE;
		if (this._isItalic) activeCodes += ChatColor.ITALIC;
		if (this._isMagic) activeCodes += ChatColor.MAGIC;
		return activeCodes;
	}

	private static String convertToColorCode(String color) {
		String result = "";
		if (color.equalsIgnoreCase("black"))
		{
			result = ChatColor.BLACK + "";
		}
		else if (color.equalsIgnoreCase("yellow"))
		{
			result = ChatColor.YELLOW + "";
		}
		else if (color.equalsIgnoreCase("blue"))
		{
			result = ChatColor.BLUE + "";
		}
		else if (color.equalsIgnoreCase("green"))
		{
			result = ChatColor.GREEN + "";
		}
		else if (color.equalsIgnoreCase("red"))
		{
			result = ChatColor.RED + "";
		}
		else if (color.equalsIgnoreCase("aqua"))
		{
			result = ChatColor.AQUA + "";
		}
		else if (color.equalsIgnoreCase("darkaqua"))
		{
			result = ChatColor.DARK_AQUA + "";
		}
		else if (color.equalsIgnoreCase("darkblue"))
		{
			result = ChatColor.DARK_BLUE + "";
		}
		else if (color.equalsIgnoreCase("darkgrey"))
		{
			result = ChatColor.DARK_GRAY + "";
		}
		else if (color.equalsIgnoreCase("darkpurple"))
		{
			result = ChatColor.DARK_PURPLE + "";
		}
		else if (color.equalsIgnoreCase("darkgreen"))
		{
			result = ChatColor.DARK_GREEN + "";
		}
		else if (color.equalsIgnoreCase("darkred"))
		{
			result = ChatColor.DARK_RED + "";
		}
		else if (color.equalsIgnoreCase("gold"))
		{
			result = ChatColor.GOLD + "";
		}
		else if (color.equalsIgnoreCase("gray") || color.equalsIgnoreCase("grey"))
		{
			result = ChatColor.GRAY + "";
		}
		else if (color.equalsIgnoreCase("lightpurple"))
		{
			result = ChatColor.LIGHT_PURPLE + "";
		}	
		else if (color.equalsIgnoreCase("white"))
		{
			result = ChatColor.WHITE + "";
		}
		else {
			result = "[Unkown color: " + color + "]";
		}
		return result;
	}
}
