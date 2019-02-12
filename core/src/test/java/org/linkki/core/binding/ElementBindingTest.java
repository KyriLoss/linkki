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

package org.linkki.core.binding;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.linkki.core.matcher.MessageMatchers.emptyMessageList;
import static org.linkki.core.matcher.MessageMatchers.hasErrorMessage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jdt.annotation.NonNull;
import org.junit.Before;
import org.junit.Test;
import org.linkki.core.binding.aspect.definition.LinkkiAspectDefinition;
import org.linkki.core.binding.dispatcher.PropertyDispatcher;
import org.linkki.core.message.Message;
import org.linkki.core.message.MessageList;
import org.linkki.core.message.Severity;
import org.linkki.core.ui.TestComponentWrapper;
import org.linkki.core.ui.TestUiComponent;
import org.linkki.util.handler.Handler;

public class ElementBindingTest {


    private TestUiComponent field = spy(new TestUiComponent());
    private TestUiComponent selectField = spy(new TestUiComponent());

    @SuppressWarnings("null")
    private ElementBinding selectBinding;

    @SuppressWarnings("null")
    private PropertyDispatcher propertyDispatcherValue;

    @SuppressWarnings("null")
    private MessageList messageList;

    @SuppressWarnings("null")
    private PropertyDispatcher propertyDispatcherEnumValue;

    @Before
    public void setUp() {
        propertyDispatcherValue = mock(PropertyDispatcher.class);
        when(propertyDispatcherValue.getProperty()).thenReturn("value");
        propertyDispatcherEnumValue = mock(PropertyDispatcher.class);
        when(propertyDispatcherEnumValue.getProperty()).thenReturn("enumValue");
        doReturn(TestEnum.class).when(propertyDispatcherEnumValue).getValueClass();

        messageList = new MessageList();
        when(propertyDispatcherValue.getMessages(any(MessageList.class))).thenReturn(messageList);
        when(propertyDispatcherEnumValue.getMessages(any(MessageList.class))).thenReturn(messageList);

        selectBinding = new ElementBinding(new TestComponentWrapper(selectField),
                propertyDispatcherEnumValue,
                Handler.NOP_HANDLER,
                new ArrayList<>());
    }

    @Test
    public void testUpdateFromPmo_updateAspect() {
        Handler componentUpdater = mock(Handler.class);
        LinkkiAspectDefinition aspectDefinition = mock(LinkkiAspectDefinition.class);
        when(aspectDefinition.supports(any())).thenReturn(true);
        when(aspectDefinition.createUiUpdater(any(), any())).thenReturn(componentUpdater);
        ElementBinding fieldBinding = new ElementBinding(new TestComponentWrapper(field),
                propertyDispatcherValue,
                Handler.NOP_HANDLER, Arrays.asList(aspectDefinition));
        fieldBinding.updateFromPmo();

        verify(componentUpdater).apply();
    }

    @Test
    public void testDisplayMessages() {
        messageList.add(Message.newError("code", "text"));

        selectBinding.displayMessages(messageList);

        @SuppressWarnings("null")
        @NonNull
        MessageList validationMessages = selectField.getValidationMessages();

        assertThat(validationMessages, hasErrorMessage("code"));

        Message firstMessage = validationMessages.getFirstMessage(Severity.ERROR).get();
        assertEquals(firstMessage.getText(), "text");
    }

    @Test
    public void testDisplayMessages_noMessages() {
        selectBinding.displayMessages(messageList);

        assertThat(selectField.getValidationMessages(), is(emptyMessageList()));
    }

    @SuppressWarnings("null")
    @Test(expected = NullPointerException.class)
    public void testDisplayMessages_noMessageList() {
        selectBinding.displayMessages(null);
    }

}