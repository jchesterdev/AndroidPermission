package app.jchester.androidpermission;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import app.jchester.library.Permission;

public class MainActivity extends AppCompatActivity {

    private Permission permission;
    private TextView permissionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permission = new Permission(this,
                new String[] {Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE});

        permissionStatus = findViewById(R.id.permission_status);

        findViewById(R.id.grant_permission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grantPermission();
            }
        });
    }

    private void grantPermission() {
        permission.executeWithPermission(this, new Permission.PermissionCallback() {
            @Override
            public void onPermissionsGranted(Permission permission) {
                permissionStatus.setText("All permissions are granted");
            }

            @Override
            public void onPermissionsDenied(Permission permission) {
                permissionStatus.setText("Denied permissions: " + permission.getDeniedPermissions());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
