package com.hangzhou.me.floatview.uninstall;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.AppUtils;
import com.hangzhou.me.floatview.databinding.ItemInstalledAppBinding;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: edison qian
 * @Email: edison.qian@applovin.com
 * @CreateDate: 2022/12/23 17:23
 * @Description:
 */
public class InstalledAppsAdapter extends RecyclerView.Adapter<InstalledAppsAdapter.InstalledAppViewHolder> {
    private List<AppInfoEx> installedApps;

    public InstalledAppsAdapter(List<AppInfoEx> installedApps) {
        this.installedApps = installedApps;
    }

    @NonNull
    @Override
    public InstalledAppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInstalledAppBinding view = ItemInstalledAppBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        InstalledAppViewHolder holder = new InstalledAppViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull InstalledAppViewHolder holder, int position) {
        holder.bind(installedApps.get(position));
    }

    @Override
    public int getItemCount() {
        return installedApps.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<String> getCheckedApp() {
        return installedApps.stream().filter(it -> it.isChecked()).map(it -> it.getPackageName()).collect(Collectors.toList());
    }

    public static class InstalledAppViewHolder extends RecyclerView.ViewHolder {
        ItemInstalledAppBinding binding;

        public InstalledAppViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemInstalledAppBinding.bind(itemView);
        }

        public InstalledAppViewHolder(ItemInstalledAppBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AppInfoEx appInfo) {
            binding.appImage.setImageDrawable(appInfo.getIcon());
            binding.appName.setText(appInfo.getName());
            binding.unintallBtn.setOnClickListener(v -> {
                AppUtils.uninstallApp(appInfo.getPackageName());
            });
            binding.checkbox.setChecked(appInfo.isChecked());
            binding.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    appInfo.setChecked(isChecked);
                }
            });
        }
    }

    public static class AppInfoEx extends AppUtils.AppInfo {
        private boolean checked;

        public AppInfoEx(String packageName, String name, Drawable icon, String packagePath, String versionName, int versionCode, int minSdkVersion, int targetSdkVersion, boolean isSystem, boolean checked) {
            super(packageName, name, icon, packagePath, versionName, versionCode, minSdkVersion, targetSdkVersion, isSystem);
            this.checked = checked;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }
    }
}
