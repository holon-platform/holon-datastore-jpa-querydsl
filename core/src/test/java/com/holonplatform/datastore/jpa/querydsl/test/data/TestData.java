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
package com.holonplatform.datastore.jpa.querydsl.test.data;

import java.io.Serializable;
import java.util.Date;

import com.holonplatform.datastore.jpa.querydsl.test.domain.TestEnum;
import com.holonplatform.datastore.jpa.querydsl.test.domain.TestNested;

public interface TestData extends Serializable {

	Long getKey();

	String getStringValue();

	Double getDecimalValue();

	Date getDateValue();

	TestEnum getEnumValue();

	public int getNumericBooleanValue();

	TestNested getNested();

}
