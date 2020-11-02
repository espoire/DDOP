package util;

public class RomanNumeral {
	private static final boolean SMALL_ROMAN_NUMERALS_ONLY = true;
	private static final String ROMAN_NUMERAL_REGEX = "^(?=[MDCLXVI])M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$";
	private static final String LARGE_ROMAN_NUMERAL_REGEX = ".*[MDCL].*";
	
	public static boolean isRomanNumeral(String s) {
		if(s == null) return false;
		s = s.toUpperCase();
		
		if(SMALL_ROMAN_NUMERALS_ONLY && s.matches(LARGE_ROMAN_NUMERAL_REGEX)) return false;
		return s.matches(ROMAN_NUMERAL_REGEX);
	}
	
	/** Converts Roman numerals to int. No error checking.
	 * 
	 * @param s - A String to convert.
	 * @return int - The valuse represented by the String.
	 */
	public static int parseRomanNumeral(String s) {
		s = s.toUpperCase();
	    return (int) evaluateNextRomanNumeral(s, s.length() - 1, 0);
	}
	
	/** Deep Voodoo, copied from https://stackoverflow.com/posts/29218799/revisions */
	private static double evaluateNextRomanNumeral(String roman, int pos, double rightNumeral) {
	    if (pos < 0) return 0;
	    char ch = roman.charAt(pos);
	    double value = Math.floor(Math.pow(10, "IXCM".indexOf(ch))) + 5 * Math.floor(Math.pow(10, "VLD".indexOf(ch)));
	    return value * Math.signum(value + 0.5 - rightNumeral) + evaluateNextRomanNumeral(roman, pos - 1, value);
	}
}
