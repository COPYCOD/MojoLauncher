package git.artdeell.mojo.fragments;

import static git.artdeell.mojo.Tools.openPath;
import static git.artdeell.mojo.Tools.shareLog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kdt.mcgui.ProgressLayout;
import com.kdt.mcgui.mcVersionSpinner;

import git.artdeell.mojo.CustomControlsActivity;
import git.artdeell.mojo.LauncherActivity;
import git.artdeell.mojo.MojoApplication;
import git.artdeell.mojo.R;

import git.artdeell.mojo.Tools;
import git.artdeell.mojo.authenticator.AuthType;
import git.artdeell.mojo.authenticator.accounts.Account;
import git.artdeell.mojo.authenticator.accounts.Accounts;
import git.artdeell.mojo.contracts.OpenDocumentWithExtension;
import git.artdeell.mojo.extra.ExtraConstants;
import git.artdeell.mojo.extra.ExtraCore;
import git.artdeell.mojo.instances.Instance;
import git.artdeell.mojo.instances.Instances;
import git.artdeell.mojo.prefs.LauncherPreferences;
import git.artdeell.mojo.utils.FileUtils;

import java.io.File;

public class MainMenuFragment extends Fragment {
    public static final String TAG = "MainMenuFragment";

    private static final String URL_NEWS = "https://www.minecraft.net/en-us/articles";
    private static final String URL_MODS = "https://modrinth.com/mods";

    private mcVersionSpinner mVersionSpinner;

    private final ActivityResultLauncher<Object> mModInstallerLauncher =
            registerForActivityResult(new OpenDocumentWithExtension("jar"), (data)->{
                if(data != null) Tools.launchModInstaller(requireContext(), data);
            });

    public MainMenuFragment(){
        super(R.layout.fragment_launcher);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button mNewsButton = view.findViewById(R.id.news_button);
        Button mDiscordButton = view.findViewById(R.id.social_media_button);
        Button mCustomControlButton = view.findViewById(R.id.custom_control_button);
        Button mInstallJarButton = view.findViewById(R.id.install_jar_button);
        Button mShareLogsButton = view.findViewById(R.id.share_logs_button);
        Button mOpenDirectoryButton = view.findViewById(R.id.open_files_button);

        ImageButton mEditProfileButton = view.findViewById(R.id.edit_profile_button);
        Button mPlayButton = view.findViewById(R.id.play_button);
        mVersionSpinner = view.findViewById(R.id.mc_version_spinner);

        mNewsButton.setOnClickListener(v -> Tools.openURL(requireActivity(), URL_NEWS));
        mDiscordButton.setOnClickListener(v -> Tools.openURL(requireActivity(), getString(R.string.social_media_invite)));
        mCustomControlButton.setOnClickListener(v -> startActivity(new Intent(requireContext(), CustomControlsActivity.class)));
        mInstallJarButton.setOnClickListener(v -> runInstallerWithConfirmation());
        mEditProfileButton.setOnClickListener(v -> mVersionSpinner.openProfileEditor(requireActivity()));

        mPlayButton.setOnClickListener(v -> ExtraCore.setValue(ExtraConstants.LAUNCH_GAME, true));

        mShareLogsButton.setOnClickListener((v) -> shareLog(requireContext()));

        mOpenDirectoryButton.setOnClickListener((v)-> openGameDirectory(v.getContext()));


        mNewsButton.setOnLongClickListener((v)->{
            Tools.swapFragment(requireActivity(), GamepadMapperFragment.class, GamepadMapperFragment.TAG, null);
            return true;
        });

        // Ritm: кнопки нового дизайну
        bindClick(view, R.id.hero_play_button, v -> ExtraCore.setValue(ExtraConstants.LAUNCH_GAME, true));
        bindClick(view, R.id.hero_settings_button, v -> mVersionSpinner.openProfileEditor(requireActivity()));
        bindNav(view, R.id.adv_settings_button, R.id.nav_settings);
        bindNav(view, R.id.qa_versions, R.id.nav_versions);
        bindNav(view, R.id.qa_mods, R.id.nav_mods);
        bindNav(view, R.id.qa_shaders, R.id.nav_shaders);
        bindNav(view, R.id.qa_settings, R.id.nav_settings);
        bindNav(view, R.id.acc_header, R.id.nav_accounts);
        bindNav(view, R.id.acc_manage, R.id.nav_accounts);
        bindClick(view, R.id.acc_row, v -> {
            if(requireActivity() instanceof LauncherActivity) {
                ((LauncherActivity) requireActivity()).openAccountPicker();
            }
        });
        bindClick(view, R.id.news_card_1, v -> Tools.openURL(requireActivity(), URL_NEWS));
        bindClick(view, R.id.news_card_2, v -> Tools.openURL(requireActivity(), URL_MODS));
        bindNav(view, R.id.news_card_3, R.id.nav_settings);
    }

    private void bindClick(View root, int id, View.OnClickListener listener) {
        View target = root.findViewById(id);
        if(target != null) target.setOnClickListener(listener);
    }

    private void bindNav(View root, int viewId, int navId) {
        bindClick(root, viewId, v -> {
            if(requireActivity() instanceof LauncherActivity) {
                ((LauncherActivity) requireActivity()).ritmNavigate(navId);
            }
        });
    }

    /** Ritm: підтягує версію, акаунт і пам'ять для карток головного екрана */
    private void refreshRitmInfo() {
        MojoApplication.sExecutorService.execute(() -> {
            String version = null;
            try {
                Instance instance = Instances.loadSelectedInstance();
                if(instance != null) version = instance.versionId;
            } catch (Throwable ignored) {}
            Account account = null;
            try {
                account = Accounts.getCurrent();
            } catch (Throwable ignored) {}
            final String fVersion = version;
            final Account fAccount = account;
            Tools.runOnUiThread(() -> applyRitmInfo(fVersion, fAccount));
        });
    }

    private void applyRitmInfo(@Nullable String version, @Nullable Account account) {
        View root = getView();
        if(root == null || !isAdded()) return;
        String versionText = version != null ? version : "-";
        String none = getString(R.string.ritm_acc_none);
        String name = account != null ? account.username : none;
        int typeRes = R.string.ritm_acc_local;
        if(account != null && account.authType == AuthType.MICROSOFT) typeRes = R.string.ritm_acc_microsoft;
        else if(account != null && account.authType == AuthType.ELY_BY) typeRes = R.string.ritm_acc_elyby;

        setText(root, R.id.chip_version_value, versionText);
        setText(root, R.id.qa_last_sub, getString(R.string.ritm_qa_last_sub, versionText));
        setText(root, R.id.chip_account_value, name);
        setText(root, R.id.acc_name, name);
        setText(root, R.id.acc_type, account != null ? getString(typeRes) : none);
        setText(root, R.id.chip_ram_value, getString(R.string.ritm_ram_value, LauncherPreferences.PREF_RAM_ALLOCATION));
    }

    private void setText(View root, int id, String text) {
        TextView target = root.findViewById(id);
        if(target != null) target.setText(text);
    }

    private void openGameDirectory(Context context) {
        Instance instance = Instances.loadSelectedInstance();
        if(instance == null) {
            Toast.makeText(context, R.string.no_instance, Toast.LENGTH_LONG).show();
            return;
        }
        File gameDirectory = instance.getGameDirectory();
        if(FileUtils.ensureDirectorySilently(gameDirectory)) {
            openPath(context, gameDirectory, false);
        }else {
            Toast.makeText(context, R.string.gamedir_open_failed, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ExtraCore.setValue(ExtraConstants.REFRESH_ACCOUNT_SPINNER, true);
        refreshRitmInfo();
    }

    private void runInstallerWithConfirmation() {
        if(!ProgressLayout.ensureNoOngoingTasks(requireContext())) return;
        mModInstallerLauncher.launch(null);
    }
}
