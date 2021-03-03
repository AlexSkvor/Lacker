package com.lacker.visitors.utils

open class ImpossibleSituationException(
    additionalMessage: String? = null
) : Exception("Impossible situation happened! Additional info: $additionalMessage")