# MyCalendar
This is a Calendar App.You can record your trifle or your event and don't forget set date.
It can help you calculate how many days are there from the target.
And in this day the App can notice you.
This is my First App

2017.12.11 此次更新仅对界面进行了一定的美化。今日版本能在虚拟机上运行，但是生成安装包的过程中报错
Error:Execution failed for task ':app:transformDexWithDexForRelease'. > com.android.build.api.transform.TransformException: com.android.ide.common.process.ProcessException: java.util.concurrent.ExecutionException: com.android.dex.DexException: Multiple de

添加了一句  multiDexEnabled true，可以生成安装包，但是安装到手机上应用闪退，应用大小为3.2M。
删除此句安装包生成成功，并且可以安装，大小为2.2M。
