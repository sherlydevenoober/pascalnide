package com.duy.pascal.backend.pascaltypes.type_converter;

import com.duy.pascal.backend.exceptions.ParsingException;
import com.duy.pascal.backend.linenumber.LineInfo;
import com.duy.pascal.backend.pascaltypes.BasicType;
import com.duy.pascal.backend.pascaltypes.RuntimeType;
import com.js.interpreter.expressioncontext.CompileTimeContext;
import com.js.interpreter.expressioncontext.ExpressionContext;
import com.duy.pascal.backend.runtime.value.AssignableValue;
import com.duy.pascal.backend.runtime.value.RuntimeValue;
import com.duy.pascal.backend.runtime.VariableContext;
import com.js.interpreter.codeunit.RuntimeExecutableCodeUnit;
import com.duy.pascal.backend.runtime.exception.RuntimePascalException;

public class NumberToLongType implements RuntimeValue {
    private RuntimeValue[] outputFormat;
    private RuntimeValue other;


    public NumberToLongType(RuntimeValue other) {
        this.other = other;
        this.outputFormat = other.getOutputFormat();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public RuntimeValue[] getOutputFormat() {
        return outputFormat;
    }

    @Override
    public void setOutputFormat(RuntimeValue[] formatInfo) {
        this.outputFormat = formatInfo;
    }


    @Override
    public Object getValue(VariableContext f, RuntimeExecutableCodeUnit<?> main)
            throws RuntimePascalException {
        Number i = (Number) other.getValue(f, main);
        return i.longValue();
    }

    @Override
    public RuntimeType getType(ExpressionContext f)
            throws ParsingException {
        return new RuntimeType(BasicType.Long, false);
    }

    @Override
    public LineInfo getLineNumber() {
        return other.getLineNumber();
    }

    @Override
    public Object compileTimeValue(CompileTimeContext context)
            throws ParsingException {
        Object o = other.compileTimeValue(context);
        if (o != null) {
            return ((Number) o).longValue();
        } else {
            return null;
        }
    }


    @Override
    public RuntimeValue compileTimeExpressionFold(CompileTimeContext context)
            throws ParsingException {
        return new NumberToLongType(other.compileTimeExpressionFold(context));
    }

    @Override
    public AssignableValue asAssignableValue(ExpressionContext f) {
        return null;
    }
}
