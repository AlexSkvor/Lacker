package com.lacker.utils.exceptions

open class ImpossibleSituationException(
    additionalMessage: String? = null
) : Exception("Impossible situation happened! Additional info: $additionalMessage")