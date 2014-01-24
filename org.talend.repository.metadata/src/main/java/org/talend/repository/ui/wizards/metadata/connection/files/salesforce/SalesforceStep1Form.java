// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.ui.wizards.metadata.connection.files.salesforce;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.ui.swt.dialogs.ErrorDialogWidthDetailArea;
import org.talend.commons.ui.swt.formtools.Form;
import org.talend.commons.ui.swt.formtools.LabelledCombo;
import org.talend.commons.ui.swt.formtools.LabelledText;
import org.talend.commons.ui.swt.formtools.UtilsButton;
import org.talend.core.model.metadata.IMetadataContextModeManager;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.utils.TalendPropertiesUtil;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.designer.core.model.utils.emf.talendfile.ContextType;
import org.talend.repository.metadata.i18n.Messages;
import org.talend.repository.preview.SalesforceSchemaBean;
import org.talend.repository.ui.swt.utils.AbstractSalesforceStepForm;
import org.talend.repository.ui.utils.ConnectionContextHelper;
import org.talend.salesforce.oauth.OAuthClient;
import org.talend.salesforce.oauth.Token;

/**
 * DOC YeXiaowei class global comment. Detailled comment <br/>
 * 
 */
public class SalesforceStep1Form extends AbstractSalesforceStepForm {

    private String endPoint = null;

    private String username = null;

    private String pwd = null;

    private String batchSize = null;

    private String timeOut = null;

    private LabelledText webServiceUrlText = null;

    private LabelledText userNameText = null;

    private LabelledText passwordText = null;

    private LabelledText batchSizeText = null;

    private LabelledText timeOutText = null;

    private UtilsButton cancelButton = null;

    private Button useProxyBtn = null;

    private Button useHttpBtn = null;

    private LabelledText proxyHostText = null;

    private LabelledText proxyPortText = null;

    private LabelledText proxyUsernameText = null;

    private LabelledText proxyPasswordText = null;

    private LabelledCombo authBtn = null;

    private static final String[] authCombo = new String[] { "Basic", "OAuth2" };

    private static final String BASIC = "basic";

    private static final String OAUTH = "OAuth2";

    private LabelledText webServiceUrlTextForOAuth = null;

    private LabelledText apiVersionText = null;

    private LabelledText consumeKeyText = null;

    private LabelledText consumeKeySecretText = null;

    private LabelledText callbackHostText = null;

    private LabelledText callbackPortText = null;

    private LabelledText tokenText = null;

    private String endPointForOAuth = null;

    private String apiVersion = null;

    private String consumeKey = null;

    private String consumeKeySrcret = null;

    private String callbackHost = null;

    private String callbackPort = null;

    private String token = null;

    private StackLayout stackLayout;

    private Composite basicComposite;

    private Composite oauthComposite;

    private Group authGroup;

    private Composite stackComposite;

    /*
     * 
     */

    private Button checkButton = null;

    private boolean loginOk = false;

    private boolean readOnly;

    private final char pwdEhcoChar = '*';

    private SalesforceModuleParseAPI salesforceAPI = new SalesforceModuleParseAPI();

    Object[] modulename = null;

    Object[] standardModulename = null;

    private SalesforceModuleParseAPI salesforceModuleParseAPI = null;

    private String code;

    public SalesforceModuleParseAPI getSalesforceModuleParseAPI() {
        return this.salesforceModuleParseAPI;
    }

    public void setSalesforceModuleParseAPI(SalesforceModuleParseAPI salesforceModuleParseAPI) {
        this.salesforceModuleParseAPI = salesforceModuleParseAPI;
    }

    /**
     * DOC YeXiaowei SalesforceStep1Form constructor comment.
     * 
     * @param parent
     * @param connectionItem
     * @param existingNames
     */
    public SalesforceStep1Form(Composite parent, ConnectionItem connectionItem, String[] existingNames,
            SalesforceModuleParseAPI salesforceAPI, IMetadataContextModeManager contextModeManager) {
        super(parent, connectionItem, existingNames, salesforceAPI);
        setConnectionItem(connectionItem);
        setContextModeManager(contextModeManager);
        setupForm(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.swt.utils.AbstractForm#adaptFormToReadOnly()
     */
    @Override
    protected void adaptFormToReadOnly() {
        readOnly = isReadOnly();
        updateStatus(IStatus.OK, ""); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.swt.utils.AbstractForm#addFields()
     */
    @Override
    protected void addFields() {

        Group group = Form.createGroup(this, 3, Messages.getString("SalesforceStep1Form.SalesforceParam")); //$NON-NLS-1$

        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        group.setLayoutData(data);

        authGroup = Form.createGroup(group, 4, Messages.getString("SalesforceStep1Form.AuthParam")); //$NON-NLS-1$
        GridData authLayoutData = new GridData(GridData.FILL_HORIZONTAL);
        authLayoutData.horizontalSpan = 4;
        authGroup.setLayoutData(authLayoutData);

        authBtn = new LabelledCombo(authGroup, Messages.getString("SalesforceStep1Form.authBtn"), "", authCombo, 1, false);
        authBtn.select(0);

        stackComposite = new Composite(authGroup, SWT.NONE);
        authLayoutData = new GridData(GridData.FILL_HORIZONTAL);
        authLayoutData.horizontalSpan = 4;
        stackComposite.setLayoutData(authLayoutData);
        stackLayout = new StackLayout();
        stackComposite.setLayout(stackLayout);

        basicComposite = new Composite(stackComposite, SWT.NONE);
        authLayoutData = new GridData(GridData.FILL_HORIZONTAL);
        authLayoutData.horizontalSpan = 4;
        basicComposite.setLayoutData(authLayoutData);
        basicComposite.setLayout(new GridLayout(4, false));

        webServiceUrlText = new LabelledText(basicComposite, Messages.getString("SalesforceStep1Form.webURL"), 3, true); //$NON-NLS-1$

        userNameText = new LabelledText(basicComposite, Messages.getString("SalesforceStep1Form.Username"), 3); //$NON-NLS-1$

        passwordText = new LabelledText(basicComposite, Messages.getString("SalesforceStep1Form.Password"), 3); //$NON-NLS-1$
        passwordText.getTextControl().setEchoChar(pwdEhcoChar);

        oauthComposite = new Composite(stackComposite, SWT.NONE);
        authLayoutData = new GridData(GridData.FILL_HORIZONTAL);
        authLayoutData.horizontalSpan = 4;
        oauthComposite.setLayoutData(authLayoutData);
        oauthComposite.setLayout(new GridLayout(4, false));

        webServiceUrlTextForOAuth = new LabelledText(oauthComposite, Messages.getString("webServiceUrlTextForOAuth"), 3);
        consumeKeyText = new LabelledText(oauthComposite, Messages.getString("SalesforceStep1Form.ConsumeKey"), 1);
        consumeKeySecretText = new LabelledText(oauthComposite, Messages.getString("SalesforceStep1Form.ConsumeKeySecret"), 1);
        callbackHostText = new LabelledText(oauthComposite, Messages.getString("SalesforceStep1Form.CallbackHost"), 1);
        callbackPortText = new LabelledText(oauthComposite, Messages.getString("SalesforceStep1Form.CallbackPort"), 1);
        apiVersionText = new LabelledText(oauthComposite, Messages.getString("SalesforceStep1Form.Version"), 1);

        Composite tokenComposite = new Composite(oauthComposite, SWT.NONE);
        authLayoutData = new GridData(GridData.FILL_HORIZONTAL);
        authLayoutData.horizontalSpan = 2;
        tokenComposite.setLayoutData(authLayoutData);
        GridLayout tokenLayout = new GridLayout(3, false);
        tokenComposite.setLayout(tokenLayout);

        tokenText = new LabelledText(tokenComposite, Messages.getString("SalesforceStep1Form.Token"), 1);
        Button tokenButton = new Button(tokenComposite, SWT.NONE);
        tokenButton.setText("...");
        tokenButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
                dialog.setFilterExtensions(new String[] { "*.*" });
                dialog.setText("");
                dialog.setFileName("token.properties");
                String filename = dialog.open();
                filename = filename.replace("\\", "/");
                tokenText.setText(filename);
            }

        });

        stackLayout.topControl = basicComposite;

        batchSizeText = new LabelledText(group, Messages.getString("SalesforceStep1Form.BatchSize"), 2); //$NON-NLS-1$
        timeOutText = new LabelledText(group, Messages.getString("SalesforceStep1Form.TimeOutTitle"), 2); //$NON-NLS-1$

        Group proxyGroup = Form.createGroup(group, 4, Messages.getString("SalesforceStep1Form.SocksProxyParam")); //$NON-NLS-1$
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 4;
        proxyGroup.setLayoutData(layoutData);

        useProxyBtn = new Button(proxyGroup, SWT.CHECK);
        useProxyBtn.setText(Messages.getString("SalesforceStep1Form.EnabledProxy")); //$NON-NLS-1$

        useHttpBtn = new Button(proxyGroup, SWT.CHECK);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 3;
        useHttpBtn.setLayoutData(layoutData);
        useHttpBtn.setText(Messages.getString("SalesforceStep1Form.EnabledHttpProxy")); //$NON-NLS-1$

        proxyHostText = new LabelledText(proxyGroup, Messages.getString("SalesforceStep1Form.ProxyHost")); //$NON-NLS-1$
        proxyPortText = new LabelledText(proxyGroup, Messages.getString("SalesforceStep1Form.ProxyPort")); //$NON-NLS-1$
        proxyUsernameText = new LabelledText(proxyGroup, Messages.getString("SalesforceStep1Form.ProxyUsername")); //$NON-NLS-1$
        proxyPasswordText = new LabelledText(proxyGroup, Messages.getString("SalesforceStep1Form.ProxyPassword")); //$NON-NLS-1$
        enableProxyParameters(false);

        new Label(this, SWT.NONE);

        checkButton = new Button(this, SWT.NONE);
        checkButton.setText(Messages.getString("SalesforceStep1Form.checkLogin")); //$NON-NLS-1$
        checkButton.setEnabled(false);

        GridData wd = new GridData();
        wd.horizontalSpan = 3;
        wd.horizontalAlignment = GridData.CENTER;

        checkButton.setLayoutData(wd);

        if (!isInWizard()) {
            Composite compositeBottomButton = Form.startNewGridLayout(this, 2, false, SWT.CENTER, SWT.CENTER);
            cancelButton = new UtilsButton(compositeBottomButton, Messages.getString("CommonWizard.cancel"), WIDTH_BUTTON_PIXEL, //$NON-NLS-1$
                    HEIGHT_BUTTON_PIXEL);
        }
        addUtilsButtonListeners();
        setSize(600, 700);

    }

    /**
     * DOC YeXiaowei Comment method "setCheckEnable".
     */
    private void setCheckEnable() {
        if (authBtn.getSelectionIndex() == 0) {
            checkButton.setEnabled(isValueValid(webServiceUrlText.getText()) && isValueValid(userNameText.getText())
                    && isValueValid(passwordText.getText()));
        } else {
            checkButton.setEnabled(isValueValid(webServiceUrlTextForOAuth.getText()) && isValueValid(consumeKeyText.getText())
                    && isValueValid(consumeKeySecretText.getText()) && isValueValid(callbackHostText.getText())
                    && isValueValid(callbackPortText.getText()) && isValueValid(apiVersionText.getText())
                    && isValueValid(tokenText.getText()));
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.swt.utils.AbstractForm#addFieldsListeners()
     */
    @Override
    protected void addFieldsListeners() {

        webServiceUrlText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (!isContextMode()) {
                    loginOk = false;
                    checkFieldsValue();
                    getConnection().setWebServiceUrl(webServiceUrlText.getText());
                    setCheckEnable();
                }
            }
        });

        userNameText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (!isContextMode()) {
                    loginOk = false;
                    checkFieldsValue();
                    getConnection().setUserName(userNameText.getText());
                    setCheckEnable();
                }
            }
        });

        passwordText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (!isContextMode()) {
                    loginOk = false;
                    checkFieldsValue();
                    getConnection().setPassword(passwordText.getText());
                    setCheckEnable();
                }
            }
        });
        batchSizeText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (!isContextMode()) {
                    loginOk = false;
                    checkFieldsValue();
                    getConnection().setBatchSize(batchSizeText.getText());
                    setCheckEnable();
                }
            }
        });
        timeOutText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (!isContextMode()) {
                    loginOk = false;
                    checkFieldsValue();
                    String timeOutStr = timeOutText.getText();
                    if (!"".equals(timeOutStr)) { //$NON-NLS-1$
                        try {
                            Integer.parseInt(timeOutStr);
                            getConnection().setTimeOut(timeOutStr);
                        } catch (NumberFormatException e1) {
                            updateStatus(IStatus.ERROR, Messages.getString("SalesforceStep1Form.TimeOutErrorMessage")); //$NON-NLS-1$
                        }
                    }

                    setCheckEnable();
                }
            }
        });
        useProxyBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                boolean selection = useProxyBtn.getSelection();
                checkFieldsValue();
                enableProxyParameters(selection);
                getConnection().setUseProxy(selection);
                if (selection && useHttpBtn.getSelection()) {
                    getConnection().setUseHttpProxy(false);
                    useHttpBtn.setSelection(false);
                }

            }

        });
        useHttpBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                boolean selection = useHttpBtn.getSelection();
                checkFieldsValue();
                enableProxyParameters(selection);
                getConnection().setUseHttpProxy(selection);
                if (selection && useProxyBtn.getSelection()) {
                    getConnection().setUseProxy(false);
                    useProxyBtn.setSelection(false);
                }
            }
        });
        proxyHostText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (!isContextMode()) {
                    checkFieldsValue();
                    getConnection().setProxyHost(proxyHostText.getText());
                }
            }

        });
        proxyPortText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (!isContextMode()) {
                    checkFieldsValue();
                    getConnection().setProxyPort(proxyPortText.getText());
                }
            }

        });
        proxyUsernameText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (!isContextMode()) {
                    checkFieldsValue();
                    getConnection().setProxyUsername(proxyUsernameText.getText());
                }
            }

        });
        proxyPasswordText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (!isContextMode()) {
                    checkFieldsValue();
                    getConnection().setProxyPassword(proxyPasswordText.getText());
                }
            }

        });
        authBtn.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (!isContextMode()) {
                    loginOk = false;
                    checkFieldsValue();
                    getConnection().setLoginType(authBtn.getItem(authBtn.getSelectionIndex()));
                    setCheckEnable();
                }
            }
        });
        webServiceUrlTextForOAuth.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (!isContextMode()) {
                    loginOk = false;
                    checkFieldsValue();
                    getConnection().setWebServiceUrlTextForOAuth(webServiceUrlTextForOAuth.getText());
                    setCheckEnable();
                }
            }
        });
        consumeKeyText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (!isContextMode()) {
                    loginOk = false;
                    checkFieldsValue();
                    getConnection().setConsumeKey(consumeKeyText.getText());
                    setCheckEnable();
                }
            }
        });
        consumeKeySecretText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (!isContextMode()) {
                    loginOk = false;
                    checkFieldsValue();
                    getConnection().setConsumeSecret(consumeKeySecretText.getText());
                    setCheckEnable();
                }
            }
        });
        callbackHostText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (!isContextMode()) {
                    loginOk = false;
                    checkFieldsValue();
                    getConnection().setCallbackHost(callbackHostText.getText());
                    setCheckEnable();
                }
            }
        });
        callbackPortText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (!isContextMode()) {
                    loginOk = false;
                    checkFieldsValue();
                    getConnection().setCallbackPort(callbackPortText.getText());
                    setCheckEnable();
                }
            }
        });
        apiVersionText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (!isContextMode()) {
                    loginOk = false;
                    checkFieldsValue();
                    getConnection().setSalesforceVersion(apiVersionText.getText());
                    setCheckEnable();
                }
            }
        });
        tokenText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (!isContextMode()) {
                    loginOk = false;
                    checkFieldsValue();
                    getConnection().setToken(tokenText.getText());
                    setCheckEnable();
                }
            }
        });
        checkButton.addSelectionListener(new SelectionAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!isContextMode()) {
                    checkFieldsValue();
                }
                testSalesforceLogin();
                if (authBtn.getSelectionIndex() == 0) {
                    String proxy = null;
                    if (useProxyBtn.getSelection()) {
                        proxy = SalesforceModuleParseAPI.USE_SOCKS_PROXY;
                    } else if (useHttpBtn.getSelection()) {
                        proxy = SalesforceModuleParseAPI.USE_HTTP_PROXY;
                    }
                    SalesforceModuleParseAPI checkSalesfoceLogin = checkSalesfoceLogin(proxy, endPoint, username, pwd, timeOut,
                            proxyHostText.getText(), proxyPortText.getText(), proxyUsernameText.getText(),
                            proxyPasswordText.getText());
                    if (checkSalesfoceLogin != null) {
                        setSalesforceModuleParseAPI(checkSalesfoceLogin);
                        loginOk = checkSalesfoceLogin.getCurrentAPI().isLogin();
                    }

                    if (loginOk) {
                        checkFieldsValue();
                    }
                } else {
                    String errors = null;
                    final OAuthClient client = new OAuthClient();
                    client.setBaseOAuthURL(endPointForOAuth);
                    client.setCallbackHost(callbackHost);
                    client.setCallbackPort(Integer.parseInt(callbackPort));
                    client.setClientID(consumeKey);
                    client.setClientSecret(consumeKeySrcret);
                    boolean result = false;
                    try {
                        client.startServer();
                        Token token = null;
                        if (TalendPropertiesUtil.isEnabledUseBrowser()) {
                            Display.getDefault().syncExec(new Runnable() {

                                @Override
                                public void run() {
                                    BrowerDialog brower;
                                    try {
                                        // Display display = new Display();
                                        Shell shell = new Shell(Display.getDefault(), SWT.ON_TOP);
                                        brower = new BrowerDialog(shell, client.getUrl());
                                        if (Window.OK == brower.open()) {
                                            code = client.getServer().getCode();
                                        } else {
                                            return;
                                        }
                                    } catch (Exception e2) {
                                        ExceptionHandler.process(e2);
                                    }
                                }
                            });
                        } else {
                            MessageDialog.openError(getShell(), Messages.getString("SalesforceForm.checkConnectionTitle"),
                                    Messages.getString("SalesforceForm.noAvailableBroswer"));
                            return;
                        }
                        Thread.sleep(100);
                        client.stopServer();
                        if (code != null && !code.equals("")) {
                            token = client.getTokenForWizard(code);
                            org.talend.salesforce.SforceManagement sfMgr = new org.talend.salesforce.SforceManagementImpl();
                            String endpoint = null;
                            endpoint = client.getSOAPEndpoint(token, apiVersion);

                            if (token != null) {
                                java.util.Properties properties = new java.util.Properties();
                                FileOutputStream outputStream = new FileOutputStream(tokenText.getText());
                                properties.setProperty("refreshtoken", token.getRefresh_token());
                                FileWriter w = new FileWriter(tokenText.getText());
                                properties.store(w, "");
                                w.close();
                                result = sfMgr.login(token.getAccess_token(), endpoint, Integer.parseInt(timeOut), false);
                            }

                            if (!result) {
                                String mainMsg = Messages.getString("SalesforceForm.checkFailure") + " " //$NON-NLS-1$ //$NON-NLS-2$
                                        + Messages.getString("SalesforceForm.checkFailureTip"); //$NON-NLS-1$
                                new ErrorDialogWidthDetailArea(getShell(), PID, mainMsg, errors);
                            } else {
                                loginOk = true;
                                MessageDialog.openInformation(getShell(),
                                        Messages.getString("SalesforceForm.checkConnectionTitle"), //$NON-NLS-1$ 
                                        Messages.getString("SalesforceForm.checkIsDone")); //$NON-NLS-1$  

                            }
                        } else {
                            MessageDialog.openError(getShell(), Messages.getString("SalesforceForm.checkConnectionTitle"),
                                    Messages.getString("SalesforceForm.checkFailure"));
                        }
                    } catch (Exception e1) {
                        errors = e1.getMessage();
                    }
                    if (loginOk) {
                        checkFieldsValue();
                    }
                }
            }
        });
    }

    private void enableProxyParameters(boolean enable) {
        proxyHostText.setEnabled(enable);
        proxyPortText.setEnabled(enable);
        proxyUsernameText.setEnabled(enable);
        proxyPasswordText.setEnabled(enable);
    }

    private void testSalesforceLogin() {
        endPoint = webServiceUrlText.getText();
        username = userNameText.getText();
        pwd = passwordText.getText();
        timeOut = timeOutText.getText();
        endPointForOAuth = webServiceUrlTextForOAuth.getText();
        apiVersion = apiVersionText.getText();
        consumeKey = consumeKeyText.getText();
        consumeKeySrcret = consumeKeySecretText.getText();
        callbackHost = callbackHostText.getText();
        callbackPort = callbackPortText.getText();
        token = tokenText.getText();
        if (isContextMode() && getContextModeManager() != null) {
            ContextType contextTypeForContextMode = ConnectionContextHelper.getContextTypeForContextMode(connectionItem
                    .getConnection());
            getContextModeManager().setSelectedContextType(contextTypeForContextMode);
            endPoint = getContextModeManager().getOriginalValue(endPoint);
            username = getContextModeManager().getOriginalValue(username);
            pwd = getContextModeManager().getOriginalValue(pwd);
            timeOut = getContextModeManager().getOriginalValue(timeOut);
            endPointForOAuth = getContextModeManager().getOriginalValue(endPointForOAuth);
            apiVersion = getContextModeManager().getOriginalValue(apiVersion);
            consumeKey = getContextModeManager().getOriginalValue(consumeKey);
            consumeKeySrcret = getContextModeManager().getOriginalValue(consumeKeySrcret);
            callbackHost = getContextModeManager().getOriginalValue(callbackHost);
            callbackPort = getContextModeManager().getOriginalValue(callbackPort);
            token = getContextModeManager().getOriginalValue(token);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (isReadOnly() != readOnly) {
            adaptFormToReadOnly();
        }
        if (visible) {
            initialize();
            adaptFormToEditable();
        }
        if (!isContextMode()) {
            checkFieldsValue();
            setCheckEnable();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.swt.utils.AbstractForm#addUtilsButtonListeners()
     */
    @Override
    protected void addUtilsButtonListeners() {
        if (!isInWizard()) {
            cancelButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(final SelectionEvent e) {
                    getShell().close();
                }
            });
        }
        authBtn.getCombo().addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int index = authBtn.getSelectionIndex();
                if (index == 0) {
                    stackLayout.topControl = basicComposite;
                    stackComposite.layout();
                    getConnection().setLoginType(BASIC);
                } else {
                    stackLayout.topControl = oauthComposite;
                    stackComposite.layout();
                    getConnection().setLoginType(OAUTH);
                }
                setCheckEnable();
            }

        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.swt.utils.AbstractForm#checkFieldsValue()
     */
    @Override
    protected boolean checkFieldsValue() {
        int index = authBtn.getSelectionIndex();
        if (index == 0) {
            if (!isValueValid(webServiceUrlText.getText())) {
                updateStatus(IStatus.ERROR, Messages.getString("SalesforceForm.needUrl")); //$NON-NLS-1$
                return false;
            }

            if (!isValueValid(userNameText.getText())) {
                updateStatus(IStatus.ERROR, Messages.getString("SalesforceForm.needUsername")); //$NON-NLS-1$
                return false;
            }

            if (!isValueValid(passwordText.getText())) {
                updateStatus(IStatus.ERROR, Messages.getString("SalesforceForm.needPassword")); //$NON-NLS-1$
                return false;
            }

            if (!loginOk) {
                updateStatus(IStatus.ERROR, Messages.getString("SalesforceForm.basicClick")); //$NON-NLS-1$
                return false;
            }
        } else {
            if (!isValueValid(webServiceUrlTextForOAuth.getText())) {
                updateStatus(IStatus.ERROR, Messages.getString("SalesforceForm.needOauthUrl")); //$NON-NLS-1$
                return false;
            }
            if (!isValueValid(consumeKeyText.getText())) {
                updateStatus(IStatus.ERROR, Messages.getString("SalesforceForm.needConsumerKey")); //$NON-NLS-1$
                return false;
            }
            if (!isValueValid(consumeKeySecretText.getText())) {
                updateStatus(IStatus.ERROR, Messages.getString("SalesforceForm.needConsumerSecret")); //$NON-NLS-1$
                return false;
            }
            if (!isValueValid(callbackHostText.getText())) {
                updateStatus(IStatus.ERROR, Messages.getString("SalesforceForm.needCallbackHost")); //$NON-NLS-1$
                return false;
            }
            if (!isValueValid(callbackPortText.getText())) {
                updateStatus(IStatus.ERROR, Messages.getString("SalesforceForm.needCallbackPort")); //$NON-NLS-1$
                return false;
            }
            if (!isValueValid(apiVersionText.getText())) {
                updateStatus(IStatus.ERROR, Messages.getString("SalesforceForm.needVersion")); //$NON-NLS-1$
                return false;
            }
            if (!isValueValid(tokenText.getText())) {
                updateStatus(IStatus.ERROR, Messages.getString("SalesforceForm.needToken")); //$NON-NLS-1$
                return false;
            }
            if (!loginOk) {
                updateStatus(IStatus.ERROR, Messages.getString("SalesforceForm.needOauthClick")); //$NON-NLS-1$
                return false;
            }
        }
        updateStatus(IStatus.OK, null);
        return true;
    }

    private boolean isValueValid(String value) {
        return value != null && !value.equals(""); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.swt.utils.AbstractForm#initialize()
     */
    @Override
    protected void initialize() {

        if (getConnection() == null) {
            return;
        }

        if (getConnection().getWebServiceUrl() != null && !getConnection().getWebServiceUrl().equals("")) { //$NON-NLS-1$
            webServiceUrlText.setText(getConnection().getWebServiceUrl());
        }

        if (getConnection().getWebServiceUrl() == null || getConnection().getWebServiceUrl().equals("")) { //$NON-NLS-1$
            getConnection().setWebServiceUrl(TSALESFORCE_INPUT_URL);
        } // Give a default value

        if (webServiceUrlText.getText() == null || webServiceUrlText.getText().equals("")) { //$NON-NLS-1$
            webServiceUrlText.setText(TSALESFORCE_INPUT_URL);
        }

        setTextValue(getConnection().getUserName(), userNameText);
        setTextValue(getConnection().getPassword(), passwordText);
        String batchSize2 = getConnection().getBatchSize();
        setTextValue((batchSize2 != null && !"".equals(batchSize2)) ? batchSize2 : String //$NON-NLS-1$
                .valueOf(SalesforceSchemaBean.DEFAULT_BATCH_SIZE), batchSizeText); //$NON-NLS-1$
        useProxyBtn.setSelection(getConnection().isUseProxy());
        useHttpBtn.setSelection(getConnection().isUseHttpProxy());
        setTextValue(getConnection().getProxyHost(), proxyHostText);
        setTextValue(getConnection().getProxyPort(), proxyPortText);
        setTextValue(getConnection().getProxyUsername(), proxyUsernameText);
        setTextValue(getConnection().getProxyPassword(), proxyPasswordText);
        String timeOutStr = getConnection().getTimeOut();
        String value = (timeOutStr != null && !"".equals(timeOutStr)) ? timeOutStr : String //$NON-NLS-1$
                .valueOf(SalesforceSchemaBean.DEFAULT_TIME_OUT);
        timeOut = value;
        setTextValue(value, timeOutText);

        if (getConnection().getLoginType() != null && !getConnection().getLoginType().equals("")) {
            authBtn.setText(getConnection().getLoginType());
            if (getConnection().getLoginType().equalsIgnoreCase(BASIC)) {
                authBtn.select(0);
                stackLayout.topControl = basicComposite;
                stackComposite.layout();
            } else {
                authBtn.select(1);
                stackLayout.topControl = oauthComposite;
                stackComposite.layout();
            }
        } else {
            getConnection().setLoginType(BASIC);
        }
        setTextValue(getConnection().getWebServiceUrlTextForOAuth(), webServiceUrlTextForOAuth);
        if (webServiceUrlTextForOAuth.getText() == null || webServiceUrlTextForOAuth.getText().equals("")) {
            webServiceUrlTextForOAuth.setText(TSALESFORCE_INPUT_URL_OAUTH);
            getConnection().setWebServiceUrlTextForOAuth(TSALESFORCE_INPUT_URL_OAUTH);
        }

        setTextValue(getConnection().getSalesforceVersion(), apiVersionText);
        if (apiVersionText.getText() == null || apiVersionText.getText().equals("")) {
            apiVersionText.setText(TSALESFORCE_VERSION);
            getConnection().setSalesforceVersion(TSALESFORCE_VERSION);
        }

        setTextValue(getConnection().getConsumeKey(), consumeKeyText);
        setTextValue(getConnection().getConsumeSecret(), consumeKeySecretText);
        setTextValue(getConnection().getCallbackHost(), callbackHostText);
        setTextValue(getConnection().getCallbackPort(), callbackPortText);
        setTextValue(getConnection().getToken(), tokenText);
    }

    private void setTextValue(String value, LabelledText control) {
        if (value != null) { //$NON-NLS-1$
            control.setText(value);
        }
    }

    @Override
    protected void adaptFormToEditable() {
        super.adaptFormToEditable();
        webServiceUrlText.setEditable(!isContextMode());
        userNameText.setEditable(!isContextMode());
        passwordText.setEditable(!isContextMode());
        batchSizeText.setEditable(!isContextMode());
        timeOutText.setEditable(!isContextMode());
        proxyHostText.setEditable(!isContextMode());
        proxyPortText.setEditable(!isContextMode());
        proxyUsernameText.setEditable(!isContextMode());
        proxyPasswordText.setEditable(!isContextMode());
        authBtn.setEnabled(!isContextMode());
        webServiceUrlTextForOAuth.setEditable(!isContextMode());
        apiVersionText.setEditable(!isContextMode());
        consumeKeyText.setEditable(!isContextMode());
        consumeKeySecretText.setEditable(!isContextMode());
        callbackHostText.setEditable(!isContextMode());
        callbackPortText.setEditable(!isContextMode());
        tokenText.setEditable(!isContextMode());
        if (isContextMode()) {
            passwordText.getTextControl().setEchoChar('\0');
            proxyPasswordText.getTextControl().setEchoChar('\0');
            checkButton.setEnabled(isContextMode());
        } else {
            passwordText.getTextControl().setEchoChar(pwdEhcoChar);
            proxyPasswordText.getTextControl().setEchoChar(pwdEhcoChar);
        }
    }

    public IPreferenceStore getPreferenceStore() {
        return CoreRuntimePlugin.getInstance().getCoreService().getPreferenceStore();
    }
}
