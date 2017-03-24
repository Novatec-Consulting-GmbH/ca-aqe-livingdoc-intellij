package org.livingdoc.intellij.gui.settings;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.livingdoc.intellij.common.I18nSupport;
import org.livingdoc.intellij.domain.ModuleSettings;
import org.livingdoc.intellij.domain.ProjectSettings;
import org.livingdoc.intellij.gui.GuiUtils;
import org.livingdoc.intellij.rest.PluginLivingDocRestClient;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

/**
 * Graphical user interface for the module settings.<br>
 *
 * @see ModuleSettings
 */
public class ModuleSettingsEditor extends SettingsEditor<ModuleSettings> {

    private static final Logger LOG = Logger.getInstance(ModuleSettings.class);
    private final ProjectSettings projectSettings;
    private JPanel myWholePanel;
    private JPanel centerPanel;
    private JPanel southPanel;
    private JPanel mainPanel;
    private JBCheckBox livingDocEnabledCheck;
    private ComboBox<String> projectCombo;
    private ComboBox<String> sudCombo;
    private JBTextField classField;
    private JBTextField argsField;
    private JBLabel errorLabel;


    public ModuleSettingsEditor(@NotNull final Project project) {

        super(project);
        add(myWholePanel, BorderLayout.CENTER);

        errorLabel.setForeground(Color.RED);

        centerPanel.setBorder(GuiUtils.createTitledBorder(I18nSupport.getValue("module.settings.title")));
        southPanel.setBorder(GuiUtils.createTitledBorder(I18nSupport.getValue("module.settings.sud.title")));

        projectSettings = ProjectSettings.getInstance(project);
    }

    @Override
    public void apply(@NotNull final ModuleSettings moduleSettings) {
        moduleSettings.setLivingDocEnabled(livingDocEnabledCheck.isSelected());
        moduleSettings.setProject((String) projectCombo.getSelectedItem());
        moduleSettings.setSud((String) sudCombo.getSelectedItem());
        moduleSettings.setSudClassName(classField.getText());
        moduleSettings.setSudArgs(argsField.getText());
    }

    @Override
    public boolean isModified(@NotNull final ModuleSettings moduleSettings) {

        enableOrDisablePanel();

        boolean isModifiedFactory = !StringUtils.equals(moduleSettings.getSudClassName(), String.valueOf(classField.getText()))
                || !StringUtils.equals(moduleSettings.getSudArgs(), String.valueOf(argsField.getText()));

        return moduleSettings.isLivingDocEnabled() != livingDocEnabledCheck.isSelected()
                || !StringUtils.equals(moduleSettings.getProject(), (String) projectCombo.getSelectedItem())
                || !StringUtils.equals(moduleSettings.getSud(), (String) sudCombo.getSelectedItem())
                || isModifiedFactory;
    }

    @Override
    public void reset(@NotNull final ModuleSettings moduleSettings) {

        boolean isLivingDocEnabled = moduleSettings.isLivingDocEnabled();
        livingDocEnabledCheck.setSelected(isLivingDocEnabled);

        projectCombo.addActionListener(actionEvent -> loadSud(moduleSettings.getSud()));
        livingDocEnabledCheck.addChangeListener(actionEvent -> enableOrDisablePanel());

        if (projectSettings.isConnected()) {
            loadProjects(moduleSettings.getProject());
        } else {
            errorLabel.setText(I18nSupport.getValue("module.settings.error.loading.project.unauthorized"));
        }

        classField.setText(moduleSettings.getSudClassName());
        argsField.setText(moduleSettings.getSudArgs());
    }

    private void loadProjects(final String selectedProject) {

        PluginLivingDocRestClient service = new PluginLivingDocRestClient(projectSettings);

        try {
            Set<info.novatec.testit.livingdoc.server.domain.Project> projects = service.getAllProjects();

            if (CollectionUtils.isEmpty(projects)) {
                LOG.info(I18nSupport.getValue("module.settings.error.loading.noprojects"));
                errorLabel.setText(I18nSupport.getValue("module.settings.error.loading.noprojects"));
                errorLabel.setIcon(AllIcons.General.Error);

            } else {
                for (info.novatec.testit.livingdoc.server.domain.Project prj : projects) {
                    projectCombo.addItem(prj.getName());
                }
                if (StringUtils.isNotBlank(selectedProject)) {
                    projectCombo.setSelectedItem(selectedProject);
                } else {
                    projectCombo.setSelectedIndex(0);
                }
            }
        } catch (HttpServerErrorException | LivingDocServerException ldse) {
            LOG.warn(ldse);
            errorLabel.setText(I18nSupport.getValue("module.settings.error.loading.project"));
            errorLabel.setIcon(AllIcons.General.Error);

        } catch (HttpClientErrorException hcee) {
            LOG.warn(hcee);
            errorLabel.setText(I18nSupport.getValue("module.settings.error.loading.project.unauthorized"));
            errorLabel.setIcon(AllIcons.General.Error);
        }
    }

    private void loadSud(final String selectedSud) {

        sudCombo.removeAllItems();

        String selectedProject = (String) projectCombo.getSelectedItem();
        if (StringUtils.isBlank(selectedProject)) {
            return;
        }

        try {
            PluginLivingDocRestClient service = new PluginLivingDocRestClient(projectSettings);
            Set<SystemUnderTest> systems = service.getSystemUnderTestsOfProject(selectedProject);
            for (SystemUnderTest system : systems) {
                sudCombo.addItem(system.getName());
            }
            if (StringUtils.isNotBlank(selectedSud)) {
                sudCombo.setSelectedItem(selectedSud);

            } else {
                sudCombo.setSelectedIndex(0);
            }
        } catch (LivingDocServerException ldse) {
            LOG.warn(ldse);
            errorLabel.setText(I18nSupport.getValue("module.settings.error.loading.systems"));
            errorLabel.setIcon(AllIcons.General.Error);
        }
    }

    private void enableOrDisablePanel() {

        boolean isEnable = livingDocEnabledCheck.isSelected();

        mainPanel.setEnabled(isEnable);

        Component[] components = centerPanel.getComponents();
        components = ArrayUtils.addAll(components, southPanel.getComponents());
        for (Component component : components) {
            component.setEnabled(isEnable);
        }
    }
}
