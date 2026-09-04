package com.turnin.core.data.analytics

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.turnin.core.domain.util.analytics.PerformanceTrace
import com.turnin.core.domain.util.analytics.PerformanceTracer
import javax.inject.Inject

class FirebasePerformanceTracer @Inject constructor(
    private val firebasePerf: FirebasePerformance,
) : PerformanceTracer {
    override fun startTrace(name: String): PerformanceTrace {
        val trace = firebasePerf.newTrace(name)
        trace.start()
        return FirebasePerformanceTrace(trace)
    }
}

private class FirebasePerformanceTrace(
    private val trace: Trace,
) : PerformanceTrace {
    override fun putAttribute(key: String, value: String) {
        trace.putAttribute(key, value)
    }

    override fun stop() {
        trace.stop()
    }
}
