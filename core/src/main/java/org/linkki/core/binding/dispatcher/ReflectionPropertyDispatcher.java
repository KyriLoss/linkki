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
package org.linkki.core.binding.dispatcher;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.linkki.core.binding.BindingContext;
import org.linkki.core.binding.aspect.Aspect;
import org.linkki.core.binding.dispatcher.accessor.PropertyAccessor;
import org.linkki.core.binding.dispatcher.accessor.PropertyAccessorCache;
import org.linkki.core.message.MessageList;

/**
 * {@link PropertyDispatcher} that reads properties from an arbitrary object via reflection. Falls
 * back to another dispatcher if no property/method is available in the accessed object.
 */
public class ReflectionPropertyDispatcher implements PropertyDispatcher {

    private final PropertyNamingConvention propertyNamingConvention = new PropertyNamingConvention();

    private final PropertyDispatcher fallbackDispatcher;

    private final Supplier<?> boundObjectSupplier;

    private final String property;

    /**
     * @param boundObjectSupplier a supplier to get the object accessed via reflection. Must not be
     *            {@code null}. The object is provided via a supplier because it may change.
     * @param property the name of the property of the bound object that this {@link PropertyDispatcher}
     *            will handle
     * @param fallbackDispatcher the dispatcher accessed in case a value cannot be read or written
     *            (because no getters/setters exist) from the accessed object property. Must not be
     *            {@code null}.
     */
    public ReflectionPropertyDispatcher(Supplier<?> boundObjectSupplier, String property,
            PropertyDispatcher fallbackDispatcher) {
        this.boundObjectSupplier = requireNonNull(boundObjectSupplier, "boundObjectSupplier must not be null");
        this.property = requireNonNull(property, "property must not be null");
        this.fallbackDispatcher = requireNonNull(fallbackDispatcher, "fallbackDispatcher must not be null");
    }

    @Override
    public String getProperty() {
        return property;
    }

    @CheckForNull
    @Override
    public Object getBoundObject() {
        return boundObjectSupplier.get();
    }

    @Override
    public Class<?> getValueClass() {
        if (canRead(getProperty())) {
            Class<?> valueClass = getAccessor(getProperty()).getValueClass();
            return valueClass;
        } else {
            return fallbackDispatcher.getValueClass();
        }
    }

    private PropertyAccessor getAccessor(String propertyToAccess) {
        Object boundObject = getBoundObject();
        if (boundObject == null) {
            throw new IllegalStateException("Should not be called without checking canRead or canWrite!");
        } else {
            return PropertyAccessorCache.get(boundObject.getClass(), propertyToAccess);
        }
    }

    @Override
    @CheckForNull
    public Object getValue() {
        return get(propertyNamingConvention::getValueProperty, fallbackDispatcher::getValue);
    }

    @Override
    public void setValue(@Nullable Object value) {
        if (!isReadOnly()) {
            Object boundObject = getBoundObject();
            // double check to avoid null check warning
            if (canWrite(getProperty()) && boundObject != null) {
                getAccessor(getProperty()).setPropertyValue(boundObject, value);
            } else {
                fallbackDispatcher.setValue(value);
            }
        }
    }

    @Override
    public boolean isReadOnly() {
        return !canWrite(getProperty()) && fallbackDispatcher.isReadOnly();
    }

    @Override
    public boolean isEnabled() {
        return (boolean)get(propertyNamingConvention::getEnabledProperty, fallbackDispatcher::isEnabled);
    }

    @Override
    public boolean isVisible() {
        return (boolean)get(propertyNamingConvention::getVisibleProperty, fallbackDispatcher::isVisible);
    }

    @Override
    public boolean isRequired() {
        return (boolean)get(propertyNamingConvention::getRequiredProperty, fallbackDispatcher::isRequired);
    }

    @Override
    public void invoke() {
        Object boundObject = getBoundObject();
        try {
            MethodUtils.invokeExactMethod(boundObject, getProperty());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean canRead(String propertyToRead) {
        if (getBoundObject() == null) {
            return false;
        } else {
            return getAccessor(propertyToRead).canRead();
        }
    }

    private boolean canWrite(String propertyToWrite) {
        if (getBoundObject() == null) {
            return false;
        } else {
            return getAccessor(propertyToWrite).canWrite();
        }
    }

    /**
     * Returns the messages stating the {@link #getBoundObject() bound object} as
     * {@link org.linkki.core.message.Message#getInvalidObjectProperties() invalid} and those the
     * {@code fallbackDispatcher} returns.
     */
    @Override
    public MessageList getMessages(MessageList messageList) {
        Object boundObject = getBoundObject();
        if (boundObject == null) {
            return new MessageList();
        } else {
            MessageList msgListForBoundObject = messageList.getMessagesFor(boundObject, getProperty());
            msgListForBoundObject.add(fallbackDispatcher.getMessages(messageList));
            // TODO may additionally call a method like "get<Property>NlsText()"
            return msgListForBoundObject;
        }
    }

    @Override
    public String toString() {
        return "ReflectionPropertyDispatcher [boundObject=" + boundObjectSupplier.get() + ", fallbackDispatcher="
                + fallbackDispatcher + "]";
    }

    private Object get(Function<String, String> methodNameProvider, Supplier<Object> fallbackProvider) {
        String methodName = methodNameProvider.apply(property);
        Object boundObject = getBoundObject();
        // double check to avoid null check warning
        if (canRead(methodName) && boundObject != null) {
            return getAccessor(methodName).getPropertyValue(boundObject);
        } else {
            return fallbackProvider.get();
        }
    }

    @CheckForNull
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAspectValue(Aspect<T> aspect) {
        if (aspect.isStatic()) {
            throw new IllegalStateException(String
                    .format("Aspect %s should not be handled by %s. It seems like the dispatcher chain is broken, check your %s",
                            aspect, getClass().getSimpleName(), BindingContext.class.getSimpleName()));
        }
        return (T)get(p -> propertyNamingConvention.checkAndAddSuffix(aspect.getName(), property),
                      () -> fallbackDispatcher.getAspectValue(aspect));
    }
}
