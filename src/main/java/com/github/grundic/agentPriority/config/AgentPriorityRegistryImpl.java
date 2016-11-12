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

package com.github.grundic.agentPriority.config;

import com.github.grundic.agentPriority.prioritisation.AgentPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: g.chernyshev
 * Date: 07/11/16
 * Time: 18:02
 */
public class AgentPriorityRegistryImpl implements AgentPriorityRegistry {
    private final Map<String, AgentPriority<? extends Comparable>> agentPriorityMap = new ConcurrentHashMap<>();

    @Override
    public void register(@NotNull AgentPriority<? extends Comparable> agentPriority) {
        agentPriorityMap.put(agentPriority.getType(), agentPriority);
    }

    @NotNull
    @Override
    public List<AgentPriority<? extends Comparable>> getPriorities() {
        return new ArrayList<>(agentPriorityMap.values());
    }

    @Override
    public AgentPriority<? extends Comparable> get(@NotNull String type) {
        return agentPriorityMap.get(type);
    }
}
