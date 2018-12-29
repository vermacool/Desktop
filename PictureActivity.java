/***
 Copyright (c) 2015 CommonsWare, LLC

 Licensed under the Apache License, Version 2.0 (the "License"); you may
 not use this file except in compliance with the License. You may obtain
 a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.magictel.attendancemanagement.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.commonsware.cwac.cam2.AbstractCameraActivity;
import com.commonsware.cwac.cam2.BuildConfig;
import com.commonsware.cwac.cam2.CameraActivity;
import com.commonsware.cwac.cam2.CameraEngine;
import com.commonsware.cwac.cam2.Facing;
import com.commonsware.cwac.cam2.FlashMode;
import com.commonsware.cwac.cam2.FocusMode;
import com.commonsware.cwac.cam2.OrientationLockMode;
import com.commonsware.cwac.cam2.ZoomStyle;
import com.magictel.attendancemanagement.AttMgmtLogUtility;
import com.magictel.attendancemanagement.AttMgmtShPreferenceUtility;
import com.magictel.attendancemanagement.R;

import java.io.File;
import java.io.FileOutputStream;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static com.magictel.attendancemanagement.Globals.USER_TYPE_VALUE_HARDWARE;
import static com.magictel.attendancemanagement.Globals.USER_TYPE_VALUE_HARDWARE_ASM;
import static com.magictel.attendancemanagement.Globals.USER_TYPE_VALUE_SOFTWARE;
import static com.magictel.attendancemanagement.Globals.USER_TYPE_VALUE_SOFTWARE_ASM;


public class PictureActivity extends Activity implements View.OnClickListener {
    private static final int REQUEST_CAMERA = 1337;
    //private static final String TAG_PLAYGROUND=PictureFragment.class.getCanonicalName();
    private static final String TAG_RESULT = ResultFragment.class.getCanonicalName();
    private static final String STATE_OUTPUT =
            "com.commonsware.cwac.cam2.playground.PictureActivity.STATE_OUTPUT";
    private static final String AUTHORITY =
            BuildConfig.APPLICATION_ID + ".provider";
    boolean SHOP_CHECKED = false;
    boolean SALE_VALUE_CHECK = false;
    boolean PAYMENT_COLLECTED_CHECK = false;
    boolean REMARK_CHECKED = false;
    CheckBox check_saleValue, check_payment_collect, remark_check;
    Bitmap mBitmap;
    private String manufacturer = "HUAWEI";
    //private PictureFragment playground=null;
    private ResultFragment result = null;
    private Uri output = null;
    private String TAG = "PictureActivity";
    private FloatingActionButton ok, ok_hardware, cancel_hardware;
    private FloatingActionButton cancel;
    private RelativeLayout lower;
    private RelativeLayout lowerhardware;
    private EditText et_visit_place, et_visit_purpose,etCounterSize ,et_ShopName, et_CostValue, et_Remark, et_Pay_collected;
    private String shopNameText, saleValueText, paymentcollectedText, remarkText;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(LogUtils.crashlog_filename, LogUtils.log_dir, null, TAG));

        setContentView(R.layout.activity_picture);
        ok = (FloatingActionButton) findViewById(R.id.ok);
        cancel = (FloatingActionButton) findViewById(R.id.cancel);
        et_visit_place = (EditText) findViewById(R.id.visit_place);
        ok_hardware = (FloatingActionButton) findViewById(R.id.ok_hardware);
        cancel_hardware = (FloatingActionButton) findViewById(R.id.cancel_hardware);
        et_visit_purpose = (EditText) findViewById(R.id.visit_purpose);
        etCounterSize = (EditText) findViewById(R.id.counterSize);
        et_ShopName = (EditText) findViewById(R.id.txtShopName);
        et_CostValue = (EditText) findViewById(R.id.txtCostValue);
        et_Remark = (EditText) findViewById(R.id.et_Remark);
        et_Pay_collected = (EditText) findViewById(R.id.et_Pay_collected);
        lower = (RelativeLayout) findViewById(R.id.lower);
        lowerhardware = (RelativeLayout) findViewById(R.id.lowerhardware);

        SHOP_CHECKED = false;
        SALE_VALUE_CHECK = false;
        PAYMENT_COLLECTED_CHECK = false;
        REMARK_CHECKED = false;

        // get shared preference value
        int userTypeSataus = AttMgmtShPreferenceUtility.shPreferenceGetData(PictureActivity.this,
                AttMgmtShPreferenceUtility.ShpRetailUserTypeTag, AttMgmtShPreferenceUtility.ShpRetailUserTypeDefaultVal);
        if (userTypeSataus == USER_TYPE_VALUE_HARDWARE|| userTypeSataus== USER_TYPE_VALUE_HARDWARE_ASM) {
            lowerhardware.setVisibility(View.VISIBLE);
            lower.setVisibility(View.GONE);
            et_ShopName.setVisibility(View.VISIBLE);
            et_CostValue.setVisibility(View.VISIBLE);
            SHOP_CHECKED = true;
            SALE_VALUE_CHECK = true;
            check_saleValue = (CheckBox) findViewById(R.id.check_saleValue);
            check_payment_collect = (CheckBox) findViewById(R.id.check_payment_collect);
            remark_check = (CheckBox) findViewById(R.id.remark_check);
            remark_check.setOnClickListener(this);
            check_payment_collect.setOnClickListener(this);
            check_saleValue.setOnClickListener(this);
            check_saleValue.setChecked(true);
        } else {
            lowerhardware.setVisibility(View.GONE);
            lower.setVisibility(View.VISIBLE);
        }


        mBitmap = null;

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mCurrentPhotoPath = extras.getString(MediaStore.EXTRA_OUTPUT);
        }

        if (savedInstanceState != null) {
            output = savedInstanceState.getParcelable(STATE_OUTPUT);
        }

        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(this, "Cannot access external storage!", Toast.LENGTH_LONG).show();
            finish();
        }

        result = (ResultFragment) getFragmentManager().findFragmentByTag(TAG_RESULT);

        if (result == null) {
            result = ResultFragment.newInstance();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.frag_container, result, TAG_RESULT)
                    .hide(result)
                    .commit();

            AttMgmtLogUtility.Print(TAG, "Result added" + result);

        }

        takePicture();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STATE_OUTPUT, output);
    }

    public void takePicture(Intent i) {
        if (manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
            EasyImage.openCamera(PictureActivity.this, 1);
        } else {
            startActivityForResult(i, REQUEST_CAMERA);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bitmap = data.getParcelableExtra("data");

                if (bitmap == null) {
                    result.setImage(output);
                    AttMgmtLogUtility.Print(TAG, "from output" + output.getPath());
                } else {
                    result.setImage(bitmap);

                    AttMgmtLogUtility.Print(TAG, "from bitmap");
                }

                getFragmentManager()
                        .beginTransaction()
                        .show(result)
                        .commit();

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String placeText = et_visit_place.getText().toString().trim();
                        String puposeText = et_visit_purpose.getText().toString().trim();
                        String counterSize = etCounterSize.getText().toString().trim();
                        if (TextUtils.isEmpty(placeText) == true) {
                            Toast.makeText(getBaseContext(), "'Place of visit' can't be blank", Toast.LENGTH_LONG).show();
                            return;
                        } else if (TextUtils.isEmpty(puposeText) == true|| puposeText.length()<10) {
                            Toast.makeText(getBaseContext(), "Please enter 'Purpose or result of visit' with minimum 10 character...", Toast.LENGTH_LONG).show();
                            return;
                        }else if(TextUtils.isEmpty(counterSize)){
                            Toast.makeText(getBaseContext(), "counter size can't be blank", Toast.LENGTH_LONG).show();
                        } else {
                            Intent result = new Intent();
                            result.putExtra("remark", "Place:" + placeText + ";" + "Purpose:" + puposeText +";"+ "Counter size: "+counterSize );
                            result.putExtra("shopname", "");
                            result.putExtra("salevalue", "");
                            result.putExtra("paymentcollected", "");
                            setResult(Activity.RESULT_OK, result);
                            finish();
                        }
                    }
                });
                ok_hardware.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shopNameText = et_ShopName.getText().toString().trim();
                        saleValueText = et_CostValue.getText().toString().trim();
                        paymentcollectedText = et_Pay_collected.getText().toString().trim();
                        remarkText = et_Remark.getText().toString().trim();
                        /*final int castValue = Integer.parseInt(saleValueText);*/

                        if ((SHOP_CHECKED == true) && shopNameText.length() < 5) {
                            Toast.makeText(getBaseContext(), "Please enter shop name with minimum 5 characters.", Toast.LENGTH_LONG).show();
                            return;
                        } else if (SALE_VALUE_CHECK == false &&
                                PAYMENT_COLLECTED_CHECK == false &&
                                REMARK_CHECKED == false) {
                            Toast.makeText(PictureActivity.this, "Please select at least one option for sale", Toast.LENGTH_LONG).show();
                        } else if ((SALE_VALUE_CHECK == true) &&
                                (TextUtils.isEmpty(saleValueText) == true)) {
                            Toast.makeText(PictureActivity.this, "'Sale value' can not be blank.", Toast.LENGTH_LONG).show();
                            return;
                        } else if ((PAYMENT_COLLECTED_CHECK == true) &&
                                (TextUtils.isEmpty(paymentcollectedText) == true)) {
                            Toast.makeText(PictureActivity.this, "'Payment collected' can not be blank.", Toast.LENGTH_LONG).show();
                            return;
                        } else if ((REMARK_CHECKED == true && remarkText.length() < 10)) {
                            Toast.makeText(getBaseContext(), "Please enter remarks with minimum 10 characters.", Toast.LENGTH_LONG).show();
                            return;
                        } else if ((SALE_VALUE_CHECK == true && Integer.parseInt(saleValueText) <= 0 && remarkText.length() < 10)) {
                            Toast.makeText(getBaseContext(), "Please enter remarks for 0 sale value.", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            Intent result = new Intent();
                            result.putExtra("shopname", shopNameText);
                            result.putExtra("salevalue", saleValueText);
                            result.putExtra("paymentcollected", paymentcollectedText);
                            result.putExtra("remark", remarkText);
                            setResult(Activity.RESULT_OK, result);
                            finish();
                        }
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        et_visit_place.getText().clear();
                        et_visit_purpose.getText().clear();
                        takePicture();
                    }
                });

                cancel_hardware.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        et_ShopName.getText().clear();
                        et_CostValue.getText().clear();
                        et_Pay_collected.getText().clear();
                        et_Remark.getText().clear();
                        takePicture();
                    }
                });
            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            finish();
        }
        //Handle image capture for HUAWAI Phones
        if (manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {

            EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
                @Override
                public void onImagePicked(final File imageFile, EasyImage.ImageSource source, int type) {
                    if (imageFile != null) {
                        Handler uiHanlder = new Handler(Looper.getMainLooper());
                        uiHanlder.post(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(PictureActivity.this)
                                        .load(imageFile)
                                        .asBitmap()
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                                                //perform action

                                                if (bitmap == null) {
                                                    result.setImage(output);
                                                    AttMgmtLogUtility.Print(TAG, "from output" + output.getPath());
                                                } else {
                                                    //write file in specified directory
                                                    try {
                                                        File file = new File(mCurrentPhotoPath);
                                                        if (file.exists()) {
                                                            file.delete();
                                                        }
                                                        FileOutputStream out = new FileOutputStream(file);
                                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                                        out.flush();
                                                        out.close();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }


                                                    result.setImage(bitmap);
                                                    AttMgmtLogUtility.Print(TAG, "from bitmap");

                                                }

                                                getFragmentManager()
                                                        .beginTransaction()
                                                        .show(result)
                                                        .commit();

                                                ok.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        String placeText = et_visit_place.getText().toString().trim();
                                                        String puposeText = et_visit_purpose.getText().toString().trim();
                                                        String counterSize = etCounterSize.getText().toString().trim();
                                                        if (TextUtils.isEmpty(placeText) == true) {
                                                            Toast.makeText(getBaseContext(), "'Place of visit' can't be blank", Toast.LENGTH_LONG).show();
                                                            return;
                                                        } else if (TextUtils.isEmpty(puposeText) == true|| puposeText.length()<10) {
                                                            Toast.makeText(getBaseContext(), "Please enter 'Purpose or result of visit' with minimum 10 character...", Toast.LENGTH_LONG).show();
                                                            return;
                                                        } else if (TextUtils.isEmpty(puposeText) == true) {
                                                            Toast.makeText(getBaseContext(), "'Purpose of visit' can't be blank", Toast.LENGTH_LONG).show();
                                                            return;
                                                        } else {
                                                            Intent result = new Intent();
                                                            result.putExtra("remark", "Place:" + placeText + ";" + "Purpose:" + puposeText+";"+ "Counter size: "+counterSize );
                                                            result.putExtra("shopname", "");
                                                            result.putExtra("salevalue", "");
                                                            result.putExtra("paymentcollected", "");
                                                            setResult(Activity.RESULT_OK, result);
                                                            finish();
                                                        }
                                                    }
                                                });
                                                ok_hardware.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        shopNameText = et_ShopName.getText().toString().trim();
                                                        saleValueText = et_CostValue.getText().toString().trim();
                                                        paymentcollectedText = et_Pay_collected.getText().toString().trim();
                                                        remarkText = et_Remark.getText().toString().trim();
                                                        /*final int castValue = Integer.parseInt(saleValueText);*/

                                                        if ((SHOP_CHECKED == true) && shopNameText.length() < 5) {
                                                            Toast.makeText(getBaseContext(), "Please enter shop name with minimum 5 characters.", Toast.LENGTH_LONG).show();
                                                            return;
                                                        } else if (SALE_VALUE_CHECK == false &&
                                                                PAYMENT_COLLECTED_CHECK == false &&
                                                                REMARK_CHECKED == false) {
                                                            Toast.makeText(PictureActivity.this, "Please select at least one option for sale", Toast.LENGTH_LONG).show();
                                                        } else if ((SALE_VALUE_CHECK == true) &&
                                                                (TextUtils.isEmpty(saleValueText) == true)) {
                                                            Toast.makeText(PictureActivity.this, "'Sale value' can not be blank.", Toast.LENGTH_LONG).show();
                                                            return;
                                                        } else if ((PAYMENT_COLLECTED_CHECK == true) &&
                                                                (TextUtils.isEmpty(paymentcollectedText) == true)) {
                                                            Toast.makeText(PictureActivity.this, "'Payment collected' can not be blank.", Toast.LENGTH_LONG).show();
                                                            return;
                                                        } else if ((REMARK_CHECKED == true && remarkText.length() < 10)) {
                                                            Toast.makeText(getBaseContext(), "Please enter remarks with minimum 10 characters.", Toast.LENGTH_LONG).show();
                                                            return;
                                                        } else if ((SALE_VALUE_CHECK == true && Integer.parseInt(saleValueText) <= 0 && remarkText.length() < 10)) {
                                                            Toast.makeText(getBaseContext(), "Please enter remarks for 0 sale value.", Toast.LENGTH_LONG).show();
                                                            return;
                                                        } else {
                                                            Intent result = new Intent();
                                                            result.putExtra("shopname", shopNameText);
                                                            result.putExtra("salevalue", saleValueText);
                                                            result.putExtra("paymentcollected", paymentcollectedText);
                                                            result.putExtra("remark", remarkText);
                                                            setResult(Activity.RESULT_OK, result);
                                                            finish();
                                                        }
                                                    }
                                                });

                                                cancel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        et_visit_place.getText().clear();
                                                        et_visit_purpose.getText().clear();
                                                        takePicture();
                                                    }
                                                });

                                                cancel_hardware.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        et_ShopName.getText().clear();
                                                        et_CostValue.getText().clear();
                                                        et_Pay_collected.getText().clear();
                                                        et_Remark.getText().clear();
                                                        takePicture();
                                                    }
                                                });

                                            }
                                        });//(200, 200);

                            }
                        });
                    }
                }
            });
        }

    }

    public void setOutput(Uri uri) {
        output = uri;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.check_saleValue:
                if (check_saleValue.isChecked()) {
                    et_CostValue.setVisibility(View.VISIBLE);
                    et_CostValue.setCursorVisible(true);
                    SALE_VALUE_CHECK = true;
                } else {
                    et_CostValue.setVisibility(View.GONE);
                    et_CostValue.getText().clear();
                    et_CostValue.setCursorVisible(false);
                    SALE_VALUE_CHECK = false;
                }
                break;
            case R.id.check_payment_collect:
                if (check_payment_collect.isChecked()) {
                    et_Pay_collected.setVisibility(View.VISIBLE);
                    et_Pay_collected.setCursorVisible(true);
                    PAYMENT_COLLECTED_CHECK = true;
                } else {
                    et_Pay_collected.setVisibility(View.GONE);
                    et_Pay_collected.getText().clear();
                    et_Pay_collected.setCursorVisible(false);
                    PAYMENT_COLLECTED_CHECK = false;
                }
                break;
            case R.id.remark_check:
                if (remark_check.isChecked()) {
                    et_Remark.setVisibility(View.VISIBLE);
                    et_Remark.setCursorVisible(true);
                    REMARK_CHECKED = true;
                } else {
                    et_Remark.setVisibility(View.GONE);
                    et_Remark.getText().clear();
                    et_Remark.setCursorVisible(false);
                    REMARK_CHECKED = false;
                }
                break;
        }
    }

    private void takePicture() {
        SharedPreferences prefs = getBaseContext().getSharedPreferences("sharedPrefName", 0);

        CameraActivity.IntentBuilder b = new CameraActivity.IntentBuilder(getBaseContext());
        if (!prefs.getBoolean("confirm", false)) {
            b.skipConfirm();
        }

        b.facing(Facing.FRONT);


        if (prefs.getBoolean("exact_match", false)) {
            b.facingExactMatch();
        }

        if (prefs.getBoolean("debug", false)) {
            b.debug();
        }

        if (prefs.getBoolean("updateMediaStore", false)) {
            b.updateMediaStore();
        }

        int rawEngine = Integer.valueOf(prefs.getString("forceEngine", "0"));

        switch (rawEngine) {
            case 1:
                b.forceEngine(CameraEngine.ID.CLASSIC);
                break;
            case 2:
                b.forceEngine(CameraEngine.ID.CAMERA2);
                break;
        }

        if (1 == 1) {
            File f = new File(mCurrentPhotoPath);
            if (prefs.getBoolean("useProvider", false)) {
                Uri uri = FileProvider.getUriForFile(getBaseContext(), AUTHORITY, f);
                b.to(uri);
                setOutput(uri);
                AttMgmtLogUtility.Print(TAG, " from FileProvider" + uri.toString());
            } else {
                b.to(f);
                setOutput(Uri.fromFile(f));
                AttMgmtLogUtility.Print(TAG, " from storage" + f.getAbsolutePath());
            }
        }


        if (prefs.getBoolean("mirrorPreview", false)) {
            b.mirrorPreview();
        }
        if (1 == 1/*prefs.getBoolean("highQuality", false)*/) {
            b.quality(AbstractCameraActivity.Quality.HIGH);
        } else {
            b.quality(AbstractCameraActivity.Quality.LOW);
        }

        b.focusMode(FocusMode.CONTINUOUS);

        if (prefs.getBoolean("debugSavePreview", false)) {
            b.debugSavePreviewFrame();
        }

        b.skipOrientationNormalization();

        b.flashMode(FlashMode.AUTO);


        if (prefs.getBoolean("allowSwitchFlashMode", false)) {
            b.allowSwitchFlashMode();
        }

        b.zoomStyle(ZoomStyle.PINCH);

        b.orientationLockMode(OrientationLockMode.PORTRAIT);

        int rawTimer = Integer.valueOf(prefs.getString("timer", "0"));

        if (rawTimer > 0) {
            b.timer(rawTimer);
        }

        b.confirmationQuality(Float.parseFloat("1.0"));


        b.onError(new ErrorResultReceiver());

        if (prefs.getBoolean("requestPermissions", true)) {
            b.requestPermissions();
        }

        if (prefs.getBoolean("showRuleOfThirds", false)) {
            b.showRuleOfThirdsGrid();
        }

        Intent result;

        if (prefs.getBoolean("useChooser", false)) {
            result = b.buildChooser("Choose a picture-taking thingy");

        } else {
            result = b.build();
        }
        takePicture(result);
    }


    @SuppressLint("ParcelCreator")
    private class ErrorResultReceiver extends ResultReceiver {
        public ErrorResultReceiver() {
            super(new Handler(Looper.getMainLooper()));
        }

        @Override
        protected void onReceiveResult(int resultCode,
                                       Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            if (getBaseContext() != null) {
        /*Toast
                .makeText(getBaseContext(), "We had an error",
                        Toast.LENGTH_LONG)
                .show();*/
                AttMgmtLogUtility.Print(TAG, "Activity context error.");
            }
        }
    }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    //if (mBitmap != null) {
    //  mBitmap.recycle();
    //  mBitmap = null;
    //}
  }
}


