/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.apollo.util;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Random;

/**
 */
public class HashRingTest extends TestCase {

    public void testHashRingTest() throws Exception {
        HashRing<String, String> ring = new HashRing<String, String>();
        assertNull( ring.get("foo") );

        ring.add("node1");
        assertEquals("node1", ring.get("foo"));

        assertFalse(ring.remove("node2"));
        assertTrue(ring.remove("node1"));

        assertNull( ring.get("foo") );

    }

    // Changing this random seed wiil change the generated data used
    // in the distribution tests and change the deviation results.  
    private static final int RANDOM_SEED = 274423;


    public void testJENKINSDistribution() throws Exception {
        double deviationPercent = findDistribution(new Hasher.ToStringHasher(Hasher.JENKINS), "JENKINS");
        assertTrue(deviationPercent < 3.23 );
    }

    public void testMURMURDistribution() throws Exception {
        double deviationPercent = findDistribution(new Hasher.ToStringHasher(Hasher.MURMUR), "MURMUR");
        assertTrue(deviationPercent < 6.13 );
    }

    public void testMD5Distribution() throws Exception {
        double deviationPercent = findDistribution(new Hasher.ToStringHasher(Hasher.MD5), "MD5");
        assertTrue(deviationPercent < 7.18 );
    }

    public void testFNV1ADistribution() throws Exception {
        double deviationPercent = findDistribution(new Hasher.ToStringHasher(Hasher.FNV1A), "FNV1A");
        assertTrue(deviationPercent < 6.66 );
    }

    public void testCRC32Distribution() throws Exception {
        double deviationPercent = findDistribution(new Hasher.ToStringHasher(Hasher.CRC32), "CRC32");
        assertTrue(deviationPercent < 25.92 );
    }

    /**
     * Goes to show that the native String.hashCode() implementation is not very good a generating
     * a uniformly distributed hash values.
     */
    public void testNativeDistribution() throws Exception {
        double deviationPercent = findDistribution(new Hasher.Native(), "Native");
        assertTrue(deviationPercent < 60.66 );
    }

    Random random;

    private double findDistribution(Hasher hasher, String name) {
        double deviation;
        final int NODE_COUNT = 5;
        final int KEYS_PER_NODE = 100000;
        final int KEY_COUNT = KEYS_PER_NODE*NODE_COUNT;
        int counts[] = new int[NODE_COUNT];

        System.out.println("Tesing key distribution of hasher: "+name);
        random = new Random(RANDOM_SEED);
        HashRing<String, String> ring = new HashRing<String, String>(hasher);
        HashMap<String, Integer> nodes = new  HashMap<String, Integer>();
        for( int i=0; i< NODE_COUNT; i++) {
            String node = randomWord(8);
            ring.add(node);
            nodes.put(node, i);
        }

        long start = System.currentTimeMillis();
        for( int i=0; i< KEY_COUNT; i++) {
            String node = ring.get(randomWord(15));
            counts[nodes.get(node)] ++;
        }
        long end = System.currentTimeMillis();
        System.out.println("Hashed "+KEY_COUNT+" keys in "+(end-start)+" ms.");

        deviation = stdDeviation(counts);
        double deviationPercent = (deviation / KEYS_PER_NODE) * 100;
        System.out.println(name +" stadard deviation: "+deviation+", as percent of keys/node: "+ deviationPercent +"%");
        return deviationPercent;
    }

    char [] wordCharacters = createWordCharacters();

    private char[] createWordCharacters() {
        StringBuilder sb = new StringBuilder();
        for( char c='a'; c <= 'z'; c++) {
            sb.append(c);
        }
        for( char c='A'; c <= 'Z'; c++) {
            sb.append(c);
        }
        for( char c='0'; c <= '9'; c++) {
            sb.append(c);
        }
        return sb.toString().toCharArray();
    }


    public double stdDeviation(int []values) {
        long c=0;
        for (int value : values) {
            c += value;
        }
        double mean = (1.0 * c) / values.length;
        double rc = 0;
        for (int value : values) {
            double v = value - mean;
            rc += (v*v);
        }
        return Math.sqrt(rc / values.length);
    }

    private String randomWord(int size) {
        StringBuilder sb = new StringBuilder(size);
        for( int i=0; i < size; i++) {
            sb.append(wordCharacters[random.nextInt(wordCharacters.length)]);
        }
        return sb.toString();
    }
}
