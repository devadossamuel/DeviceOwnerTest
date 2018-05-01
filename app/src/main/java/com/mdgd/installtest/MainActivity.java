package com.mdgd.installtest;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int DEVICE_ADMIN_ADD_RESULT_ENABLE = 1201;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
             if(AdminReceiver.ACTION_ADMIN_ENABLED.equals(action)){
                Toast.makeText(context, "Admin: success", Toast.LENGTH_SHORT).show();
            }
            else if("pkgInstalled".equals(action)){
                Toast.makeText(context, "Installed", Toast.LENGTH_SHORT).show();
                System.out.println("Installed");
            }
            else if("uninstalled".equals(action)){
                Toast.makeText(context, "Uninstalled", Toast.LENGTH_SHORT).show();
                System.out.println("Uninstalled");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.removeOwner).setOnClickListener(this);
        findViewById(R.id.install).setOnClickListener(this);
        findViewById(R.id.uninstall).setOnClickListener(this);

        findViewById(R.id.enableFactory).setOnClickListener(this);
        findViewById(R.id.disableFactory).setOnClickListener(this);

        findViewById(R.id.setProxy).setOnClickListener(this);
        findViewById(R.id.isProxySet).setOnClickListener(this);
        findViewById(R.id.removeProxy).setOnClickListener(this);

        requestAdmin();
    }

    private void requestAdmin() {
        DevicePolicyManager dpm = getDpm();
        if(!(dpm != null && dpm.isDeviceOwnerApp(MyApp.getInstance().getPackageName()))) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, getAdminComponent());
            startActivityForResult(intent, DEVICE_ADMIN_ADD_RESULT_ENABLE);
        }
    }

    private ComponentName getAdminComponent() {
        return new ComponentName(MyApp.getInstance(), AdminReceiver.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DEVICE_ADMIN_ADD_RESULT_ENABLE) {
            Toast.makeText(this, Activity.RESULT_OK == resultCode ? "Success" : "Fail", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(AdminReceiver.ACTION_ADMIN_ENABLED);
        filter.addAction("pkgInstalled");
        filter.addAction("uninstalled");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(R.id.install == id){
            File dataDir = getExternalCacheDir();
            System.out.println(dataDir.getAbsolutePath());
            installPackage(this, "com.my.tefilathaderch", "/storage/emulated/0/Android/data/com.mdgd.installtest/cache/tefilathaderch.apk");
        }
        else if(R.id.removeOwner == id){
            getDpm().clearDeviceOwnerApp(getPackageName());
        }
        else if(R.id.uninstall == id){
            uninstallPackage(this, "com.appstore");
        }
        else if(R.id.enableFactory == id) {
            DevicePolicyManager dpm = getDpm();
            if (dpm == null){
                Toast.makeText(this, "DPM is null", Toast.LENGTH_SHORT).show();
            }
            else{
                dpm.addUserRestriction(getAdminComponent(), UserManager.DISALLOW_FACTORY_RESET);
            }
        }
        else if(R.id.disableFactory == id){
            DevicePolicyManager dpm = getDpm();
            if (dpm == null){
                Toast.makeText(this, "DPM is null", Toast.LENGTH_SHORT).show();
            }
            else{
                dpm.clearUserRestriction(getAdminComponent(), UserManager.DISALLOW_FACTORY_RESET);
            }
        }
        else if(R.id.setProxy == id){
            final DevicePolicyManager dpm = getDpm();
            final Method m = getSetGlobalProxyMethod();
            if(m != null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            m.invoke(dpm, getAdminComponent(), new Proxy(Proxy.Type.HTTP, new InetSocketAddress("159.203.84.241", 3128)), new ArrayList<>());
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
        else if(R.id.removeProxy == id){
            final DevicePolicyManager dpm = getDpm();
            final Method m = getSetGlobalProxyMethod();
            if(m != null){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                m.invoke(dpm, getAdminComponent(), Proxy.NO_PROXY, new ArrayList<>());
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
            }
        }
        else if(R.id.isProxySet == id){
            ActivityManager amService = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
            try {
                // clearApplicationUserData [class java.lang.String, interface android.content.pm.IPackageDataObserver, int]
                int uid = 0;
                try {
                    uid = this.getPackageManager().getApplicationInfo("com.mutham", 0).uid;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                Class cls = Class.forName("android.app.ActivityManagerNative");
                Method method = cls.getDeclaredMethod("getDefault");
                Object amNative = method.invoke(cls);
                method = amNative.getClass().getMethod("clearApplicationUserData", String.class, Class.forName("android.content.pm.IPackageDataObserver"), int.class);
                method.invoke(amNative, "com.mutham", null, uid);
//                for (Method m : methods){
//                    Log.d("TEST", "M " + m.getName() + " " + Arrays.toString(m.getParameterTypes()));
//                }
//                Method method = ActivityManager.class.getDeclaredMethod("clearApplicationUserData", String.class, Class.forName("android.content.pm.IPackageDataObserver"));
//                method.invoke(amService, "com.mutham", null);
            }
            catch (Throwable e){
                e.printStackTrace();
            }
//            DevicePolicyManager dpm = getDpm();
//            Method[] declaredMethods = DevicePolicyManager.class.getDeclaredMethods();
//            Method m = null;
//            int size = declaredMethods.length;
//            for(int i = 0; i < size; i++){
//                if("getGlobalProxyAdmin".equals(declaredMethods[i].getName())){
//                    m = declaredMethods[i];
//                    break;
//                }
//            }
//            if(m != null){
//                try {
//                    Object adminComponent = m.invoke(dpm);
//                    if(adminComponent != null){
//                        System.out.println(adminComponent);
//                    }
//                }
//                catch (Throwable e){
//                    e.printStackTrace();
//                }
//            }
        }
    }

    private Method getSetGlobalProxyMethod(){
        Method[] declaredMethods = DevicePolicyManager.class.getDeclaredMethods();
        Method m = null;
        int size = declaredMethods.length;
        for(int i = 0; i < size; i++){
            if("setGlobalProxy".equals(declaredMethods[i].getName())){
                m = declaredMethods[i];
                break;
            }
        }
        return m;
    }

    private boolean uninstallPackage(Context context, String packageName) {
        PackageManager packageManger = context.getPackageManager();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            PackageInstaller packageInstaller = packageManger.getPackageInstaller();
            PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
            params.setAppPackageName(packageName);
            int sessionId = 0;
            try {
                sessionId = packageInstaller.createSession(params);
            }
            catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            packageInstaller.uninstall(packageName, PendingIntent.getBroadcast(context, sessionId, new Intent("uninstalled"), 0).getIntentSender());
            return true;
        }
        System.err.println("old sdk");
        return false;
    }

    private boolean installPackage(Context context, String packageName, String packagePath) {
        PackageManager packageManger = context.getPackageManager();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            PackageInstaller packageInstaller = packageManger.getPackageInstaller();
            PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
            params.setAppPackageName(packageName);
            try {
                int sessionId = packageInstaller.createSession(params);
//                clearMyRestrictions();
                PackageInstaller.Session session = packageInstaller.openSession(sessionId);
                File file = new File(packagePath);
                OutputStream out = session.openWrite(packageName + ".apk", 0, file.length());
                readTo(file, out); // read the apk content and write it to out
                session.fsync(out);
                out.close();
                System.out.println("installing...");
                session.commit(PendingIntent.getBroadcast(context, sessionId, new Intent("pkgInstalled"), 0).getIntentSender());
                System.out.println("install request sent");
                return true;
            }
            catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        System.err.println("old sdk");
        return false;
    }

    public void addMyRestrictions() {
        getDpm().addUserRestriction(getAdminComponent(), UserManager.DISALLOW_INSTALL_APPS);
        getDpm().addUserRestriction(getAdminComponent(), UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES);
    }

    public void clearMyRestrictions() {
        getDpm().clearUserRestriction(getAdminComponent(), UserManager.DISALLOW_INSTALL_APPS);
        getDpm().clearUserRestriction(getAdminComponent(), UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES);
    }

    public DevicePolicyManager getDpm() {
        return (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    private void readTo(File packagePath, OutputStream out) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(packagePath));
        byte[] buffer = new byte[1024]; //  * 1024
        int c;
        while ((c = bis.read(buffer)) != -1){
            System.out.println("Bytes red " + c);
            out.write(buffer, 0, c);
        }
        bis.close();
    }
}
