//================================================================================================================================
//
// Copyright (c) 2015-2021 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
// EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
// and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
//
//================================================================================================================================

package com.njupt.zyhy;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import cn.easyar.CameraDevice;
import cn.easyar.Engine;
import cn.easyar.ImageTracker;

public class ARActivity extends Activity
{
    /*
    * Steps to create the key for this sample:
    *  1. login www.easyar.com
    *  2. create app with
    *      Name: HelloAR
    *      Package Name: cn.easyar.samples.helloar
    *  3. find the created item in the list and show key
    *  4. set key string bellow
    */
    private static String key = "RslLEkLaUw5avHJ8XNfdhEann4rK9eTLMOVjaXb7fTlC63skduZsaTmqdCJ37Woqd/1qLjK6KH9D73UqauQ2KGzlOmch5Xk4d+1qAGbxUS8hsilnIeRxKGbmay5wqiIQeKp6Pm3sdC5K7GtpOdNFZyH+eTlq6XY/cKoiECHrdyZu/XYid/E6Fi+qaCdi/H4kceVraTnTOjxq5nwkdPs6ZyHleSgh1TRpbud8Pm/ta2k50zo4ZuZrLi3BdSpk7Uw5YutzIm3vOmch+30lcO02CG/nbS9R7XskZOZxP2rndmkvqmsubft9ZVHteyRx7HElZKo0aXDtdjhmplcpae17P1f6eSho4XYsIaQ6OGbmay4t2205Zel7Llf6eSho4XYsIaQ6OGbmay4t22gqcft9GHPpbCJi5FUqc6o0aXDtdjhmplUkd+F3JVf6eSho4XYsIaQ6OGbmay4tzH0lcO1LO2L8cSpvxXk7IaQ6OGbmay4ty1kPV/p5KGjhdiwh1TRpZvBoInHtTCJu7Us/YuVoaTnmbSdvpDoicMR3KGLkOnFl6XQ4ZvU0MCHqbSVn5H0CZ/s6cViqeyRupnYhdvhsZXnxcDIh1TRpdelqImLmbDghskNpYOd1JnbmcT96qkVnIfh0Knfudzlu+zpxWKp5JWf6dyJnqkVnIeV3L3bkfTghskNpcO12OGamUSZi730fcel7IGrmf2kvqmsubft9ZUDkdz5n2n0obO92InfhdyUhpDo4ZuZrLi3afShs+nwibe86ZyH7fSVw7TYEYeJ9KHfcaipg43ElZKo0aXDtdjhmpks+ce55KGbcaipg43ElZKo0aXDtdjhmpks7YvprLlD4eT9q6XQGYvg6ZyH7fSVw7TYGbPxxJG3caipg43ElZKo0aXDtdjhmplwubft9GHPpbCJi5FUqc6o0aXDtdjhmplsKR9xqKmDjcSVkqkVnIe1gO2r6fR9q5X0Yd+l1OyGydj5v5DRpavtUJGDpdGk57nkncO1lZ3iqej5t7HQuSuxraTnTOmlepDo9YvpxKm38a2k50zoobOV1Pm3hbDIh1TRpc+R5P2XnaiZwqiIQIeF3OCHVNGlu53w+b+1raTnTOjhm5msuLcF1KmTtTDli63Mibe86ZyH7fSVw7TYIb+dtL1HteyRk5nE/aud2aS+qay5t+31lUe17JHHscSVkqjRpcO12OGamVylp7Xs/V/p5KGjhdiwhpDo4ZuZrLi3bbTll6XsuV/p5KGjhdiwhpDo4ZuZrLi3baCpx+30Yc+lsImLkVSpzqjRpcO12OGamVSR34XclV/p5KGjhdiwhpDo4ZuZrLi3MfSVw7Us7YvxxKm/FeTshpDo4ZuZrLi3LWQ9X+nkoaOF2LCHVNGlm8Ggice1MIm7tSz9i5WhpOeZtJ2+kOiJwxHcoYuQ6cWXpdDhm9UU2llldJqBDeJD4huLA+Z/eqbh9KnIKmpCyDiq3EhzGDi2GfF8ifBEM8z2V3ss4/FNAbvvIvK/x2U6akdwYiwII0/NLT1Zu0udRdYGFiaV+pNZkHxrBXZOKwJ3z48+fpaeKI8t8B5SHjI2vaK6DPu6bUGgKIyTwbJCWDg18lgKvOeHZq9ZIbci64X0sUWoJwiQowa5wpkkNkmrYDVfcNz4BlYKb/9CVEwpOdAqfynNyHA2+Xkkm0NYw31+PuurITa2e5c1WaLcnqnb+jUmOpb6FJYDC2J485FcRgPbXffgVLC+iCY9ojywUqqnpF0jS3qKVC4TEsEa4DMsAoe6ZA4gYSw==";
    private GLView glView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!Engine.initialize(this, key)) {
            Log.e("HelloAR", "Initialization Failed.");
            Toast.makeText(ARActivity.this, Engine.errorMessage(), Toast.LENGTH_LONG).show();
            return;
        }
        if (!CameraDevice.isAvailable()) {
            Toast.makeText(ARActivity.this, "CameraDevice not available.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!ImageTracker.isAvailable()) {
            Toast.makeText(ARActivity.this, "ImageTracker not available.", Toast.LENGTH_LONG).show();
            return;
        }

        glView = new GLView(this);

        requestCameraPermission(new PermissionCallback() {
            @Override
            public void onSuccess() {
                ((ViewGroup) findViewById(R.id.preview)).addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }

            @Override
            public void onFailure() {
            }
        });
    }

    private interface PermissionCallback
    {
        void onSuccess();
        void onFailure();
    }
    private HashMap<Integer, PermissionCallback> permissionCallbacks = new HashMap<Integer, PermissionCallback>();
    private int permissionRequestCodeSerial = 0;
    @TargetApi(23)
    private void requestCameraPermission(PermissionCallback callback)
    {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                int requestCode = permissionRequestCodeSerial;
                permissionRequestCodeSerial += 1;
                permissionCallbacks.put(requestCode, callback);
                requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
            } else {
                callback.onSuccess();
            }
        } else {
            callback.onSuccess();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (permissionCallbacks.containsKey(requestCode)) {
            PermissionCallback callback = permissionCallbacks.get(requestCode);
            permissionCallbacks.remove(requestCode);
            boolean executed = false;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    executed = true;
                    callback.onFailure();
                }
            }
            if (!executed) {
                callback.onSuccess();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (glView != null) { glView.onResume(); }
    }

    @Override
    protected void onPause()
    {
        if (glView != null) { glView.onPause(); }
        super.onPause();
    }
}
