package com.white_horse.photocollage.utils

interface Action<T> {
    fun run(value : T):  Unit
}