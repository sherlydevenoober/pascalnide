/*
 *  Copyright (c) 2017 Tran Le Duy
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

package com.duy.pascal.interperter.ast.instructions.assign_statement;

import android.support.annotation.NonNull;

import com.duy.pascal.interperter.ast.codeunit.RuntimeExecutableCodeUnit;
import com.duy.pascal.interperter.ast.expressioncontext.CompileTimeContext;
import com.duy.pascal.interperter.ast.instructions.ExecutionResult;
import com.duy.pascal.interperter.ast.variablecontext.VariableContext;
import com.duy.pascal.interperter.ast.runtime_value.references.Reference;
import com.duy.pascal.interperter.ast.runtime_value.value.AssignableValue;
import com.duy.pascal.interperter.ast.runtime_value.value.RuntimeValue;
import com.duy.pascal.interperter.debugable.DebuggableExecutable;
import com.duy.pascal.interperter.linenumber.LineInfo;
import com.duy.pascal.interperter.parse_exception.ParsingException;
import com.duy.pascal.interperter.runtime_exception.RuntimePascalException;
import com.duy.pascal.frontend.debug.CallStack;

public class AssignStatement extends DebuggableExecutable implements AssignExecutable {
    private AssignableValue left;
    private RuntimeValue value;
    private LineInfo line;

    public AssignStatement(@NonNull AssignableValue left, RuntimeValue value, LineInfo line) {
        this.left = left;
        this.value = value;
        this.line = line;
    }

    @Override
    public ExecutionResult executeImpl(VariableContext context, RuntimeExecutableCodeUnit<?> main) throws RuntimePascalException {

        Reference ref = left.getReference(context, main);
        Object old = ref.get();
        Object v = this.value.getValue(context, main);
        ref.set(v);

        if (main.isDebug()) main.getDebugListener().onVariableChange(new CallStack(context));

        return ExecutionResult.NOPE;
    }

    @Override
    public String toString() {
        return left + " := " + value;
    }

    @Override
    public LineInfo getLineNumber() {
        return this.line;
    }

    @Override
    public AssignExecutable compileTimeConstantTransform(CompileTimeContext c)
            throws ParsingException {
        return new AssignStatement(left, value.compileTimeExpressionFold(c), line);
    }
}
