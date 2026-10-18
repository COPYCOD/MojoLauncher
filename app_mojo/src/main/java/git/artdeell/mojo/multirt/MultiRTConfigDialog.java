package git.artdeell.mojo.multirt;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.activity.result.ActivityResultLauncher;
import androidx.recyclerview.widget.RecyclerView;

import git.artdeell.mojo.R;
import git.artdeell.mojo.dialogs.picker.PickerDialog;
import git.artdeell.mojo.dialogs.picker.PickerRecyclerView;

public class MultiRTConfigDialog extends PickerDialog {

    /** Show the dialog, refreshes the adapter data before showing it */
    public void show(){
        refresh();
        mDialog.show();
    }

    @SuppressLint("NotifyDataSetChanged") //only used to completely refresh the list, it is necessary
    public void refresh() {
        RecyclerView.Adapter<?> adapter = mDialogView.getAdapter();
        if(adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    protected int getTitleRes() {
        return R.string.multirt_config_title;
    }

    @Override
    protected int getPositiveButtonText() {
        return R.string.multirt_config_add;
    }

    @Override
    protected int getNeutralButtonText() {
        return R.string.multirt_delete_runtime;
    }

    @Override
    protected int getEditingButtonLabel() {
        return mAdapter.isEditing() ? R.string.multirt_config_setdefault : R.string.multirt_delete_runtime;
    }

    @Override
    protected PickerRecyclerView<?> createAdapter(Context context, PickerDialog dialog) {
        return new RTRecyclerViewAdapter(dialog);
    }

    @Override
    protected void onPositive(Context context, ActivityResultLauncher<Object> launcher) {
        launcher.launch(null);
    }
}
