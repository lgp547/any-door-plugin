package io.github.lgp547.anydoorplugin.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiParameterList;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TextAreaDialog extends DialogWrapper {

    private final JSONEditor textArea;
    private final ContentPanel contentPanel;

    private Runnable okAction;

    public TextAreaDialog(Project project, String title, PsiParameterList psiParameterList, String cacheContent) {
        super(project, true, IdeModalityType.MODELESS);
        setTitle(title);
        textArea = new JSONEditor(cacheContent, psiParameterList, project);
        contentPanel = new ContentPanel(textArea);
        contentPanel.addCacheButtonListener(e -> textArea.genCacheContent());
        contentPanel.addSimpleButtonListener(e -> textArea.genSimpleContent());
        contentPanel.addJsonButtonListener(e -> textArea.genJsonContent());
        contentPanel.addJsonToQueryButtonListener(e -> textArea.jsonToQuery());
        contentPanel.addQueryToJsonButtonListener(e -> textArea.queryToJson());


        init();
    }

    public void setOkAction(Runnable runnable) {
        okAction = runnable;
    }

    public String getText() {
        return textArea.getText();
    }

    @Override
    protected void doOKAction() {
        okAction.run();
        super.doOKAction();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPanel;
    }

}