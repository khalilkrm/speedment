/*
 *
 * Copyright (c) 2006-2019, Speedment, Inc. All Rights Reserved.
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
module com.speedment.runtime.application {
    exports com.speedment.runtime.application;

    requires com.speedment.runtime.join;
    requires com.speedment.runtime.welcome;
    requires com.speedment.runtime.connector.mysql;
    requires com.speedment.runtime.connector.mariadb;
    requires com.speedment.runtime.connector.postgres;
    requires com.speedment.runtime.connector.sqlite;
    requires com.speedment.common.jvm_version;
    requires com.speedment.common.logger;

    requires transitive com.speedment.runtime.core;

}