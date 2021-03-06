/*
 *
 * Copyright (c) 2006-2020, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.speedment.runtime.config.mutator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.speedment.runtime.config.Schema;
import com.speedment.runtime.config.mutator.trait.HasEnabledMutatorMixin;
import com.speedment.runtime.config.mutator.trait.HasIdMutatorMixin;
import com.speedment.runtime.config.mutator.trait.HasNameMutatorMixin;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

final class SchemaMutatorTest implements
        HasEnabledMutatorMixin<Schema, SchemaMutator<Schema>>,
        HasIdMutatorMixin<Schema, SchemaMutator<Schema>>,
        HasNameMutatorMixin<Schema, SchemaMutator<Schema>> {

    @Override
    @SuppressWarnings("unchecked")
    public SchemaMutator<Schema> getMutatorInstance() {
        return (SchemaMutator<Schema>) Schema.create(null, new HashMap<>()).mutator();
    }

    @Test
    void setDefaultSchema() {
        assertDoesNotThrow(() -> getMutatorInstance().setDefaultSchema(true));
    }

    @Test
    void addNewTable() {
        assertNotNull(getMutatorInstance().addNewTable());
    }
}
