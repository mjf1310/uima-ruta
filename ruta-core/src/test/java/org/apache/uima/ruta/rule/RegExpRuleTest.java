/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.uima.ruta.rule;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.ruta.engine.Ruta;
import org.apache.uima.ruta.engine.RutaEngine;
import org.apache.uima.ruta.engine.RutaTestUtils;
import org.apache.uima.ruta.engine.RutaTestUtils.TestFeature;
import org.junit.Assert;
import org.junit.Test;

public class RegExpRuleTest {

  @Test
  public void test() throws Exception {
    String name = this.getClass().getSimpleName();
    String namespace = this.getClass().getPackage().getName().replaceAll("\\.", "/");

    Map<String, String> complexTypes = new HashMap<String, String>();
    Map<String, List<TestFeature>> features = new TreeMap<String, List<TestFeature>>();
    String typeName = "org.apache.uima.Complex";
    complexTypes.put(typeName, "uima.tcas.Annotation");
    List<TestFeature> list = new ArrayList<RutaTestUtils.TestFeature>();
    features.put(typeName, list);
    String fn1 = "a";
    list.add(new TestFeature(fn1, "", "uima.tcas.Annotation"));
    String fn2 = "b";
    list.add(new TestFeature(fn2, "", "uima.cas.Boolean"));
    String fn3 = "s";
    list.add(new TestFeature(fn3, "", "uima.cas.String"));

    CAS cas = RutaTestUtils.process(namespace + "/" + name + RutaEngine.SCRIPT_FILE_EXTENSION,
            namespace + "/" + name + ".txt", 50, false, false, complexTypes, features, null);
    Type t = null;
    AnnotationIndex<AnnotationFS> ai = null;
    FSIterator<AnnotationFS> iterator = null;

    t = RutaTestUtils.getTestType(cas, 1);
    ai = cas.getAnnotationIndex(t);
    assertEquals(1, ai.size());
    iterator = ai.iterator();
    assertEquals(28, iterator.next().getCoveredText().length());

    t = RutaTestUtils.getTestType(cas, 2);
    ai = cas.getAnnotationIndex(t);
    iterator = ai.iterator();
    assertEquals(1, ai.size());
    assertEquals(26, iterator.next().getCoveredText().length());

    RutaTestUtils.assertAnnotationsEquals(cas, 3, 2, "y", "z");
    RutaTestUtils.assertAnnotationsEquals(cas, 4, 2, "y", "z");
    RutaTestUtils.assertAnnotationsEquals(cas, 5, 3, "B", "B", "B");
    RutaTestUtils.assertAnnotationsEquals(cas, 6, 2, "y", "z");
    RutaTestUtils.assertAnnotationsEquals(cas, 7, 3, "ByB", "BzB", "BB");

    t = cas.getTypeSystem().getType(typeName);
    ai = cas.getAnnotationIndex(t);
    iterator = ai.iterator();
    assertEquals(5, ai.size());
    AnnotationFS next = null;
    String stringValue = null;
    Boolean booleanValue = null;
    AnnotationFS afs = null;

    next = iterator.next();
    assertEquals("y", next.getCoveredText());
    stringValue = next.getStringValue(t.getFeatureByBaseName(fn3));
    assertEquals("ByBB", stringValue);
    booleanValue = next.getBooleanValue(t.getFeatureByBaseName(fn2));
    assertEquals(true, booleanValue);
    afs = (AnnotationFS) next.getFeatureValue(t.getFeatureByBaseName(fn1));
    assertEquals("B", afs.getCoveredText());

    next = iterator.next();
    assertEquals("B", next.getCoveredText());
    stringValue = next.getStringValue(t.getFeatureByBaseName(fn3));
    assertEquals("ByBB", stringValue);
    booleanValue = next.getBooleanValue(t.getFeatureByBaseName(fn2));
    assertEquals(false, booleanValue);
    afs = (AnnotationFS) next.getFeatureValue(t.getFeatureByBaseName(fn1));
    assertEquals("y", afs.getCoveredText());

    next = iterator.next();
    assertEquals("z", next.getCoveredText());
    stringValue = next.getStringValue(t.getFeatureByBaseName(fn3));
    assertEquals("BzBB", stringValue);
    booleanValue = next.getBooleanValue(t.getFeatureByBaseName(fn2));
    assertEquals(true, booleanValue);
    afs = (AnnotationFS) next.getFeatureValue(t.getFeatureByBaseName(fn1));
    assertEquals("B", afs.getCoveredText());

    next = iterator.next();
    assertEquals("B", next.getCoveredText());
    stringValue = next.getStringValue(t.getFeatureByBaseName(fn3));
    assertEquals("BzBB", stringValue);
    booleanValue = next.getBooleanValue(t.getFeatureByBaseName(fn2));
    assertEquals(false, booleanValue);
    afs = (AnnotationFS) next.getFeatureValue(t.getFeatureByBaseName(fn1));
    assertEquals("z", afs.getCoveredText());

    next = iterator.next();
    assertEquals("B", next.getCoveredText());
    stringValue = next.getStringValue(t.getFeatureByBaseName(fn3));
    assertEquals("BBB", stringValue);
    booleanValue = next.getBooleanValue(t.getFeatureByBaseName(fn2));
    assertEquals(false, booleanValue);

    cas.release();
  }

  @Test
  public void testSegments() throws Exception {
    String document = "bla concepta bla";
    String script = "\"(concept)([a-z])\"-> 1 = T1, 2 = T2;\n";
    script += "T1{-PARTOF(T2)-> T2};";
    CAS cas = RutaTestUtils.getCAS(document);
    Ruta.apply(cas, script);
    RutaTestUtils.assertAnnotationsEquals(cas, 2, 2, "concept", "a");
  }

  @Test
  public void testPartitioningInSequentialMatching() throws Exception {
    String document = "11\n11ab\n1122\n11";
    String script = " ";

    script += "\"11\" -> T1;\r\n";
    script += "\"[0-9]\" -> T2;\r\n";
    script += "ADDRETAINTYPE(WS);\r\n";
    script += "a:(T1 Annotation*{PARTOF({W,T2})}){-> T3};\r\n";
    script += "REMOVERETAINTYPE(WS);";

    CAS cas = RutaTestUtils.getCAS(document);
    Ruta.apply(cas, script);

    RutaTestUtils.assertAnnotationsEquals(cas, 1, 4, "11", "11", "11", "11");
    Assert.assertEquals(10,
            cas.getAnnotationIndex(cas.getTypeSystem().getType(RutaTestUtils.TYPE + "2")).size());
    RutaTestUtils.assertAnnotationsEquals(cas, 3, 4, "11", "11ab", "1122", "11");
  }
}
