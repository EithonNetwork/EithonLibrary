package net.eithon.library.chat;
import java.util.HashMap;

public class FontPixels
{
	private static FontPixels singleton = null;
	private static HashMap<String, Integer> pixelWidths = new HashMap<String, Integer>();

	private FontPixels() {
		initializeHashMap();
	}

	public static FontPixels get()
	{
		if (singleton == null) {
			singleton = new FontPixels();
		}
		return singleton;
	}

	public int pixelWidth(char c)
	{
		Integer width = pixelWidths.get(Character.toString(c));
		if (width == null) {
			String message = String.format("No pixel width for character '%s'", c);
			if (System.console() != null) System.console().printf("%s", message);
			return 0;
		}
		return width;
	}

	public int pixelWidth(StringBuilder s) {
		return pixelWidth(s.toString());
	}

	public int pixelWidth(String s) {
		int length = 0;
		for(char c : s.toCharArray()){
			length += pixelWidth(c);
		}
		return length;
	}

	public String breakAfterPixels(StringBuilder s, int lengthInPixels, StringBuilder rest) {
		return breakAfterPixels(s.toString(), lengthInPixels, rest);
	}	

	public String breakAfterPixels(String s, int lengthInPixels, StringBuilder rest) {
		int length = 0;
		String first = "";
		rest.setLength(0);
		char[] charArray = s.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			char c = charArray[i];
			length += pixelWidth(c);
			if (length > lengthInPixels) {
				first = s.substring(0, i-1);
				rest.append(s.substring(i));
			}
		}
		return first;
	}

	private void initializeHashMap() 
	{	
		pixelWidths.put ("\n", new Integer(0));
		pixelWidths.put ("☺", new Integer(9));
		pixelWidths.put ("☻", new Integer(9));
		pixelWidths.put ("♥", new Integer(8));
		pixelWidths.put ("♦", new Integer(8));
		pixelWidths.put ("♣", new Integer(8));
		pixelWidths.put ("♠ ", new Integer(8));
		pixelWidths.put ("•", new Integer(5));
		pixelWidths.put ("◘", new Integer(9));
		pixelWidths.put ("○", new Integer(7));
		pixelWidths.put ("◙", new Integer(9));
		pixelWidths.put ("♂", new Integer(9));
		pixelWidths.put ("♀", new Integer(7));
		pixelWidths.put ("♪", new Integer(9));
		pixelWidths.put ("♫", new Integer(9));
		pixelWidths.put ("☼", new Integer(9));
		pixelWidths.put ("►", new Integer(8));
		pixelWidths.put ("◄", new Integer(8));
		pixelWidths.put ("↕", new Integer(7));
		pixelWidths.put ("‼", new Integer(7));
		pixelWidths.put ("¶", new Integer(9));
		pixelWidths.put ("§", new Integer(7));
		pixelWidths.put ("▬", new Integer(7));
		pixelWidths.put ("↨", new Integer(9));
		pixelWidths.put ("↑", new Integer(7));
		pixelWidths.put ("↓", new Integer(7));
		pixelWidths.put ("→", new Integer(8));
		pixelWidths.put ("←", new Integer(8));
		pixelWidths.put ("∟", new Integer(8));
		pixelWidths.put ("↔", new Integer(9));
		pixelWidths.put ("▲", new Integer(9));
		pixelWidths.put ("▼", new Integer(9));
		pixelWidths.put (" ", new Integer(5));
		pixelWidths.put ("!", new Integer(2));
		pixelWidths.put ("\"", new Integer(5));
		pixelWidths.put ("#", new Integer(6));
		pixelWidths.put ("$", new Integer(6));
		pixelWidths.put ("%", new Integer(6));
		pixelWidths.put ("&", new Integer(6));
		pixelWidths.put ("'", new Integer(3));
		pixelWidths.put ("(", new Integer(5));
		pixelWidths.put (")", new Integer(5));
		pixelWidths.put ("*", new Integer(5));
		pixelWidths.put ("+", new Integer(6));
		pixelWidths.put (",", new Integer(2));
		pixelWidths.put ("-", new Integer(6));
		pixelWidths.put (".", new Integer(2));
		pixelWidths.put ("/", new Integer(6));
		pixelWidths.put ("0", new Integer(6));
		pixelWidths.put ("1", new Integer(6));
		pixelWidths.put ("2", new Integer(6));
		pixelWidths.put ("3", new Integer(6));
		pixelWidths.put ("4", new Integer(6));
		pixelWidths.put ("5", new Integer(6));
		pixelWidths.put ("6", new Integer(6));
		pixelWidths.put ("7", new Integer(6));
		pixelWidths.put ("8", new Integer(6));
		pixelWidths.put ("9", new Integer(6));
		pixelWidths.put (":", new Integer(2));
		pixelWidths.put (";", new Integer(2));
		pixelWidths.put ("<", new Integer(5));
		pixelWidths.put ("=", new Integer(6));
		pixelWidths.put (">", new Integer(5));
		pixelWidths.put ("?", new Integer(6));
		pixelWidths.put ("@", new Integer(7));
		pixelWidths.put ("A", new Integer(6));
		pixelWidths.put ("B", new Integer(6));
		pixelWidths.put ("C", new Integer(6));
		pixelWidths.put ("D", new Integer(6));
		pixelWidths.put ("E", new Integer(6));
		pixelWidths.put ("F", new Integer(6));
		pixelWidths.put ("G", new Integer(6));
		pixelWidths.put ("H", new Integer(6));
		pixelWidths.put ("I", new Integer(4));
		pixelWidths.put ("J", new Integer(6));
		pixelWidths.put ("K", new Integer(6));
		pixelWidths.put ("L", new Integer(6));
		pixelWidths.put ("M", new Integer(6));
		pixelWidths.put ("N", new Integer(6));
		pixelWidths.put ("O", new Integer(6));
		pixelWidths.put ("P", new Integer(6));
		pixelWidths.put ("Q", new Integer(6));
		pixelWidths.put ("R", new Integer(6));
		pixelWidths.put ("S", new Integer(6));
		pixelWidths.put ("T", new Integer(6));
		pixelWidths.put ("U", new Integer(6));
		pixelWidths.put ("V", new Integer(6));
		pixelWidths.put ("W", new Integer(6));
		pixelWidths.put ("X", new Integer(6));
		pixelWidths.put ("Y", new Integer(6));
		pixelWidths.put ("Z", new Integer(6));
		pixelWidths.put ("[", new Integer(4));
		pixelWidths.put ("\\", new Integer(6));
		pixelWidths.put ("]", new Integer(4));
		pixelWidths.put ("^", new Integer(6));
		pixelWidths.put ("_", new Integer(6));
		pixelWidths.put ("`", new Integer(3));
		pixelWidths.put ("a", new Integer(6));
		pixelWidths.put ("b", new Integer(6));
		pixelWidths.put ("c", new Integer(6));
		pixelWidths.put ("d", new Integer(6));
		pixelWidths.put ("e", new Integer(6));
		pixelWidths.put ("f", new Integer(5));
		pixelWidths.put ("g", new Integer(6));
		pixelWidths.put ("h", new Integer(6));
		pixelWidths.put ("i", new Integer(2));
		pixelWidths.put ("j", new Integer(6));
		pixelWidths.put ("k", new Integer(5));
		pixelWidths.put ("l", new Integer(3));
		pixelWidths.put ("m", new Integer(6));
		pixelWidths.put ("n", new Integer(6));
		pixelWidths.put ("o", new Integer(6));
		pixelWidths.put ("p", new Integer(6));
		pixelWidths.put ("q", new Integer(6));
		pixelWidths.put ("r", new Integer(6));
		pixelWidths.put ("s", new Integer(6));
		pixelWidths.put ("t", new Integer(4));
		pixelWidths.put ("u", new Integer(6));
		pixelWidths.put ("v", new Integer(6));
		pixelWidths.put ("w", new Integer(6));
		pixelWidths.put ("x", new Integer(6));
		pixelWidths.put ("y", new Integer(6));
		pixelWidths.put ("z", new Integer(6));
		pixelWidths.put ("{", new Integer(5));
		pixelWidths.put ("|", new Integer(2));
		pixelWidths.put ("}", new Integer(5));
		pixelWidths.put ("~", new Integer(7));
		pixelWidths.put ("⌂", new Integer(6));
		pixelWidths.put ("Ç", new Integer(6));
		pixelWidths.put ("ü", new Integer(6));
		pixelWidths.put ("é", new Integer(6));
		pixelWidths.put ("â", new Integer(6));
		pixelWidths.put ("ä", new Integer(6));
		pixelWidths.put ("à", new Integer(6));
		pixelWidths.put ("å", new Integer(6));
		pixelWidths.put ("ç", new Integer(6));
		pixelWidths.put ("ê", new Integer(6));
		pixelWidths.put ("ë", new Integer(6));
		pixelWidths.put ("è", new Integer(6));
		pixelWidths.put ("ï", new Integer(4));
		pixelWidths.put ("î", new Integer(6));
		pixelWidths.put ("ì", new Integer(3));
		pixelWidths.put ("Ä", new Integer(6));
		pixelWidths.put ("Å", new Integer(6));
		pixelWidths.put ("É", new Integer(6));
		pixelWidths.put ("æ", new Integer(6));
		pixelWidths.put ("Æ", new Integer(6));
		pixelWidths.put ("ô", new Integer(6));
		pixelWidths.put ("ö", new Integer(6));
		pixelWidths.put ("ò", new Integer(6));
		pixelWidths.put ("û", new Integer(6));
		pixelWidths.put ("ù", new Integer(6));
		pixelWidths.put ("ÿ", new Integer(6));
		pixelWidths.put ("Ö", new Integer(6));
		pixelWidths.put ("Ü", new Integer(6));
		pixelWidths.put ("¢", new Integer(6));
		pixelWidths.put ("£", new Integer(6));
		pixelWidths.put ("¥", new Integer(6));
		pixelWidths.put ("₧", new Integer(4));
		pixelWidths.put ("ƒ", new Integer(6));
		pixelWidths.put ("á", new Integer(6));
		pixelWidths.put ("í", new Integer(3));
		pixelWidths.put ("ó", new Integer(6));
		pixelWidths.put ("ú", new Integer(6));
		pixelWidths.put ("ñ", new Integer(6));
		pixelWidths.put ("Ñ", new Integer(6));
		pixelWidths.put ("ª", new Integer(6));
		pixelWidths.put ("º", new Integer(6));
		pixelWidths.put ("¿", new Integer(6));
		pixelWidths.put ("¬", new Integer(7));
		pixelWidths.put ("¬", new Integer(6));
		pixelWidths.put ("½", new Integer(6));
		pixelWidths.put ("¼", new Integer(6));
		pixelWidths.put ("¡", new Integer(2));
		pixelWidths.put ("«", new Integer(6));
		pixelWidths.put ("»", new Integer(6));
		pixelWidths.put ("░", new Integer(9));
		pixelWidths.put ("▒", new Integer(9));
		pixelWidths.put ("▓", new Integer(9));
		pixelWidths.put ("│", new Integer(3));
		pixelWidths.put ("┤", new Integer(6));
		pixelWidths.put ("╡", new Integer(6));
		pixelWidths.put ("╢", new Integer(8));
		pixelWidths.put ("╖", new Integer(8));
		pixelWidths.put ("╕", new Integer(6));
		pixelWidths.put ("╣", new Integer(8));
		pixelWidths.put ("║", new Integer(6));
		pixelWidths.put ("╗", new Integer(8));
		pixelWidths.put ("╝", new Integer(8));
		pixelWidths.put ("╜", new Integer(9));
		pixelWidths.put ("╛", new Integer(9));
		pixelWidths.put ("┐", new Integer(9));
		
		pixelWidths.put ("└", new Integer(6));
		pixelWidths.put ("┴", new Integer(9));
		pixelWidths.put ("┬", new Integer(9));
		pixelWidths.put ("├", new Integer(6));
		pixelWidths.put ("─", new Integer(9));
		pixelWidths.put ("┼", new Integer(9));
		pixelWidths.put ("╞", new Integer(5));

		pixelWidths.put ("╟", new Integer(7));
		pixelWidths.put ("╚", new Integer(7));
		pixelWidths.put ("╔", new Integer(7));
		pixelWidths.put ("╩", new Integer(9));
		pixelWidths.put ("╦", new Integer(9));
		pixelWidths.put ("╠", new Integer(7));
		pixelWidths.put ("═", new Integer(9));
		pixelWidths.put ("╬", new Integer(9));
		pixelWidths.put ("╧", new Integer(9));
		
		pixelWidths.put ("╨", new Integer(9));
		pixelWidths.put ("╤", new Integer(9));
		pixelWidths.put ("╥", new Integer(9));
		pixelWidths.put ("╙", new Integer(7));
		pixelWidths.put ("╘", new Integer(6));
		pixelWidths.put ("╒", new Integer(6));
		pixelWidths.put ("╓", new Integer(7));
		pixelWidths.put ("╫", new Integer(9));
		pixelWidths.put ("╪", new Integer(9));
		pixelWidths.put ("┘", new Integer(7));
		pixelWidths.put ("┌", new Integer(6));
		pixelWidths.put ("█", new Integer(9));
		pixelWidths.put ("▄", new Integer(9));
		pixelWidths.put ("▌", new Integer(5));
		pixelWidths.put ("▐", new Integer(5));
		pixelWidths.put ("▀", new Integer(9));
		
		pixelWidths.put ("α", new Integer(8));
		pixelWidths.put ("ß", new Integer(7));
		pixelWidths.put ("Γ", new Integer(7));
		pixelWidths.put ("π", new Integer(8));
		pixelWidths.put ("Σ", new Integer(7));
		pixelWidths.put ("σ", new Integer(8));
		pixelWidths.put ("µ", new Integer(7));
		pixelWidths.put ("τ", new Integer(8));
		pixelWidths.put ("Φ", new Integer(7));
		pixelWidths.put ("Θ", new Integer(8));
		pixelWidths.put ("Ω", new Integer(8));
		pixelWidths.put ("δ", new Integer(7));
		pixelWidths.put ("∞", new Integer(9));
		pixelWidths.put ("φ", new Integer(9));
		pixelWidths.put ("ε", new Integer(6));
		pixelWidths.put ("∩", new Integer(7));
		
		pixelWidths.put ("≡", new Integer(7));
		pixelWidths.put ("±", new Integer(7));
		pixelWidths.put ("≥", new Integer(7));
		pixelWidths.put ("≤", new Integer(7));
		pixelWidths.put ("⌠", new Integer(6));
		pixelWidths.put ("⌡", new Integer(6));
		pixelWidths.put ("÷", new Integer(7));
		pixelWidths.put ("≈", new Integer(8));
		pixelWidths.put ("°", new Integer(6));
		pixelWidths.put ("∙", new Integer(3));
		pixelWidths.put ("·", new Integer(3));
		pixelWidths.put ("√", new Integer(9));
		pixelWidths.put ("ⁿ", new Integer(6));
		pixelWidths.put ("²", new Integer(5));
		pixelWidths.put ("■", new Integer(5));
		pixelWidths.put (" ", new Integer(4));

	}
}