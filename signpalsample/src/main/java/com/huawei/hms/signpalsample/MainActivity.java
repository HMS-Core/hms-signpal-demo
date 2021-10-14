/**
 *  Copyright 2021 . Huawei Technologies Co., Ltd. All rights reserved.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.huawei.hms.signpalsample;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.signpal.GeneratorCallback;
import com.huawei.hms.signpal.GeneratorConstants;
import com.huawei.hms.signpal.GeneratorSetting;
import com.huawei.hms.signpal.SignGenerator;
import com.huawei.hms.signpal.SignMotionFragment;
import com.huawei.hms.signpal.SignPalError;
import com.huawei.hms.signpal.SignPalWarning;
import com.huawei.hms.signpal.common.agc.SignPalApplication;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "DDLActivity";

    // please set your ApiKey or accessToken
    private String apikey ="" ;
    private String token ="" ;

    private ImageView imageView;
    public int mode= GeneratorConstants.QUEUE_MODE;
    public String lan = GeneratorConstants.CN_CSL;
    private EditText editText;
    private SignGenerator signGenerator;
    private GeneratorSetting generatorSetting;
    private String text;
    private String cnText = "你好";
    private long starTime ;
    private long costTime ;
    private TextView tvSign;
    private TextView tvMode;
    private TextView faceTypeView;
    private String faceFromat = "FaceType is: %d %s";
    private String[] faceTypes = new String[]{"没表情","开心","愤怒","伤心","疑惑","害怕","讨厌","惊讶","痛苦","失望"};
    private int model = 1;
    private AvatarPaint avatarPaint;
    private boolean isPlay = false;


    private GeneratorCallback callback =  new GeneratorCallback() {
        @Override
        public void onEvent(String taskId, int eventId, Bundle bundle) {
            switch (eventId){
                case GeneratorConstants.EVENT_START:
                    starTime = System.currentTimeMillis();
                    break;
                case GeneratorConstants.EVENT_STOP:
                    costTime = System.currentTimeMillis()-starTime;
                    Log.d(TAG,String.format("task: %s ,time cost:%s",taskId,costTime));
                    break;
                default:break;

            }
        }
        @Override
        public void onSignDataAvailable(String taskId, SignMotionFragment signFragment, Pair<Integer, Integer> range, Bundle bundle) {
                // get the motion data
                ArrayList<Map<String,float[]>> motionDataList= signFragment.getSignMotionDataMap();
                // get the face data
                int[] faceArr = signFragment.getFaceMotion();
                // Developers need to render sign language animations themselves.The sample code provides an example of drawing a matchman.

                ArrayList<FrameData> frameDataList = new ArrayList<>();
                for(int i =0;i<signFragment.getFrameCount();i++) {
                    FrameData frameData = new FrameData();
                    frameData.setMotionData(motionDataList.get(i));
                    frameData.setFaceType(faceArr[i]);
                    frameData.setFrameIdx(i);
                    frameDataList.add(frameData);
                }
            Message message = new Message();
            message.obj= frameDataList;
            avatarPaint.mHandler.handleMessage(message);
        }
        @Override
        public void onError(String taskId, SignPalError err) {

            String msg = String.format("taskID:%s ,errMsg:%s",taskId,err.getErrorMsg());
            Log.i(TAG,msg);
        }
        @Override
        public void onWarning(String taskId, SignPalWarning warning) {
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set APIKey
        SignPalApplication.getInstance().setApiKey(apikey);
        // to set AccessToken, use SignPalApplication.getInstance().setAccessToken(token)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvSign = findViewById(R.id.sign);
        tvMode = findViewById(R.id.mode);
        editText = findViewById(R.id.editText);
        faceTypeView = findViewById(R.id.faceTypeView);
        editText.setText(cnText);

        imageView = findViewById(R.id.imageView);
        avatarPaint = new AvatarPaint();
        avatarPaint.startUp();

        // init SignGenerator
        generatorSetting = new GeneratorSetting().setLanguage(lan);
        signGenerator = new SignGenerator(generatorSetting);
        signGenerator.setCallback(callback);


        setSpinner();
        // Waiting for Matchman Rendering
        handler.post(runnable);
    }
    private Handler handler = new Handler();
    public void onStopClick(View view){
        signGenerator.stop();
        isPlay = false;
    }
    public   void onSendClick(View view) {
        isPlay = true;
        avatarPaint.setScaleRation(imageView.getMeasuredWidth(),imageView.getMeasuredWidth());
        text = editText.getText().toString();
        avatarPaint.frameQueue.clear();
        signGenerator.text2SignMotion(text,mode);
        Avatar.getInstance().initBone();
    }

    @Override
    protected void  onResume(){
        super.onResume();

    }
    @Override
    public void onWindowFocusChanged(boolean var1){
        super.onWindowFocusChanged(var1);

    }


    private void setSpinner(){
        tvSign.setText(getString(R.string.chinese_sign));
        tvMode.setText(getString(R.string.queue_model));
    }
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign:
                break;
            case R.id.mode:
                SelectBottomDialog dialog1 = new SelectBottomDialog(this, new SelectBottomDialog.SelectRegionClick() {
                    @Override
                    public void regionClick(int selectRegion) {
                        model = selectRegion;
                        if(model==1){
                            mode = GeneratorConstants.QUEUE_MODE;
                            tvMode.setText(getString(R.string.queue_model));
                        }else{
                            mode = GeneratorConstants.FLUSH_MODE;
                            tvMode.setText(getString(R.string.clear_model));
                        }
                    }
                }, model, getString(R.string.mode_text), getString(R.string.queue_model), getString(R.string.clear_model));
                dialog1.show();
                break;
        }
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            setBitMapView();
            handler.post(this);
        }
    };
    private void setBitMapView(){
        if (!avatarPaint.frameQueue.isEmpty() &&isPlay){
            Pair<Bitmap,Integer>  frameDataPair = avatarPaint.frameQueue.poll();
            imageView.setImageBitmap(frameDataPair.first);
            faceTypeView.setText(String.format(faceFromat,frameDataPair.second,faceTypes[frameDataPair.second]));
        }
    }

    @Override
    public void onDestroy(){
        signGenerator.shutdown();
        avatarPaint.destroy();
        super.onDestroy();
    }


}
