/*******************************************************************************
 * Copyright (c) 2014 Faktor Zehn AG.
 * 
 * Alle Rechte vorbehalten.
 *******************************************************************************/

package org.linkki.framework.ui.component;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.linkki.core.message.Message;
import org.linkki.core.message.ObjectProperty;

import com.vaadin.server.ErrorMessage.ErrorLevel;
import com.vaadin.server.FontAwesome;

@RunWith(Parameterized.class)
public class MessagePmoTest {


    @Parameterized.Parameter(value = 0)
    public ErrorLevel errorLevel;

    @Parameterized.Parameter(value = 1)
    public ObjectProperty objectProperty;

    @Parameterized.Parameter(value = 2)
    public FontAwesome icon;

    @Parameterized.Parameter(value = 3)
    public String tooltip;


    @Parameterized.Parameters
    public static Object[][] data() {
        return new Object[][] {
                { ErrorLevel.ERROR, new ObjectProperty(new Object(), "foo"), FontAwesome.EXCLAMATION_CIRCLE,
                        "Object: foo" },
                { ErrorLevel.WARNING, new ObjectProperty(new Object()), FontAwesome.EXCLAMATION_TRIANGLE, "Object" },
                { ErrorLevel.INFORMATION, null, FontAwesome.INFO_CIRCLE, "" }
        };
    }


    @Test
    public void testMessagePmo() {
        Message.Builder messageBuilder = Message.builder("text", errorLevel);
        if (objectProperty != null) {
            messageBuilder.invalidObject(objectProperty);
        }

        MessagePmo message = new MessagePmo(messageBuilder.create());

        assertThat(message.getIcon(), is(icon));
        assertThat(message.getStyle(), endsWith(errorLevel.name().toLowerCase()));
        assertThat(message.getToolTip(), is(tooltip));
    }

}
