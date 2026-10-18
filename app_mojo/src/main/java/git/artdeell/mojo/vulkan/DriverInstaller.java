package git.artdeell.mojo.vulkan;

import static git.artdeell.mojo.MojoApplication.sExecutorService;
import static git.artdeell.mojo.Tools.runOnUiThread;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
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
            FileOutputStream fos = null;
            InputStream is = null;
            try {
                is = context.getContentResolver().openInputStream(uri);
                if(!DriverManager.validateDriver(is)){
                    runOnUiThread(() -> Toast.makeText(context, R.string.driver_config_import_invalid, Toast.LENGTH_LONG).show());
                    return;
                }
                // I hate InputStream
                is.close();
                is = context.getContentResolver().openInputStream(uri);
                File f = File.createTempFile("at-extract", ".tmp", context.getCacheDir());
                fos = new FileOutputStream(f);
                f.deleteOnExit();
                byte[] buffer = new byte[4096];
                int read;
                while((read = is.read(buffer)) != -1){
                    fos.write(buffer, 0, read);
                }
                fos.close();
                Driver driver = DriverManager.installDriver(f, true);
                if(driver == null) {
                    runOnUiThread(() -> Toast.makeText(context, R.string.driver_config_import_failed, Toast.LENGTH_SHORT).show());
                }
            } catch (IOException e){
                Tools.showError(context, e);
            } finally {
                try {
                    if(fos != null) fos.close();
                    if(is != null) is.close();
                    runOnUiThread(callback);
                } catch (IOException e) {
                    Tools.showError(context, e);
                }
            }
        });
    }

}
