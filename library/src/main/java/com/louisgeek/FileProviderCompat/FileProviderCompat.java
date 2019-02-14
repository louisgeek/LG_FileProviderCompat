package com.louisgeek.FileProviderCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;

import java.io.File;
import java.util.List;

import androidx.core.content.FileProvider;

/**
 * Created by louisgeek on 2019/1/23.
 */
public class FileProviderCompat {
    public static Uri getUriFromFile(Context context, File file) {
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= 24) {
            fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".FileProvider", file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }


    public static void setDataAddFlags(Context context,
                                       Intent intent,
                                       File file) {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setData(FileProviderCompat.getUriFromFile(context, file));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            intent.setData(Uri.fromFile(file));
        }
    }

    public static void setDataAndTypeAddFlags(Context context,
                                              Intent intent,
                                              String type,
                                              File file) {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setDataAndType(FileProviderCompat.getUriFromFile(context, file), type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
    }

    public static void grantPermissions(Context context, Intent intent, Uri uri) {
        int flag = Intent.FLAG_GRANT_READ_URI_PERMISSION;
//        int flag = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
        intent.addFlags(flag);
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, flag);
        }
    }
}
