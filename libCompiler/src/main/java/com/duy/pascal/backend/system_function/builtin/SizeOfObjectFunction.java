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

package com.duy.pascal.backend.system_function.builtin;


import android.support.annotation.NonNull;

import com.duy.pascal.backend.ast.codeunit.RuntimeExecutableCodeUnit;
import com.duy.pascal.backend.ast.expressioncontext.CompileTimeContext;
import com.duy.pascal.backend.ast.expressioncontext.ExpressionContext;
import com.duy.pascal.backend.ast.instructions.Executable;
import com.duy.pascal.backend.ast.runtime_value.VariableContext;
import com.duy.pascal.backend.ast.runtime_value.value.FunctionCall;
import com.duy.pascal.backend.ast.runtime_value.value.RuntimeValue;
import com.duy.pascal.backend.linenumber.LineInfo;
import com.duy.pascal.backend.parse_exception.ParsingException;
import com.duy.pascal.backend.runtime_exception.RuntimePascalException;
import com.duy.pascal.backend.types.ArgumentType;
import com.duy.pascal.backend.types.BasicType;
import com.duy.pascal.backend.types.DeclaredType;
import com.duy.pascal.backend.types.RuntimeType;
import com.duy.pascal.frontend.DLog;

public class SizeOfObjectFunction implements IMethodDeclaration {

    private static final String TAG = "LengthFunction";
    private ArgumentType[] argumentTypes = {new RuntimeType(BasicType.create(Object.class), false)};

    @Override
    public String getName() {
        return "sizeof";
    }

    @Override
    public FunctionCall generateCall(LineInfo line, RuntimeValue[] arguments,
                                     ExpressionContext f) throws ParsingException {
        RuntimeValue array = arguments[0];
        DLog.d(TAG, "generateCall: ");
        return new SizeOfObjectCall(array, line);
    }

    @Override
    public FunctionCall generatePerfectFitCall(LineInfo line, RuntimeValue[] values, ExpressionContext f) throws ParsingException {
        DLog.d(TAG, "generatePerfectFitCall: ");
        return generateCall(line, values, f);
    }

    @Override
    public ArgumentType[] argumentTypes() {
        return argumentTypes;
    }

    @Override
    public DeclaredType returnType() {
        return BasicType.Integer;
    }

    @Override
    public String description() {
        return null;
    }

    private class SizeOfObjectCall extends FunctionCall {

        private LineInfo line;
        private RuntimeValue array;

        SizeOfObjectCall(RuntimeValue array, LineInfo line) {
            this.array = array;
            this.line = line;
        }

        @Override
        public RuntimeType getType(ExpressionContext f) throws ParsingException {
            return new RuntimeType(BasicType.Integer, false);
        }

        @NonNull
        @Override
        public LineInfo getLineNumber() {
            return line;
        }

        @Override
        public void setLineNumber(LineInfo lineNumber) {
            this.line = lineNumber;
        }

        @Override
        public Object compileTimeValue(CompileTimeContext context) {
            return null;
        }

        @Override
        public RuntimeValue compileTimeExpressionFold(CompileTimeContext context)
                throws ParsingException {
            return new SizeOfObjectCall(array.compileTimeExpressionFold(context), line);
        }

        @Override
        public Executable compileTimeConstantTransform(CompileTimeContext c)
                throws ParsingException {
            return new SizeOfObjectCall(array.compileTimeExpressionFold(c), line);
        }

        @Override
        protected String getFunctionName() {
            return "sizeof";
        }

        @Override
        public Object getValueImpl(@NonNull VariableContext f, @NonNull RuntimeExecutableCodeUnit<?> main)
                throws RuntimePascalException {
            Object value = array.getValue(f, main);
            if (value instanceof Integer) {
                return 4;
            } else if (value instanceof Long) {
                return 8;
            } else if (value instanceof Double) {
                return 8;
            } else if (value instanceof Short) {
                return 1;
            } else if (value instanceof Byte) {
                return 1;
            } else if (value instanceof Character) {
                return 2;
            } else if (value instanceof String) {
                return ((String) value).length() + 1;
            } else if (value instanceof StringBuilder) {
                return ((StringBuilder) value).length() + 1;
            }
            return 0;
        }
    }
}