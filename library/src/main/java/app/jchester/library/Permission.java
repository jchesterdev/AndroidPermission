package app.jchester.library;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Copyright (c) 2018 John Chester Isidoro
 */

public class Permission {

    private static final Random mIdGenerator = new Random();

    private final Context mContext;
    private final String[] mPermissions;
    private final short mRequestCode;

    private PermissionCallback mCallback;

    public interface PermissionCallback {

        void onPermissionsGranted(Permission permission);
        void onPermissionsDenied(Permission permission);
    }

    public Permission(@NonNull Context context, @NonNull String[] requiredPermissions) {
        mContext = context;
        mPermissions = verifyPermissions(context, requiredPermissions);
        mRequestCode = (short) mIdGenerator.nextInt(Short.MAX_VALUE + 1);
    }

    private String[] verifyPermissions(Context context, String[] permissions) {
        List<String> permissionsInManifest = new ArrayList<>();
        PackageManager pm = context.getPackageManager();

        try {
            PackageInfo packageInfo = pm.getPackageInfo(
                    context.getPackageName(), PackageManager.GET_PERMISSIONS);
            if (packageInfo.requestedPermissions != null) {
                permissionsInManifest = Arrays.asList(packageInfo.requestedPermissions);
            }

        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        for (String permission : permissions) {
            if (!permissionsInManifest.contains(permission)) {
                throw new RuntimeException("Add " + permission + " in manifest!");
            }
        }

        return permissions;
    }

    public void executeWithPermission(@NonNull Fragment fragment,
                                      @NonNull PermissionCallback callback) {
        mCallback = callback;

        List<String> permissions = getDeniedPermissions();
        if (permissions.size() > 0) {
            fragment.requestPermissions(permissions.toArray(
                    new String[permissions.size()]), mRequestCode);
        } else {
            callback.onPermissionsGranted(this);
        }

    }

    public void executeWithPermission(@NonNull Activity activity,
                                      @NonNull PermissionCallback callback) {
        mCallback = callback;

        List<String> permissions = getDeniedPermissions();
        if (permissions.size() > 0) {
            ActivityCompat.requestPermissions(activity, permissions.toArray(
                    new String[permissions.size()]), mRequestCode);
        } else {
            callback.onPermissionsGranted(this);
        }
    }

    public boolean isGranted() {
        return getDeniedPermissions().size() == 0;
    }

    public List<String> getDeniedPermissions() {
        List<String> permissions = new ArrayList<>();

        for (String permission : getRequiredPermissions()) {
            if (ContextCompat.checkSelfPermission(mContext, permission)
                    != PackageManager.PERMISSION_GRANTED) {

                permissions.add(permission);
            }
        }

        return permissions;
    }

    public List<String> getGrantedPermissions() {
        List<String> permissions = new ArrayList<>();

        for (String permission : getRequiredPermissions()) {
            if (ContextCompat.checkSelfPermission(mContext, permission)
                    == PackageManager.PERMISSION_GRANTED) {

                permissions.add(permission);
            }
        }

        return permissions;
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (mCallback == null || requestCode != mRequestCode) {
            return;
        }

        if (permissions.length == 0) {
            mCallback.onPermissionsDenied(this);
            return;
        }

        boolean permissionsGranted = true;
        List<String> requiredPermissions = Arrays.asList(getRequiredPermissions());

        for (int i = 0; i < permissions.length; i++) {
            permissionsGranted &= requiredPermissions.contains(permissions[i]) &&
                    grantResults[i] == PackageManager.PERMISSION_GRANTED;
        }

        if (permissionsGranted) {
            mCallback.onPermissionsGranted(this);
        } else {
            mCallback.onPermissionsDenied(this);
        }
    }

    public String[] getRequiredPermissions() {
        return mPermissions;
    }
}
