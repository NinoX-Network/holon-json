/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.json.internal.support;

import java.util.List;
import java.util.Optional;

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;

/**
 * Represents the serialization node of a {@link PropertyBox} serialization tree.
 * 
 * @since 5.1.0
 */
public interface PropertyBoxSerializationNode {

	/**
	 * Get the node serialization name.
	 * @return the node serialization name
	 */
	String getName();

	/**
	 * Get the property bound to this node, if it is a leaf node.
	 * @return Optional node property
	 */
	Optional<Property<?>> getProperty();

	/**
	 * Get the children nodes, if it isn't a leaf node.
	 * @return the children nodes, empty if leaf node
	 */
	List<PropertyBoxSerializationNode> getChildren();

}
