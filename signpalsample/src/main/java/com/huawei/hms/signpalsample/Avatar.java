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

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;

import com.huawei.hms.signpal.common.agc.SignPalApplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Avatar {
    public static HashMap<String, Bone> boneMap = new HashMap<>();
    public static  String[] boneNames =  new String[]{"Pelvis","Spine1","Spine2","Spine3","Spine4",
            "LeftShoulder","LeftArm","LeftForeArm", "LeftHand",
            "LeftHandThumb1","LeftHandThumb2","LeftHandThumb3","LeftHandThumb4",
            "LeftHandIndex1","LeftHandIndex2","LeftHandIndex3","LeftHandIndex4",
            "LeftHandMiddle1","LeftHandMiddle2","LeftHandMiddle3","LeftHandMiddle4",
            "LeftHandRing1","LeftHandRing2","LeftHandRing3","LeftHandRing4",
            "LeftHandPinky1","LeftHandPinky2","LeftHandPinky3","LeftHandPinky4",
            "RightShoulder","RightArm","RightForeArm","RightHand",
            "RightHandThumb1","RightHandThumb2","RightHandThumb3","RightHandThumb4",
            "RightHandIndex1","RightHandIndex2","RightHandIndex3","RightHandIndex4",
            "RightHandMiddle1","RightHandMiddle2","RightHandMiddle3","RightHandMiddle4",
            "RightHandRing1","RightHandRing2","RightHandRing3","RightHandRing4",
            "RightHandPinky1","RightHandPinky2","RightHandPinky3","RightHandPinky4",
            "Neck","Head"};

    private  void initSkeletalStructure(){
        initBone();
    }

    public  Avatar(){
        this.context = SignPalApplication.getInstance().getAppContext();
        initSkeletalStructure();
    }
    private Context context;

    public void initBone(){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("bonesList.csv")));
            String head=reader.readLine();
            String line = null;
            while((line=reader.readLine())!=null){
                String[] item = line.split("\t");
                if (item.length !=6){
                    continue;
                }
                Bone bone = new Bone(Float.parseFloat(item[3]),Float.parseFloat(item[4]),Float.parseFloat(item[5]));
                bone.color = Integer.parseInt(item[0]);
                bone.parentName = item[1];
                bone.name = item[2];
                boneMap.put(bone.name,bone);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(String name :boneNames){
            if (!boneMap.containsKey(name)){
                continue;
            }
            Bone bone = boneMap.get(name);
            if (!TextUtils.isEmpty(bone.parentName)) {
                bone.setLocalPosition(boneMap.get(bone.parentName));
            }else{
                bone.setLocalPosition(null);
            }
        }
    }


    public Paint getBoneColor(int bone) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3);

        if (bone ==0){
            paint.setColor(Color.rgb(0,153,0));
        }else if(bone ==1){
            paint.setColor(Color.rgb(0,0,255));
        } else if (bone==2 ) {
            paint.setColor(Color.rgb(0,102,204));
        }else if (bone==3) {
            paint.setColor(Color.rgb(0,204,204));
        } else if (bone==4) {
            paint.setColor(Color.rgb(0,0,204));
        }else if (bone<=8 &&bone>=5){
            paint.setColor(Color.rgb(0,0,255));
        }else if (bone<=12 &&bone>=9){
            paint.setColor(Color.rgb(51, 255, 51));
        }else if (bone<=16 &&bone>=13){
            paint.setColor(Color.rgb(255, 0, 0));
        }else if (bone<=20 &&bone>=17){
            paint.setColor(Color.rgb(204, 153, 255));
        }else if (bone<=24 &&bone>=21){
            paint.setColor(Color.rgb(51, 255, 255));
        }
        return paint;
    }
    public static final Avatar getInstance() {
        return Avatar.AvatarHolder.INSTANCE;
    }
    private static class AvatarHolder {
        private static final Avatar INSTANCE = new Avatar();
    }
}
