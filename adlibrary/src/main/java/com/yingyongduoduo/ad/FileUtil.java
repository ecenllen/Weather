package com.yingyongduoduo.ad;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
	public static Boolean isExist(String filePath) {
		File file = new File(filePath);
		return file.exists();

	}

	public static void deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}

	}

	public static void reName(String oldPath, String newPath) {
		File file = new File(oldPath);
		file.renameTo(new File(newPath));
	}

	/**
	 * 获得assets文件夹下,文件的内容
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static String getAssetsString(Context context, String fileName) {
		InputStream in = null;
		in = getAsserts(context, fileName);
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		try {
			for (int n; (n = in.read(b)) != -1;) {
				out.append(new String(b, 0, n));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out.toString();
	}

	/**
	 * 获取assets文件夹下kupao.html内容
	 * 
	 * @param context
	 * @return
	 */
	public static String getKupaoHtmlStr(Context context) {
		return getAssetsString(context, "kupao.html");
	}

	/**
	 * 获取assets文件夹下league.json内容
	 * 
	 * @param context
	 * @return
	 */
	public static String getLeagueJson(Context context) {
		return getAssetsString(context, "league.json");
	}

	/**
	 * 获取assets文件的输入流
	 * 
	 * @param context
	 * @param file
	 * @return
	 */
	public static InputStream getAsserts(Context context, String file) {
		String fileName = file;
		InputStream inputStream = null;
		try {
			inputStream = context.getResources().getAssets().open(fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return inputStream;
	}

}
