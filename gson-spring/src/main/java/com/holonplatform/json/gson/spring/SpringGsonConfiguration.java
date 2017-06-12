/*
 * Copyright 2000-2017 Holon TDCN.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.json.gson.spring;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.gson.GsonConfiguration;

/**
 * Utility class to handle Gson configuration for Spring framework.
 *
 * @since 5.0.0
 */
public final class SpringGsonConfiguration implements Serializable {

	private static final long serialVersionUID = -6810800710779615921L;

	private SpringGsonConfiguration() {
	}

	/**
	 * Configure Spring RestTemplate, setting up serializers and deserializers for {@link PropertyBox} type handling in
	 * Gson HttpMessageConverters, if any. If no Jackson Gson is registered, a {@link GsonHttpMessageConverter} with a
	 * configured Gson instance will be registered in RestTemplate.
	 * <p>
	 * In order to this method to work, <code>spring-web</code> artifact must be present in classpath.
	 * </p>
	 * @param restTemplate RestTemplate to configure
	 */
	public static void configure(RestTemplate restTemplate) {
		getGsonConverter(restTemplate).orElse(new GsonHttpMessageConverter())
				.setGson(GsonConfiguration.builder().create());

	}

	/**
	 * Get a registered GsonHttpMessageConverter from RestTemplate
	 * @param restTemplate RestTemplate
	 * @return Optional GsonHttpMessageConverter, empty if not registered
	 */
	private static Optional<GsonHttpMessageConverter> getGsonConverter(RestTemplate restTemplate) {
		for (HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
			if (GsonHttpMessageConverter.class.isAssignableFrom(converter.getClass())) {
				return Optional.of((GsonHttpMessageConverter) converter);
			}
		}
		return Optional.empty();
	}

}
