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

import com.huawei.hms.scene.math.Quaternion;
import com.huawei.hms.scene.math.Vector3;

public class Bone {

    public Vector3 localPosition;
    public Vector3 worldPosition;
    public Quaternion localRotate;
    public Quaternion worldRotate;
    public String name = "";
    public String parentName;
    public int color;
    public float len;
    private Vector3 initialPosition;

    public Bone(float x,float y,float z) {
        worldPosition = new Vector3(x,y,z);
        localRotate = new Quaternion(1,0,0,0);
        worldRotate = new Quaternion(1,0,0,0);
    }

    public void setLocalPosition(Bone parent) {
        if (parent == null){
            localPosition = new Vector3(0,0,0);
            len = worldPosition.length();
            worldRotate = new Quaternion(1,0,0,0);
            localRotate = new Quaternion(1,0,0,0);
        }
        else {
            // Calculate local coordinates based on parent world coordinates and rotation angle
            localPosition = parent.worldRotate.inverse().rotateVector3(Vector3.subtract(worldPosition, parent.worldPosition));
            len = (float) Math.sqrt(localPosition.length());
        }
        initialPosition = new Vector3(0,len,0); // The initial position of the object when the rotation angle is 0.
    }

    public void setRotate(Quaternion localRotate, Bone parent) {
        if (parent==null) {
            this.localRotate = localRotate;
            this.worldRotate = localRotate;
            this.worldPosition = parent.worldRotate.rotateVector3(initialPosition);
            this.localPosition = parent.worldRotate.rotateVector3(initialPosition);
        }else{
            this.localRotate = localRotate;
            // Calculate world rotation
            this.worldRotate = Quaternion.multiply(parent.worldRotate, localRotate);
            // Calculate world position
            this.worldPosition = Vector3.add(parent.worldPosition, parent.worldRotate.rotateVector3(initialPosition));
            // Calculate local position
            this.localPosition = parent.worldRotate.inverse().rotateVector3(Vector3.subtract(worldPosition, parent.worldPosition));
        }
    }


}
