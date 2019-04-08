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
package org.linkki.core.ui.section.annotations;

import static org.linkki.core.defaults.ui.element.aspects.types.EnabledType.ENABLED;
import static org.linkki.core.defaults.ui.element.aspects.types.RequiredType.NOT_REQUIRED;
import static org.linkki.core.defaults.ui.element.aspects.types.VisibleType.VISIBLE;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.DecimalFormat;

import org.linkki.core.binding.descriptor.aspect.annotation.LinkkiAspect;
import org.linkki.core.binding.descriptor.bindingdefinition.annotation.LinkkiBindingDefinition;
import org.linkki.core.defaults.ui.element.aspects.types.EnabledType;
import org.linkki.core.defaults.ui.element.aspects.types.RequiredType;
import org.linkki.core.defaults.ui.element.aspects.types.VisibleType;
import org.linkki.core.pmo.ModelObject;
import org.linkki.core.ui.section.annotations.adapters.IntegerFieldBindingDefinition;
import org.linkki.core.ui.section.annotations.aspect.FieldAspectDefinitionCreator;

/**
 * A text field for displaying formatted integers.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@LinkkiBindingDefinition(IntegerFieldBindingDefinition.class)
@LinkkiAspect(FieldAspectDefinitionCreator.class)
public @interface UIIntegerField {

    /** Mandatory attribute that defines the order in which UI-Elements are displayed */
    int position();

    /** Provides a description label next to the UI element */
    String label();

    /** Defines if an UI-Component is editable, using values of {@link EnabledType} */
    EnabledType enabled() default ENABLED;

    /** Marks mandatory fields visually */
    RequiredType required() default NOT_REQUIRED;

    /**
     * Specifies if a component is shown, using values of {@link VisibleType}
     */
    VisibleType visible() default VISIBLE;

    /** Defines the maximal count of characters which can be displayed */
    int maxLength() default 0;

    /**
     * Format for the UI representation of the value. See {@link DecimalFormat} for the documentation of
     * the pattern.
     */
    String format() default "";

    /**
     * Name of the model object that is to be bound if multiple model objects are included for model
     * binding
     */
    String modelObject() default ModelObject.DEFAULT_NAME;

    /**
     * The name of a property in the class of the bound {@link ModelObject} to use model binding
     */
    String modelAttribute() default "";

}
