package com.xtc.getappinfo;

//AppInfoProvider.java 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

/**
 * 类名称：AppInfoProvider 类描述：获取应用程序的相关信息 创建人：LXH
 */
public class AppInfoProvider {
	private PackageManager packageManager;

	// 获取一个包管理器
	public AppInfoProvider(Context context) {
		packageManager = context.getPackageManager();
	}

	/**
	 * 获取系统中所有应用信息， 并将应用软件信息保存到list列表中。
	 **/
	public List<AppInfo> getAllApps() {
		List<AppInfo> list = new ArrayList<AppInfo>();
		AppInfo myAppInfo;
		String readInfoStr = "";
		int count = 0;

		// 获取到所有安装了的应用程序的信息，包括那些卸载了的，但没有清除数据的应用程序
		List<PackageInfo> packageInfos = packageManager
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for (PackageInfo info : packageInfos) {
			myAppInfo = new AppInfo();
			// 拿到包名
			String packageName = info.packageName;
			// 拿到应用程序的信息
			ApplicationInfo appInfo = info.applicationInfo;
			// 拿到应用程序的图标
			Drawable icon = appInfo.loadIcon(packageManager);
			// 拿到应用程序的大小
			// long codesize = packageStats.codeSize;
			// Log.i("info", "-->"+codesize);
			// 拿到应用程序的程序名
			String appName = appInfo.loadLabel(packageManager).toString();
			myAppInfo.setPackageName(packageName);
			myAppInfo.setAppName(appName);
			myAppInfo.setIcon(icon);

			Log.e("APPINFO", "packageName=" + packageName);
			Log.e("APPINFO", "appName=" + appName);

			if (filterApp(appInfo)) {
				myAppInfo.setSystemApp(false);
			} else {
				myAppInfo.setSystemApp(true);
			}
			list.add(myAppInfo);

			count++;
			String curAppInfoString = packageName + "	" + appName + "	"
					+ "	" + info.versionName +"\n";
			
			readInfoStr += curAppInfoString;

		}

		try {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				File targetFile = new File("/mnt/sdcard/appinfo.txt");
				targetFile.delete();
				RandomAccessFile raf = new RandomAccessFile(targetFile, "rw");
				//raf.seek(2);
				//raf.writeUTF(readInfoStr);
				writeFileSdcardFile("/mnt/sdcard/appinfo.txt",readInfoStr);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 判断某一个应用程序是不是系统的应用程序， 如果是返回true，否则返回false。
	 */
	public boolean filterApp(ApplicationInfo info) {
		// 有些系统应用是可以更新的，如果用户自己下载了一个系统的应用来更新了原来的，它还是系统应用，这个就是判断这种情况的
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return true;
		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {// 判断是不是系统应用
			return true;
		}
		return false;
	}

	// 写数据到SD中的文件
	public void writeFileSdcardFile(String fileName, String write_str)
			throws IOException {
		try {

			FileOutputStream fout = new FileOutputStream(fileName);
			byte[] bytes = write_str.getBytes();

			fout.write(bytes);
			fout.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 读SD中的文件
	public String readFileSdcardFile(String fileName) throws IOException {
		String res = "";
		try {
			FileInputStream fin = new FileInputStream(fileName);

			int length = fin.available();

			byte[] buffer = new byte[length];
			fin.read(buffer);

			res = EncodingUtils.getString(buffer, "UTF-8");

			fin.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	// 写文件
	public void writeSDFile(String fileName, String write_str)
			throws IOException {

		File file = new File(fileName);

		FileOutputStream fos = new FileOutputStream(file);

		byte[] bytes = write_str.getBytes();

		fos.write(bytes);

		fos.close();
	}
}