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
 * �����ƣ�AppInfoProvider ����������ȡӦ�ó���������Ϣ �����ˣ�LXH
 */
public class AppInfoProvider {
	private PackageManager packageManager;

	// ��ȡһ����������
	public AppInfoProvider(Context context) {
		packageManager = context.getPackageManager();
	}

	/**
	 * ��ȡϵͳ������Ӧ����Ϣ�� ����Ӧ�������Ϣ���浽list�б��С�
	 **/
	public List<AppInfo> getAllApps() {
		List<AppInfo> list = new ArrayList<AppInfo>();
		AppInfo myAppInfo;
		String readInfoStr = "";
		int count = 0;

		// ��ȡ�����а�װ�˵�Ӧ�ó������Ϣ��������Щж���˵ģ���û��������ݵ�Ӧ�ó���
		List<PackageInfo> packageInfos = packageManager
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for (PackageInfo info : packageInfos) {
			myAppInfo = new AppInfo();
			// �õ�����
			String packageName = info.packageName;
			// �õ�Ӧ�ó������Ϣ
			ApplicationInfo appInfo = info.applicationInfo;
			// �õ�Ӧ�ó����ͼ��
			Drawable icon = appInfo.loadIcon(packageManager);
			// �õ�Ӧ�ó���Ĵ�С
			// long codesize = packageStats.codeSize;
			// Log.i("info", "-->"+codesize);
			// �õ�Ӧ�ó���ĳ�����
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
	 * �ж�ĳһ��Ӧ�ó����ǲ���ϵͳ��Ӧ�ó��� ����Ƿ���true�����򷵻�false��
	 */
	public boolean filterApp(ApplicationInfo info) {
		// ��ЩϵͳӦ���ǿ��Ը��µģ�����û��Լ�������һ��ϵͳ��Ӧ����������ԭ���ģ�������ϵͳӦ�ã���������ж����������
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return true;
		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {// �ж��ǲ���ϵͳӦ��
			return true;
		}
		return false;
	}

	// д���ݵ�SD�е��ļ�
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

	// ��SD�е��ļ�
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

	// д�ļ�
	public void writeSDFile(String fileName, String write_str)
			throws IOException {

		File file = new File(fileName);

		FileOutputStream fos = new FileOutputStream(file);

		byte[] bytes = write_str.getBytes();

		fos.write(bytes);

		fos.close();
	}
}