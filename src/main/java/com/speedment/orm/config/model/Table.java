/**
 *
 * Copyright (c) 2006-2015, Speedment, Inc. All Rights Reserved.
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
package com.speedment.orm.config.model;

import com.speedment.orm.annotations.Api;
import com.speedment.orm.config.model.parameters.ColumnCompressionTypeable;
import com.speedment.orm.config.model.parameters.FieldStorageTypeable;
import com.speedment.orm.config.model.parameters.StorageEngineTypeable;
import com.speedment.orm.platform.SpeedmentPlatform;
import java.util.Optional;

/**
 *
 * @author pemi
 */
@Api(version = 0)
public interface Table extends
        ConfigEntity<Table, Schema, ConfigEntity<?, Table, ?>>,
        FieldStorageTypeable<Table>,
        ColumnCompressionTypeable<Table>,
        StorageEngineTypeable<Table> {

    @Override
    default Optional<Class<? extends Schema>> getParentInterfaceMainClass() {
        return Optional.of(Schema.class);
    }

    default Column addNewColumn() {
        final Column e = SpeedmentPlatform.getInstance().getConfigEntityFactory().newColumn();
        add(e);
        return e;
    }

    default Index addNewIndex() {
        final Index e = SpeedmentPlatform.getInstance().getConfigEntityFactory().newIndex();
        add(e);
        return e;
    }

}
