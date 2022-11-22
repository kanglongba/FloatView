package com.hangzhou.me.floatview.sysmonitor;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.hangzhou.me.floatview.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentPage3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPage3 extends Fragment {

    private LinearLayout mAbout;
    private LinearLayout mMessage;
    private LinearLayout mText;
    private Button exitButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page3, null);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view1 = (View) getActivity().findViewById(R.id.info_set);
        mText = (LinearLayout) view1.findViewById(R.id.system);
        mText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                new AlertDialog.Builder(getActivity())
                        .setTitle("使用说明")
                        .setMessage("本简易APP可用于监测手机各部件工作状态，以及查询手机内部配置、网络状况等情况。")
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
        mMessage = (LinearLayout) view1.findViewById(R.id.message);
        mMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "18655629450"));
                startActivity(intent);
            }
        });
        mAbout = (LinearLayout) view1.findViewById(R.id.about);
        mAbout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                new AlertDialog.Builder(getActivity())
                        .setTitle("关于")
                        .setMessage("Copyright © 2011-2016 Sawatari Inc. All rights reserved.")
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
        exitButton = (Button) getActivity().findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                getActivity().finish();
                System.exit(0);
            }
        });
    }
}