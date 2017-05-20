package com.js.interpreter.codeunit;

import com.duy.pascal.backend.exceptions.ParsingException;
import com.duy.pascal.frontend.activities.RunnableActivity;
import com.google.common.collect.ListMultimap;
import com.duy.pascal.backend.function_declaretion.AbstractFunction;
import com.js.interpreter.source_include.ScriptSource;
import com.js.interpreter.runtime.codeunit.RuntimeExecutable;

import java.io.Reader;
import java.util.List;

public abstract class ExecutableCodeUnit extends CodeUnit {

    public ExecutableCodeUnit(Reader r,
                              ListMultimap<String, AbstractFunction> functionTable, //current functions
                              String sourceName, //for debug
                              List<ScriptSource> includeDirectories,
                              RunnableActivity handler)
            throws ParsingException {
        super(r, functionTable, sourceName, includeDirectories, handler);
    }

    @Override
    public abstract RuntimeExecutable<? extends ExecutableCodeUnit> run();

}