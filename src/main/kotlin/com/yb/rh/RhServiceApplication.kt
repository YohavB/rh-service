package com.yb.rh

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RhServiceApplication

fun main(args: Array<String>) {
    runApplication<RhServiceApplication>(*args)
}
