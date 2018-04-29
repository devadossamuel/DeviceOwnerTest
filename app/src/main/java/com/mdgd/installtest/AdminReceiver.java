package com.mdgd.installtest;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Owner
 * on 01/04/2018.
 */
public class AdminReceiver extends DeviceAdminReceiver {

    public static final String ACTION_ADMIN_ENABLED = "action_admin_enabled";

    void showToast(Context context, int msgResId) {
        Toast.makeText(context, msgResId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProfileProvisioningComplete(Context context, Intent intent) {
        super.onProfileProvisioningComplete(context, intent);
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        context.sendBroadcast(new Intent(ACTION_ADMIN_ENABLED));
        showToast(context, R.string.admin_enabled);
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return context.getString(R.string.warning_admin_deactivation);
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        showToast(context, R.string.admin_disabled);
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        showToast(context, R.string.pwd_changed);
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        showToast(context, R.string.pwd_fail);
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        showToast(context, R.string.pwd_success);
    }
}
