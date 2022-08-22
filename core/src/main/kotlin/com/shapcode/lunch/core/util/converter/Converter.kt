package com.shapcode.lunch.core.util.converter

interface Converter<T> {
    fun toString(value: T): String
    fun fromString(value: String): T
}