/*
 * Copyright Faktor Zehn AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.linkki.framework.state;

import java.util.function.Function;

import org.linkki.core.ui.converters.LinkkiConverterFactory;
import org.linkki.framework.ui.application.ApplicationFooter;
import org.linkki.framework.ui.application.ApplicationHeader;
import org.linkki.framework.ui.application.ApplicationLayout;
import org.linkki.framework.ui.application.ApplicationNavigator;
import org.linkki.framework.ui.application.LinkkiUi;
import org.linkki.framework.ui.application.menu.ApplicationMenu;
import org.linkki.framework.ui.application.menu.ApplicationMenuItemDefinition;
import org.linkki.framework.ui.dialogs.ApplicationInfoDialog;
import org.linkki.util.Sequence;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

/**
 * Application configuration used to {@link LinkkiUi#configure(ApplicationConfig) configure} the
 * {@link LinkkiUi}.
 */
public interface ApplicationConfig {

    /**
     * The application's name, displayed for example in the {@link ApplicationFooter} and/or the
     * {@link ApplicationInfoDialog}.
     */
    String getApplicationName();

    /**
     * The application's version, displayed for example in the {@link ApplicationFooter} and/or
     * {@link ApplicationInfoDialog}.
     */
    String getApplicationVersion();

    /**
     * The application's description, displayed for example in the {@link ApplicationInfoDialog}.
     */
    String getApplicationDescription();

    /**
     * The copyright statement displayed for example in the {@link ApplicationFooter} and/or the
     * {@link ApplicationInfoDialog}.
     */
    String getCopyright();

    /**
     * The {@link ApplicationLayout} for the {@link LinkkiUi}.
     * 
     * @implSpec Includes an {@link ApplicationHeader} created with the
     *           {@link #getHeaderDefinition()} from an {@link ApplicationMenu} including the
     *           {@link #getMenuItemDefinitions()} and an {@link ApplicationFooter} created with the
     *           {@link #getFooterDefinition()}.
     */
    default ApplicationLayout createApplicationLayout() {
        return new ApplicationLayout(getHeaderDefinition().apply(new ApplicationMenu(getMenuItemDefinitions().list())),
                getFooterDefinition().apply(this));
    }

    /**
     * The {@link ApplicationMenuItemDefinition ApplicationMenuItemDefinitions} used to create an
     * {@link ApplicationMenu} for the {@link ApplicationHeader}.
     */
    Sequence<ApplicationMenuItemDefinition> getMenuItemDefinitions();

    /**
     * Used to create an {@link ApplicationHeader} including the {@link ApplicationMenu}.
     */
    default ApplicationHeaderDefinition getHeaderDefinition() {
        return ApplicationHeader::new;
    }

    /**
     * Used to create an {@link ApplicationFooter} including this {@link ApplicationConfig}'s
     * details.
     */
    default ApplicationFooterDefinition getFooterDefinition() {
        return ApplicationFooter::new;
    }

    /**
     * The {@link ApplicationNavigator} used to navigate the {@link View Views} displayed in the
     * {@link ApplicationLayout}.
     */
    default ApplicationNavigator createApplicationNavigator(UI ui, ApplicationLayout applicationLayout) {
        return new ApplicationNavigator(ui, applicationLayout);
    }

    /**
     * The factory used to create {@link Converter Converters} to be
     * {@link VaadinSession#setConverterFactory(ConverterFactory) registered with the
     * VaadinSession}.
     */
    default ConverterFactory getConverterFactory() {
        return new LinkkiConverterFactory();
    }

    public static interface ApplicationHeaderDefinition extends Function<ApplicationMenu, ApplicationHeader> {
        ApplicationHeader createApplicationHeader(ApplicationMenu applicationMenu);

        @Override
        default ApplicationHeader apply(@SuppressWarnings("null") ApplicationMenu applicationMenu) {
            return createApplicationHeader(applicationMenu);
        }
    }

    public static interface ApplicationFooterDefinition extends Function<ApplicationConfig, ApplicationFooter> {
        ApplicationFooter createApplicationFooter(ApplicationConfig applicationConfig);

        @Override
        default ApplicationFooter apply(@SuppressWarnings("null") ApplicationConfig applicationConfig) {
            return createApplicationFooter(applicationConfig);
        }
    }

}
