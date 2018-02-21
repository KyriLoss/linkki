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

package org.linkki.core.ui.section.annotations.aspect;

import java.lang.annotation.Annotation;

import org.linkki.core.ui.section.annotations.BindingDefinition;
import org.linkki.core.ui.section.annotations.LinkkiBindingDefinition;
import org.linkki.core.ui.section.annotations.VisibleType;

/**
 * Aspect definition for {@link VisibleType} of annotations annotated with
 * {@link LinkkiBindingDefinition}.
 */
public class VisibleAspectForBindingDefinition extends VisibleAspectDefinition {

    @SuppressWarnings("null")
    private BindingDefinition bindingDefinition;

    @Override
    public void initialize(Annotation annotation) {
        bindingDefinition = BindingDefinition.from(annotation);
    }

    @Override
    public VisibleType getVisibleType() {
        return bindingDefinition.visible();
    }

}