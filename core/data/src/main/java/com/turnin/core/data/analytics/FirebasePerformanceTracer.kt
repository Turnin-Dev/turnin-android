package com.turnin.core.data.analytics

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.turnin.core.domain.util.analytics.PerformanceTrace
import com.turnin.core.domain.util.analytics.PerformanceTracer
import com.turnin.core.domain.util.analytics.TraceAttribute
import com.turnin.core.domain.util.analytics.TraceName
import javax.inject.Inject

class FirebasePerformanceTracer @Inject constructor(
    private val firebasePerf: FirebasePerformance,
) : PerformanceTracer {
    override fun startTrace(name: TraceName): PerformanceTrace {
        val trace = firebasePerf.newTrace(name.value)
        trace.start()
        return FirebasePerformanceTrace(trace)
    }
}

private class FirebasePerformanceTrace(
    private val trace: Trace,
) : PerformanceTrace {
    override fun putAttribute(key: TraceAttribute, value: String) {
        trace.putAttribute(key.value, value.take(100))
    }

    override fun stop() {
        trace.stop()
    }
}
