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

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.huawei.hms.signpalsample.databinding.DialogSelectBinding;


public class SelectBottomDialog extends Dialog {

    Context mContext;
    DialogSelectBinding binding;
    SelectRegionClick clickListener;
    int select;
    String title;
    String selectString1;
    String selectString2;

    public interface SelectRegionClick {
        void regionClick(int region);
    }

    public SelectBottomDialog(@NonNull Context context, SelectRegionClick click, int select, String title, String selectString1, String selectString2) {
        super(context, R.style.BottomAnimDialogStyle);
        this.mContext = context;
        clickListener = click;
        this.select = select;
        this.title = title;
        this.selectString1 = selectString1;
        this.selectString2 = selectString2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_select, null, false);
        initView();
        Window window = this.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.y = DataUtil.dip2px(mContext, 16);
            window.setAttributes(lp);
        }
        setContentView(binding.getRoot());
    }

    private void initView() {
        binding.tvTitle.setText(title);
        binding.tvSelect1.setText(selectString1);
        binding.tvSelect2.setText(selectString2);

        if (select == 2) {
            binding.ivChinese.setImageResource(R.mipmap.unselect_rb_icon);
            binding.ivEnglish.setImageResource(R.mipmap.select_rb_icon);
        } else {
            binding.ivChinese.setImageResource(R.mipmap.select_rb_icon);
            binding.ivEnglish.setImageResource(R.mipmap.unselect_rb_icon);
        }

        binding.rlChinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.ivChinese.setImageResource(R.mipmap.select_rb_icon);
                binding.ivEnglish.setImageResource(R.mipmap.unselect_rb_icon);
                clickListener.regionClick(1);
                dismiss();
            }
        });
        binding.rlEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.ivChinese.setImageResource(R.mipmap.unselect_rb_icon);
                binding.ivEnglish.setImageResource(R.mipmap.select_rb_icon);
                clickListener.regionClick(2);
                dismiss();
            }
        });
        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
