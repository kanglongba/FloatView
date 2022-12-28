package com.hangzhou.me.floatview.uninstall;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.AppUtils;
import com.hangzhou.me.floatview.databinding.ActivityInstalledAppBinding;

import java.util.List;
import java.util.stream.Collectors;

public class InstalledAppActivity extends AppCompatActivity {

    private ActivityInstalledAppBinding binding;
    private InstalledAppsAdapter adapter;
    private List<InstalledAppsAdapter.AppInfoEx> apps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInstalledAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initData();
        initView();
    }

    private void initData() {
        apps = AppUtils.getAppsInfo().stream().filter(it -> !it.isSystem())
                .map(it -> {
                    InstalledAppsAdapter.AppInfoEx appInfoEx = new InstalledAppsAdapter.AppInfoEx(
                            it.getPackageName(), it.getName(), it.getIcon(), it.getPackagePath(), it.getVersionName(), it.getVersionCode(), it.getMinSdkVersion(), it.getTargetSdkVersion(), it.isSystem(), true
                    );
                    return appInfoEx;
                }).collect(Collectors.toList());
    }

    private void initView() {
        adapter = new InstalledAppsAdapter(apps);
        adapter.setHasStableIds(true);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(InstalledAppActivity.this));
        binding.recyclerView.setAdapter(adapter);
    }


}