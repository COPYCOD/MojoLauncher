package git.artdeell.mojo.vulkan.ui;


import static git.artdeell.mojo.MojoApplication.sExecutorService;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import git.artdeell.mojo.dialogs.PackageAdapter;
import git.artdeell.mojo.vulkan.driver.AdrenoDriver;
import git.artdeell.mojo.vulkan.DriverManager;
import git.artdeell.mojo.vulkan.driver.Driver;

import git.artdeell.mojo.R;

import java.util.List;

public class DriverViewAdapter extends PackageAdapter<DriverViewAdapter.DriverViewHolder> {
    public DriverViewAdapter(DriverConfigDialog dialog) {
        super(dialog);
    }
    @NonNull
    public DriverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View recyclableView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_package_selector,parent,false);
        return new DriverViewHolder(recyclableView);
    }
    public void onBindViewHolder(@NonNull DriverViewHolder holder, int position) {
        // Always bind the default driver first
        if(position == 0)
            holder.bind(null, 0);
        else {
            List<String> drivers = DriverManager.getDriverPaths();
            holder.bind(drivers.get(position - 1), position);
        }
    }
    public int getItemCount() { return DriverManager.getDriverPaths().size() + 1; }
    protected DriverViewHolder abstractHolder(View recyclableView) {
        return new DriverViewHolder(recyclableView);
    }
    public boolean isDefault(Driver driver) {
        return DriverManager.isPreferredDriver(driver);
    }
    @SuppressLint("NotifyDataSetChanged") //not a problem, given the typical size of the list
    public void setDefault(Driver driver){
        DriverManager.setPreferredDriver(driver);
        notifyDataSetChanged();
    }

    public class DriverViewHolder extends PackageAdapter.AbstractHolder<String> {
        Driver mCurrentDriver;
        int mCurrentPosition;

        public DriverViewHolder(View itemView) {
            super(itemView);
            setupOnClickListeners();
        }

        @SuppressLint("NotifyDataSetChanged") // same as all the other ones
        private void setupOnClickListeners(){
            mSetDefaultButton.setOnClickListener(v -> {
                if(mCurrentDriver != null) {
                    setDefault(mCurrentDriver);
                    DriverViewAdapter.this.dialog.close();
                }
            });

            mDeleteButton.setOnClickListener(v -> {
                if (mCurrentDriver != null && mCurrentDriver.isDefault()) return;


                sExecutorService.execute(() -> {
                    boolean state = DriverManager.removeDriver(mCurrentDriver.getHash());
                    mDeleteButton.post(() -> {
                        if(!state) {
                            Toast.makeText(mContext, R.string.driver_config_remove_failed, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(getBindingAdapter() != null)
                            getBindingAdapter().notifyDataSetChanged();
                    });
                });

            });
        }

        @SuppressLint("SetTextI18n")
        public void bind(String driverPath, int pos) {
            mCurrentPosition = pos;
            updateButtonsVisibility();
            if((mCurrentDriver = driverPath != null ? DriverManager.loadDriver(driverPath) : DriverManager.DEFAULT_DRIVER) == null){
                Log.w(DriverManager.TAG, "Driver " + driverPath + " is corrupted");
                mNameTextView.setText("");
                mExtraNameTextView.setText(R.string.driver_config_corrupted);
                mExtraNameTextView.setTextColor(Color.RED);
                mSetDefaultButton.setEnabled(false);
                mSetDefaultButton.setVisibility(View.GONE);
            } else {
                if(mCurrentDriver.isDefault()){
                    mNameTextView.setText(R.string.driver_default_name);
                    mExtraNameTextView.setText(R.string.driver_default_extra_name);
                    mDeleteButton.setVisibility(View.GONE);
                    mDeleteButton.setClickable(false);
                } else {
                    mNameTextView.setText(mCurrentDriver.getName());
                    AdrenoDriver adr = (AdrenoDriver) mCurrentDriver;
                    mExtraNameTextView.setText(adr.author + " - " + adr.driverVersion);
                }
                boolean isPreferred = isDefault(mCurrentDriver);
                mSetDefaultButton.setEnabled(!isPreferred);
                mSetDefaultButton.setText(isPreferred ? R.string.driver_config_already_used : R.string.driver_config_use);
            }
        }

        private void updateButtonsVisibility(){
            mSetDefaultButton.setVisibility(isEditing() ? View.GONE : View.VISIBLE);
            mDeleteButton.setVisibility(isEditing() ? View.VISIBLE : View.GONE);
        }
    }
}
