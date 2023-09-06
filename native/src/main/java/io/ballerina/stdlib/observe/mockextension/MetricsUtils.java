/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.stdlib.observe.mockextension;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.ballerina.runtime.api.utils.JsonUtils;
import io.ballerina.runtime.observability.metrics.Counter;
import io.ballerina.runtime.observability.metrics.DefaultMetricRegistry;
import io.ballerina.runtime.observability.metrics.Gauge;
import io.ballerina.runtime.observability.metrics.Metric;
import io.ballerina.runtime.observability.metrics.PolledGauge;
import io.ballerina.stdlib.observe.mockextension.model.Metrics;
import io.ballerina.stdlib.observe.mockextension.model.MockCounter;
import io.ballerina.stdlib.observe.mockextension.model.MockGauge;
import io.ballerina.stdlib.observe.mockextension.model.MockPolledGauge;
import io.ballerina.stdlib.observe.mockextension.typeadapter.DurationTypeAdapter;

import java.time.Duration;

/**
 * Java functions called from Ballerina related to metrics.
 */
public class MetricsUtils {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();

    public static Object getMetrics() {
        Metric[] metricsList = DefaultMetricRegistry.getInstance().getAllMetrics();
        Metrics metrics = new Metrics();
        for (Metric metric : metricsList) {
            if (metric instanceof Counter counter) {
                MockCounter mockCounter = new MockCounter();
                mockCounter.setId(counter.getId());
                mockCounter.setValue(counter.getValue());
                metrics.addCounter(mockCounter);
            } else if (metric instanceof Gauge gauge) {
                MockGauge mockGauge = new MockGauge();
                mockGauge.setId(gauge.getId());
                mockGauge.setValue(gauge.getValue());
                mockGauge.setCount(gauge.getCount());
                mockGauge.setSum(gauge.getSum());
                mockGauge.setSnapshots(gauge.getSnapshots());
                metrics.addGauge(mockGauge);
            } else if (metric instanceof PolledGauge polledGauge) {
                MockPolledGauge mockPolledGauge = new MockPolledGauge();
                mockPolledGauge.setId(polledGauge.getId());
                mockPolledGauge.setValue(polledGauge.getValue());
                metrics.addPolledGauge(mockPolledGauge);
            }
        }
        return JsonUtils.parse(gson.toJson(metrics));
    }
}
