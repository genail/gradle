/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.util;

import static org.gradle.util.GUtil.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class GUtilTest {
    @Test
    public void convertStringToCamelCase() {
        assertThat(toCamelCase(null), equalTo(null));
        assertThat(toCamelCase(""), equalTo(""));
        assertThat(toCamelCase("word"), equalTo("Word"));
        assertThat(toCamelCase("twoWords"), equalTo("TwoWords"));
        assertThat(toCamelCase("TwoWords"), equalTo("TwoWords"));
        assertThat(toCamelCase("two-words"), equalTo("TwoWords"));
        assertThat(toCamelCase("two.words"), equalTo("TwoWords"));
        assertThat(toCamelCase("two words"), equalTo("TwoWords"));
        assertThat(toCamelCase("two Words"), equalTo("TwoWords"));
        assertThat(toCamelCase("Two Words"), equalTo("TwoWords"));
        assertThat(toCamelCase(" Two  \t words\n"), equalTo("TwoWords"));
        assertThat(toCamelCase("four or so Words"), equalTo("FourOrSoWords"));
        assertThat(toCamelCase("trailing-"), equalTo("Trailing"));
        assertThat(toCamelCase("ABC"), equalTo("ABC"));
        assertThat(toCamelCase("."), equalTo(""));
        assertThat(toCamelCase("-"), equalTo(""));
    }

    @Test
    public void convertStringToWords() {
        assertThat(toWords(null), equalTo(null));
        assertThat(toWords(""), equalTo(""));
        assertThat(toWords("word"), equalTo("word"));
        assertThat(toWords("twoWords"), equalTo("two words"));
        assertThat(toWords("TwoWords"), equalTo("two words"));
        assertThat(toWords("two words"), equalTo("two words"));
        assertThat(toWords("Two Words"), equalTo("two words"));
        assertThat(toWords(" Two  \t words\n"), equalTo("two words"));
        assertThat(toWords("two_words"), equalTo("two words"));
        assertThat(toWords("two.words"), equalTo("two words"));
        assertThat(toWords("two,words"), equalTo("two words"));
        assertThat(toWords("trailing-"), equalTo("trailing"));
        assertThat(toWords("ABC"), equalTo("a b c"));
        assertThat(toWords("."), equalTo(""));
        assertThat(toWords("_"), equalTo(""));
    }
}
