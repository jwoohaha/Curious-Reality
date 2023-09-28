package com.goduri.curiousaboutreality.wordCount.config;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkNewConfig {
	@Value("${spark.app.name}")
	private String appName;
	@Value("${spark.master}")
	private String masterUri;

	@Bean
	public SparkConf conf() {
		return new SparkConf().setAppName(appName).setMaster(masterUri);
	}


}
