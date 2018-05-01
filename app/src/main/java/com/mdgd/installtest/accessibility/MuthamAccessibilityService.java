package com.mdgd.installtest.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class MuthamAccessibilityService extends AccessibilityService {

    public static final String ACTION_ACCESSIBILITY_STARTED = "action_accessibility_started";
    static final String ACTION_REMOVE_WHATSAPP_VIEW = "ACTION_REMOVE_WHATSAPP_VIEW";

    public String lastPackage = null;
    private static MuthamAccessibilityService instance;

    public static Intent getIntent(Context context){
        return new Intent(context, MuthamAccessibilityService.class);
    }

    public static Intent getRemoveIntent(Context context) {
        Intent intent = new Intent(context, MuthamAccessibilityService.class);
        intent.setAction(ACTION_REMOVE_WHATSAPP_VIEW);
        return intent;
    }

    public static boolean globalBack(){
        if(isStopped()) {
            return false;
        }
        instance.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        return true;
    }

    public static boolean isStopped(){
        return instance == null;
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d("MuthamAccessibility", "onServiceConnected");
        instance = this;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("MuthamAccessibility", "onUnbind");
        instance = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onInterrupt() {
        Log.d("MuthamAccessibility", "onInterrupt");
    }

    @Override
    public void onDestroy() {
        Log.d("MuthamAccessibility", "onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("MuthamAccessibility", "onStartCommand " + startId);
        sendBroadcast(new Intent(ACTION_ACCESSIBILITY_STARTED));
        return START_STICKY; // super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {}

}
