# android-apk-download-install
安卓下载安装apk的各个版本适配，目前已适配到9.0。

##1、网络访问权限
```
<!--访问网络权限：下载安装包-->
<uses-permission android:name="android.permission.INTERNET"/>
```

##2、外部存储器权限
```
<!--写入外部存储器权限：写入安装包-->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```

###2.1、Android 6.0动态获取权限
 Android 6.0开始获取外部存储器权限需要动态获取。
 
###2.2、Android 8.0动态获取权限变化
####2.2.1、Android 8.0之前
Android 8.0之前，只要申请了同组权限中的一个，同组中的其他在清单文件中列出的权限也会被同时授予或拒绝。
####2.2.2、Android 8.0开始
Android 8.0开始，系统只会授予应用明确请求的权限，但是一旦用户为应用授予某个权限，则所有后续对该权限组中其他权限的请求都将被自动批准，但是还是需要去申请。

```
//TODO 此处如果为READ_EXTERNAL_STORAGE，则在Android 8.0会有Permission Denied错误出现
boolean write = (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
String[] permissions = new String[]{};
if (write) {
	//TODO 执行下载操作
} else {
    permissions = new String[permissions.length + 1];
    permissions[permissions.length - 1] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    ActivityCompat.requestPermissions(this, permissions, REQUEST_EXTERNAL_STORAGE);
}
```
```
/**
 * 处理权限请求结果
 *
 * @param requestCode  请求权限时传入的请求码，用于区别是哪一次请求的
 * @param permissions  所请求的所有权限的数组
 * @param grantResults 权限授予结果，和 permissions 数组参数中的权限一一对应，元素值为两种情况，如下:
 *                     授予: PackageManager.PERMISSION_GRANTED
 *                     拒绝: PackageManager.PERMISSION_DENIED
 */
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_EXTERNAL_STORAGE) {

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //TODO 执行下载操作

        }
    }
}
```

###2.3、读取和写入外部存储器的区别
```
 * TODO 当只用到读取外部存储器功能的时候只需要用到：READ_EXTERNAL_STORAGE。
 * TODO 当需要用到写入外部存储器功能的时候必须要用到：WRITE_EXTERNAL_STORAGE；此时包含了READ_EXTERNAL_STORAGE，读取外部存储器时不需要再申请READ_EXTERNAL_STORAGE。
```
##3、文件访问权限
###3.1、Android7.0文件提供器获取Uri
Android7.0开始获取文件的Uri需要使用FileProvider进行获取。
####3.1.1、AndroidManifest.xml
```
<!--TODO 此处需要注意Android 7.0：authorities要与获取文件uri的authority一致！-->
<provider
    android:name="android.support.v4.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths"/>
</provider>
```
####3.1.2、res/xml/file_paths.xml

```
<?xml version="1.0" encoding="utf-8"?>
<paths xmlnsandroid="http://schemas.android.com/apk/res/android">
    <!-- external-path代表的根目录-->
    <external-path
        name="file_path"
        path="/"/>
</paths>
```
####3.1.3、获取文件Uri
```java
/**
 * 获取文件的Uri地址
 *
 * @param context
 * @param file
 * @return
 */
public static Uri getUriForFile(Context context, File file) {
    Uri uri;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        /**
         * TODO 此处需要注意Android 7.0：authority要与AndroidManifest.xml中的android.support.v4.content.FileProvider定义的authorities一致！
         */
        uri = FileProvider.getUriForFile(context.getApplicationContext(), context.getPackageName() + ".fileprovider", file);
    } else {
        uri = Uri.fromFile(file);
    }
    return uri;
}
```
###3.2、Android8.0
```
/**
 * 安装外置存储器的apk
 *
 * @param context
 * @param file
 */
public static void installApk(Context context, File file) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //TODO Android 8.0需要增加此标志 FLAG_GRANT_READ_URI_PERMISSION，对目标应用临时授权该Uri所代表的文件，如果没有此标志，在Android8.0中将会出现解析包失败的错误
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    intent.setDataAndType(getUriForFile(context, file), "application/vnd.android.package-archive");
    context.startActivity(intent);
}

```


##4、应用安装权限
Android8.0开始安装应用需要请求安装包权限。
```
<!--请求安装包权限：Android 8.0 -->
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
```


