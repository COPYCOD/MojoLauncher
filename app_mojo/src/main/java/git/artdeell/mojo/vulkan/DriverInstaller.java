package git.artdeell.mojo.vulkan;

import static git.artdeell.mojo.MojoApplication.sExecutorService;
import static git.artdeell.mojo.Tools.runOnUiThread;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import git.artdeell.mojo.R;
import git.artdeell.mojo.Tools;
import git.artdeell.mojo.vulkan.driver.Driver;

public class DriverInstaller {

    // Install AdrenoTools driver. Callback will be executed on the main ui thread after driver installation
    public static void installDriverFromUri(Context context, Uri uri, Runnable callback) {
        sExecutorService.execute(() -> {
            File f = null;
            String target;
            try (InputStream is = context.getContentResolver().openInputStream(uri)) {
                if ((target = DriverManager.validateDriver(is)) == null) {
                    runOnUiThread(() -> Toast.makeText(context, R.string.driver_config_import_invalid, Toast.LENGTH_LONG).show());
                    return;
                }
            } catch (IOException e) {
                Tools.showErrorRemote(e);
                return;
            }
            // We need to open a new clean InputStream here
            try (InputStream is = context.getContentResolver().openInputStream(uri)) {
                DriverManager.installDriverContent(target, is, true);
            } catch (IOException e) {
                Tools.showErrorRemote(e);
                return;
            }
            Tools.runOnUiThread(callback);
        });
    }

}
