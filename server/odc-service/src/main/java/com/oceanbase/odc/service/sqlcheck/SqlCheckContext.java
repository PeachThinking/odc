/*
 * Copyright (c) 2023 OceanBase.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oceanbase.odc.service.sqlcheck;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.oceanbase.odc.common.lang.Pair;
import com.oceanbase.odc.service.sqlcheck.model.CheckViolation;
import com.oceanbase.tools.sqlparser.statement.Statement;

import lombok.Getter;
import lombok.NonNull;

/**
 * {@link SqlCheckContext}
 *
 * @author yh263208
 * @date 2023-06-20 09:50
 * @since ODC_release_4.2.0
 */
public class SqlCheckContext {

    @Getter
    Long totalStmtCount;
    @Getter
    Long currentStmtIndex;
    private final List<Pair<Statement, List<CheckViolation>>> stmt2Violations;

    public SqlCheckContext() {
        this.stmt2Violations = new ArrayList<>();
    }

    public void addCheckViolation(@NonNull Statement statement, @NonNull List<CheckViolation> violations) {
        this.stmt2Violations.add(new Pair<>(statement, violations));
    }

    public void combine(@NonNull SqlCheckContext checkContext) {
        this.stmt2Violations.addAll(checkContext.stmt2Violations);
    }

    public List<Statement> getAllCheckedStatements() {
        return this.stmt2Violations.stream().map(p -> p.left).collect(Collectors.toList());
    }

    public List<CheckViolation> getAllCheckViolations() {
        return this.stmt2Violations.stream().flatMap(p -> p.right.stream()).collect(Collectors.toList());
    }

    @SuppressWarnings("all")
    public <T extends Statement> List<T> getAllCheckedStatements(Class<T> clazz) {
        return this.stmt2Violations.stream().map(p -> p.left)
                .filter(s -> s.getClass().equals(clazz))
                .map(statement -> (T) statement).collect(Collectors.toList());
    }

}
