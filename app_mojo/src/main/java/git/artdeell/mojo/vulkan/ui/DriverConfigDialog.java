package git.artdeell.mojo.vulkan.ui;

import android.content.Context;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import git.artdeell.mojo.R;
import git.artdeell.mojo.dialogs.picker.PickerDialog;
import git.artdeell.mojo.dialogs.picker.PickerRecyclerView;

public class DriverConfigDialog extends PickerDialog {
    @Override
    protected int getTitleRes() {
        return R.string.driver_config_title;
    }

    @Override
    protected int getPositiveButtonText() {
        return R.string.driver_config_import;
    }

    @Override
    protected int getNeutralButtonText() {
        return R.string.driver_config_delete;
    }

    @Override
    protected int getEditingButtonLabel() {
        return mAdapter.isEditing() ? R.string.driver_config_delete : R.string.driver_config_select;
    }

    @Override
    protected PickerRecyclerView<?> createAdapter(Context context, PickerDialog dialog) {
        return new DriverViewAdapter(this);
    }

    @Override
    protected void onPositive(Context context, ActivityResultLauncher<Object> launcher) {
        Toast.makeText(context, R.string.driver_config_import_notify, Toast.LENGTH_LONG).show();
        launcher.launch(null);
    }
}