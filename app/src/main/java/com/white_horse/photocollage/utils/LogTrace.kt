package com.white_horse.photocollage.utils

import com.orhanobut.logger.Logger

class LogTrace {

    companion object {
        fun d(msg: String){
            Logger.d(msg)

        }

        fun d(`object` : Any?){
            Logger.d(`object`)
        }

        fun e(message : String, vararg args : Any?) {
            Logger.e(message, args)
        }
    }
}