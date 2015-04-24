import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Utility{
	public static String expand(int i, int l){
		String s = Integer.toString(i);
		while(s.length() < l){
			s = "0"+s;
		}
		return s;		
	}
	public static String crop(float f, int l){
		String fl = Float.toString(f);
		String output = new String();
		int i = 0;
		while(i < l){
			output += fl.charAt(i);
			i++;
		}
		return output;
	}
	public static int randomBetween(int max, int min){
		return (int) Math.floor((Main.generator.nextDouble() * (max - min + 1)) + min);
	}
	public static boolean percentChance(int percent){
		int random = (int) Math.round(Math.random()*100);
		if(random > percent)
			return false;
		else
			return true;
	}
	public static int findMaxValue(int[] numbers) {

	    int highest = numbers[0];
	    for (int index = 1; index < numbers.length; index ++) {
	        if (numbers[index] > highest) {
	            highest = numbers [index];
	        }
	    }
	   return highest;
	}
	
	public static void drawString(Graphics g, String text, int x, int y) {
	    for (String line : text.split("\n"))
	        g.drawString(line, x, y += g.getFontMetrics().getHeight());
	}
	public static void drawString(Graphics g, String text, int x, int y, int w) {
		List<String> paragraphs = new ArrayList<String>();
		for (String line : text.split("\n")){
	        paragraphs.add(line);
		}
		List<String> newParagraphs = new ArrayList<String>();
		for(String str : paragraphs){
			List<String> wrap = Utility.StringUtils.wrap(str, g.getFontMetrics(g.getFont()), w);
			newParagraphs.addAll(wrap);
		}
		for (String line : newParagraphs)
	        g.drawString(line, x, y += g.getFontMetrics().getHeight());
	}

	/**
	 * Globally available utility classes, mostly for string manipulation.
	 * 
	 * @author Jim Menard, <a href="mailto:jimm@io.com">jimm@io.com</a>
	 */
	public static class StringUtils {
	  /**
	   * Returns an array of strings, one for each line in the string after it has
	   * been wrapped to fit lines of <var>maxWidth</var>. Lines end with any of
	   * cr, lf, or cr lf. A line ending at the end of the string will not output a
	   * further, empty string.
	   * <p>
	   * This code assumes <var>str</var> is not <code>null</code>.
	   * 
	   * @param str
	   *          the string to split
	   * @param fm
	   *          needed for string width calculations
	   * @param maxWidth
	   *          the max line width, in points
	   * @return a non-empty list of strings
	   */
	  public static List<String> wrap(String str, FontMetrics fm, int maxWidth) {
	    List<String> lines = splitIntoLines(str);
	    if (lines.size() == 0)
	      return lines;

	    ArrayList<String> strings = new ArrayList<String>();
	    for (Iterator<String> iter = lines.iterator(); iter.hasNext();)
	      wrapLineInto((String) iter.next(), strings, fm, maxWidth);
	    return strings;
	  }

	  /**
	   * Given a line of text and font metrics information, wrap the line and add
	   * the new line(s) to <var>list</var>.
	   * 
	   * @param line
	   *          a line of text
	   * @param list
	   *          an output list of strings
	   * @param fm
	   *          font metrics
	   * @param maxWidth
	   *          maximum width of the line(s)
	   */
	  public static void wrapLineInto(String line, List<String> list, FontMetrics fm, int maxWidth) {
	    int len = line.length();
	    int width;
	    while (len > 0 && (width = fm.stringWidth(line)) > maxWidth) {
	      // Guess where to split the line. Look for the next space before
	      // or after the guess.
	      int guess = len * maxWidth / width;
	      String before = line.substring(0, guess).trim();

	      width = fm.stringWidth(before);
	      int pos;
	      if (width > maxWidth) // Too long
	        pos = findBreakBefore(line, guess);
	      else { // Too short or possibly just right
	        pos = findBreakAfter(line, guess);
	        if (pos != -1) { // Make sure this doesn't make us too long
	          before = line.substring(0, pos).trim();
	          if (fm.stringWidth(before) > maxWidth)
	            pos = findBreakBefore(line, guess);
	        }
	      }
	      if (pos == -1)
	        pos = guess; // Split in the middle of the word

	      list.add(line.substring(0, pos).trim());
	      line = line.substring(pos).trim();
	      len = line.length();
	    }
	    if (len > 0)
	      list.add(line);
	  }

	  /**
	   * Returns the index of the first whitespace character or '-' in <var>line</var>
	   * that is at or before <var>start</var>. Returns -1 if no such character is
	   * found.
	   * 
	   * @param line
	   *          a string
	   * @param start
	   *          where to star looking
	   */
	  public static int findBreakBefore(String line, int start) {
	    for (int i = start; i >= 0; --i) {
	      char c = line.charAt(i);
	      if (Character.isWhitespace(c) || c == '-')
	        return i;
	    }
	    return -1;
	  }

	  /**
	   * Returns the index of the first whitespace character or '-' in <var>line</var>
	   * that is at or after <var>start</var>. Returns -1 if no such character is
	   * found.
	   * 
	   * @param line
	   *          a string
	   * @param start
	   *          where to star looking
	   */
	  public static int findBreakAfter(String line, int start) {
	    int len = line.length();
	    for (int i = start; i < len; ++i) {
	      char c = line.charAt(i);
	      if (Character.isWhitespace(c) || c == '-')
	        return i;
	    }
	    return -1;
	  }
	  /**
	   * Returns an array of strings, one for each line in the string. Lines end
	   * with any of cr, lf, or cr lf. A line ending at the end of the string will
	   * not output a further, empty string.
	   * <p>
	   * This code assumes <var>str</var> is not <code>null</code>.
	   * 
	   * @param str
	   *          the string to split
	   * @return a non-empty list of strings
	   */
	  public static List<String> splitIntoLines(String str) {
	    ArrayList<String> strings = new ArrayList<String>();

	    int len = str.length();
	    if (len == 0) {
	      strings.add("");
	      return strings;
	    }

	    int lineStart = 0;

	    for (int i = 0; i < len; ++i) {
	      char c = str.charAt(i);
	      if (c == '\r') {
	        int newlineLength = 1;
	        if ((i + 1) < len && str.charAt(i + 1) == '\n')
	          newlineLength = 2;
	        strings.add(str.substring(lineStart, i));
	        lineStart = i + newlineLength;
	        if (newlineLength == 2) // skip \n next time through loop
	          ++i;
	      } else if (c == '\n') {
	        strings.add(str.substring(lineStart, i));
	        lineStart = i + 1;
	      }
	    }
	    if (lineStart < len)
	      strings.add(str.substring(lineStart));

	    return strings;
	  }

	}
}