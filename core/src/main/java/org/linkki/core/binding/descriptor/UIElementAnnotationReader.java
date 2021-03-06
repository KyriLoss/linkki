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
package org.linkki.core.binding.descriptor;

import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.linkki.core.binding.descriptor.aspect.LinkkiAspectDefinition;
import org.linkki.core.binding.descriptor.aspect.annotation.AspectAnnotationReader;
import org.linkki.core.binding.descriptor.modelobject.ModelObjects;
import org.linkki.core.binding.descriptor.property.BoundProperty;
import org.linkki.core.binding.descriptor.property.annotation.BoundPropertyAnnotationReader;
import org.linkki.core.pmo.ModelObject;
import org.linkki.core.uicreation.ComponentAnnotationReader;
import org.linkki.core.uicreation.PositionAnnotationReader;

/**
 * Reads UI field annotations, e.g. {@code @UITextField}, {@code @UIComboBox}, etc. from a given
 * object's class.
 * <p>
 * Provides a set of {@link ElementDescriptor ElementDescriptors} for all found properties via
 * {@link #getUiElements()}
 */
public class UIElementAnnotationReader {

    private final Class<?> annotatedClass;
    private final Map<String, PropertyElementDescriptors> descriptorsByProperty = new HashMap<>();

    public UIElementAnnotationReader(Class<?> annotatedClass) {
        this.annotatedClass = requireNonNull(annotatedClass, "annotatedClass must not be null");
        initDescriptorMaps();
    }

    private void initDescriptorMaps() {
        Method[] methods = annotatedClass.getMethods();
        for (Method method : methods) {
            BoundPropertyAnnotationReader.findBoundProperty(method)
                    .map(BoundProperty::getPmoProperty)
                    .ifPresent(p -> Arrays.stream(method.getAnnotations())
                            .forEach(a -> createAndAddDescriptor(a, method, p)));
        }
    }

    private void createAndAddDescriptor(Annotation annotation, Method method, String pmoProperty) {
        List<LinkkiAspectDefinition> aspectDefs = AspectAnnotationReader.createAspectDefinitionsFrom(annotation);

        PropertyElementDescriptors elementDescriptors = descriptorsByProperty
                .computeIfAbsent(pmoProperty,
                                 PropertyElementDescriptors::new);

        if (ComponentAnnotationReader.isComponentDefinition(annotation)) {
            elementDescriptors.addDescriptor(annotation.annotationType(),
                                             new ElementDescriptor(
                                                     PositionAnnotationReader.getPosition(method),
                                                     ComponentAnnotationReader.getComponentDefinition(annotation,
                                                                                                      method),
                                                     BoundPropertyAnnotationReader.getBoundProperty(annotation, method),
                                                     aspectDefs),
                                             annotatedClass);
        } else {
            elementDescriptors.addAspect(aspectDefs);
        }
    }

    /**
     * Currently for testing purposes only!
     *
     * @return the descriptor for the given property
     *
     * @throws NoSuchElementException if no descriptor with the given property can be found
     */
    public PropertyElementDescriptors findDescriptors(String propertyName) {
        return getUiElements()
                .filter(el -> el.getPmoPropertyName().equals(propertyName))
                .findFirst()
                .get();
    }

    /**
     * Returns all descriptors that are found by this reader. The descriptors are ordered by their
     * position.
     * 
     * @return all descriptors ordered by position
     */
    public Stream<PropertyElementDescriptors> getUiElements() {
        validateNoDuplicatePosition();
        return descriptorsByProperty.values().stream()
                .filter(PropertyElementDescriptors::isNotEmpty)
                .sorted(Comparator.comparing(PropertyElementDescriptors::getPosition));
    }

    private void validateNoDuplicatePosition() {
        Map<Integer, List<String>> propertiesByPosition = descriptorsByProperty.values().stream()
                .filter(PropertyElementDescriptors::isNotEmpty)
                .collect(Collectors.groupingBy(PropertyElementDescriptors::getPosition,
                                               Collectors.mapping(PropertyElementDescriptors::getPmoPropertyName,
                                                                  Collectors.toList())));
        propertiesByPosition.values().stream()
                .filter(s -> s.size() > 1)
                .findFirst()
                .ifPresent(propertiesWithSamePosition -> {
                    throw new IllegalStateException(String.format("Duplicate position in properties %s of pmo class %s",
                                                                  propertiesWithSamePosition,
                                                                  annotatedClass.getName()));
                });
    }

    /**
     * Reads the given presentation model object's class to find a method or field annotated with
     * {@link ModelObject @ModelObject} and the annotation's {@link ModelObject#name()} matching the
     * given model object name. Returns a supplier that supplies a model object by invoking that method
     * or retrieving the field value.
     *
     * @param pmo a presentation model object
     * @param modelObjectName the name of the model object as provided by a method/field annotated with
     *            {@link ModelObject @ModelObject}
     *
     * @return a supplier that supplies a model object by invoking the annotated method or retrieving
     *         the field value
     *
     * @throws ModelObjectAnnotationException if no matching method or field is found, the method has no
     *             return value, the field has the type {@link Void} or multiple annotations for the
     *             same model object name are present.
     * 
     * @deprecated Since 1.1 there is a dedicated class called {@link ModelObjects} to retrieve the
     *             model object supplier.
     */
    @Deprecated
    public static Supplier<?> getModelObjectSupplier(Object pmo, String modelObjectName) {
        try {
            return ModelObjects.supplierFor(pmo, modelObjectName);
        } catch (org.linkki.core.binding.descriptor.modelobject.ModelObjects.ModelObjectAnnotationException e) {
            throw new ModelObjectAnnotationException(e.getMessage());
        }
    }

    /**
     * Tests if the presentation model object has a method annotated with
     * {@link ModelObject @ModelObject} using a given name
     *
     * @param pmo an object used for a presentation model
     * @param modelObjectName the name of the model object
     *
     * @return whether the object has a method annotated with {@link ModelObject @ModelObject} using the
     *         given name
     * @throws ModelObjectAnnotationException if multiple annotations for the model object name are
     *             present
     * 
     * @deprecated Since 1.1 there is a dedicated class called {@link ModelObjects} to retrieve the
     *             model object supplier.
     */
    @Deprecated
    public static boolean hasModelObjectAnnotation(Object pmo, String modelObjectName) {
        try {
            return ModelObjects.isAccessible(pmo, modelObjectName);
        } catch (org.linkki.core.binding.descriptor.modelobject.ModelObjects.ModelObjectAnnotationException e) {
            throw new ModelObjectAnnotationException(e.getMessage());
        }
    }

    /**
     * Thrown when trying to get a method annotated with {@link ModelObject @ModelObject} via
     * {@link UIElementAnnotationReader#getModelObjectSupplier(Object, String)} fails.
     * 
     * @deprecated since 1.1 it is replaced by
     *             {@link org.linkki.core.binding.descriptor.modelobject.ModelObjects.ModelObjectAnnotationException}
     */
    @Deprecated
    public static final class ModelObjectAnnotationException extends IllegalArgumentException {
        private static final long serialVersionUID = 1L;

        private ModelObjectAnnotationException(String description) {
            super(description);
        }

        public static ModelObjectAnnotationException noAnnotatedMember(Object pmo, String modelObjectName) {
            return new ModelObjectAnnotationException("Presentation model object class " + pmo.getClass()
                    + " has no method or field annotated with " + getDescriptionForAnnotation(modelObjectName));
        }

        public static ModelObjectAnnotationException voidField(Object pmo, Field field) {
            return new ModelObjectAnnotationException(
                    "Presentation model object " + pmo + "'s field " + field.getName() + " is annotated with @"
                            + ModelObject.class.getSimpleName() + " but is of type Void");
        }

        public static ModelObjectAnnotationException voidMethod(Object pmo, Method method) {
            return new ModelObjectAnnotationException(
                    "Presentation model object " + pmo + "'s method " + method.getName() + " is annotated with @"
                            + ModelObject.class.getSimpleName() + " but returns void");
        }

        public static ModelObjectAnnotationException multipleMembersAnnotated(Object pmo,
                String modelObjectName,
                Member... annotatedMembers) {
            return new ModelObjectAnnotationException(String.format(
                                                                    "Presentation model object class %s has multiple members (%s) that are annotated with %s",
                                                                    pmo.getClass(),
                                                                    Arrays.stream(annotatedMembers).map(Member::getName)
                                                                            .collect(Collectors.joining(", ")),
                                                                    getDescriptionForAnnotation(modelObjectName)));
        }

        private static String getDescriptionForAnnotation(String modelObjectName) {
            String annotation = "@" + ModelObject.class.getSimpleName();
            return ModelObject.DEFAULT_NAME.equals(modelObjectName)
                    ? annotation
                    : annotation + " for the model object named \"" + modelObjectName + "\"";
        }
    }

}
