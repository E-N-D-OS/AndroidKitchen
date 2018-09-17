package ru.unpro.apktool.ui.fragments;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import es.dmoral.toasty.*;
import java.io.*;
import java.text.*;
import java.util.*;
import ru.unpro.apktool.R.*;
import ru.unpro.apktool.adapter.*;
import ru.unpro.apktool.ui.fragments.*;
import ru.unpro.apktool.util.*;

import android.support.v4.app.Fragment;
import ru.unpro.apktool.adapter.Adapter;
import android.support.compat.*;

public class FMFragment extends Fragment
{
	int adgravity,
	gtb = Gravity.BOTTOM,
	gtc = Gravity.CENTER,
	gtt = Gravity.TOP;

	static int count = 0;
	private TextView tvpath;
	private ListView lvFiles;
	PowerManager powerManager = null;

	String  apicode = String.valueOf(android.os.Build.VERSION.SDK_INT),
	patch = Environment.getExternalStorageDirectory().getAbsolutePath(),
	shell = new String(),
	gravityprefs = "bottom",
	WorkPath = "/data/local/AndroidKitchen",
	uri;

	private static final int DECODE = 1,
	COMPILE = 2,
	DEODEX = 3,
	DECDEX = 4,
	LONGPRESS = 5,
	UNPACKIMG = 6,
	REPACKIMG = 7,
	TASK = 8,
	JAVA = 9,
	CLASS = 10;

	private Context context;

	enum fileType {
		FOLDER, NFILE, APKFILE, ODEXFILE, TEXTFILE, MANIFEST, SMALI, IMGFILE, PIC, PROJECT
		};

	boolean tasks[] = new boolean[] { false, false, false, false };

	ProgressDialog dialogs[] = new ProgressDialog[4],
	myDialog;
	File currentParent;
	File[] currentFiles;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slidingpanel,
									 container, false);
		lvFiles = view.findViewById(R.id.files);
		tvpath = view.findViewById(R.id.tvpath);
		File root = new File(/*prefos.getString("parent", */patch)/*)*/;
		if (!root.canRead())
			root = new File("/");
		currentParent = root;
		currentFiles = currentParent.listFiles();
		inflateListView(currentFiles);


		lvFiles.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view,
										int position, long id) {
					//builder.detectFileUriExposure();
					uri = currentFiles[position].getPath();

					if (uri.contains("//"))
						uri = RunExec.removeRepeatedChar(uri);
					if (currentFiles[position].isFile()) {
						if (uri.endsWith(".apk") || uri.endsWith("jar"))
							showDialog(DECODE);
						else if (uri.endsWith(".odex"))
							showDialog(DEODEX);
						else if (uri.endsWith(".dex"))
							showDialog(DECDEX);
						else if (uri.endsWith("boot.img")
								 || uri.endsWith("recovery.img")) {
							showDialog(UNPACKIMG);
						}else if(uri.endsWith(".java")){
							showDialog(JAVA);
						}else if(uri.endsWith(".class")){
							showDialog(CLASS);
						}else if(uri.endsWith(".txt")){
							Intent ii = new Intent(Intent.ACTION_VIEW);
							Uri apkuri = Uri.fromFile( new File(uri));
							ii.setDataAndType(apkuri, "text/*");
							startActivity(ii);
						}else {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							Uri apkuri = Uri.fromFile(new File(uri));
							intent.setDataAndType(apkuri, "*/*");
							startActivity(intent);
						}
						return;
					} else if (currentFiles[position].isDirectory()
							   && (currentFiles[position].getName().endsWith("_src")
							   || currentFiles[position].getName().endsWith(
								   "_odex") || currentFiles[position]
							   .getName().endsWith("_dex"))) {
						showDialog(COMPILE);
						return;
					} else if (currentFiles[position].isDirectory()
							   && (currentFiles[position].getName().equals("ramdisk"))) {
						showDialog(REPACKIMG);
						return;
					}

					File[] tem = currentFiles[position].listFiles();
					if (tem == null) {
						Toasty.warning(context,
									   getString(R.string.directory_no_permission),
									   Toast.LENGTH_LONG).show();
					} else {
						currentParent = currentFiles[position];
						currentFiles = tem;
						inflateListView(currentFiles);
					}
				}

				private void showDialog(int dECODE)
				{
					// TODO: Implement this method
				}
			});


		lvFiles.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View view,
											   int position, long id) {
					uri = currentFiles[position].getPath();
					if (uri.contains("//"))
						uri = RunExec.removeRepeatedChar(uri);
					showDialog(LONGPRESS);
					return true;
				}

				private void showDialog(int lONGPRESS)
				{
					// TODO: Implement this method
				}
			});
		return view;
	}

	@SuppressWarnings("unchecked")
	@SuppressLint("SimpleDateFormat")
	private void inflateListView(File[] files) {
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		Arrays.sort(files, new FileComparator());

		for (int i = 0; i < files.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			if (files[i].getName().endsWith("_src")) {
				listItem.put("icon",
							 getFileIcon(context, null, fileType.PROJECT));

			} else if (files[i].isDirectory()) {
				listItem.put("icon",
							 getFileIcon(context, null, fileType.FOLDER));
			} else if (files[i].getName().endsWith(".apk")) {
				listItem.put(
					"icon",
					getFileIcon(context,
								files[i].getAbsolutePath(), fileType.APKFILE));

			} else if (files[i].getName().endsWith(".odex")) {
				listItem.put("icon",
							 getFileIcon(context, null, fileType.ODEXFILE));

			} else if (files[i].getName().endsWith(".smali")) {
				listItem.put("icon",
							 getFileIcon(context, null, fileType.SMALI));

			} else if (files[i].getName().endsWith(".png")) {
				listItem.put("icon",
							 getFileIcon(context, null, fileType.PIC));

			} else if (files[i].getName().endsWith(".img")) {
				listItem.put("icon",
							 getFileIcon(context, null, fileType.IMGFILE));

			} else if (files[i].getName().endsWith(".txt")) {
				listItem.put("icon",
							 getFileIcon(context, null, fileType.TEXTFILE));

			} else if (files[i].getName().endsWith("anifest.xml")) {
				listItem.put("icon",
							 getFileIcon(context, null, fileType.MANIFEST));

			} else {
				listItem.put("icon",
							 getFileIcon(context, null, fileType.NFILE));
			}
			listItem.put("filename", files[i].getName());
			File myFile = new File(files[i].getAbsolutePath());
			long modTime = myFile.lastModified();
			SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd-MM-yyyy HH:mm:ss");
			long size = myFile.length();
			double fileSize;
			String strSize = null;
			java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
			if (size >= 1073741824) {
				fileSize = (double) size / 1073741824.0;
				strSize = df.format(fileSize) + "G";
			} else if (size >= 1048576) {
				fileSize = (double) size / 1048576.0;
				strSize = df.format(fileSize) + "M";
			} else if (size >= 1024) {
				fileSize = (double) size / 1024;
				strSize = df.format(fileSize) + "K";
			} else {
				strSize = Long.toString(size) + "B";
			}
			if (myFile.isFile() && myFile.canRead())
				listItem.put("modify", dateFormat.format(new Date(modTime))
							 + "   " + strSize);
			else
				listItem.put("modify", dateFormat.format(new Date(modTime)));

			listItems.add(listItem);
		}

		Adapter adapter = new Adapter(context, listItems, R.layout.list_item,
									  new String[] { "filename", "icon", "modify" }, new int[] {
										  R.id.file_name, R.id.icon, R.id.file_modify });

		lvFiles.setAdapter(adapter);
		tvpath.setText(currentParent.getAbsolutePath());

	}

	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
		if (paramInt == KeyEvent.KEYCODE_BACK)
			try {
				if (!currentParent.getCanonicalPath().equals("/")) {
					currentParent = currentParent.getParentFile();
					currentFiles = currentParent.listFiles();
					inflateListView(currentFiles);
				}
			} catch (Exception localException) {
			}
		return false;
	}

	public Drawable getFileIcon(Context context, String apkPath, fileType type) {
		switch (type) {
			case FOLDER:
				return context.getResources().getDrawable(R.drawable.folder_ic);

			case TEXTFILE:
				return context.getResources().getDrawable(R.drawable.txt_ic);

			case MANIFEST:
				return context.getResources().getDrawable(R.drawable.manif_ic);

			case PROJECT:
				return context.getResources().getDrawable(R.drawable.project_ic);

			case PIC:
				return context.getResources().getDrawable(R.drawable.folder_ic);

			case IMGFILE:
				return context.getResources().getDrawable(R.drawable.img_ic);

			case SMALI:
				return context.getResources().getDrawable(R.drawable.smali_ic);

			case NFILE:
				return context.getResources().getDrawable(R.drawable.file);

			case ODEXFILE:
				return context.getResources().getDrawable(R.drawable.odex);

			case APKFILE:
				PackageManager pm = context.getPackageManager();
				PackageInfo info = pm.getPackageArchiveInfo(apkPath,
															PackageManager.GET_ACTIVITIES);
				if (info != null) {
					ApplicationInfo appInfo = info.applicationInfo;
					appInfo.sourceDir = apkPath;
					appInfo.publicSourceDir = apkPath;
					/*String a = appInfo.packageName;
					 TextView aa = findViewById(R.id.aaa);
					 aa.setText(a);*/
					try {
						return appInfo.loadIcon(pm);
					} catch (OutOfMemoryError e) {
						// Log.e("ApkIconLoader", e.toString());
					}
				} else
					return context.getResources().getDrawable(R.drawable.file);
		}
		return null;
	}

	public void extractData() {
		new Thread() {
			public void run() {
				if (!(new File("/data/data/per.pqy.apktool/lix").exists())) {
					RunExec.Cmd(
						shell,
						"dd if=/data/data/per.pqy.apktool/apktool/busybox of=/data/data/per.pqy.apktool/tar");
					RunExec.Cmd(shell,
								"chmod 777 /data/data/per.pqy.apktool/tar");
					RunExec.Cmd(
						shell,
						"/data/data/per.pqy.apktool/tar xf /data/data/per.pqy.apktool/apktool/jvm.tar --directory=/data/data/per.pqy.apktool");
					RunExec.Cmd(shell,
								"chmod -R 755 /data/data/per.pqy.apktool/lix");
					RunExec.Cmd(shell, " rm /data/data/per.pqy.apktool/tar");
				}
			}
		}.start();
	}
}
