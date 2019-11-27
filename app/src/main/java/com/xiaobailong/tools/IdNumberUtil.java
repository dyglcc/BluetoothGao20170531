package com.xiaobailong.tools;

import android.util.Log;

import java.io.IOException;

public class IdNumberUtil {

	public static boolean getInfof(String s) {

		String M = new String(s);
		if (!M.substring(6, 17).matches("[0-9]*")) {
			return false;
		}
		int y = Integer.parseInt(M.substring(6, 8));
		int m = Integer.parseInt(M.substring(8, 10));
		int d = Integer.parseInt(M.substring(10, 12));

		if (m < 1 || m > 12 || d < 1 || d > 31
				|| ((m == 4 || m == 6 || m == 9 || m == 11) && d > 30)
				|| (m == 2 && (((y + 1900) % 4 > 0 && d > 28) || d > 29))) {

			return false;
		} else {

			System.out.println("该居民出生地：" + M.substring(0, 6));
			System.out.println("该居民出生时间为：" + y + "年" + m + "月" + d + "日");
		}

		int sex = Integer.parseInt(M.substring(14, 15));

		if (sex % 2 == 0) {
			System.out.println("该居民为：女性");
		} else {
			System.out.println("该居民为：男性");
		}
		return true;
	}

	public static boolean getInfoe(String s) {

		String M = new String(s);

		if (!M.substring(6, 17).matches("[0-9]*")) {
			return false;
		}
		int y = Integer.parseInt(M.substring(6, 10));
		int m = Integer.parseInt(M.substring(10, 12));
		int d = Integer.parseInt(M.substring(12, 14));

		int[] xx = { 2, 4, 8, 5, 10, 9, 7, 3, 6, 1, 2, 4, 8, 5, 10, 9, 7 };
		char[] yy = { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };

		int mm = 0;

		int[] gg = new int[18];

		if (y < 1900 || m < 1 || m > 12 || d < 1 || d > 31
				|| ((m == 4 || m == 6 || m == 9 || m == 11) && d > 30)
				|| (m == 2 && ((y % 4 > 0 && d > 28) || d > 29))) {
		} else {
			System.out.println("该居民出生地：" + M.substring(0, 6));
			System.out.println("该居民出生时间为：" + y + "年" + m + "月" + d + "日");
		}
		int sex = Integer.parseInt(M.substring(16, 17));
		if (sex % 2 == 0) {
			System.out.println("该居民为：女性");
		} else {
			System.out.println("该居民为：男性");
		}

		for (int i = 1; i < 18; i++) {
			int j = 17 - i;
			gg[i - 1] = Integer.parseInt(M.substring(j, j + 1));
		}

		/************************ 校验身份证的真伪 ****************************/

		for (int i = 0; i < 17; i++) {
			mm += xx[i] * gg[i];
		}

		mm = mm % 11;
		char c = M.charAt(17);
		if (c == 'x') {
			c = 'X';
		}
		if (c == yy[mm]) {
			Log.e("c", c + "");
			Log.e("YY", yy[mm] + "");
			return true;
		} else {
			Log.e("c", c + "");
			Log.e("YY", yy[mm] + "");
			return false;
		}
	}

	public static String Convert(String s) throws IOException {

		StringBuffer sad = new StringBuffer(s);
		sad.insert(6, "19");

		int[] xx = { 2, 4, 8, 5, 10, 9, 7, 3, 6, 1, 2, 4, 8, 5, 10, 9, 7 };
		char[] yy = { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };
		int mm = 0;
		int[] gg = new int[18];

		for (int i = 1; i < 18; i++) {
			int j = 17 - i;
			try {
				gg[i - 1] = Integer.parseInt(sad.substring(j, j + 1));
			} catch (Exception e) {
				return null;
			}

		}

		for (int i = 0; i < 17; i++) {
			mm += xx[i] * gg[i];
		}
		mm = mm % 11;
		System.out.println("该居民身份证是新的身份证" + sad + yy[mm]);
		return sad.toString() + yy[mm];
	}

	public static int getCheckNumber(String cardNumber) {
		int totalNumber = 0;
		int lastNumber = 0;
		for (int i = cardNumber.length() - 1; i >= 0; i -= 2) {
			int tmpNumber = calculate(Integer.parseInt(String
					.valueOf(cardNumber.charAt(i))) * 2);
			if (i == 0) {
				totalNumber += tmpNumber;
			} else {
				totalNumber += tmpNumber
						+ Integer.parseInt(String.valueOf(cardNumber
								.charAt(i - 1)));
			}

		}
		if (totalNumber >= 0 && totalNumber < 9) {
			return (10 - totalNumber);
		} else {
			String str = String.valueOf(totalNumber);
			if (Integer.parseInt(String.valueOf(str.charAt(str.length() - 1))) == 0) {
				return 0;
			} else {
				return (10 - Integer.parseInt(String.valueOf(str.charAt(str
						.length() - 1))));
			}
		}

	}

	public static int calculate(int number) {
		String str = String.valueOf(number);
		int total = 0;
		for (int i = 0; i < str.length(); i++) {
			total += Integer.valueOf(Integer.parseInt(String.valueOf(str
					.charAt(i))));
		}
		return total;
	}
}
