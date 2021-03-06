package com.duy.pascal.interperter.runtime_exception.internal;

import com.duy.pascal.interperter.linenumber.LineInfo;
import com.duy.pascal.interperter.runtime_exception.RuntimePascalException;

public class InternalInterpreterException extends RuntimePascalException {
    public InternalInterpreterException(LineInfo line) {
        super(line);
    }

    public String getInternalError() {
        return "Unspecified";
    }

    @Override
    public String getMessage() {
        return "Internal Interpreter Error: " + getInternalError();
    }
}
