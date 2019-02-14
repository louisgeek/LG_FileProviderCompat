# FileProviderCompat

Android 7、8 FileProvider 兼容适配

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```


Step 2. Add the dependency  [![](https://jitpack.io/v/louisgeek/FileProviderCompat.svg)](https://jitpack.io/#louisgeek/FileProviderCompat)

	dependencies {
	        compile 'com.github.louisgeek:FileProviderCompat:x.x.x'
	}



···
installApk(this, "/storage/emulated/0/Download/mytest-6.apk");
···

···

   /**
     * @param filePath not startWith  file://  or  content://
     *                 like  /storage/emulated/0/Download/mytest-6.apk
     */
    public void installApk(final FragmentActivity fragmentActivity, String filePath) {
        if (fragmentActivity == null) {
            Log.e(TAG, "installApk: fragmentActivity is null");
            return;
        }
        if (TextUtils.isEmpty(filePath)) {
            Log.e(TAG, "installApk: error");
            return;
        }
        //
        try {
            //提升一下文件的读写权限,否则在安装的时候会出现apk解析失败的页面
            Runtime.getRuntime().exec("chmod 777 " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //兼容8.0 未知应用安装权限放到了单个应用范畴了
            // xml 必须加 android.permission.REQUEST_INSTALL_PACKAGES 权限
            Log.e(TAG, "installN: >= Build.VERSION_CODES.O " + filePath);
            boolean canRequestPackageInstalls = fragmentActivity.getPackageManager().canRequestPackageInstalls();
            if (canRequestPackageInstalls) {
                //如果已经勾选了 即可和 8以下 一样安装
                installApkCompat(fragmentActivity, filePath);
            } else {
                //设置-允许安装未知来源
                new AlertDialog.Builder(fragmentActivity)
                        .setTitle("温馨提示")
                        .setMessage("安装应用需要打开未知来源权限，请去设置中开启权限")
                        .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /* Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                                fragmentActivity.startActivityForResult(intent, REQUEST_CODE_ACTION_MANAGE_UNKNOWN_APP_SOURCES);*/
                                String packageName = fragmentActivity.getPackageName();
                                //直接跳转到对应APP的未知来源权限设置界面
                                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + packageName));
                                fragmentActivity.startActivityForResult(intent, REQUEST_CODE_ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                            }
                        }).create()
                        .show();
            }
        } else {
            installApkCompat(fragmentActivity, filePath);
        }
    }

    private void installApkCompat(FragmentActivity fragmentActivity, String filePath) {
        File file = new File(filePath);
        if (fragmentActivity == null) {
            Log.e(TAG, "installCompat: fragmentActivity is null");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //here
        FileProviderCompat.setDataAndTypeAddFlags(fragmentActivity, intent, APK_MIME_TYPE, file);
        fragmentActivity.startActivity(intent);
//        fragmentActivity.startActivityForResult(intent, REQUEST_CODE_INSTALL_APK);
    }
	
···