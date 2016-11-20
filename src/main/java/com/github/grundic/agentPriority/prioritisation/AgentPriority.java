/*
 * The MIT License
 *
 * Copyright (c) 2016 Grigory Chernyshev.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.grundic.agentPriority.prioritisation;

import com.google.common.base.Function;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.SBuildAgent;
import jetbrains.buildServer.serverSide.ServerExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: g.chernyshev
 * Date: 02/11/16
 * Time: 21:03
 */

public abstract class AgentPriority implements Function<SBuildAgent, Comparable>, ServerExtension, Comparable<AgentPriority> {
    @Nullable
    private Map<String, String> parameters;

    @NotNull
    protected Map<String, String> getParameters() {
        if (null == parameters) {
            return Collections.emptyMap();
        }
        return parameters;
    }

    void setParameters(@Nullable Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @NotNull
    public abstract String getType();

    @NotNull
    public abstract String getName();

    @NotNull
    public String getJspPath() {
        return String.format("priority/%s.jsp", getType());
    }

    @Nullable
    public PropertiesProcessor getPropertiesProcessor() {
        return null;
    }

    @NotNull
    public Map<String, String> getDefaultProperties() {
        return new HashMap<>();
    }

    @Override
    public int compareTo(@NotNull AgentPriority agentPriority) {
        return this.getType().compareTo(agentPriority.getType());
    }
}
