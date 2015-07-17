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
package org.talend.core.service;

import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.talend.core.IService;
import org.talend.core.model.components.IComponentsFactory;
import org.talend.core.model.process.IGEFProcess;

/**
 * DOC ggu class global comment. Detailled comment
 */
public interface IDesignerCoreUIService extends IService {

    PaletteRoot createPalette(IComponentsFactory factory);

    PaletteRoot createPalette(IComponentsFactory factory, boolean isFavorite);

    PaletteRoot createPalette(IComponentsFactory compFac, PaletteRoot root);

    PaletteRoot createPalette(IComponentsFactory compFac, PaletteRoot root, boolean isFavorite);

    PaletteRoot getAllNodeStructure(IComponentsFactory factory);

    PaletteRoot createEmptyPalette();

    PaletteDrawer createTalendPaletteDrawer(String family);

    void setPaletteFilter(String filter);

    IPreferenceStore getPreferenceStore();

    void removePreferenceStorePropertyChangeListener(IPropertyChangeListener listener);

    List<PaletteEntry> createJobletEtnry();

    boolean executeCommand(IGEFProcess process, Command cmd);
}