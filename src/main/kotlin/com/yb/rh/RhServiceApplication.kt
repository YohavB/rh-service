package com.yb.rh

import mu.KotlinLogging
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class RhServiceApplication

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    logger.info("@}}>----- Launching Rush-Hour-Service -----<{{@")

    SpringApplication.run(RhServiceApplication::class.java, *args)

    logger.info("°º¤ø,¸¸,ø¤º°`°º¤ø,¸,ø¤°º¤ø,¸¸,ø¤º°`°º¤ø,¸ Rush-Hour-Service is up °º¤ø,¸¸,ø¤º°`°º¤ø,¸,ø¤°º¤ø,¸¸,ø¤º°`°º¤ø,¸")
}
