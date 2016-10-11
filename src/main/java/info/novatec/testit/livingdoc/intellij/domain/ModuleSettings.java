package info.novatec.testit.livingdoc.intellij.domain;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * Service implementation for module service extension defined in <b>plugin.xml</b> with
 * <code>id="LivingDoc.Module.Service.Settings"</code>
 *
 * @see PersistentStateComponent
 */


@State(name = "LivingDoc", storages = @Storage(StoragePathMacros.MODULE_FILE))
public final class ModuleSettings implements PersistentStateComponent<ModuleSettings>, Serializable {

    private static final long serialVersionUID = 813394555374572452L;
    private boolean livingDocEnabled;
    private String project;
    private String sud;
    private String user;
    private String password;
    private String sudClassName;
    private String sudArgs;


    public static ModuleSettings getInstance(@NotNull final Module module) {
        return ModuleServiceManager.getService(module, ModuleSettings.class);
    }

    @Nullable
    @Override
    public ModuleSettings getState() {
        return this;
    }

    @Override
    public void loadState(final ModuleSettings moduleSettings) {
        XmlSerializerUtil.copyBean(moduleSettings, this);
    }

    public boolean isLivingDocEnabled() {
        return livingDocEnabled;
    }

    public void setLivingDocEnabled(final boolean livingDocEnabled) {
        this.livingDocEnabled = livingDocEnabled;
    }

    public String getProject() {
        return project;
    }

    public void setProject(final String project) {
        this.project = project;
    }

    public String getSud() {
        return sud;
    }

    public void setSud(final String sud) {
        this.sud = sud;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getSudClassName() {
        return sudClassName;
    }

    public void setSudClassName(final String sudClassName) {
        this.sudClassName = sudClassName;
    }

    public String getSudArgs() {
        return sudArgs;
    }

    public void setSudArgs(final String sudArgs) {
        this.sudArgs = sudArgs;
    }
}
