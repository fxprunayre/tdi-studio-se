// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.core.ui.editor.properties.controllers;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.DecoratedField;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IControlCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.talend.commons.ui.swt.colorstyledtext.ColorStyledText;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.MetadataTable;
import org.talend.core.model.process.EParameterFieldType;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.properties.tab.IDynamicProperty;
import org.talend.core.tis.ITDQPatternService;
import org.talend.designer.core.i18n.Messages;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.model.components.EmfComponent;
import org.talend.designer.core.ui.editor.cmd.PropertyChangeCommand;
import org.talend.designer.core.ui.editor.cmd.QueryGuessCommand;
import org.talend.designer.core.ui.editor.nodes.Node;

/**
 * DOC yzhang class global comment. Detailled comment <br/>
 * 
 * $Id: ComboController.java 1 2006-12-12 涓嬪崍01:58:48 +0000 (涓嬪崍01:58:48) yzhang $
 * 
 */
public class ComboController extends AbstractElementPropertySectionController {

    /**
     * DOC dev ColumnListController constructor comment.
     * 
     * @param parameterBean
     */
    public ComboController(IDynamicProperty dp) {
        super(dp);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.designer.core.ui.editor.properties2.editors.AbstractElementPropertySectionController#createCommand()
     */
    public Command createComboCommand(SelectionEvent event) {
        Set<String> elementsName;
        Control ctrl;
        elementsName = hashCurControls.keySet();
        for (String name : elementsName) {
            Object o = hashCurControls.get(name);
            if (o instanceof Control) {
                ctrl = (Control) o;
                if (ctrl == null) {
                    hashCurControls.remove(name);
                    return null;
                }
                CCombo combo = (CCombo) event.getSource();

                Object data = ctrl.getData(PARAMETER_NAME);
                if (!(ctrl instanceof CCombo)) {
                    continue;
                }
                boolean isDisposed = ((CCombo) ctrl).isDisposed() || combo.isDisposed();
                if (isDisposed) {
                    continue;
                }
                if (data != null && data.equals(combo.getData(PARAMETER_NAME))) {
                    if (!elem.getPropertyValue(name).equals(((CCombo) ctrl).getText())) {

                        String value = new String(""); //$NON-NLS-1$
                        for (int i = 0; i < elem.getElementParameters().size(); i++) {
                            IElementParameter param = elem.getElementParameters().get(i);
                            if (param.getName().equals(name)) {
                                for (int j = 0; j < param.getListItemsValue().length; j++) {
                                    if (j < param.getListItemsDisplayName().length
                                            && ((CCombo) ctrl).getText().equals(param.getListItemsDisplayName()[j])) {
                                        value = (String) param.getListItemsValue()[j];
                                    }
                                }
                            }
                        }
                        if (value.equals(elem.getPropertyValue(name))) { // same value so no need to do anything
                            return null;
                        }
                        return new PropertyChangeCommand(elem, name, value);
                    }
                }
            }
        }
        return null;
    }

    IControlCreator cbCtrl = new IControlCreator() {

        public Control createControl(final Composite parent, final int style) {
            CCombo cb = new CCombo(parent, style);
            return cb;
        }
    };

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.designer.core.ui.editor.properties2.editors.AbstractElementPropertySectionController#createControl()
     */
    @Override
    public Control createControl(final Composite subComposite, final IElementParameter param, final int numInRow,
            final int nbInRow, final int top, final Control lastControl) {

        DecoratedField dField = new DecoratedField(subComposite, SWT.BORDER, cbCtrl);
        if (param.isRequired()) {
            FieldDecoration decoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
                    FieldDecorationRegistry.DEC_REQUIRED);
            dField.addFieldDecoration(decoration, SWT.RIGHT | SWT.TOP, false);
        }
        if (param.isRepositoryValueUsed()) {
            FieldDecoration decoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
                    FieldDecorationRegistry.DEC_CONTENT_PROPOSAL);
            decoration.setDescription(Messages.getString("ComboController.valueFromRepository")); //$NON-NLS-1$
            dField.addFieldDecoration(decoration, SWT.RIGHT | SWT.BOTTOM, false);
        }

        Control cLayout = dField.getLayoutControl();
        CCombo combo = (CCombo) dField.getControl();
        FormData data;
        combo.setItems(getListToDisplay(param));
        combo.setEditable(false);
        cLayout.setBackground(subComposite.getBackground());
        combo.setEnabled(!param.isReadOnly());
        combo.addSelectionListener(listenerSelection);
        combo.setData(PARAMETER_NAME, param.getName());
        if (elem instanceof Node) {
            combo.setToolTipText(VARIABLE_TOOLTIP + param.getVariableName());
        }

        CLabel labelLabel = getWidgetFactory().createCLabel(subComposite, param.getDisplayName());
        data = new FormData();
        if (lastControl != null) {
            data.left = new FormAttachment(lastControl, 0);
        } else {
            data.left = new FormAttachment((((numInRow - 1) * MAX_PERCENT) / nbInRow), 0);
        }

        data.top = new FormAttachment(0, top);
        labelLabel.setLayoutData(data);
        if (numInRow != 1) {
            labelLabel.setAlignment(SWT.RIGHT);
        }
        // *********************
        data = new FormData();
        int currentLabelWidth = STANDARD_LABEL_WIDTH;
        GC gc = new GC(labelLabel);
        Point labelSize = gc.stringExtent(param.getDisplayName());
        gc.dispose();

        if ((labelSize.x + ITabbedPropertyConstants.HSPACE) > currentLabelWidth) {
            currentLabelWidth = labelSize.x + ITabbedPropertyConstants.HSPACE;
        }

        if (numInRow == 1) {
            if (lastControl != null) {
                data.left = new FormAttachment(lastControl, currentLabelWidth);
            } else {
                data.left = new FormAttachment(0, currentLabelWidth);
            }

        } else {
            data.left = new FormAttachment(labelLabel, 0, SWT.RIGHT);
        }
        data.top = new FormAttachment(0, top);
        cLayout.setLayoutData(data);
        // **********************
        hashCurControls.put(param.getName(), combo);

        Point initialSize = dField.getLayoutControl().computeSize(SWT.DEFAULT, SWT.DEFAULT);
        dynamicProperty.setCurRowSize(initialSize.y + ITabbedPropertyConstants.VSPACE);
        return cLayout;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.designer.core.ui.editor.properties.controllers.AbstractElementPropertySectionController#estimateRowSize
     * (org.eclipse.swt.widgets.Composite, org.talend.core.model.process.IElementParameter)
     */
    @Override
    public int estimateRowSize(Composite subComposite, IElementParameter param) {
        DecoratedField dField = new DecoratedField(subComposite, SWT.BORDER, cbCtrl);
        Point initialSize = dField.getLayoutControl().computeSize(SWT.DEFAULT, SWT.DEFAULT);
        dField.getLayoutControl().dispose();

        return initialSize.y + ITabbedPropertyConstants.VSPACE;
    }

    // /**
    // * This method is used for creating a Button named "Guess Query".
    // *
    // * @param subComposite
    // * @param lastControl
    // * @param numInRow
    // * @param top
    // */
    // private void addGuessQueryButton(Composite subComposite, IElementParameter param, Control lastControl, int
    // numInRow, int top) {
    // final DecoratedField dField1 = new DecoratedField(subComposite, SWT.PUSH, new IControlCreator() {
    //
    // public Control createControl(Composite parent, int style) {
    // return new Button(parent, style);
    // }
    // });
    // Button guessQueryButton = null;
    // Control buttonControl = dField1.getLayoutControl();
    // guessQueryButton = (Button) dField1.getControl();
    // guessQueryButton.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    // buttonControl.setBackground(subComposite.getBackground());
    // guessQueryButton.setEnabled(true);
    // guessQueryButton.setData(NAME, GUESS_QUERY_NAME);
    // guessQueryButton.setData(PARAMETER_NAME, param.getName());
    // guessQueryButton.setText(GUESS_QUERY_NAME);
    //
    // FormData data1 = new FormData();
    // data1.left = new FormAttachment(lastControl, 210);
    // data1.top = new FormAttachment(0, top);
    //
    // buttonControl.setLayoutData(data1);
    // guessQueryButton.addSelectionListener(listenerSelection);
    // queryButton.put(param, guessQueryButton);
    //
    // }

    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        // TODO Auto-generated method stub
    }

    SelectionListener listenerSelection = new SelectionAdapter() {

        public void widgetSelected(SelectionEvent event) {
            // dynamicProperty.updateRepositoryList();
            Command cmd = createCommand(event);
            executeCommand(cmd);
            IElementParameter elementParameterFromField = elem.getElementParameterFromField(EParameterFieldType.MEMO_SQL);
            if (elementParameterFromField != null) {
                Object value = elementParameterFromField.getValue();
                Object object = hashCurControls.get(elementParameterFromField.getName());
                if (object instanceof ColorStyledText) {
                    ColorStyledText text = (ColorStyledText) object;
                    text.setText(value.toString());
                }
            }
            // dynamicProperty.refresh();

        }
    };

    /**
     * This method is used for getting command base on component type.
     * 
     * @param e
     * @return
     */
    private Command createCommand(SelectionEvent event) {
        Command cmd = null;
        if (event.getSource() instanceof CCombo) {
            cmd = createComboCommand(event);
        } else if (event.getSource() instanceof Button) {
            cmd = createButtonCommand();
        }
        return cmd;
    }

    /**
     * This method is used for "Guess Query" button.
     * 
     * @return
     */
    private Command createButtonCommand() {
        Map<String, IMetadataTable> repositoryTableMap = dynamicProperty.getRepositoryTableMap();
        IMetadataTable repositoryMetadata = null;
        IMetadataTable newRepositoryMetadata = null;
        String realTableName = null;
        String realTableId = null;

        // Only for getting the real table name.
        if (elem.getPropertyValue(EParameterName.SCHEMA_TYPE.getName()).equals(EmfComponent.REPOSITORY)) {
            String paramName;
            IElementParameter repositorySchemaTypeParameter = elem.getElementParameter(EParameterName.REPOSITORY_SCHEMA_TYPE
                    .getName());
            Object repositoryControl = hashCurControls.get(repositorySchemaTypeParameter.getName());

            paramName = EParameterName.REPOSITORY_SCHEMA_TYPE.getName();

            if (repositoryControl != null) {

                String selectedComboItem = ((CCombo) repositoryControl).getText();
                if (selectedComboItem != null && selectedComboItem.length() > 0) {
                    String value = new String(""); //$NON-NLS-1$
                    for (int i = 0; i < elem.getElementParameters().size(); i++) {
                        IElementParameter param = elem.getElementParameters().get(i);
                        if (param.getName().equals(paramName)) {
                            for (int j = 0; j < param.getListItemsValue().length; j++) {
                                if (selectedComboItem.equals(param.getListItemsDisplayName()[j])) {
                                    value = (String) param.getListItemsValue()[j];
                                }
                            }
                        }
                    }
                    if (elem instanceof Node) {
                        // this.dynamicProperty.updateRepositoryList();
                        if (repositoryTableMap.containsKey(value)) {
                            repositoryMetadata = repositoryTableMap.get(value);
                            realTableName = repositoryMetadata.getTableName();
                            realTableId = repositoryMetadata.getId();
                        } else {
                            repositoryMetadata = new MetadataTable();
                        }
                    }
                }
            }
        }// Ends

        QueryGuessCommand cmd = null;
        Node node = null;
        if (elem instanceof Node) {
            node = (Node) elem;
        } else { // else instanceof Connection
            node = ((org.talend.designer.core.ui.editor.connections.Connection) elem).getSource();
        }

        newRepositoryMetadata = node.getMetadataList().get(0);

        if (newRepositoryMetadata == null) {
            String schemaSelected = (String) node.getPropertyValue(EParameterName.REPOSITORY_SCHEMA_TYPE.getName());
            if (repositoryTableMap != null && schemaSelected != null && repositoryTableMap.containsKey(schemaSelected)) {
                repositoryMetadata = repositoryTableMap.get(schemaSelected);
            } else if (newRepositoryMetadata == null) {
                MessageDialog.openWarning(new Shell(),
                        Messages.getString("ComboController.alert"), Messages.getString("ComboController.nothingGuess")); //$NON-NLS-1$ //$NON-NLS-2$
                return cmd;
            }
        }
        cmd = new QueryGuessCommand(node, newRepositoryMetadata);

        cmd.setMaps(dynamicProperty.getTableIdAndDbTypeMap(), dynamicProperty.getTableIdAndDbSchemaMap(), repositoryTableMap);
        String type = getValueFromRepositoryName("TYPE"); //$NON-NLS-1$
        cmd.setParameters(realTableId, realTableName, type);

        return cmd;
    }

    @Override
    public void refresh(IElementParameter param, boolean check) {
        String paramName = param.getName();
        CCombo combo = (CCombo) hashCurControls.get(paramName);

        if (combo == null || combo.isDisposed()) {
            return;
        }
        // for feature 8147
        try {
            ITDQPatternService service = null;
            try {
                service = (ITDQPatternService) GlobalServiceRegister.getDefault().getService(ITDQPatternService.class);
            } catch (RuntimeException e) {
                // nothing to do
            }
            if (service != null && elem instanceof Node) {
                String dbtype = "java";

                Node node = (Node) elem;

                IElementParameter propertyParam = node.getElementParameter("TYPE");
                if (propertyParam != null) {
                    dbtype = propertyParam.getValue().toString();
                }

                String[][][] tdqPatterns = service.retrieveTDQPatterns();
                if (tdqPatterns != null) {
                    if (StringUtils.equals(paramName, "PATTERN_LIST")) { //$NON-NLS-1$
                        Map<String, String> patternMap = retrievePattern(dbtype, tdqPatterns);

                        // set into paramlist
                        addPatternToComponent(param, patternMap);
                    }
                }
            }

        } catch (Exception e) {
            // nothing to do
        }

        Object value = param.getValue();

        if (value instanceof String) {
            String strValue = ""; //$NON-NLS-1$
            int nbInList = 0, nbMax = param.getListItemsValue().length;
            String name = (String) value;
            while (strValue.equals(new String("")) && nbInList < nbMax) { //$NON-NLS-1$
                if (name.equals(param.getListItemsValue()[nbInList])) {
                    strValue = param.getListItemsDisplayName()[nbInList];
                }
                nbInList++;
            }
            String[] paramItems = getListToDisplay(param);
            String[] comboItems = combo.getItems();

            if (!Arrays.equals(paramItems, comboItems)) {
                combo.setItems(paramItems);
            }
            combo.setText(strValue);
            combo.setVisible(true);
        }

        if (param.isContextMode()) {
            combo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_YELLOW));
            combo.setEnabled(false);
        }

    }

    /**
     * DOC bZhou Comment method "addPatternToComponent".
     * 
     * @param param
     * @param patternMap
     */
    private void addPatternToComponent(IElementParameter param, Map<String, String> patternMap) {
        param.setListItemsDisplayCodeName(patternMap.keySet().toArray(new String[patternMap.size()]));
        param.setListItemsValue(patternMap.values().toArray(new String[patternMap.size()]));
        param.setListItemsDisplayName(patternMap.keySet().toArray(new String[patternMap.size()]));
        param.setListItemsNotShowIf(new String[patternMap.size()]);
        param.setListItemsShowIf(new String[patternMap.size()]);
    }

    /**
     * DOC bZhou Comment method "retrievePattern".
     * 
     * @param dbtype
     * @param tdqPatterns
     * @return
     */
    private Map<String, String> retrievePattern(String dbtype, String[][][] tdqPatterns) {
        Map<String, String> patternMap = new HashMap<String, String>();

        for (String[][] pattern : tdqPatterns) {
            String properExpression = null;
            String label = pattern[0][0];
            for (String[] expression : pattern) {
                if (StringUtils.equalsIgnoreCase(expression[2], dbtype)) {
                    properExpression = expression[1];
                }
            }

            if (properExpression == null) {
                for (String[] expression : pattern) {
                    if (StringUtils.equalsIgnoreCase(expression[2], "sql")) {
                        properExpression = expression[1];
                    }
                }
            }

            if (properExpression != null) {
                patternMap.put(label, properExpression);
            }
        }
        return patternMap;
    }

    private String[] getListToDisplay(IElementParameter param) {
        String[] originalList = param.getListItemsDisplayName();
        List<String> stringToDisplay = new ArrayList<String>();
        String[] itemsShowIf = param.getListItemsShowIf();
        if (itemsShowIf != null) {
            String[] itemsNotShowIf = param.getListItemsNotShowIf();
            for (int i = 0; i < originalList.length; i++) {
                if (param.isShow(itemsShowIf[i], itemsNotShowIf[i], elem.getElementParameters())) {
                    stringToDisplay.add(originalList[i]);
                }
            }
        } else {
            for (int i = 0; i < originalList.length; i++) {
                stringToDisplay.add(originalList[i]);
            }
        }
        return stringToDisplay.toArray(new String[0]);
    }
}
