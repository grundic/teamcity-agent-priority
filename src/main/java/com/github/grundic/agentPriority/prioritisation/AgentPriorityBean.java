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

import jetbrains.buildServer.controllers.BasePropertiesBean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * User: g.chernyshev
 * Date: 13/11/16
 * Time: 18:12
 */
public class AgentPriorityBean extends BasePropertiesBean {

    @Nullable
    private String priorityId;
    @Nullable
    private String priorityType;

    public AgentPriorityBean() {
        super(new HashMap<>(), new HashMap<>());
    }

    public AgentPriorityBean(@NotNull Map<String, String> properties, @NotNull Map<String, String> defaultProperties) {
        super(properties, defaultProperties);
    }

    @Nullable
    public String getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(@NotNull String priorityId) {
        this.priorityId = priorityId;
    }

    @Nullable
    public String getPriorityType() {
        return priorityType;
    }

    public void setPriorityType(@Nullable String priorityType) {
        this.priorityType = priorityType;
    }
}
