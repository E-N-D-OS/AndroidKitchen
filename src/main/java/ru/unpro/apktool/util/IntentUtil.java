package ru.unpro.apktool.util;

import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.support.v4.content.*;
import android.webkit.*;
import java.io.*;
import java.util.*;
import ru.unpro.apktool.*;

public class IntentUtil
{
	public static Intent getInstallAppIntent(Context context, String filePath)
	{
		return getInstallAppIntent(context, new File(filePath));
	}

	public static Intent getInstallAppIntent(Context context, File file)
	{
		if (file == null) return null;
		Intent intent = new Intent(Intent.ACTION_VIEW);
		String type;

//		if (Build.VERSION.SDK_INT > 23)
//		{
            type = "application/vnd.android.package-archive";
//		}
//		else
//		{
//			type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtil.getExtension(file));
//		}
//
//		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N)
//		{
//			intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//			Uri contentUri = FileProvider
//				.getUriForFile(context, context.getApplicationContext().getPackageName(), file);
//			intent.setData(contentUri);
//		}
//		else
//			{
			intent.setDataAndType(Uri.fromFile(file), type);
//			}
		return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	}
	
	public static Intent getIntent(Context context, File file, String type)
	{
		if (file == null) return null;
		Intent intent = new Intent(Intent.ACTION_VIEW);
		return intent.setDataAndType(Uri.fromFile(file), type);
	}

	public static Intent getUninstallAppIntent(String packageName)
	{
		Intent intent = new Intent(Intent.ACTION_DELETE);
		intent.setData(Uri.parse("package:" + packageName));
		return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	}

	public static Intent getLaunchAppIntent(Context context, String packageName)
	{
		return context.getPackageManager().getLaunchIntentForPackage(packageName);
	}

	public static Intent getAppDetailsSettingsIntent(String packageName)
	{
		Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
		intent.setData(Uri.parse("package:" + packageName));
		return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	}

	public static Intent getShareTextIntent(String content)
	{
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, content);
		return intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	}

	public static Intent getShareImageIntent(String content, String imagePath)
	{
		return getShareImageIntent(content, new File(imagePath));
	}

	public static Intent getShareImageIntent(String content, File image)
	{
		if (!FileUtil.isExists(image))
		{
			return null;
		}

		return getShareImageIntent(content, Uri.fromFile(image));
	}

	public static Intent getShareImageIntent(String content, Uri uri)
	{
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_TEXT, content);
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		intent.setType("image/*");
		return intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	}

	public static Intent getComponentIntent(String packageName, String className)
	{
		return getComponentIntent(packageName, className, null);
	}

	public static Intent getComponentIntent(String packageName, String className, Bundle bundle)
	{
		Intent intent = new Intent(Intent.ACTION_VIEW);
		if (bundle != null) intent.putExtras(bundle);
		ComponentName cn = new ComponentName(packageName, className);
		intent.setComponent(cn);
		return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	}

	public static Intent getShutdownIntent()
	{
		Intent intent = new Intent(Intent.ACTION_SHUTDOWN);
		return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	}

	public static Intent getDialIntent(String phoneNumber)
	{
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
		return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	}

	public static Intent getCallIntent(String phoneNumber)
	{
		Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNumber));
		return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	}

	public static Intent getSendSmsIntent(String phoneNumber, String content)
	{
		Uri uri = Uri.parse("smsto:" + phoneNumber);
		Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
		intent.putExtra("sms_body", content);
		return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	}

	public static Intent getCaptureIntent(Uri outUri)
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
		return intent
			.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
	}

	public static List<ResolveInfo> queryIntentActivities(Context context, Intent intent)
	{
		PackageManager packageManager = context.getPackageManager();
		return packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
	}
}
