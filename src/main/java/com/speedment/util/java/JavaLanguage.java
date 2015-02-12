/**
 *
 * Copyright (c) 2006-2015, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.util.java;

import static com.speedment.util.java.sql.SqlUtil.unQuote;
import static com.speedment.util.stream.CollectorUtil.toUnmodifiableSet;
import static com.speedment.util.stream.CollectorUtil.unmodifiableSetOf;
import com.speedment.util.stream.StreamUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

/**
 *
 * @author pemi
 */
public class JavaLanguage {

    private JavaLanguage() {
    }

    // From http://download.oracle.com/javase/tutorial/java/nutsandbolts/_keywords.html
    //
    // Literals
    public final static Set<String> JAVA_LITERAL_WORDS = unmodifiableSetOf(
            "true", "false", "null"
    );

    // Java reserved keywords
    public final static Set<String> JAVA_RESERVED_WORDS = unmodifiableSetOf(
            // Unused
            "const", "goto",
            // The real ones...
            "abstract",
            "continue",
            "for",
            "new",
            "switch",
            "assert",
            "default",
            "goto",
            "package",
            "synchronized",
            "boolean",
            "do",
            "if",
            "private",
            "this",
            "break",
            "double",
            "implements",
            "protected",
            "throw",
            "byte",
            "else",
            "import",
            "public",
            "throws",
            "case",
            "enum",
            "instanceof",
            "return",
            "transient",
            "catch",
            "extends",
            "int",
            "short",
            "try",
            "char",
            "final",
            "interface",
            "static",
            "void",
            "class",
            "finally",
            "long",
            "strictfp",
            "volatile",
            "const",
            "float",
            "native",
            "super",
            "while"
    );

    public static final Set<Class<?>> JAVA_BUILT_IN_CLASSES = unmodifiableSetOf(
            Boolean.class,
            Byte.class,
            Character.class,
            Double.class,
            Float.class,
            Integer.class,
            Long.class,
            Object.class,
            Short.class,
            String.class,
            BigDecimal.class,
            BigInteger.class,
            boolean.class,
            byte.class,
            char.class,
            double.class,
            float.class,
            int.class,
            long.class,
            short.class
    );

    public static final Set<String> JAVA_DEFAULT_IMPORTS = unmodifiableSetOf("java.lang");

    public final static Set<String> JAVA_BUILT_IN_CLASS_WORDS = JAVA_BUILT_IN_CLASSES.stream().map(Class::getSimpleName).collect(toUnmodifiableSet());

    public final static Set<String> JAVA_USED_WORDS = StreamUtil.of(JAVA_LITERAL_WORDS, JAVA_RESERVED_WORDS, JAVA_BUILT_IN_CLASS_WORDS).collect(toUnmodifiableSet());

    private static final Set<String> REPLACEMENT_STRING_SET = unmodifiableSetOf("_", "-", "+", " ");

    public static String getJavaNameFromSqlName(final String sqlName) {
        String result = unQuote(sqlName.trim()); // Trim if there are initial spaces or trailing spaces...
        int underscoreIndex;
        for (String replacement : REPLACEMENT_STRING_SET) {
            while ((underscoreIndex = result.indexOf(replacement)) != -1) {
                String c = result.charAt(underscoreIndex + 1) + "";
                c = c.toUpperCase();
                result = result.substring(0, underscoreIndex) + c + result.substring(underscoreIndex + 2, result.length());
            }
        }
        if (Character.isDigit(result.charAt(0))) {
            // First character is a digit... Add two underscores...
            // This is just an emergency workaround required for informix which names its indexes to numerical...
            result = "__" + result;
        }

        return replaceIfJavaUsedWord(result);
    }

    public static String replaceIfJavaUsedWord(final String word) {
        for (final String javaUsedWord : JAVA_USED_WORDS) {
            if (word.equalsIgnoreCase(javaUsedWord)) {
                // If it is a java reseved/literal/class, add a "_" at the end to avoid naming conflics
                return word + "_";
            }
        }
        return word;
    }

    public static String getJavaObjectName(final String javaTypeName) {
        String result = null;
        if (javaTypeName.startsWith("int")) {
            result = Integer.class.getSimpleName();
        } else if (javaTypeName.startsWith("long")) {
            result = Long.class.getSimpleName();
        } else if (javaTypeName.startsWith("double")) {
            result = Double.class.getSimpleName();
        } else if (javaTypeName.startsWith("float")) {
            result = Float.class.getSimpleName();
        } else if (javaTypeName.startsWith("boolean")) {
            result = Boolean.class.getSimpleName();
        } else if (javaTypeName.startsWith("byte")) {
            result = Byte.class.getSimpleName();
        }

        if (result != null) {
            if (javaTypeName.endsWith("[]")) {
                result += "[]";
            }
        } else {
            result = javaTypeName;
        }

        return result;
    }

}
