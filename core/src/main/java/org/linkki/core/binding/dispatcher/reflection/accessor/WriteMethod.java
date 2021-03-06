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
package org.linkki.core.binding.dispatcher.reflection.accessor;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;

import org.linkki.core.binding.LinkkiBindingException;

import edu.umd.cs.findbugs.annotations.CheckForNull;

/**
 * Wrapper for a setter {@link Method}. {@link #canWrite()} can safely be accessed even if no write
 * method exists. {@link #writeValue(Object, Object)} will access the setter via
 * {@link LambdaMetafactory}.
 * 
 * @param <T> the type containing the property
 * @param <V> the property's type
 */
public class WriteMethod<T, V> extends AbstractMethod<T> {

    @CheckForNull
    private BiConsumer<T, V> setter;

    WriteMethod(PropertyAccessDescriptor<T, V> descriptor) {
        super(descriptor, descriptor.getReflectionWriteMethod());
    }

    /**
     * Checks whether a write method exists.
     */
    public boolean canWrite() {
        return hasMethod();
    }

    /**
     * Writes a value by accessing the respective write method.
     * 
     * @param value the value to be written
     * @throws LinkkiBindingException if an error occurs while accessing the write method
     * @see #canWrite()
     */
    public void writeValue(T target, @CheckForNull V value) {
        try {
            setter().accept(target, value);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new LinkkiBindingException(
                    "Cannot write value: " + value + " in " + getBoundClass() + "#" + getPropertyName(),
                    e);
        }
    }

    @SuppressWarnings({ "unchecked" })
    private BiConsumer<T, V> setter() {
        if (setter == null) {
            setter = getMethodAs(BiConsumer.class);
        }
        return setter;
    }

    @Override
    protected CallSite getCallSite(Lookup lookup, MethodHandle methodHandle, MethodType func) {
        try {
            return LambdaMetafactory.metafactory(lookup,
                                                 "accept",
                                                 MethodType.methodType(BiConsumer.class),
                                                 MethodType.methodType(Void.TYPE, Object.class, Object.class),
                                                 methodHandle,
                                                 wrap(methodHandle));
        } catch (LambdaConversionException e) {
            throw new IllegalStateException("Can't create " + CallSite.class.getSimpleName() + " for "
                    + methodHandle, e);
        }
    }

}
