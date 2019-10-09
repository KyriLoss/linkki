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

package org.linkki.tooling.apt.util;

import java.util.Map.Entry;

import static org.linkki.tooling.apt.util.Constants.POSITION;

import java.util.Optional;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;

/**
 * Utility for working with {@link AnnotationMirror AnnotationMirrors}.
 */
public final class AnnotationMirrorUtils {

    private AnnotationMirrorUtils() {
        // util
    }

    private static Optional<? extends Entry<? extends ExecutableElement, ? extends AnnotationValue>> findPosition(
            AnnotationMirror annotationMirror) {
        return annotationMirror
                .getElementValues().entrySet().stream()
                .filter(it -> POSITION.equals(it.getKey().getSimpleName().toString()))
                .findFirst();
    }

    public static AnnotationValue findPositionAttributeValue(
            AnnotationMirror annotationMirror) {
        return findPosition(annotationMirror)
                .orElseThrow(() -> new IllegalStateException("expected " + POSITION + " to be present"))
                .getValue();
    }
}
