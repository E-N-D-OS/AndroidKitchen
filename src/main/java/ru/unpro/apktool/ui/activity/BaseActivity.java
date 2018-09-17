package ru.unpro.apktool.ui.activity;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.os.PowerManager.*;
import android.preference.*;
import android.support.annotation.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.ViewGroup.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.sothree.slidinguppanel.*;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.*;
import es.dmoral.toasty.*;
import java.io.*;
import java.text.*;
import java.util.*;
import ru.unpro.apktool.*;
import ru.unpro.apktool.adapter.*;
import ru.unpro.apktool.ui.settings.*;
import ru.unpro.apktool.util.*;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.ClipboardManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import ru.unpro.apktool.adapter.Adapter;
import ru.unpro.apktool.ui.dialogs.*;

@SuppressWarnings("deprecation")
@SuppressLint("HandlerLeak")
public class BaseActivity extends AppCompatActivity {
	
	int adgravity;
	private PackageManager pm;
	private List<AppDetail> apps;
	static int count = 0;
	MyHandler myHandler = new MyHandler();
	private TextView tvpath;
	private ListView lvFiles;
	PowerManager powerManager;
	WakeLock wakeLock;
	
	String  apicode = String.valueOf(android.os.Build.VERSION.SDK_INT),
			patch = Environment.getExternalStorageDirectory().getAbsolutePath(),
			shell = new String(),
			gravityprefs = "bottom",
			ptlv,
			olv = "OpListView",
			WorkPath = "/data/local/AndroidKitchen",
			uri;
			
	private static final int DECODE = 1,COMPILE = 2,DEODEX = 3,DECDEX = 4,LONGPRESS = 5,UNPACKIMG = 6,REPACKIMG = 7,TASK = 8,JAVA = 9,CLASS = 10;
	private SlidingUpPanelLayout mLayout;
	private Window WA;
	private WindowManager.LayoutParams WL;
	enum fileType { FOLDER, NFILE, APKFILE, ODEXFILE, TEXTFILE, MANIFEST, SMALI, IMGFILE, PIC, PROJECT };
	boolean tasks[] = new boolean[] { false, false, false, false }, pmlist, fmlist;
	ProgressDialog dialogs[] = new ProgressDialog[4], myDialog;
	File currentParent;
	File[] currentFiles;
	Context context = BaseActivity.this;
	SharedPreferences prefs;
	String gp;
	
	@Override
	protected void onStart()
	{
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		gp = prefs.getString(getString(R.string.k_adgravity), "");
		super.onStart();
	}
	//Создание экрана
	public void onCreate(Bundle savedInstanceState) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		gp = prefs.getString(getString(R.string.k_theme), "Dark");
		int theme = SettingsLoader.loadTheme(gp);
		setTheme(theme);
		SharedPreferences.Editor editor = prefs.edit();

		//Проверка рут-доступа
		if (new File("/system/bin/su").exists()
			|| new File("/system/xbin/su").exists())
			shell = "su ";
		else {
			shell = "su ";
		}
		// shell += new String("LANG=") +
		// getResources().getConfiguration().locale.toString() + ".UTF-8 ";
		super.onCreate(savedInstanceState);
		myHandler = new MyHandler();
		this.powerManager = (PowerManager) this
			.getSystemService(Context.POWER_SERVICE);
		this.wakeLock = this.powerManager.newWakeLock(
			PowerManager.FULL_WAKE_LOCK, "My Lock");

		//Диалог с ООО
		if (!(new File("/data/data/per.pqy.apktool/tag").exists())) {
			AlertDialog.Builder b1 = new AlertDialog.Builder(context);
			b1.setTitle(getString(R.string.declaration)).setMessage(
				getString(R.string.agreement));
			b1.setPositiveButton(getString(R.string.ok), null);
			b1.setNeutralButton((getString(R.string.never_remind)),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						RunExec.Cmd(shell,
									" mkdir  /data/data/per.pqy.apktool/tag");
					}
				});
			b1.create().show();
		}

		//Первый запуск
		if (!new File("/data/data/per.pqy.apktool/apktool").exists()) {
			if (new File("/sdcard/apktool").exists()) {
				RunExec.Cmd(shell, "rm /data/data/per.pqy.apktool/apktool");
				RunExec.Cmd(shell, "ln -s /sdcard/apktool /data/data/per.pqy.apktool/apktool");
				extractData();
			} else {
				AlertDialog.Builder b1 = new AlertDialog.Builder(context);
				b1.setTitle(getString(R.string.warning)).setMessage(getString(R.string.data_not_in_sdcard));
				b1.setPositiveButton(getString(R.string.ok), null);
				b1.create().show();
			}
		}
		//final StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		//StrictMode.setVmPolicy(builder.build());
		setContentView(R.layout.main);
		final LinearLayout mst = findViewById(R.id.mainst);
		final LinearLayout dst = findViewById(R.id.dragst);
		final LinearLayout flst = findViewById(R.id.flistst);
		final LinearLayout plst = findViewById(R.id.plistst);
		lvFiles = this.findViewById(R.id.files);
		tvpath = this.findViewById(R.id.tvpath);
		setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
		mLayout = findViewById(R.id.sliding_layout);
		loadFL();
		mLayout.addPanelSlideListener(new PanelSlideListener() {
				@Override
				public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
					if(newState==PanelState.COLLAPSED){
						mst.setVisibility(View.VISIBLE);
						dst.setVisibility(View.GONE);
						plst.setVisibility(View.GONE);
						flst.setVisibility(View.GONE);
					}
					else if(newState==PanelState.DRAGGING){
						mst.setVisibility(View.GONE);
						dst.setVisibility(View.VISIBLE);
						plst.setVisibility(View.GONE);
						flst.setVisibility(View.GONE);
					}
					else if(newState==PanelState.EXPANDED){
						mst.setVisibility(View.GONE);
						dst.setVisibility(View.GONE);
						if(prefs.getString(olv, ptlv)=="plist"){
							plst.setVisibility(View.VISIBLE);
							flst.setVisibility(View.GONE);}
						else{
							plst.setVisibility(View.GONE);
							flst.setVisibility(View.VISIBLE);
						}
					}
				}

				@Override
				public void onPanelSlide(View panel, float slideOffset) {
				}});
        mLayout.setFadeOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					mLayout.setPanelState(PanelState.COLLAPSED);
				}
			});
		BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
			new BottomNavigationView.OnNavigationItemSelectedListener() {
				@Override
				public boolean onNavigationItemSelected(@NonNull MenuItem item) {
					switch (item.getItemId()) {
						case R.id.action_files:
							item.setChecked(true);
							loadFL();
							break;
						case R.id.action_app:
							item.setChecked(true);
							loadPM();
							break;
						case R.id.action_mail:
							break;
					}
					return false;
				}
			});
	}
	
	//Загрузка списка файлов
	public void loadFL(){
		SharedPreferences prefs = PreferenceManager
			.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		ptlv = "flist";
		editor.putString(olv, ptlv);
		editor.commit();
		File root = new File(prefs.getString("parent", patch));
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
						else if (uri.endsWith(".img")) {
							showDialog(UNPACKIMG);
						}else if(uri.endsWith(".java")){
							showDialog(JAVA);
						}else if(uri.endsWith(".class")){
							showDialog(CLASS);
						}else if(uri.endsWith(".txt")){
							Intent i = IntentUtil.getIntent(context, new File(uri), "text/txt");
							startActivity(i);
						}else {
							Intent i = IntentUtil.getIntent(context, new File(uri), "*/*");
							startActivity(i);
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
			});
		lvFiles.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View view,
											   int position, long id) {
					uri = currentFiles[position].getPath();
					if (uri.contains("//"))
						uri = RunExec.removeRepeatedChar(uri);
					//showDialog(LONGPRESS);
					BottomSheetDialog mBSD = BottomSheetBuilder.build(context, R.layout.settings);
					mBSD.show();
					return true;
				}
			});
	}
	class MyHandler extends Handler
	{
		public void doWork(String str, final Bundle bundle)
		{
			if (bundle.getBoolean("isTemp"))
			{  
				myDialog.setMessage(bundle.getString("op"));
			}
			else
			{
				SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(context);
				if (prefs.getBoolean(getString(R.string.k_vib), false))
				{
					Vibrator v = (Vibrator) getApplication().getSystemService(
						Service.VIBRATOR_SERVICE);
					v.vibrate(new long[] { 0, 200, 100, 200 }, -1);
				}
				if (prefs.getBoolean(getString(R.string.k_notf), false))
				{
					CharSequence contentTitle = bundle.getString("filename");
					CharSequence contentText = getString(R.string.op_done);
					//Intent notificationIntent = context.getIntent();
					//PendingIntent pendingIntent = PendingIntent.getActivity(
					//	context, 0, notificationIntent, 0);

					Notification.Builder builder = new Notification.Builder(context);
					Notification notification = new Notification(
						R.drawable.ic_launcher,
						getString(R.string.op_done),
						System.currentTimeMillis());

					NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

					builder.setAutoCancel(false);
					builder.setTicker("this is ticker text");
					builder.setContentTitle(contentTitle);               
					builder.setContentText(contentText);
					builder.setSmallIcon(R.drawable.ic_launcher);
					//builder.setContentIntent(pendingIntent);
					builder.setOngoing(true);
					builder.setSubText("This is subtext...");   //API level 16
					builder.setNumber(100);
					builder.build();

					notification = builder.getNotification();
					mNotificationManager.notify(count++, notification);
				}
				myDialog.dismiss();
				int num = bundle.getInt("tasknum");
				tasks[num] = false;
				Toasty.success(context, str, Toast.LENGTH_LONG, true).show();
				gravityprefs = prefs.getString(getString(R.string.k_adgravity), "");
				SettingsLoader.loadDGravity(gravityprefs);

				AlertDialog.Builder msgDialog = new AlertDialog.Builder(
					context);
				String tmp_str = bundle.getString("filename") + "\n"
					+ getString(R.string.cost_time) + " ";

				long time = (System.currentTimeMillis() - bundle.getLong("time")) / 1000;
				if (time > 3600)
				{
					tmp_str += Integer.toString((int) (time / 3600))
						+ getString(R.string.hour)
						+ Integer.toString((int) (time % 3600) / 60)
						+ getString(R.string.minute)
						+ Integer.toString((int) (time % 60))
						+ getString(R.string.second);
				}
				else if (time > 60)
				{
					tmp_str += Integer.toString((int) (time % 3600) / 60)
						+ getString(R.string.minute)
						+ Integer.toString((int) (time % 60))
						+ getString(R.string.second);
				}
				else
				{
					tmp_str += Integer.toString((int) time)
						+ getString(R.string.second);
				}
				if (prefs.getBoolean(getString(R.string.k_wrap), false))
				{
					HorizontalScrollView hscv = new HorizontalScrollView(
						context);
					ScrollView scv = new ScrollView(context);
					TextView tv;

					tv = new TextView(context);

					tv.setText(bundle.getString("output"));
					scv.addView(tv);
					hscv.addView(scv);
					msgDialog.setView(hscv);
				}
				else
					msgDialog.setMessage(bundle.getString("output"));
				msgDialog.setTitle(tmp_str)
					.setPositiveButton(getString(R.string.ok), null)
					.setNeutralButton((getString(R.string.copy)),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
											int which)
						{
							ClipboardManager cmb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
							cmb.setText(bundle.getString("output"));
						}
					});
				AlertDialog AD = msgDialog.create();
				WA = AD.getWindow();
				WL = WA.getAttributes();
				WL.gravity = adgravity;

				WA.setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				WA.setAttributes(WL);
				AD.show();
				currentFiles = currentParent.listFiles();
				inflateListView(currentFiles);
			}
		}

		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			final Bundle bundle = msg.getData();
			switch (bundle.getInt("what"))
			{
				case 0:
					doWork(getString(R.string.decompile_all_finish), bundle);
					break;
				case 1:
					doWork(getString(R.string.sign_finish), bundle);
					break;
				case 2:
					doWork(getString(R.string.recompile_finish), bundle);
					break;
				case 3:
					doWork(getString(R.string.decompile_dex_finish), bundle);
					break;
				case 4:
					doWork(getString(R.string.decompile_res_finish), bundle);
					break;
				case 5:
					doWork(getString(R.string.decompile_odex_finish), bundle);
					break;
				case 6:
					doWork(getString(R.string.op_done), bundle);
					break;
				case 7:
					doWork(getString(R.string.import_finish), bundle);
					break;
				case 8:
					doWork(getString(R.string.align_finish), bundle);
					break;
				case 9:
					doWork(getString(R.string.add_finish), bundle);
					break;
				case 10:
					doWork(getString(R.string.delete_finish), bundle);
					break;
			}
		}
	}

	//Выполнение комманд
	public void threadWork(Context context, String message,
						   final String command, final int what)
	{
		int freeTask = -1;
		if (!tasks[0])
			freeTask = 0;
		else if (!tasks[1])
			freeTask = 1;
		else if (!tasks[2])
			freeTask = 2;
		else if (!tasks[3])
			freeTask = 3;
		if (freeTask == -1)
		{
			Toasty.warning(context, getString(R.string.nofreetask),
						   Toast.LENGTH_SHORT).show();
			return;
		}
		Thread thread = new mThread(freeTask) {
			public void run()
			{
				java.lang.Process process = null;
				DataOutputStream os = null;
				InputStream proerr = null;
				InputStream proin = null;
				try
				{
					Bundle tb = new Bundle();
					tb.putString("filename", new File(uri).getName());
					tb.putInt("what", what);
					tb.putLong("time", System.currentTimeMillis());
					tb.putBoolean("isTemp", false);
					process = Runtime.getRuntime().exec(shell);
					os = new DataOutputStream(process.getOutputStream());
					proerr = process.getErrorStream();
					proin = process.getInputStream();
					os.writeBytes(new String(
									  "LD_LIBRARY_PATH=/data/data/per.pqy.apktool/lix:$LD_LIBRARY_PATH ")
								  + command + "\n");
					os.writeBytes("exit\n");
					os.flush();
					BufferedReader br1 = new BufferedReader(
						new InputStreamReader(proerr));
					String str = "";
					String totalstr = "";
					while ((str = br1.readLine()) != null)
					{
						Message msg = new Message();
						Bundle bundle = new Bundle();
						totalstr += str + "\n";
						bundle.putString("op", str);
						bundle.putInt("what", what);
						bundle.putBoolean("isTemp", true);
						bundle.putInt("tasknum", tasknum);
						msg.setData(bundle);
						myHandler.sendMessage(msg);
					}
					process.waitFor();
					Message tmsg = new Message();
					tb.putString("output",
								 totalstr + RunExec.inputStream2String(proin, "utf-8"));
					tmsg.setData(tb);
					myHandler.sendMessage(tmsg);
				}
				catch (Exception e)
				{
					Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
				}
				finally
				{
					try
					{
						if (os != null)
						{
							os.close();
						}
						process.destroy();
					}
					catch (Exception e)
					{
					}
				}
			}
		};

		thread.start();
		myDialog = new ProgressDialog(context);
		WA = myDialog.getWindow();
		WL = WA.getAttributes();
		WL.gravity = adgravity;

		WA.setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		WA.setAttributes(WL);
		myDialog.setMessage(message);
		myDialog.setIndeterminate(true);
		myDialog.setCancelable(false);
		myDialog.setButton(DialogInterface.BUTTON_POSITIVE,
			getString(R.string.put_background),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					myDialog.dismiss();
				}
			});
		
		  myDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
		  getString(R.string.cancel), new DialogInterface.OnClickListener() {
		  @Override public void onClick(DialogInterface dialog, int which) {
		  dialog.dismiss();
		 } });
		dialogs[freeTask] = myDialog;
		tasks[freeTask] = true;
		myDialog.show();
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

		Adapter adapter = new Adapter(this, listItems, R.layout.list_item,
				new String[] { "filename", "icon", "modify" }, new int[] {
						R.id.file_name, R.id.icon, R.id.file_modify });
		lvFiles.setAdapter(adapter);
		tvpath.setText(currentParent.getAbsolutePath());
	}

	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
		AlertDialog.Builder exitDialog = new AlertDialog.Builder(
			this);
		exitDialog.setTitle(getString(R.string.want_to_exit))
			.setPositiveButton(getString(R.string.yes),
			new DialogInterface.OnClickListener() {
				public void onClick(
					DialogInterface paramAnonymousDialogInterface,
					int paramAnonymousInt) {
					finish();
				}
			})			
			.setNegativeButton(getString(R.string.no), null);

		AlertDialog ADTASK = exitDialog.create();
		WA = ADTASK.getWindow();
		WL = WA.getAttributes();
		WL.gravity = adgravity;
		WA.setLayout(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		WA.setAttributes(WL);
		if (paramInt == KeyEvent.KEYCODE_BACK)
			if (mLayout != null &&
				(mLayout.getPanelState() == PanelState.COLLAPSED || mLayout.getPanelState() == PanelState.ANCHORED)) {
				ADTASK.show();
			} else {
			try {
				if (!currentParent.getCanonicalPath().equals("/")) {
					currentParent = currentParent.getParentFile();
					currentFiles = currentParent.listFiles();
					inflateListView(currentFiles);
				} else {
					if (mLayout != null &&
						(mLayout.getPanelState() == PanelState.EXPANDED || mLayout.getPanelState() == PanelState.ANCHORED)) {
						mLayout.setPanelState(PanelState.COLLAPSED);
					} else {
						ADTASK.show();
					}
				}
			} catch (Exception localException) {
			}
		}
		return false;
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu paramMenu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, paramMenu);
        MenuItem item = paramMenu.findItem(R.id.donate);
        if (mLayout != null) {
            if (mLayout.getPanelState() == PanelState.HIDDEN) {
                item.setTitle(R.string.ok);
            } else {
                item.setTitle(R.string.hour);
            }
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
	
	public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
		switch (paramMenuItem.getItemId()) {
		default:
			return false;
		case R.id.about:
			AlertDialog.Builder aboutDialog = new AlertDialog.Builder(this);
			aboutDialog.setTitle(getString(R.string.about)).setMessage(
					"refer to https://code.google.com/p/apktool")
			.setPositiveButton(getString(R.string.ok), null);
				AlertDialog ADABT = aboutDialog.create();
				WA = ADABT.getWindow();
				WL = WA.getAttributes();
				WL.gravity = adgravity;
				WA.setLayout(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
				WA.setAttributes(WL);
				ADABT.show();
			return false;
		case R.id.exit:
			finish();
			return false;
		case R.id.task:
			showDialog(TASK);
			return false;
		case R.id.rom:
			AlertDialog.Builder romDialog = new AlertDialog.Builder(this);
			romDialog.setMessage(getString(R.string.make_rom))
			.setPositiveButton(getString(R.string.ok),new DialogInterface.OnClickListener() {
				public void onClick(
						DialogInterface paramAnonymousDialogInterface,
						int paramAnonymousInt) {
							uri = "update.zip";
							final String cmd = new String(" busybox sh /data/data/per.pqy.apktool/apktool/genscript.sh");
							threadWork(context, getString(R.string.making_rom), cmd, 6);
				}
			})
			.setNegativeButton(getString(R.string.cancel),null);

				AlertDialog ADROM = romDialog.create();
				WA = ADROM.getWindow();
				WL = WA.getAttributes();
				WL.gravity = adgravity;
				WA.setLayout(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
				WA.setAttributes(WL);
				ADROM.show();
			return false;
			
		case R.id.donate:
//			Intent intent = new Intent();
//			intent.setAction(Intent.ACTION_VIEW);
//			intent.setData(Uri.parse("https://me.alipay.com/pangqingyuan"));
//			startActivity(intent);
			SharedPreferences settings = getSharedPreferences("Settings",
				 MODE_PRIVATE);
				AlertDialog.Builder ADBPREFS = new AlertDialog.Builder(this)
				 .setTitle(getString(R.string.settings))
				 .setMultiChoiceItems(
				 new String[] { getString(R.string.vibration),
				 getString(R.string.notify),
				 getString(R.string.white_background),
				 getString(R.string.keep_screen_on),
				 getString(R.string.wrap_msg) },
				 new boolean[] { settings.getInt("Vib", 0) == 1,
				 settings.getInt("Noti", 0) == 1,
				 settings.getInt("bg", 0) == 1,
				 settings.getInt("bl", 0) == 1,
				 settings.getInt("warp", 0) == 1 },
				 new DialogInterface.OnMultiChoiceClickListener() {
				 @Override
				 public void onClick(DialogInterface dialog,
				 int which, boolean isChecked) {
				 // TODO 自动生成的方法存根
				 SharedPreferences settings = getSharedPreferences(
				 "Settings", MODE_PRIVATE);
				 SharedPreferences.Editor editor = settings
				 .edit();
				 if (isChecked) {
				 switch (which) {
				 case 0:
				 editor.putInt("Vib", 1);
				 editor.commit();
				 break;
				 case 1:
				 editor.putInt("Noti", 1);
				 editor.commit();
				 break;
				 case 2:
				 editor.putInt("bg", 1);
				 tvpath.setBackgroundColor(Color.WHITE);
				 editor.commit();
				 break;
				 case 3:
				 editor.putInt("bl", 1);
				 wakeLock.acquire();
				 editor.commit();
				 break;
				 case 4:
				 editor.putInt("warp", 1);
				 editor.commit();
				 break;

				 }
				 } else {
				 switch (which) {
				 case 0:
				 editor.putInt("Vib", 0);
				 editor.commit();
				 break;
				 case 1:
				 editor.putInt("Noti", 0);
				 editor.commit();
				 break;
				 case 2:
				 editor.putInt("bg", 0);
				 tvpath.setBackgroundColor(Color.BLACK);
				 editor.commit();
				 break;
				 case 3:
				 editor.putInt("bl", 0);
				 wakeLock.release();
				 editor.commit();
				 break;
				 case 4:
				 editor.putInt("warp", 0);
				 editor.commit();
				 break;
				 }
				 }

				 }
				 }).setPositiveButton(getString(R.string.ok), null);
				AlertDialog ADPREFS = ADBPREFS.create();
				WA = ADPREFS.getWindow();
				WL = WA.getAttributes();
				WL.gravity = adgravity;
				
				WA.setLayout(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
				WA.setAttributes(WL);
				ADPREFS.show();
			return false;
			
		case R.id.refresh:
			refreshFL();
			return false;
			
		case R.id.setting:
				Intent i = new Intent();
				i.setClass(this, SettingsActivity.class);
				startActivity(i);
			return false;
		}

	}

	protected void onResume() {
		super.onResume();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		final String gp = prefs.getString(getString(R.string.k_adgravity), "");
		adgravity = SettingsLoader.loadDGravity(gp);
		if (prefs.getBoolean(getString(R.string.k_scrn), false)) 
			this.wakeLock.acquire();

	}

	protected void onPause() {
		super.onPause();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		final String gp = prefs.getString(getString(R.string.k_adgravity), "");
		adgravity = SettingsLoader.loadDGravity(gp);
		if (prefs.getBoolean(getString(R.string.k_scrn), false)) 
			this.wakeLock.release();

	}

	protected void onDestroy() {
		SharedPreferences prefs = getSharedPreferences("Settings",
				MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("parent", currentParent.toString());
		editor.commit();
		super.onDestroy();
		System.exit(0);
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
	
	//Обновление списка файлов
	public void refreshFL() {
		currentFiles = currentParent.listFiles();
		inflateListView(currentFiles);
	}
	
	//Загрузка данных приложений
	public void loadApps()
	{
		pm = getPackageManager();
		apps = new ArrayList<AppDetail>();

		Intent i = new Intent(Intent.ACTION_MAIN, null);
		//i.addCategory(Intent.CATEGORY_DEFAULT);

		List<ResolveInfo> availableActivities = pm.queryIntentActivities(i, 0);
		for (ResolveInfo ri:availableActivities)
		{
			AppDetail app = new AppDetail();
			app.label = ri.loadLabel(pm);
			app.name = ri.activityInfo.packageName;
			app.icon = ri.activityInfo.loadIcon(pm);
			apps.add(app);
		}
	}
	
	//Загрузка списка приложений
	public void loadPM()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		ptlv = "plist";
		editor.putString(olv, ptlv);
		editor.commit();
		loadApps();
		ListView list;
		list = findViewById(R.id.files);

		ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(this, 
																	  R.layout.list_item, 
																	  apps) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				if (convertView == null)
				{
					convertView = getLayoutInflater().inflate(R.layout.list_item, null);
				}

				ImageView appIcon = convertView.findViewById(R.id.icon);
				appIcon.setImageDrawable(apps.get(position).icon);

				TextView appLabel = convertView.findViewById(R.id.file_name);
				appLabel.setText(apps.get(position).label);

				TextView appName = convertView.findViewById(R.id.file_modify);
				appName.setText(apps.get(position).name);

				return convertView;
			}
		};

		list.setAdapter(adapter); 
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> av, View v, int pos,
										long id)
				{
					Intent i = pm.getLaunchIntentForPackage(apps.get(pos).name.toString());
					BaseActivity.this.startActivity(i);
				}
			});
		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> av, View v, int pos,
										long id)
				{
					String an = apps.get(pos).name.toString();
					//Intent i = pm.;getLaunchIntentForPackage(apps.get(pos).name.toString());
					//BaseActivity.this.startActivity(i);
					return true;
				}
			});
	}
	
	//Создание диалогов
	protected Dialog onCreateDialog(int id)
	{
		switch (id)
		{
			case DECODE:
				AlertDialog.Builder ADBDCD = new AlertDialog.Builder(this)
					.setItems(
					R.array.dec_array, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which)
						{
							switch (which)
							{
								case 0:
									final String command = new String(
										" ak_apktool d -f ")
										+ "-o '" + uri.substring(0, uri.length() - 4) + "_src'"
										+ " '" + uri + "'";
									threadWork(context,
											   getString(R.string.decompiling),
											   command, 0);
									break;
								case 1:
									final String command1 = new String(
										" ak_apktool d -f -r ")
										+ "-o '" + uri.substring(0, uri.length() - 4) + "_src'"
										+ " '" + uri + "'";
									threadWork(context,
											   getString(R.string.decompiling),
											   command1, 3);
									break;
								case 2:
									final String command2 = new String(
										" ak_apktool d -f -s ")
										+ "-o '" + uri.substring(0, uri.length() - 4) + "_src'"
										+ " '" + uri + "'";
									threadWork(context,
											   getString(R.string.decompiling),
											   command2, 4);
									break;
								case 3:
									final String command3 = new String(
										" sh /data/data/ru.unpro.apktool/mydata/signapk.sh ")
										+ "'"
										+ uri
										+ "' '"
										+ uri.substring(0, uri.length() - 4)
										+ "_sign.apk'";
									threadWork(context,
											   getString(R.string.signing), command3,
											   1);
									break;
								case 4:
									final String command4 = new String(
										" /data/data/ru.unpro.apktool/lix/dexopt-wrapper ")
										+ "'"
										+ uri
										+ "' '"
										+ uri.substring(0, uri.length() - 3)
										+ "odex'";
									threadWork(context,
											   getString(R.string.making),
											   command4, 6);
									break;

								case 5:
									final String command5 = new String(
										" /data/data/ru.unpro.apktool/lix/zipalign -f -v 4 ")
										+ "'"
										+ uri
										+ "' '"
										+ uri.substring(0, uri.length() - 4)
										+ "_zipalign.apk'";
									threadWork(context,
											   getString(R.string.aligning), command5,
											   8);
									break;
								case 6:
									/*Intent intent = new Intent(Intent.ACTION_VIEW);
									 final Uri apkuri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(uri));
									 intent.setDataAndType(apkuri,
									 "application/vnd.android.package-archive");*/
									Intent intent = IntentUtil.getInstallAppIntent(context, uri);
									startActivity(intent);
									break;
								case 7:
									final String command7 = new String(
										"/data/data/ru.unpro.apktool/lix/7z d -tzip '")
										+ uri + "' classes.dex";
									threadWork(context,
											   getString(R.string.deleting), command7,
											   10);
									break;
								case 8:
									File f = new File(uri);
									if (!new File(f.getParent() + "/META-INF")
										.exists())
									{
										final String command8 = new String(
											"sh /data/data/ru.unpro.apktool/mydata/tool.sh ")
											+ "'"
											+ f.getParent()
											+ "' '"
											+ f.getName() + "'";
										threadWork(context,
												   getString(R.string.extracting),
												   command8, 6);
									}
									else
										Toasty.warning(context,
													   getString(R.string.dir_exist),
													   Toast.LENGTH_LONG).show();
									break;
								case 9:
									final String command9 = new String(
										"/data/data/ru.unpro.apktool/lix/7z d -tzip ")
										+ "'" + uri + "'" + " META-INF";
									threadWork(context,
											   getString(R.string.deleting), command9,
											   10);
									break;
								case 10:
									String str = new File(uri).getParent();
									if (new File(str + "/META-INF").exists())
									{
										str = new File(uri).getParent();
										final String command10 = new String(
											"/data/data/ru.unpro.apktool/lix/7z a -tzip ")
											+ "'"
											+ uri
											+ "' '"
											+ str
											+ "/META-INF'";
										threadWork(context,
												   getString(R.string.adding),
												   command10, 8);
									}
									else
										Toasty.warning(context,
													   getString(R.string.dir_not_exist),
													   Toast.LENGTH_LONG).show();
									break;
								case 11:
									final String command11 = new String(
										" sh /data/data/ru.unpro.apktool/mydata/apktool.sh if ")
										+ "'" + uri + "'";
									threadWork(
										context,
										getString(R.string.importing_framework),
										command11, 7);
									break;
								case 12:
									if (uri.endsWith(".apk"))
									{
										final String command12 = new String(" sh /data/data/ru.unpro.apktool/mydata/dex2jar/d2j-dex2jar.sh ")
											+ "'"
											+ uri
											+ "' -o '"
											+ uri.substring(0, uri.length() - 3)
											+ "jar'";
										threadWork(context, getString(R.string.making), command12, 6);
									}
									break;
								case 13:
									if (uri.endsWith(".jar"))
									{
										final String command13 = new String(" sh /data/data/ru.unpro.apktool/mydata/dex2jar/d2j-jar2dex.sh ")
											+ "'"
											+ uri
											+ "' -o '"
											+ uri.substring(0, uri.length() - 3)
											+ "dex'";
										threadWork(context, getString(R.string.making), command13, 6);
									}
									break;
								case 14:
									return;
							}
						}
					})
					.setTitle(R.string.choose_action);
				AlertDialog ADDCD = ADBDCD.create();
				WA = ADDCD.getWindow();
				WL = WA.getAttributes();
				WL.gravity = adgravity;
				WA.setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				WA.setAttributes(WL);
				ADDCD.show();
				return ADDCD;
			case COMPILE:
				AlertDialog.Builder ADBCMP = new AlertDialog.Builder(this)
					.setItems(
					R.array.comp_array, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which)
						{
							switch (which)
							{
								case 0:
									if (uri.endsWith("_src"))
									{
										final String command0 = new String(
											" ak_apktool b ")
											+ "-a '/data/local/aapt7.0' "
											+ "-f "
											+ " '" + uri + "' "
											+ "-o '" + uri + ".apk'";
										threadWork(context,
												   getString(R.string.recompiling),
												   command0, 2);
									}
									else if (uri.endsWith("_odex"))
									{
										final String command0 = new String(
											" ak_smali -a ")
											+ apicode
											+ " '"
											+ uri
											+ "' -o '"
											+ uri.substring(0, uri.length() - 5)
											+ ".dex'";
										threadWork(context,
												   getString(R.string.recompiling),
												   command0, 2);
									}
									else if (uri.endsWith("_dex"))
									{
										final String command0 = new String(
											" ak_smali -a ")
											+ apicode
											+ " '"
											+ uri
											+ "' -o '"
											+ uri.substring(0, uri.length() - 4)
											+ ".dex'";
										threadWork(context,
												   getString(R.string.recompiling),
												   command0, 2);
									}
									break;
								case 1:
									currentParent = new File(uri);
									currentFiles = currentParent.listFiles();
									inflateListView(currentFiles);
									break;
								case 2:
									return;
							}
						}
					})
					.setTitle(R.string.choose_action);
				AlertDialog ADCMP = ADBCMP.create();
				WA = ADCMP.getWindow();
				WL = WA.getAttributes();
				WL.gravity = adgravity;

				WA.setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				WA.setAttributes(WL);
				ADCMP.show();
				return ADCMP;
			case DEODEX:
				AlertDialog.Builder ADBDDX = new AlertDialog.Builder(this)
					.setItems(
					R.array.deodex_array,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which)
						{
							switch (which)
							{
								case 0:
									final String command0 = new String(
										" sh /data/data/per.pqy.apktool/apktool/baksmali.sh -x -a ")
										+ apicode
										+ " '"
										+ uri
										+ "' -o '"
										+ uri.substring(0, uri.length() - 5)
										+ "_odex'";
									threadWork(context,
											   getString(R.string.decompiling),
											   command0, 5);
									break;
								case 1:
									return;
							}
						}
					})
					.setTitle(R.string.choose_action);
				AlertDialog ADDDX = ADBDDX.create();
				WA = ADDDX.getWindow();
				WL = WA.getAttributes();
				WL.gravity = adgravity;

				WA.setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				WA.setAttributes(WL);
				ADDDX.show();
				return ADDDX;
			case DECDEX:
				AlertDialog.Builder ADBDCDX = new AlertDialog.Builder(this)
					.setItems(
					R.array.decdex_array,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which)
						{
							switch (which)
							{
								case 0:
									final String command0 = new String(
										" sh /data/data/per.pqy.apktool/apktool/baksmali.sh '")
										+ uri
										+ "' -o '"
										+ uri.substring(0, uri.length() - 4)
										+ "_dex'";
									threadWork(context,
											   getString(R.string.decompiling),
											   command0, 3);
									break;
								case 1:
									String apkFile = uri.substring(0,
																   uri.length() - 3) + "apk";
									if (new File(apkFile).exists())
									{
										apkFile = uri.substring(0, uri.length() - 3)
											+ "apk";
										RunExec.Cmd(shell,
													new String(" mv '") + uri + "' '"
													+ new File(uri).getParent()
													+ "/classes.dex'");
										final String command1 = new String(
											" /data/data/per.pqy.apktool/lix/7z a -tzip '"
											+ apkFile + "' '"
											+ new File(uri).getParent()
											+ "/classes.dex'");
										threadWork(context,
												   getString(R.string.adding),
												   command1, 9);
									}
									else
										Toasty.warning(context,
													   getString(R.string.apk_not_exist),
													   Toast.LENGTH_LONG).show();
									break;
								case 2:
									String jarFile = uri.substring(0,
																   uri.length() - 3) + "jar";
									if (new File(jarFile).exists())
									{
										jarFile = uri.substring(0, uri.length() - 3)
											+ "jar";
										RunExec.Cmd(shell,
													new String(" mv '") + uri + "' '"
													+ new File(uri).getParent()
													+ "/classes.dex'");
										final String command2 = new String(
											" /data/data/per.pqy.apktool/lix/7z a -tzip '"
											+ jarFile + "' '"
											+ new File(uri).getParent()
											+ "/classes.dex'");
										threadWork(context,
												   getString(R.string.adding),
												   command2, 9);
									}
									else
										Toasty.warning(context,
													   getString(R.string.jar_not_exist),
													   Toast.LENGTH_LONG).show();
									break;
								case 3:
									return;
							}
						}
					})
					.setTitle(R.string.choose_action);
				AlertDialog ADDCDX = ADBDCDX.create();
				WA = ADDCDX.getWindow();
				WL = WA.getAttributes();
				WL.gravity = adgravity;
				WA.setBackgroundDrawableResource(R.drawable.lightdialog_bg);
				WA.setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				WA.setAttributes(WL);
				ADDCDX.show();
				return ADDCDX;
			case LONGPRESS:
				AlertDialog.Builder ADBLP = new AlertDialog.Builder(this)
					.setItems(
					R.array.longpress_array,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which)
						{
							switch (which)
							{
								case 0:
									final EditText et = new EditText(
										context);
									et.setText(new File(uri).getName());
									new AlertDialog.Builder(context)
										.setTitle(getString(R.string.new_name))
										.setView(et)
										.setPositiveButton(
										getString(R.string.ok),
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
												DialogInterface dialog,
												int which)
											{
												// TODO Auto-generated
												// method stub
												String newName = et
													.getText()
													.toString();
												newName = currentParent
													+ "/" + newName;
												RunExec.Cmd(
													shell,
													" chmod 777 "
													+ currentParent);
												new File(uri)
													.renameTo(new File(
																  newName));
												currentFiles = currentParent
													.listFiles();
												inflateListView(currentFiles);
											}
										})
										.setNegativeButton(
										getString(R.string.cancel),
										null).show();
									break;
								case 1:
									new AlertDialog.Builder(context)
										.setTitle(
										getString(R.string.want_to_delete))
										.setPositiveButton(
										getString(R.string.ok),
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
												DialogInterface dialog,
												int which)
											{
												// TODO Auto-generated
												// method stub
												final String command = new String(
													" rm -r '")
													+ uri + "'";
												threadWork(
													context,
													getString(R.string.deleting),
													command, 10);
											}
										})
										.setNegativeButton(
										getString(R.string.cancel),
										null).show();
									break;
								case 2:
									RunExec.Cmd(shell, new String(" chmod 777 '")
												+ uri + "'");
									break;
								case 3:
									File file = new File(
										"/data/data/per.pqy.apktool/apktool");
									file.delete();
									RunExec.Cmd(shell, new String(" ln -s '") + uri
												+ "' /data/data/per.pqy.apktool/apktool");
									extractData();
									break;
								case 4:
									return;
							}
						}
					})
					.setTitle(R.string.choose_action);
				AlertDialog ADLP = ADBLP.create();
				WA = ADLP.getWindow();
				WL = WA.getAttributes();
				WL.gravity = adgravity;

				WA.setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				WA.setAttributes(WL);
				ADLP.show();
				return ADLP;
			case UNPACKIMG:
				AlertDialog.Builder ADBUNP = new AlertDialog.Builder(this)
					.setItems(
					R.array.unpackimg, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which)
						{
							switch (which)
							{
								case 0:
									if (uri.endsWith("boot.img"))
									{
										File tmp = new File(uri);
										final String command0 = new String(
											" busybox sh /data/data/per.pqy.apktool/apktool/unpackimg.sh '")
											+ tmp.getParent()
											+ "' boot.img new.img mt65xx";
										threadWork(context,
												   getString(R.string.extracting),
												   command0, 6);
									}
									else
									{
										File tmp = new File(uri);
										final String command0 = new String(
											" busybox sh /data/data/per.pqy.apktool/apktool/unpackimg.sh '")
											+ tmp.getParent()
											+ "' recovery.img new.img mt65xx";
										threadWork(context,
												   getString(R.string.extracting),
												   command0, 6);
									}
									break;
								case 1:
									if (uri.endsWith("boot.img"))
									{
										File tmp = new File(uri);
										final String command1 = new String(
											" busybox sh /data/data/per.pqy.apktool/apktool/unpackimg.sh '")
											+ tmp.getParent()
											+ "' boot.img new.img";
										threadWork(context,
												   getString(R.string.extracting),
												   command1, 6);
									}
									else
									{
										File tmp = new File(uri);
										final String command1 = new String(
											" busybox sh /data/data/per.pqy.apktool/apktool/unpackimg.sh '")
											+ tmp.getParent()
											+ "' recovery.img new.img";
										threadWork(context,
												   getString(R.string.extracting),
												   command1, 6);
									}
									break;
								case 2:
									return;
							}
						}
					})
					.setTitle(R.string.choose_action);
				AlertDialog ADUNP = ADBUNP.create();
				WA = ADUNP.getWindow();
				WL = WA.getAttributes();
				WL.gravity = adgravity;
				WA.setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				WA.setAttributes(WL);
				ADUNP.show();
				return ADUNP;
			case REPACKIMG:
				AlertDialog.Builder ADBREP = new AlertDialog.Builder(this)
					.setItems(
					R.array.repackimg, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which)
						{
							switch (which)
							{
								case 0:

									File tmp = new File(uri);
									final String command0 = new String(
										" busybox  sh /data/data/per.pqy.apktool/apktool/repackimg.sh '")
										+ tmp.getParent() + "' new.img mtk";
									threadWork(context,
											   getString(R.string.compressing),
											   command0, 6);

									break;
								case 1:

									File tmp1 = new File(uri);
									final String command1 = new String(
										" busybox sh /data/data/per.pqy.apktool/apktool/repackimg.sh '")
										+ tmp1.getParent() + "' new.img";
									threadWork(context,
											   getString(R.string.compressing),
											   command1, 6);

									break;
								case 2:
									currentParent = new File(uri);
									currentFiles = currentParent.listFiles();
									inflateListView(currentFiles);
									break;
								case 3:
									return;
							}
						}
					})
					.setTitle(R.string.choose_action);
				AlertDialog ADREP = ADBREP.create();
				WA = ADREP.getWindow();
				WL = WA.getAttributes();
				WL.gravity = adgravity;

				WA.setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				WA.setAttributes(WL);
				ADREP.show();
				return ADREP;
			case TASK:
				AlertDialog.Builder ADBTASK = new AlertDialog.Builder(this)
					.setItems(
					R.array.Task, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which)
						{
							switch (which)
							{
								case 0:
									if (tasks[0])
									{
										dialogs[0].show();
									}
									else
									{
										Toasty.info(
											context,
											getString(R.string.cur_task_not_run),
											Toast.LENGTH_SHORT).show();
									}
									break;
								case 1:
									if (tasks[1])
									{
										dialogs[1].show();
									}
									else
									{
										Toasty.info(
											context,
											getString(R.string.cur_task_not_run),
											Toast.LENGTH_SHORT).show();
									}
									break;
								case 2:
									if (tasks[2])
									{
										dialogs[2].show();
									}
									else
									{
										Toasty.info(
											context,
											getString(R.string.cur_task_not_run),
											Toast.LENGTH_SHORT).show();
									}
									break;
								case 3:
									if (tasks[3])
									{
										dialogs[3].show();
									}
									else
									{
										Toasty.info(
											context,
											getString(R.string.cur_task_not_run),
											Toast.LENGTH_SHORT).show();
									}
							}
						}
					})
					.setTitle(R.string.choose_action);
				AlertDialog ADTASK = ADBTASK.create();
				WA = ADTASK.getWindow();
				WL = WA.getAttributes();
				WL.gravity = adgravity;

				WA.setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				WA.setAttributes(WL);
				ADTASK.show();
				return ADTASK;
			case JAVA:
				AlertDialog.Builder ADBJAVA = new AlertDialog.Builder(this)
					.setItems(
					R.array.java,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which)
						{
							switch (which)
							{
								case 0:
									final String command0 = new String(
										" /data/data/ru.unpro.apktool/lix/jvm/java-7-openjdk-armhf/bin/javac '") + uri + "'";
									threadWork(context,
											   getString(R.string.recompiling),
											   command0, 5);
									break;
								case 1:
									return;
							}
						}
					})
					.setTitle(R.string.choose_action);
				AlertDialog ADJAVA = ADBJAVA.create();
				WA = ADJAVA.getWindow();
				WL = WA.getAttributes();
				WL.gravity = adgravity;

				WA.setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				WA.setAttributes(WL);
				ADJAVA.show();
				return ADJAVA;
			case CLASS:
				AlertDialog.Builder ADBCLS = new AlertDialog.Builder(this)
					.setItems(
					R.array.Class,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which)
						{
							switch (which)
							{
								case 0:
									File tmp = new File(uri);
									String parent,file;
									parent = tmp.getParent();
									file = tmp.getName();
									final String command0 = new String(
										" /data/data/per.pqy.apktool/lix/jvm/java-7-openjdk-armhf/jre/bin/java -cp '")
										+ parent + "' '" + file.substring(0, file.length() - 6) + "'";
									threadWork(context,
											   getString(R.string.running),
											   command0, 6);
									break;
								case 1:
									return;
							}
						}
					})
					.setTitle(R.string.choose_action);
				AlertDialog ADCLS = ADBCLS.create();
				WA = ADCLS.getWindow();
				WL = WA.getAttributes();
				WL.gravity = adgravity;
				WA.setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				WA.setAttributes(WL);
				ADCLS.show();
				return ADCLS;
		}
		return null;
	}
}
