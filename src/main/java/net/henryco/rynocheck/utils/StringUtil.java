package net.henryco.rynocheck.utils;

public class StringUtil {

	public static String precise(String str, int precision) {

		double d = new Double(str); // WHAT THE HOLY FUCK I'VE DONE HERE
		if (d % 1.0 != 0) return String.format("%s", new Double(formatPrecision(precision, d)));
		return formatPrecision(0, d);
	}

	public static String precise(String str) {
		return precise(str, 8);
	}

	public static String formatPrecision(int precision, Object... args) {
		String form = "%." + precision + "f";
		return String.format(form, args);
	}
}