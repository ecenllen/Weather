package com.duoduoapp.yqlibrary.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by shanlin on 2018/1/6.
 */

public class EngineUtil {

    private static String engine(File cacheDir, String engineVersion) {
//        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) {
            return "";
        }
        return cacheDir.getAbsolutePath() + File.separator + engineVersion + File.separator + engineVersion + ".apk";
    }


    private static void initEngine(File cacheDir, String engineVersion) {
//        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) {
            return;
        }
        File file = new File(cacheDir.getAbsolutePath() + File.separator + engineVersion);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private static boolean haveEngine(File cacheDir, String engineVersion) {
        initEngine(cacheDir, engineVersion);
//        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) {
            return false;
        }
        String enginePath = engine(cacheDir, engineVersion);
        return new File(enginePath).exists();
    }


    public static String engineFilePath(Context context,String engineVersion, String engineUrl) {
        if(TextUtils.isEmpty(engineVersion) ||TextUtils.isEmpty(engineUrl))
            return "";
        File cacheDir;
        if (Build.VERSION.SDK_INT < 21) { //5.0以下，不包含5.0
//            cacheDir = context.getDir("libs", 0);
            cacheDir = context.getExternalCacheDir();
        } else {
            cacheDir = context.getExternalCacheDir();
        }
        initEngine(cacheDir, engineVersion);
        if (!haveEngine(cacheDir, engineVersion)) {
            downloadEngine(cacheDir, engineVersion, engineUrl);
        }
        return engine(cacheDir, engineVersion);
    }

    private static void downloadEngine(File cacheDir,String engineVersion, String engineUrl) {
        initEngine(cacheDir, engineVersion);
        downLoadFromUrl(engineUrl, engineVersion + ".apk", cacheDir.getAbsolutePath() + File.separator + engineVersion);
    }

    private static void downLoadFromUrl(String urlStr, String fileName, String savePath) {

        FileOutputStream randomAccessFile = null;
        InputStream inputStream = null;
        HttpURLConnection connection = null;
        try {
            //文件保存位置
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            File file = new File(saveDir + File.separator + fileName);
            File tempFile = new File(saveDir + File.separator + fileName + ".temp");

            long where = 0;
            if (file.exists()) {
                where = tempFile.length();
            }
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);

            if (Build.VERSION.SDK_INT >= 21) { //经测试，5.0以下报错
                connection.setRequestMethod("GET");
                if (where > 0) {
                    connection.setRequestProperty("Range", "bytes=" + where + "-"); // 断点代码
                }
//                connection.setRequestProperty("Connection", "Keep-Alive");
            }
            randomAccessFile = new FileOutputStream(tempFile, true);
//                if (where > 0) {
//                    randomAccessFile.seek(where); // 移动到哪里开始写
//                }

            //得到输入流
            inputStream = connection.getInputStream();

            byte[] buffer = new byte[1024];
            int progress = 0;
            int length = -1;
            while ((length = inputStream.read(buffer)) != -1) {
                randomAccessFile.write(buffer, 0, length);
                progress += length;
            }
            if (progress == connection.getContentLength()) {
                //下载完成
                tempFile.renameTo(file); // 重命名
                // 删除文件
                tempFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // fos.close();
                if (inputStream != null) {
                    inputStream.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

}
