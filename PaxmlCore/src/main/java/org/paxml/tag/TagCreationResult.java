/**
 * This file is part of PaxmlCore.
 *
 * PaxmlCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaxmlCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with PaxmlCore.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.paxml.tag;

/**
 * The tag creation result.
 * 
 * @author Xuetao Niu
 * 
 * @param <T>
 *            the type of the tag that is to be enclosed by this result.
 */
public class TagCreationResult<T extends ITag> {
    private final T tagObject;
    private final boolean childrenParsed;

    /**
     * Construct from tag object.
     * 
     * @param tagObject
     *            the tag object
     */
    public TagCreationResult(final T tagObject) {
        this(tagObject, false);
    }

    /**
     * Construct from tag object and a flag.
     * 
     * @param tagObject
     *            the tag object
     * @param childrenParsed
     *            true to flag that all children tags of the tag is parsed.
     *            false otherwise.
     */
    public TagCreationResult(final T tagObject, final boolean childrenParsed) {
        this.tagObject = tagObject;
        this.childrenParsed = childrenParsed;
    }

    public T getTagObject() {
        return tagObject;
    }

    public boolean isChildrenParsed() {
        return childrenParsed;
    }

}
