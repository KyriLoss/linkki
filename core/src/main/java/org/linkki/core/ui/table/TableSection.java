/*******************************************************************************
 * Copyright (c) 2014 Faktor Zehn AG.
 * 
 * Alle Rechte vorbehalten.
 *******************************************************************************/

package org.linkki.core.ui.table;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.linkki.core.ui.section.AbstractSection;

import com.google.gwt.thirdparty.guava.common.base.Preconditions;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;

/**
 * A section containing a single table. This kind of section is created by the
 * {@link PmoBasedTableSectionFactory}.
 */
public class TableSection<T> extends AbstractSection {

    private static final long serialVersionUID = 1L;

    private Table table;

    /* package private, used by the PmoBaseTableFactory */
    TableSection(String caption, Optional<Button> addItemButton) {
        super(caption, false, addItemButton);
    }

    /**
     * Sets the table shown in the section.
     */
    /* package private, used by the PmoBaseTableFactory */
    void setTable(Table table) {
        Preconditions.checkState(this.table == null, "Table already set.");
        this.table = requireNonNull(table);
        addComponent(table);
    }

    /**
     * Returns the table shown in the section.
     */
    @Nonnull
    public Table getTable() {
        return requireNonNull(table, "Table hasn't been set, yet");
    }

    @Override
    public String toString() {
        return "TableSection based on " + table.getContainerDataSource();
    }
}
