package com.starskvim.printmodelsarchive

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class PrintModelsArchiveApplication

fun main(args: Array<String>) {
	runApplication<PrintModelsArchiveApplication>(*args)
}
