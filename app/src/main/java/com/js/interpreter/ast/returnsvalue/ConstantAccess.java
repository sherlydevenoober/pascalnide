package com.js.interpreter.ast.returnsvalue;

import com.duy.pascal.backend.debugable.DebuggableReturnsValue;
import com.duy.pascal.backend.exceptions.ParsingException;
import com.duy.pascal.backend.exceptions.UnAssignableTypeException;
import com.duy.pascal.backend.linenumber.LineInfo;
import com.duy.pascal.backend.pascaltypes.BasicType;
import com.duy.pascal.backend.pascaltypes.RuntimeType;
import com.js.interpreter.ast.expressioncontext.CompileTimeContext;
import com.js.interpreter.ast.expressioncontext.ExpressionContext;
import com.js.interpreter.ast.instructions.SetValueExecutable;
import com.js.interpreter.runtime.VariableContext;
import com.js.interpreter.runtime.codeunit.RuntimeExecutable;

public class ConstantAccess extends DebuggableReturnsValue {
    final LineInfo line;
    public Object constant_value;

    public ConstantAccess(Object o, LineInfo line) {
        this.constant_value = o;
        this.line = line;
    }

    @Override
    public LineInfo getline() {
        return line;
    }

    @Override
    public Object getValueImpl(VariableContext f, RuntimeExecutable<?> main) {
        return constant_value;
    }

    @Override
    public String toString() {
        return constant_value.toString();
    }

    @Override
    public RuntimeType getType(ExpressionContext f) {
        return new RuntimeType(BasicType.anew(constant_value.getClass()), false);
    }

    @Override
    public Object compileTimeValue(CompileTimeContext context) {
        return constant_value;
    }

    @Override
    public SetValueExecutable createSetValueInstruction(ReturnsValue r)
            throws UnAssignableTypeException {
        throw new UnAssignableTypeException(this);
    }

    @Override
    public ReturnsValue compileTimeExpressionFold(CompileTimeContext context)
            throws ParsingException {
        return this;
    }

}
