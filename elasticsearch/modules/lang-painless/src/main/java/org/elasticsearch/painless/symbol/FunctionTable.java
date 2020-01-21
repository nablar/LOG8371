/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.painless.symbol;

import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.objectweb.asm.commons.Method;

import java.lang.invoke.MethodType;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Stores information about functions directly invokable on the generated script class.
 * Functions marked as internal are generated by lambdas or method references, and are
 * not directly callable by a user.
 */
public class FunctionTable {

    public static class LocalFunction {

        protected final String functionName;
        protected final Class<?> returnType;
        protected final List<Class<?>> typeParameters;
        protected final boolean isInternal;

        protected final MethodType methodType;
        protected final Method asmMethod;

        public LocalFunction(String functionName, Class<?> returnType, List<Class<?>> typeParameters, boolean isInternal) {
            this.functionName = Objects.requireNonNull(functionName);
            this.returnType = Objects.requireNonNull(returnType);
            this.typeParameters = Collections.unmodifiableList(Objects.requireNonNull(typeParameters));
            this.isInternal = isInternal;

            Class<?> javaReturnType = PainlessLookupUtility.typeToJavaType(returnType);
            Class<?>[] javaTypeParameters = typeParameters.stream().map(PainlessLookupUtility::typeToJavaType).toArray(Class<?>[]::new);

            this.methodType = MethodType.methodType(javaReturnType, javaTypeParameters);
            this.asmMethod = new org.objectweb.asm.commons.Method(functionName,
                    MethodType.methodType(javaReturnType, javaTypeParameters).toMethodDescriptorString());
        }

        public String getFunctionName() {
            return functionName;
        }

        public Class<?> getReturnType() {
            return returnType;
        }

        public List<Class<?>> getTypeParameters() {
            return typeParameters;
        }

        public boolean isInternal() {
            return isInternal;
        }

        public MethodType getMethodType() {
            return methodType;
        }

        public Method getAsmMethod() {
            return asmMethod;
        }
    }

    /**
     * Generates a {@code LocalFunction} key.
     * @param functionName the name of the {@code LocalFunction}
     * @param functionArity the number of parameters for the {@code LocalFunction}
     * @return a {@code LocalFunction} key used for {@code LocalFunction} look up within the {@code FunctionTable}
     */
    public static String buildLocalFunctionKey(String functionName, int functionArity) {
        return functionName + "/" + functionArity;
    }

    protected Map<String, LocalFunction> localFunctions = new HashMap<>();

    public LocalFunction addFunction(String functionName, Class<?> returnType, List<Class<?>> typeParameters, boolean isInternal) {
        String functionKey = buildLocalFunctionKey(functionName, typeParameters.size());
        LocalFunction function = new LocalFunction(functionName, returnType, typeParameters, isInternal);
        localFunctions.put(functionKey, function);
        return function;
    }

    public LocalFunction addFunction(LocalFunction function) {
        String functionKey = buildLocalFunctionKey(function.getFunctionName(), function.getTypeParameters().size());
        localFunctions.put(functionKey, function);
        return function;
    }

    public LocalFunction getFunction(String functionName, int functionArity) {
        String functionKey = buildLocalFunctionKey(functionName, functionArity);
        return localFunctions.get(functionKey);
    }

    public LocalFunction getFunction(String functionKey) {
        return localFunctions.get(functionKey);
    }
}
