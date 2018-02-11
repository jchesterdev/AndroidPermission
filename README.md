# AndroidPermission
Simplifies android permission use.

How to use this library
=======

1. Add jitpack.io in root build.gradle at the end of repositories:
```groovy
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```
2. Add the dependency
```groovy
  dependencies {
        compile 'com.github.jchesterdev:AndroidPermission:1.0.1'
  }
```
3. Create an instance in you Activity or Fragment
```java
  private Permission permission;
    
  @Override
  protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // You can add multiple permission request
        permission = new Permission(this,
                new String[] {Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE});
    }
```

4. Call `permission.executeWithPermission`
```java
    permission.executeWithPermission(this, new Permission.PermissionCallback() {
        
        // This will be called if ALL of your permissions are granted
        @Override
        public void onPermissionsGranted(Permission permission) {
            permissionStatus.setText("All permissions are granted");
        }

        // This will be called if there's AT LEAST ONE permission that isn't granted
        @Override
        public void onPermissionsDenied(Permission permission) {
            permissionStatus.setText("Denied permissions: " + permission.getDeniedPermissions());
        }
   });
```

5. Add `permission.onRequestPermissionResult`
```java
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        // Do not forget to add this.
        permission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
```

6. Add permissions in manifest.xml
