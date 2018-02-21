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

import static java.util.Objects.requireNonNull;

import org.linkki.core.binding.dispatcher.PropertyDispatcher;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Binds an element to a control using the property dispatchers.
 */
public interface ElementBinding extends Binding {

    PropertyDispatcher getPropertyDispatcher();

    @SuppressFBWarnings
    @Override
    default Object getPmo() {
        return requireNonNull(getPropertyDispatcher().getBoundObject());
    }

}
