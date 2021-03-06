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

package org.linkki.util;

import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public class LazyReference<T> {

    private Supplier<T> supplier;
    @CheckForNull
    private T reference;

    public LazyReference(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T getReference() {
        if (reference == null) {
            reference = supplier.get();
        }
        return requireNonNull(reference);
    }
}
