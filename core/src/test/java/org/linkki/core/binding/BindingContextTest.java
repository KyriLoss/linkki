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
package org.linkki.core.binding;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linkki.core.ButtonPmo;
import org.linkki.core.PresentationModelObject;
import org.linkki.core.binding.ButtonPmoBindingTest.TestButtonPmo;
import org.linkki.core.binding.dispatcher.ExceptionPropertyDispatcher;
import org.linkki.core.binding.dispatcher.ReflectionPropertyDispatcher;
import org.linkki.core.message.MessageList;
import org.linkki.core.ui.components.LabelComponentWrapper;
import org.linkki.core.ui.section.BaseSection;
import org.linkki.core.ui.section.PmoBasedSectionFactory;
import org.linkki.core.ui.section.annotations.BindingDefinition;
import org.linkki.core.ui.section.annotations.EnabledType;
import org.linkki.core.ui.section.annotations.RequiredType;
import org.linkki.core.ui.section.annotations.UISection;
import org.linkki.core.ui.section.annotations.UITextField;
import org.linkki.core.ui.section.annotations.VisibleType;
import org.linkki.core.ui.section.descriptor.ElementDescriptor;
import org.linkki.core.ui.table.PmoBasedTableFactory;
import org.linkki.core.ui.table.TestRowPmo;
import org.linkki.core.ui.table.TestTablePmo;
import org.linkki.core.ui.util.ComponentFactory;
import org.linkki.util.handler.Handler;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@RunWith(MockitoJUnitRunner.class)
public class BindingContextTest {

    @SuppressWarnings("null")
    private TestBindingContext context;

    @SuppressWarnings("null")
    @Mock
    private Label label1;
    @SuppressWarnings("null")
    private Label label2;
    private TestPmo pmo = new TestPmo();
    private TestModelObject modelObject = new TestModelObject();
    private AbstractField<String> field1 = spy(new TextField());
    private AbstractField<String> field2 = spy(new TextField());

    @SuppressWarnings("null")
    private ComponentBinding binding1;
    @SuppressWarnings("null")
    private ComponentBinding binding2;

    private void setUpPmo() {
        context = TestBindingContext.create();
        pmo.setModelObject(modelObject);
    }

    @After
    public void cleanUpUi() {
        UI.setCurrent(null);
    }

    private Handler setUpPmoWithAfterUpdateUiHandler() {
        Handler afterUpdateUi = mock(Handler.class);
        context = TestBindingContext.create(afterUpdateUi);
        pmo.setModelObject(modelObject);
        return afterUpdateUi;
    }

    private void setUpBinding1() {
        binding1 = new ComponentBinding(new LabelComponentWrapper(label1, field1),
                new ReflectionPropertyDispatcher(this::getPmo, "value", new ExceptionPropertyDispatcher("value", pmo)),
                context::modelChanged, new ArrayList<>());
    }

    private void setUpBinding2() {
        binding2 = new ComponentBinding(new LabelComponentWrapper(label2, field2),
                new ReflectionPropertyDispatcher(this::getPmo, "value", new ExceptionPropertyDispatcher("value", pmo)),
                context::modelChanged, new ArrayList<>());
    }

    private TestPmo getPmo() {
        return pmo;
    }

    @Test
    public void testAdd() {
        setUpPmo();
        setUpBinding1();
        assertEquals(0, context.getBindings().size());
        context.add(binding1);
        assertEquals(1, context.getBindings().size());
    }

    @Test
    public void testModelChangedBindings() {
        Handler afterUpdateUi = setUpPmoWithAfterUpdateUiHandler();
        setUpBinding1();

        binding1 = spy(binding1);

        context.add(binding1);

        context.uiUpdated();
        verify(binding1).updateFromPmo();
        verify(afterUpdateUi, never()).apply();
    }

    @Test
    public void testModelChangedBindings_noBindingInContext() {
        Handler afterUpdateUi = setUpPmoWithAfterUpdateUiHandler();
        setUpBinding1();

        binding1 = spy(binding1);

        context.uiUpdated();

        verify(binding1, never()).updateFromPmo();
        verify(binding1, never()).displayMessages(any(MessageList.class));
        verify(afterUpdateUi, never()).apply();
    }

    @Test
    public void testModelChangedBindingsAndValidate_noBindingInContext() {
        Handler afterUpdateUi = setUpPmoWithAfterUpdateUiHandler();
        setUpBinding1();

        binding1 = spy(binding1);

        context.modelChanged();

        verify(afterUpdateUi).apply();
    }

    @Test
    public void testModelChangedBindingsAndValidate() {
        Handler afterUpdateUi = setUpPmoWithAfterUpdateUiHandler();

        setUpBinding1();
        binding1 = spy(binding1);

        context.add(binding1);

        context.modelChanged();
        verify(binding1).updateFromPmo();
        verify(afterUpdateUi).apply();
    }

    @Test
    public void testChangeBoundObject() {
        setUpPmo();
        setUpBinding1();
        binding1 = spy(binding1);

        context.uiUpdated();
        verify(binding1, never()).updateFromPmo();

        context.add(binding1);

        context.uiUpdated();
        verify(binding1).updateFromPmo();
    }

    @Test
    public void testRemoveBindingsForComponent() {
        setUpPmo();
        setUpBinding1();
        setUpBinding2();
        context.add(binding1);
        context.add(binding2);

        assertThat(context.getBindings(), hasSize(2));

        VerticalLayout layout = new VerticalLayout(field1, field2);

        context.removeBindingsForComponent(layout);
        assertThat(context.getBindings(), is(empty()));
    }

    @Test
    public void testRemoveBindingsForComponent_Container() {
        context = TestBindingContext.create();
        TestTablePmo tablePmo = new TestTablePmo();
        tablePmo.addItem();
        Table table = new PmoBasedTableFactory<>(tablePmo, context).createTable();
        UI ui = MockUi.mockUi();
        table.setParent(ui);
        table.attach();

        assertThat(context.getBindings(), hasSize(1));
        Binding binding = context.getBindings().iterator().next();
        assertThat(binding, is(instanceOf(TableBinding.class)));
        @SuppressWarnings("unchecked")
        TableBinding<TestRowPmo> tableBinding = (TableBinding<TestRowPmo>)binding;
        assertThat(tableBinding.getBindings(), hasSize(3));

        context.removeBindingsForComponent(table);
        assertThat(context.getBindings(), is(empty()));
        assertThat(tableBinding.getBindings(), is(empty()));
    }

    @Test
    public void testRemoveBindingsForComponent_Button() {
        context = TestBindingContext.create();
        TestPmoWithButton testPmoWithButton = new TestPmoWithButton();
        BaseSection section = new PmoBasedSectionFactory().createBaseSection(testPmoWithButton, context);

        assertThat(context.getBindings(), hasSize(1));

        context.removeBindingsForComponent(section);
        assertThat(context.getBindings(), is(empty()));
    }

    @Test
    public void testRemoveBindingsForPmo() {
        setUpPmo();
        setUpBinding1();
        setUpBinding2();
        context.add(binding1);
        context.add(binding2);

        assertThat(context.getBindings(), hasSize(2));

        context.removeBindingsForPmo(pmo);
        assertThat(context.getBindings(), is(empty()));
    }

    @Test
    public void testRemoveBindingsForPmo_Container() {
        context = TestBindingContext.create();
        TestTablePmo tablePmo = new TestTablePmo();
        tablePmo.addItem();
        Table table = new PmoBasedTableFactory<>(tablePmo, context).createTable();
        UI ui = MockUi.mockUi();
        table.setParent(ui);
        table.attach();

        assertThat(context.getBindings(), hasSize(1));
        Binding binding = context.getBindings().iterator().next();
        assertThat(binding, is(instanceOf(TableBinding.class)));
        @SuppressWarnings("unchecked")
        TableBinding<TestRowPmo> tableBinding = (TableBinding<TestRowPmo>)binding;
        assertThat(tableBinding.getBindings(), hasSize(3));

        context.removeBindingsForPmo(tablePmo);
        assertThat(context.getBindings(), is(empty()));
        assertThat(tableBinding.getBindings(), is(empty()));
    }

    @Test
    public void testRemoveBindingsForPmo_Button() {
        context = TestBindingContext.create();
        TestPmoWithButton testPmoWithButton = new TestPmoWithButton();
        new PmoBasedSectionFactory().createBaseSection(testPmoWithButton, context);

        assertThat(context.getBindings(), hasSize(1));

        context.removeBindingsForPmo(testPmoWithButton);
        assertThat(context.getBindings(), is(empty()));
    }

    @Test
    public void testBind_BoundComponentsAreMadeImmediate() {
        setUpPmo();
        TextField field = new TextField();
        BindingDefinition fieldDefintion = mock(BindingDefinition.class);
        when(fieldDefintion.required()).thenReturn(RequiredType.REQUIRED);
        when(fieldDefintion.enabled()).thenReturn(EnabledType.ENABLED);
        when(fieldDefintion.visible()).thenReturn(VisibleType.VISIBLE);
        ElementDescriptor fieldDescriptor = new ElementDescriptor(fieldDefintion, "value", Void.class,
                new ArrayList<>());

        // Precondition
        assertThat(field.isImmediate(), is(false));

        context.bind(pmo, fieldDescriptor, new LabelComponentWrapper(field));
        assertThat(field.isImmediate(), is(true));
    }

    @Test
    public void testBind_ButtonPmoBindningToCheckUpdateFromPmo() {
        context = TestBindingContext.create();
        TestButtonPmo buttonPmo = new TestButtonPmo();
        Button button = ComponentFactory.newButton(buttonPmo.getButtonIcon(), buttonPmo.getStyleNames());
        buttonPmo.enabled = false;

        context.bind(buttonPmo, button);

        assertThat(button.isEnabled(), is(false));
    }

    @UISection
    public static class TestModelObject {

        public static final String PROPERTY_MODEL_PROP = "modelProp";

        @Nullable
        private String modelProp;

        @UITextField(position = 1)
        @CheckForNull
        public String getModelProp() {
            return modelProp;
        }

        public void setModelProp(String modelProp) {
            this.modelProp = modelProp;
        }

    }

    public static class TestPmoWithButton implements PresentationModelObject {

        private final ButtonPmo buttonPmo = () -> {
            /* nothing to do */
        };

        @Override
        public Optional<ButtonPmo> getEditButtonPmo() {
            return Optional.of(buttonPmo);
        }
    }
}
