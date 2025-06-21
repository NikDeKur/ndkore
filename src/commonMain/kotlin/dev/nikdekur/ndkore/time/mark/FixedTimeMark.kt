package dev.nikdekur.ndkore.time.mark

import kotlin.time.Duration
import kotlin.time.TimeMark

public class FixedTimeMark(
    public val value: Duration
) : TimeMark {

    override fun elapsedNow(): Duration {
        return value
    }

    public companion object {
        public val Zero: FixedTimeMark = FixedTimeMark(Duration.ZERO)
    }
}