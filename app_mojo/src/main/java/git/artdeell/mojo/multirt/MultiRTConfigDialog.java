package git.artdeell.mojo.multirt;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.activity.result.ActivityResultLauncher;
import androidx.recyclerview.widget.RecyclerView;

import git.artdeell.mojo.R;
import git.artdeell.mojo.dialogs.picker.PickerDialog;
import git.artdeell.mojo.dialogs.picker.PickerRecyclerView;

public class MultiRTConfigDialog extends PickerDialog {
    public void show(){
        refresh();
        mDialog.show();
    }
    @SuppressLint("NotifyDataSetChanged") //only used to completely refresh the list, it is necessary
    public void refresh() {
        RecyclerView.Adapter<?> adapter = mDialogView.getAdapter();
        if(adapter != null) adapter.notifyDataSetChanged();
    }
    protected int getTitleRes() { return R.string.multirt_config_title; }
    protected int getPositiveButtonText() { return R.string.multirt_config_add; }
    protected int getNeutralButtonText() { return R.string.multirt_delete_runtime; }
    protected int getEditingButtonLabel() { return mAdapter.isEditing() ? R.string.multirt_config_setdefault : R.string.multirt_delete_runtime; }
    protected PickerRecyclerView<?> createAdapter(Context context, PickerDialog dialog) { return new RTRecyclerViewAdapter(dialog); }
    protected void onPositive(Context context, ActivityResultLauncher<Object> launcher) { launcher.launch(null); }
}
