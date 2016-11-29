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

package com.github.grundic.agentPriority.prioritisation.impl;

import com.github.grundic.agentPriority.prioritisation.AgentPriority;
import com.google.common.primitives.Ints;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.BuildHistory;
import jetbrains.buildServer.serverSide.SBuildAgent;
import jetbrains.buildServer.serverSide.SFinishedBuild;
import jetbrains.buildServer.util.ItemProcessor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * User: g.chernyshev
 * Date: 05/11/16
 * Time: 22:17
 */
public class ByBuildStatus extends AgentPriority {
    private final static String TYPE = "byBuildStatus";

    private final static String BUILD_LIMIT = "buildLimit";
    private final static String HISTORY_COEFFICIENT = "historyCoefficient";
    private final static String BASE_SCALE = "baseScale";
    private final static String SUCCESSFUL_SCORE = "successfulScore";
    private final static String FAILED_SCORE = "failedScore";

    private final static Integer DEFAULT_BUILD_LIMIT = 10;
    private final static Integer DEFAULT_HISTORY_COEFFICIENT = -1;
    private final static Integer DEFAULT_BASE_SCALE = 10;
    private final static Integer DEFAULT_SUCCESSFUL_SCORE = 1;
    private final static Integer DEFAULT_FAILED_SCORE = -1;


    private BuildHistory buildHistory;

    public ByBuildStatus(BuildHistory buildHistory) {
        this.buildHistory = buildHistory;
    }

    @NotNull
    @Override
    public String getType() {
        return TYPE;
    }

    @NotNull
    @Override
    public String getName() {
        return "By build status";
    }

    @Nullable
    @Override
    public Integer apply(@Nullable SBuildAgent buildAgent) {
        if (null == buildAgent) {
            return null;
        }

        if (getBuildType() == null) {
            return null;
        }

        Integer buildLimit = getValue(BUILD_LIMIT, DEFAULT_BUILD_LIMIT);
        Integer historyCoefficient = getValue(HISTORY_COEFFICIENT, DEFAULT_HISTORY_COEFFICIENT);
        Integer baseScale = getValue(BASE_SCALE, DEFAULT_BASE_SCALE);
        Integer successfulScore = getValue(SUCCESSFUL_SCORE, DEFAULT_SUCCESSFUL_SCORE);
        Integer failedScore = getValue(FAILED_SCORE, DEFAULT_FAILED_SCORE);

        BuildStatusWeightCalculator weightCalculator = new BuildStatusWeightCalculator(
                buildAgent, buildLimit, historyCoefficient, baseScale, successfulScore, failedScore
        );

        buildHistory.processEntries(
                getBuildType().getBuildTypeId(),
                null,
                false,
                false,
                false,
                weightCalculator
        );

        // Return opposite weight for correct ordering
        return -(weightCalculator.getWeight());
    }

    @NotNull
    private Integer getValue(@NotNull String name, @NotNull Integer defaultValue) {
        String strValue = getParameters().get(name);
        if (null == strValue) {
            return defaultValue;
        }

        Integer value = Ints.tryParse(strValue);
        if (null == value) {
            return defaultValue;
        } else {
            return value;
        }
    }

    @NotNull
    @Override
    public Map<String, String> getDefaultProperties() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(BUILD_LIMIT, Integer.toString(DEFAULT_BUILD_LIMIT));
        parameters.put(HISTORY_COEFFICIENT, Integer.toString(DEFAULT_HISTORY_COEFFICIENT));
        parameters.put(BASE_SCALE, Integer.toString(DEFAULT_BASE_SCALE));
        parameters.put(SUCCESSFUL_SCORE, Integer.toString(DEFAULT_SUCCESSFUL_SCORE));
        parameters.put(FAILED_SCORE, Integer.toString(DEFAULT_FAILED_SCORE));

        return parameters;
    }
}

class BuildStatusWeightCalculator implements ItemProcessor<SFinishedBuild> {

    private int index;
    private int weight;

    @NotNull
    private final SBuildAgent buildAgent;
    private final int buildLimit;
    private final int historyCoefficient;
    private final int baseScale;
    private final int successfulScore;
    private final int failedScore;

    BuildStatusWeightCalculator(@NotNull SBuildAgent buildAgent, int buildLimit, int historyCoefficient, int baseScale, int successfulScore, int failedScore) {

        index = 0;
        weight = 0;

        this.buildAgent = buildAgent;
        this.buildLimit = buildLimit;
        this.historyCoefficient = historyCoefficient;
        this.baseScale = baseScale;
        this.successfulScore = successfulScore;
        this.failedScore = failedScore;
    }

    @Override
    public boolean processItem(SFinishedBuild build) {
        index++;

        if (build.getAgent() != buildAgent) {
            return true;
        }

        if (index > buildLimit) {
            return false;
        }

        int coefficient = index * historyCoefficient + baseScale;
        Status status = build.getBuildStatus();
        if (status.isSuccessful()) {
            weight += successfulScore * coefficient;
        } else {
            weight += failedScore * coefficient;
        }

        return true;
    }

    int getWeight() {
        return weight;
    }
}
