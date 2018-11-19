/*
 * MIT License
 *
 * Copyright (c) 2018 Tassu <hello@tassu.me>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.tassu.snake.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import lombok.val;
import org.bson.Document;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * https://github.com/Minespree/Feather/blob/master/src/main/java/net/minespree/feather/util/BSONUtil.java
 */
public class BSONUtil {

    public static Stream<String> stringListToStream(Document document, String key) {
        Preconditions.checkNotNull(document);
        Preconditions.checkNotNull(key);

        //noinspection unchecked
        val list = (List<Object>) document.get(key);

        if (list == null) {
            return null;
        }

        return list.stream().filter(Objects::nonNull).map(String::valueOf);
    }

    public static Set<String> stringListToSet(Document document, String key) {
        val stream = stringListToStream(document, key);

        if (stream == null) {
            return Sets.newHashSet();
        }

        return stream.collect(Collectors.toSet());
    }

}
