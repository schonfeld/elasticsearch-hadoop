/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.serialization.builder;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.hadoop.cfg.Settings;
import org.elasticsearch.hadoop.serialization.FieldType;
import org.elasticsearch.hadoop.serialization.Parser;
import org.elasticsearch.hadoop.serialization.Parser.Token;
import org.elasticsearch.hadoop.serialization.SettingsAware;
import org.elasticsearch.hadoop.util.StringUtils;


/**
 * Basic value reader handling using the implied JSON type.
 */
public class JdkValueReader implements SettingsAware, ValueReader {

    private boolean emptyAsNull = true;

    @Override
    public Object readValue(Parser parser, String value, FieldType esType) {
        if (esType == null) {
            return nullValue();
        }

        switch (esType) {
        case NULL:
            return nullValue();
        case STRING:
            return textValue(value);
        case BYTE:
            return byteValue(value, parser);
        case SHORT:
            return shortValue(value, parser);
        case INTEGER:
            return intValue(value, parser);
        case TOKEN_COUNT:
        case LONG:
            return longValue(value, parser);
        case FLOAT:
            return floatValue(value, parser);
        case DOUBLE:
            return doubleValue(value, parser);
        case BOOLEAN:
            return booleanValue(value, parser);
        case BINARY:
            return binaryValue(parser.binaryValue());
        case DATE:
            return date(value, parser);
        case OBJECT:
            // everything else (IP, GEO) gets translated to strings
        default:
            return textValue(value);
        }
    }

    @Override
    public Object createMap() {
        return new LinkedHashMap<Object, Object>();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void addToMap(Object map, Object key, Object value) {
        ((Map) map).put(key, (value != null ? value : nullValue()));
    }

    @Override
    public Object createArray(FieldType type) {
        // no need to create a collection, we'll just reuse the one passed to #addToArray
        return Collections.emptyList();
    }

    @Override
    public Object addToArray(Object array, List<Object> value) {
        return value;
    }

    @Override
    public Object wrapString(String value) {
        return textValue(value);
    }

    private boolean isEmpty(String value) {
        return value.length() == 0 && emptyAsNull;
    }

    protected Object binaryValue(byte[] value) {
        return value;
    }

    protected Object booleanValue(String value, Parser parser) {
        Boolean val = null;

        if (value == null || isEmpty(value)) {
            return nullValue();
        }
        else {
            Token tk = parser.currentToken();

            if (tk == Token.VALUE_BOOLEAN) {
                val = parser.booleanValue();
            }
            else {
                val = parseBoolean(value);
            }
        }

        return processBoolean(val);
    }

    protected Boolean parseBoolean(String value) {
        return Boolean.parseBoolean(value);
    }

    protected Object processBoolean(Boolean value) {
        return value;
    }

    protected Object doubleValue(String value, Parser parser) {
        Double val = null;

        if (value == null || isEmpty(value)) {
            return nullValue();
        }
        else {
            Token tk = parser.currentToken();

            if (tk == Token.VALUE_NUMBER) {
                val = parser.doubleValue();
            }
            else {
                val = parseDouble(value);
            }
        }

        return processDouble(val);
    }

    protected Double parseDouble(String value) {
        return Double.parseDouble(value);
    }

    protected Object processDouble(Double value) {
        return value;
    }

    protected Object floatValue(String value, Parser parser) {
        Float val = null;

        if (value == null || isEmpty(value)) {
            return nullValue();
        }
        else {
            Token tk = parser.currentToken();

            if (tk == Token.VALUE_NUMBER) {
                val = parser.floatValue();
            }
            else {
                val = parseFloat(value);
            }
        }

        return processFloat(val);
    }

    protected Float parseFloat(String value) {
        return Float.parseFloat(value);
    }

    protected Object processFloat(Float value) {
        return value;
    }

    protected Object longValue(String value, Parser parser) {
        Long val = null;

        if (value == null || isEmpty(value)) {
            return nullValue();
        }
        else {
            Token tk = parser.currentToken();

            if (tk == Token.VALUE_NUMBER) {
                val = parser.longValue();
            }
            else {
                val = parseLong(value);
            }
        }

        return processLong(val);
    }

    protected Long parseLong(String value) {
        return Long.parseLong(value);
    }

    protected Object processLong(Long value) {
        return value;
    }

    protected Object intValue(String value, Parser parser) {
        Integer val = null;

        if (value == null || isEmpty(value)) {
            return nullValue();
        }
        else {
            Token tk = parser.currentToken();

            if (tk == Token.VALUE_NUMBER) {
                val = parser.intValue();
            }
            else {
                val = parseInteger(value);
            }
        }

        return processInteger(val);
    }

    protected Integer parseInteger(String value) {
        return Integer.parseInt(value);
    }

    protected Object processInteger(Integer value) {
        return value;
    }

    protected Object byteValue(String value, Parser parser) {
        Byte val = null;

        if (value == null || isEmpty(value)) {
            return nullValue();
        }
        else {
            Token tk = parser.currentToken();

            if (tk == Token.VALUE_NUMBER) {
                val = (byte) parser.intValue();
            }
            else {
                val = parseByte(value);
            }
        }

        return processByte(val);
    }

    protected Byte parseByte(String value) {
        return Byte.parseByte(value);
    }

    protected Object processByte(Byte value) {
        return value;
    }

    protected Object shortValue(String value, Parser parser) {
        Short val = null;

        if (value == null || isEmpty(value)) {
            return nullValue();
        }
        else {
            Token tk = parser.currentToken();

            if (tk == Token.VALUE_NUMBER) {
                val = parser.shortValue();
            }
            else {
                val = parseShort(value);
            }
        }

        return processShort(val);
    }

    protected Short parseShort(String value) {
        return Short.parseShort(value);
    }

    protected Object processShort(Short value) {
        return value;
    }

    protected Object textValue(String value) {
        return (value != null ? (!StringUtils.hasText(value) && emptyAsNull ? nullValue() : parseString(value)) : nullValue());
    }

    protected Object parseString(String value) {
        return value;
    }

    protected Object nullValue() {
        return null;
    }

    protected Object date(String value, Parser parser) {
        Object val = null;

        if (value == null || isEmpty(value)) {
            return nullValue();
        }
        else {
            Token tk = parser.currentToken();

            // UNIX time format
            if (tk == Token.VALUE_NUMBER) {
                val = parseDate(parser.longValue());
            }
            else {
                val = parseDate(value);
            }
        }

        return processDate(val);
    }

    protected Object parseDate(Long value) {
        return new Date(value);
    }

    protected Object parseDate(String value) {
        return parseString(value);
    }

    protected Object processDate(Object value) {
        return value;
    }

    @Override
    public void setSettings(Settings settings) {
        emptyAsNull = settings.getFieldReadEmptyAsNull();
    }
}