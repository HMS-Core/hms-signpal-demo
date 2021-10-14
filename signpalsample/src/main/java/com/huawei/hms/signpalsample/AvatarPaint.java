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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.huawei.hms.signpal.GeneratorConstants.FPS_30;


public class AvatarPaint{
    private final static String  TAG  = "AvatarPaint";
    private  int DEFAULT_CANVAS_WIDTH=650;
    private  int DEFAULT_CANVAS_HEIGH=650;
    private  int WIDTH_OFFSET=300;
    private  int HEIGH_OFFSET=4250;
    private  float SCALE = 10;
    public Queue<Pair<Bitmap,Integer>> frameQueue = new ConcurrentLinkedQueue<>();
    public Queue<FrameData> frameDataQueue = new ConcurrentLinkedQueue<>();
    private HandlerThread mHandlerThread = null;
    public Handler mHandler = null;
    private Avatar avatar =  Avatar.getInstance();
    private int viewWidth;
    private int viewheight;
    private Handler handler = new Handler();
    public AvatarPaint() {
        handler.post(runnable);
    }

    public void setScaleRation(int viewWidth,int viewheight) {
        this.viewWidth = viewheight;
        this.viewheight = viewWidth;
    }

    public void drawLine(Canvas canvas, Point start, Point end, Paint paint){
            canvas.drawLine(start.x,start.y,end.x, end.y,paint);
    }
    public Bitmap getBitMapWithBackground(int color){
        Paint paint = new Paint();
        paint.setColor(color);
        Bitmap bitmap = Bitmap.createBitmap(DEFAULT_CANVAS_WIDTH,DEFAULT_CANVAS_HEIGH, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmap;
    }
    public void drawFrame(FrameData data){

        Bitmap bitmap = getBitMapWithBackground(Color.rgb(255,255,255));
        // init canvas
        Canvas canvas = new Canvas(bitmap);
        for(String name: Avatar.boneNames){
            Bone endBone = Avatar.boneMap.get(name);
            if (TextUtils.isEmpty(endBone.parentName)){
                continue;
            }
            Bone startBone =  Avatar.boneMap.get(endBone.parentName);

            // get paint setting
            Paint paint = avatar.getBoneColor(endBone.color);

            // draw bone
            endBone.setRotate(data.getDataByBoneName(startBone.name),startBone);
            Avatar.boneMap.put(name,endBone); // update endBone pose
            Point start = transTobitMapPoint(startBone.worldPosition.x,startBone.worldPosition.y);
            Point end = transTobitMapPoint(endBone.worldPosition.x,endBone.worldPosition.y);
            Log.i(TAG,String.format("start:%s(%s,%s) end:%s(%s,%s)", startBone.name,start.x,start.y,
                    endBone.name,end.x,end.y));
            drawLine(canvas, start, end, paint);
        }
        Log.i(TAG,">>>>>>>>>>>>>>>>>>>>>");
        frameQueue.offer(new Pair<>(scaleBitmap(bitmap),data.getFaceType()));
    }

    private Bitmap scaleBitmap(Bitmap src) {
        if (src == null) {
            return null;
        }
        Bitmap dst = Bitmap.createScaledBitmap(src, viewWidth, viewheight, false);
        if (dst.equals(src)) {
            return dst;
        }
        src.recycle();
        return dst;
    }


    public Point transTobitMapPoint(float x1, float y1){
        return new Point((int)(SCALE*x1+WIDTH_OFFSET),DEFAULT_CANVAS_HEIGH-(int)(SCALE*y1+HEIGH_OFFSET));
    }


    public void startUp() {

        mHandlerThread = new HandlerThread("AvatarPaint");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                ArrayList<FrameData> dataList = (ArrayList<FrameData>)msg.obj;
                frameDataQueue.addAll(dataList);
            }

        };
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!frameDataQueue.isEmpty()) {
                drawFrame(frameDataQueue.poll());
            }
            handler.postDelayed(runnable, 1000/FPS_30); // FPS30 Delayed rendering to prevent memory overflow
        }
    };


    public void destroy(){
        mHandlerThread.quit();
    }

}
