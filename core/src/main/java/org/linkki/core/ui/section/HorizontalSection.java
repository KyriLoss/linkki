package org.linkki.core.ui.section;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.linkki.core.ui.application.ApplicationStyles;
import org.linkki.core.ui.util.UiUtil;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * A horizontal section is a section that contains label / control pairs one after another.
 */
public class HorizontalSection extends BaseSection {

    private static final long serialVersionUID = 1L;

    private HorizontalLayout content;

    /**
     * Creates a new section with the given caption that is not closeable.
     */
    public HorizontalSection(@Nonnull String caption) {
        this(caption, false);
    }

    /**
     * Creates a new section with a caption.
     * 
     * @param caption the caption
     * @param closeable <code>true</code> if the section can be closed/opened.
     */
    public HorizontalSection(@Nonnull String caption, boolean closeable) {
        this(caption, closeable, Optional.empty());
    }

    /**
     * Creates a new section with the given caption.
     * 
     * @param caption the caption
     * @param closeable <code>true</code> if the section can be closed and opened.
     * @param editButton If present the section has an edit button in the header.
     */
    public HorizontalSection(@Nonnull String caption, boolean closeable, Optional<Button> editButton) {
        super(caption, closeable, editButton);
        content = new HorizontalLayout();
        addComponent(content);
    }

    @Override
    public Label add(String label, Component component) {
        Label l = new Label(label);
        l.setWidthUndefined();
        content.addComponent(l);
        content.addStyleName(ApplicationStyles.SPACING_HORIZONTAL_SECTION);
        add(component);
        return l;
    }

    @Override
    public void add(Component component) {
        content.addComponent(component);
        // content.setComponentAlignment(component, Alignment.MIDDLE_LEFT);
        if (UiUtil.isWidth100Pct(component)) {
            updateExpandRatio();
        }
    }

    private void updateExpandRatio() {
        float ratio = 1f / getNumOfComponentsWith100PctWidth();
        for (Component c : content) {
            if (UiUtil.isWidth100Pct(c)) {
                content.setExpandRatio(c, ratio);
            }
        }
    }

    private int getNumOfComponentsWith100PctWidth() {
        int num = 0;
        for (Component c : content) {
            if (UiUtil.isWidth100Pct(c)) {
                num++;
            }
        }
        return num;
    }

}
