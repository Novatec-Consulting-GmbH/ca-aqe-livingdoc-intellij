package org.livingdoc.intellij.run;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.livingdoc.intellij.common.PluginProperties;

import java.io.File;
import java.io.IOException;

/**
 * To create the files used in LivingDoc execution.<br>
 * NOTE: File names are configured in <b>config.properties</b>
 */
class FilesManager {

    private static final Logger LOG = Logger.getInstance(FilesManager.class);

    private static final String HTML = ".html";
    private static final String XML = ".xml";
    private static final String SEPARATOR = "_";

    private final RemoteRunConfiguration runConfiguration;

    /**
     * @param runConfiguration {@link RemoteRunConfiguration}
     */
    public FilesManager(@NotNull final RemoteRunConfiguration runConfiguration) {
        this.runConfiguration = runConfiguration;
    }

    /**
     * Return the temporal <b>specification</b> file.<br>
     * The file is created whether it doesn't exist.
     *
     * @return {@link File}
     * @throws IOException If an I/O error occurred
     */
    public File createSpecificationFile() throws IOException {

        return createFile(PluginProperties.getValue("livingdoc.file.specification"), HTML);
    }

    /**
     * Return the temporal <b>report</b> file.<br>
     * The file is created whether it doesn't exist.
     *
     * @return {@link File}
     * @throws IOException If an I/O error occurred
     */
    public File createReportFile() throws IOException {

        return createFile(PluginProperties.getValue("livingdoc.file.report"), XML);
    }

    /**
     * Return the temporal <b>result</b> file.<br>
     * The file is created whether it doesn't exist.
     *
     * @return {@link File}
     * @throws IOException If an I/O error occurred
     */
    public File createResultFile() throws IOException {

        return createFile(PluginProperties.getValue("livingdoc.file.results"), HTML);
    }

    private File createFile(final String fileType, final String extension) throws IOException {

        File file = new File(getLivingDocDir(), buildFileName(fileType, extension));

        if (!file.exists() && !file.createNewFile()) {
            LOG.error("The file " + fileType + " has not been created.");
        }

        return file;
    }

    /**
     * Returns the parent folder path for the created files.<br>
     * If it does not exist, it is created only once under the select module.
     *
     * @return String
     */
    private String getLivingDocDir() {

        Module selectedModule = runConfiguration.getConfigurationModule().getModule();
        assert selectedModule != null;
        String[] contentRootUrls = ModuleRootManager.getInstance(selectedModule).getContentRootUrls();

        VirtualFile parentDir = VirtualFileManager.getInstance().findFileByUrl(contentRootUrls[0]);

        String folderName = PluginProperties.getValue("livingdoc.dir.project");
        assert parentDir != null;
        final VirtualFile[] livingDocDir = {parentDir.findChild(folderName)};

        if (livingDocDir[0] == null) {
            Application application = ApplicationManager.getApplication();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        livingDocDir[0] = parentDir.createChildDirectory(this, folderName);
                        LOG.info("Folder created: " + livingDocDir[0].getPath());

                    } catch (IOException ioe) {
                        LOG.error(ioe);
                    }
                }
            };
            if (application.isDispatchThread()) {
                application.runWriteAction(runnable);
            } else {
                application.invokeLater(() -> application.runWriteAction(runnable));
            }
        }
        return PathUtil.toSystemDependentName(livingDocDir[0].getPath());
    }

    private String buildFileName(final String fileType, final String extension) {
        String prefix = runConfiguration.getRepositoryUID().replaceAll("\\\\", SEPARATOR).replaceAll("/", SEPARATOR).replaceAll("-", SEPARATOR);
        String altName = runConfiguration.getSpecificationName().replaceAll("\\\\", SEPARATOR).replaceAll("/", SEPARATOR).replaceAll("\"", "''");
        return String.format("%s_%s_%s%s", prefix, altName, fileType, extension);
    }
}
