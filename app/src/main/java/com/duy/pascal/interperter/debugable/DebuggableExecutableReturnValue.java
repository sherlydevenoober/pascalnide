package com.duy.pascal.interperter.debugable;

import android.support.annotation.NonNull;

import com.duy.pascal.interperter.ast.codeunit.RuntimeExecutableCodeUnit;
import com.duy.pascal.interperter.ast.expressioncontext.ExpressionContext;
import com.duy.pascal.interperter.ast.instructions.Executable;
import com.duy.pascal.interperter.ast.instructions.ExecutionResult;
import com.duy.pascal.interperter.ast.variablecontext.VariableContext;
import com.duy.pascal.interperter.ast.runtime_value.value.AssignableValue;
import com.duy.pascal.interperter.ast.runtime_value.value.RuntimeValue;
import com.duy.pascal.interperter.config.DebugMode;
import com.duy.pascal.interperter.linenumber.LineInfo;
import com.duy.pascal.interperter.runtime_exception.RuntimePascalException;
import com.duy.pascal.interperter.runtime_exception.UnhandledPascalException;
import com.duy.pascal.interperter.utils.NullSafety;

public abstract class DebuggableExecutableReturnValue implements Executable,
        RuntimeValue {

    @NonNull
    @Override
    public Object getValue(VariableContext f, RuntimeExecutableCodeUnit<?> main)
            throws RuntimePascalException {
        try {
            return NullSafety.zReturn(getValueImpl(f, main));
        } catch (RuntimePascalException e) {
            throw e;
        } catch (Exception e) {
            throw new UnhandledPascalException(this.getLineNumber(), e);
        }
    }

    @Override
    public AssignableValue asAssignableValue(ExpressionContext f) {
        return null;
    }

    @Override
    public void setLineNumber(LineInfo lineNumber) {

    }

    public abstract Object getValueImpl(@NonNull VariableContext f, @NonNull RuntimeExecutableCodeUnit<?> main)
            throws RuntimePascalException;

    @Override
    public ExecutionResult execute(VariableContext context, RuntimeExecutableCodeUnit<?> main)
            throws RuntimePascalException {
        try {
            if (main.isDebug()) {
                main.getDebugListener().onLine((Executable) this, getLineNumber());
            }
            main.scriptControlCheck(getLineNumber());
            //backup mode
            boolean last = main.isDebug();
            if (main.isDebug()) {
                if (main.getDebugMode().equals(DebugMode.STEP_OVER)) {
                    main.setDebug(false);
                }
            }
            main.incStack(getLineNumber());

            ExecutionResult result = executeImpl(context, main);

            main.setDebug(last);
            main.decStack();
            return result;
        } catch (RuntimePascalException e) {
            throw e;
        } catch (Exception e) {
            throw new UnhandledPascalException(this.getLineNumber(), e);
        }
    }

    public abstract ExecutionResult executeImpl(VariableContext f,
                                                RuntimeExecutableCodeUnit<?> main)
            throws RuntimePascalException;
}
