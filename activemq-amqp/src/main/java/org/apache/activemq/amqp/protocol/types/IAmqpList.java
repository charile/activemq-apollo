/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with his work
 * for additional information regarding copyright ownership. The ASF licenses
 * this file to You under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.activemq.amqp.protocol.types;

import java.util.Iterator;
import java.util.List;

import org.apache.activemq.amqp.protocol.types.AmqpType;
import org.apache.activemq.amqp.protocol.types.IAmqpList;

public interface IAmqpList extends Iterable<AmqpType<?, ?>> {

    public AmqpType<?, ?> get(int index);

    public void set(int index, AmqpType<?, ?> value);

    public int getListCount();

    public static class AmqpListIterator implements Iterator<AmqpType<?, ?>> {
        int next = 0;
        final IAmqpList list;

        public AmqpListIterator(IAmqpList list) {
            this.list = list;
        }

        public boolean hasNext() {
            return next < list.getListCount();
        }

        public AmqpType<?, ?> next() {
            return list.get(next++);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static abstract class AbstractAmqpList implements IAmqpList {
        public static final int hashCodeFor(IAmqpList l) {
            int hashCode = 1;
            for (Object obj : l) {
                hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
            }
            return hashCode;
        }
        
        public static final boolean checkEqual(IAmqpList l1, IAmqpList l2) {
            if (l1 == null ^ l2 == null) {
                return false;
            }

            if (l1 == null) {
                return true;
            }

            if (l1.getListCount() != l2.getListCount()) {
                return false;
            }

            Iterator<?> i1 = l1.iterator();
            Iterator<?> i2 = l2.iterator();
            while (i1.hasNext()) {
                Object e1 = i1.next();
                Object e2 = i2.next();
                if (!(e1 == null ? e2 == null : e1.equals(e2))) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class AmqpWrapperList extends AbstractAmqpList {
        private final List<AmqpType<?, ?>> list;

        public AmqpWrapperList(List<AmqpType<?, ?>> list) {
            this.list = list;
        }

        public AmqpType<?, ?> get(int index) {
            return list.get(index);
        }

        public int getListCount() {
            return list.size();
        }

        public void set(int index, AmqpType<?, ?> value) {
            list.set(index, value);
        }

        public Iterator<AmqpType<?, ?>> iterator() {
            return list.iterator();
        }

        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }

            if (o instanceof IAmqpList) {
                return equals((IAmqpList) o);
            }
            return false;
        }

        public boolean equals(IAmqpList l) {
            return checkEqual(this, l);
        }

        public int hashCode() {
            return hashCodeFor(this);
        }
    }

    public static class ArrayBackedList extends AbstractAmqpList {
        AmqpType<?, ?>[] list;

        ArrayBackedList(int size) {
            list = new AmqpType<?, ?>[size];
        }

        public AmqpType<?, ?> get(int index) {
            return list[index];
        }

        public int getListCount() {
            return list.length;
        }

        public void set(int index, AmqpType<?, ?> value) {
            list[index] = value;
        }

        public Iterator<AmqpType<?, ?>> iterator() {
            return new AmqpListIterator(this);
        }

        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }

            if (o instanceof IAmqpList) {
                return equals((IAmqpList) o);
            }
            return false;
        }

        public boolean equals(IAmqpList l) {
            return checkEqual(this, l);
        }

        public int hashCode() {
            return hashCodeFor(this);
        }
    }
}
