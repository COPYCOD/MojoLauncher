package git.artdeell.mojo.vulkan.driver;

public final class BuiltinDriver implements Driver {
    @Override
    public String getName() {
        return "Built-in";
    }

    @Override
    public String getMainLibrary() {
        return "libvulkan_freedreno.so";
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public String getHash() {
        return null;
    }
}
