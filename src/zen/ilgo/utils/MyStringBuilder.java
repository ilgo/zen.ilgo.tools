package zen.ilgo.utils;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A StringBuilder that has better replace methods
 * 
 * @author roger holenweger (ilgo711@gmail.com)
 * @since Oct 19, 2009
 */
public class MyStringBuilder {

    private StringBuilder sb;

    public MyStringBuilder(String txt) {
        sb = new StringBuilder(txt);
    }

    public MatchResult find(String regex) {
        return find(regex, 0);
    }
    
    public MatchResult find(String regex, int start) {
        Matcher m = getMatcher(regex, Pattern.DOTALL);
        if (m.find(start)) {
            return m.toMatchResult();
        } else {
            return null;
        }
    }

    public MyStringBuilder replaceFirst(String oldString, String newString) {
        int pos = sb.indexOf(oldString);
        sb.replace(pos, pos + oldString.length(), newString);
        return this;
    }

    public MyStringBuilder replaceLast(String oldString, String newString) {
        int pos = sb.lastIndexOf(oldString);
        sb.replace(pos, pos + oldString.length(), newString);
        return this;
    }

    public MyStringBuilder replaceAll(String oldString, String newString) {
        int pos = sb.indexOf(oldString);
        while (pos != -1) {
            int end = pos + oldString.length();
            sb.replace(pos, pos + oldString.length(), newString);
            pos = sb.indexOf(oldString, end);
        }
        return this;
    }

    public MyStringBuilder replaceFirstRegex(String regex, String newString) {
        return replaceFirstRegex(regex, newString, 0);
    }

    public MyStringBuilder replaceLastRegex(String regex, String newString) {
        return replaceLastRegex(regex, newString, 0);
    }

    public MyStringBuilder replaceAllRegex(String regex, String newString) {
        return replaceAllRegex(regex, newString, 0);
    }

    public MyStringBuilder replaceFirstRegex(String regex, String newString, int flags) {
        Matcher m = getMatcher(regex, flags);
        sb = new StringBuilder(m.replaceFirst(newString));
        return this;
    }

    public MyStringBuilder replaceLastRegex(String regex, String newString, int flags) {
        Matcher m = getMatcher(regex, flags);
        int start = 0;
        int end = 0;
        while (m.find(end)) {
            start = m.start();
            end = m.end();
        }
        if (start != 0) {
            sb = new StringBuilder(sb.replace(start, end, newString));
        }
        return this;
    }

    public MyStringBuilder replaceAllRegex(String regex, String newString, int flags) {
        Matcher m = getMatcher(regex, flags);
        sb = new StringBuilder(m.replaceAll(newString));
        return this;
    }

    public MyStringBuilder append(char c) {
        sb.append(c);
        return this;
    }

    public MyStringBuilder append(String str) {
        sb.append(str);
        return this;
    }

    public int capacity() {
        return sb.capacity();
    }

    public char charAt(int index) {
        return sb.charAt(index);
    }

    public int codePointAt(int index) {
        return sb.codePointAt(index);
    }

    public int codePointBefore(int index) {
        return sb.codePointBefore(index);
    }

    public int codePointCount(int beginIndex, int endIndex) {
        return sb.codePointCount(beginIndex, endIndex);
    }

    public MyStringBuilder delete(int start, int end) {
        sb.delete(start, end);
        return this;
    }

    public MyStringBuilder deleteCharAt(int index) {
        sb.deleteCharAt(index);
        return this;
    }

    public int indexOf(String str) {
        return sb.indexOf(str);
    }

    public int indexOf(String str, int fromIndex) {
        return sb.indexOf(str, fromIndex);
    }

    public MyStringBuilder insert(int offset, char c) {
        sb.insert(offset, c);
        return this;
    }

    public MyStringBuilder insert(int offset, String str) {
        sb.insert(offset, str);
        return this;
    }

    public int lastIndexOf(String str) {
        return sb.lastIndexOf(str);
    }

    public int lastIndexOf(String str, int fromIndex) {
        return sb.lastIndexOf(str, fromIndex);
    }

    public int length() {
        return sb.length();
    }

    public MyStringBuilder replace(int start, int end, String str) {
        sb.replace(start, end, str);
        return this;
    }

    public void setCharAt(int index, char ch) {
        sb.setCharAt(index, ch);
    }

    public String substring(int start) {
        return sb.substring(start);
    }

    public String substring(int start, int end) {
        return sb.substring(start, end);
    }

    public String toString() {
        return sb.toString();
    }

    private Matcher getMatcher(String regex, int flags) {
        Pattern pat = Pattern.compile(regex, flags);
        return pat.matcher(sb);
    }
}
