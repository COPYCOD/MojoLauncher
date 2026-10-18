package git.artdeell.mojo.vulkan.driver;

public interface Driver {
    String getName();
    String getMainLibrary();
    boolean isDefault();
    String getHash();
}
