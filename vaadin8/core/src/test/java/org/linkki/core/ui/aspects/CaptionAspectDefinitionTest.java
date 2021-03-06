/*
 * Copyright Faktor Zehn GmbH.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */

package org.linkki.core.ui.aspects;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.linkki.core.binding.wrapper.ComponentWrapper;
import org.linkki.core.defaults.ui.aspects.types.CaptionType;
import org.linkki.core.ui.creation.table.TableComponentWrapper;
import org.linkki.core.ui.wrapper.LabelComponentWrapper;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

public class CaptionAspectDefinitionTest {

    @Test
    public void testCreateComponentValueSetter() {
        CaptionAspectDefinition captionAspectDefinition = new CaptionAspectDefinition(CaptionType.DYNAMIC, "foo");
        Component component = new Label();
        ComponentWrapper componentWrapper = new LabelComponentWrapper(component);

        Consumer<String> componentValueSetter = captionAspectDefinition.createComponentValueSetter(componentWrapper);

        componentValueSetter.accept("bar");
        assertThat(component.getCaption(), is("bar"));
    }

    @Test
    public void testCreateComponentValueSetter_NOPonTable() {
        CaptionAspectDefinition captionAspectDefinition = new CaptionAspectDefinition(CaptionType.DYNAMIC, "foo");
        @SuppressWarnings("deprecation")
        com.vaadin.v7.ui.Table table = new com.vaadin.v7.ui.Table();
        ComponentWrapper componentWrapper = new TableComponentWrapper<String>("4711", table);

        Consumer<String> componentValueSetter = captionAspectDefinition.createComponentValueSetter(componentWrapper);

        componentValueSetter.accept("bar");
        assertThat(table.getCaption(), is(nullValue()));
    }

}
