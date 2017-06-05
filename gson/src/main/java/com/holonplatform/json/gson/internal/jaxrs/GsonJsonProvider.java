/*
 * Copyright 2000-2016 Holon TDCN.
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
package com.holonplatform.json.gson.internal.jaxrs;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.holonplatform.json.gson.GsonConfiguration;

/**
 * JAX-RS message body reader and writer using {@link Gson} as JSON serializer/deserializer.
 *
 * @since 5.0.0
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonJsonProvider implements MessageBodyWriter<Object>, MessageBodyReader<Object> {

	private static final Charset CHARSET = StandardCharsets.UTF_8;

	@Context
	private Providers providers;

	private Gson gson;

	private Gson getGson() {
		if (gson == null) {
			// init using a contextresolver, if available
			ContextResolver<Gson> contextResolver = providers.getContextResolver(Gson.class,
					MediaType.APPLICATION_JSON_TYPE);
			if (contextResolver != null) {
				gson = contextResolver.getContext(Gson.class);
			}
			if (gson == null) {
				// use default
				gson = GsonConfiguration.builder().create();
			}
		}
		return gson;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.ext.MessageBodyReader#isReadable(java.lang.Class, java.lang.reflect.Type,
	 * java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
	 */
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.ext.MessageBodyReader#readFrom(java.lang.Class, java.lang.reflect.Type,
	 * java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap,
	 * java.io.InputStream)
	 */
	@Override
	public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		final Type jsonType = type.equals(genericType) ? type : genericType;
		try (final Reader reader = new InputStreamReader(entityStream, CHARSET)) {
			return getGson().fromJson(reader, jsonType);
		} catch (JsonIOException e) {
			throw new IOException(e);
		} catch (JsonSyntaxException e) {
			throw new WebApplicationException(e.getMessage(), e, Status.BAD_REQUEST);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.ext.MessageBodyWriter#isWriteable(java.lang.Class, java.lang.reflect.Type,
	 * java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
	 */
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.ext.MessageBodyWriter#getSize(java.lang.Object, java.lang.Class, java.lang.reflect.Type,
	 * java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
	 */
	@Override
	public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object, java.lang.Class, java.lang.reflect.Type,
	 * java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap,
	 * java.io.OutputStream)
	 */
	@Override
	public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {
		final Type jsonType = type.equals(genericType) ? type : genericType;
		try (final Writer writer = new OutputStreamWriter(entityStream, CHARSET)) {
			try {
				getGson().toJson(t, jsonType, writer);
			} catch (JsonIOException e) {
				throw new IOException(e);
			} catch (JsonSyntaxException e) {
				throw new WebApplicationException(e.getMessage(), e, Status.BAD_REQUEST);
			}
		}
	}

}
