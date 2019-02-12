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

import org.eclipse.jdt.annotation.Nullable;

public class TestModelObject {

    public static final String PROPERTY_MODEL_PROP = "modelProp";

    @Nullable
    private String modelProp;
    private boolean modelPropVisible = true;

    @Nullable
    public String getModelProp() {
        return modelProp;
    }

    public void setModelProp(String modelProp) {
        this.modelProp = modelProp;
    }

    public boolean isModelPropVisible() {
        return modelPropVisible;
    }

    public void setModelPropVisible(boolean modelPropVisible) {
        this.modelPropVisible = modelPropVisible;
    }

}