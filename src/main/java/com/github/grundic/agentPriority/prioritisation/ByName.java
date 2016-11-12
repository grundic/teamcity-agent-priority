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

import com.github.grundic.agentPriority.config.AgentPriorityRegistry;
import com.github.grundic.agentPriority.config.BaseConfig;
import jetbrains.buildServer.serverSide.SBuildAgent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import static com.github.grundic.agentPriority.Constants.PLUGIN_PATH;

/**
 * User: g.chernyshev
 * Date: 02/11/16
 * Time: 21:06
 */
public class ByName implements AgentPriority<String> {

    final public static String TYPE = "byName";

    public ByName(@NotNull AgentPriorityRegistry registry) {
        registry.register(this);
    }

    @NotNull
    @Override
    public String getType() {
        return TYPE;
    }

    @NotNull
    @Override
    public String getName() {
        return "By name.";
    }

    @NotNull
    @Override
    public String getJspPath() {
        return String.format("%s/jsp/%s.jsp", PLUGIN_PATH, getType());
    }

    @Nullable
    @Override
    public String apply(@Nullable SBuildAgent buildAgent) {
        if (null == buildAgent) {
            return null;
        }

        return buildAgent.getName();
    }

    @XmlRootElement
    @XmlType(name = TYPE)
    public static class Config implements BaseConfig {
        @NotNull
        @Override
        public String getType() {
            return TYPE;
        }
    }
}
