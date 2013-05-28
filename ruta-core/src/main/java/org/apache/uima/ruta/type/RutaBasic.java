/* First created by JCasGen Wed Apr 16 17:01:16 CEST 2008 */
package org.apache.uima.ruta.type;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;

/**
 * Updated by JCasGen Thu Jul 12 10:42:33 CEST 2012 XML source:
 * D:/work/workspace-uima6/uimaj-ruta/src
 * /main/java/org/apache/uima/ruta/engine/InternalTypeSystem.xml
 * 
 * @generated
 */
public class RutaBasic extends Annotation {

  private static final int INITIAL_CAPACITY = 2;

  private boolean lowMemoryProfile = true;

  private Map<String, Integer> partOf = new TreeMap<String, Integer>();

  private final Map<Type, Set<AnnotationFS>> beginMap = new HashMap<Type, Set<AnnotationFS>>(
          INITIAL_CAPACITY);

  private final Map<Type, Set<AnnotationFS>> endMap = new HashMap<Type, Set<AnnotationFS>>(
          INITIAL_CAPACITY);

  public boolean isLowMemoryProfile() {
    return lowMemoryProfile;
  }

  public void setLowMemoryProfile(boolean lowMemoryProfile) {
    this.lowMemoryProfile = lowMemoryProfile;
  }

  public void addPartOf(Type type) {
    Integer count = partOf.get(type.getName());
    if (count == null) {
      count = 0;
    }
    count++;
    partOf.put(type.getName(), count);
    if (!lowMemoryProfile) {
      TypeSystem typeSystem = getCAS().getTypeSystem();
      Type parent = typeSystem.getParent(type);
      if (parent != null) {
        addPartOf(parent);
      }
    }
  }

  public void removePartOf(Type type) {
    Integer count = partOf.get(type.getName());
    if (count != null && count > 0) {
      count--;
      partOf.put(type.getName(), count);
    }
    if (!lowMemoryProfile) {
      TypeSystem typeSystem = getCAS().getTypeSystem();
      Type parent = typeSystem.getParent(type);
      if (parent != null) {
        removePartOf(parent);
      }
    }
  }

  public boolean isPartOf(Type type) {
    Integer count = partOf.get(type.getName());
    if (count != null && count > 0) {
      return true;
    }
    if (lowMemoryProfile) {
      List<Type> subsumedTypes = getCAS().getTypeSystem().getProperlySubsumedTypes(type);
      for (Type each : subsumedTypes) {
        Integer parentCount = partOf.get(each.getName());
        if (parentCount != null && parentCount > 0) {
          return true;
        }
      }

    }
    return false;

  }

  public Set<AnnotationFS> getBeginAnchors(Type type) {
    Set<AnnotationFS> set = beginMap.get(type);
    if (lowMemoryProfile) {
      Set<AnnotationFS> result = new HashSet<AnnotationFS>();
      if (set != null) {
        result.addAll(set);
      }
      List<Type> subsumedTypes = getCAS().getTypeSystem().getProperlySubsumedTypes(type);
      for (Type each : subsumedTypes) {
        Set<AnnotationFS> c = beginMap.get(each);
        if (c != null) {
          result.addAll(c);
        }
      }
      return result;
    } else {
      if (set == null) {
        return Collections.emptySet();
      } else {
        return set;
      }
    }
  }

  public Set<AnnotationFS> getEndAnchors(Type type) {
    Set<AnnotationFS> set = endMap.get(type);
    if (lowMemoryProfile) {
      Set<AnnotationFS> result = new HashSet<AnnotationFS>(set);
      if (set != null) {
        result.addAll(set);
      }
      List<Type> subsumedTypes = getCAS().getTypeSystem().getProperlySubsumedTypes(type);
      for (Type each : subsumedTypes) {
        Set<AnnotationFS> c = endMap.get(each);
        if (c != null) {
          result.addAll(c);
        }
      }
      return result;
    } else {
      if (set == null) {
        return Collections.emptySet();
      } else {
        return set;
      }
    }
  }

  public boolean beginsWith(Type type) {
    if (beginMap.containsKey(type)) {
      return true;
    }
    if (lowMemoryProfile) {
      List<Type> subsumedTypes = getCAS().getTypeSystem().getProperlySubsumedTypes(type);
      for (Type each : subsumedTypes) {
        if (beginsWith(each)) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean endsWith(Type type) {
    if (endMap.containsKey(type)) {
      return true;
    }
    if (lowMemoryProfile) {
      List<Type> subsumedTypes = getCAS().getTypeSystem().getProperlySubsumedTypes(type);
      for (Type each : subsumedTypes) {
        if (endsWith(each)) {
          return true;
        }
      }
    }
    return false;
  }

  public void addBegin(AnnotationFS annotation, Type type) {
    Set<AnnotationFS> list = beginMap.get(type);
    if (list == null) {
      list = new HashSet<AnnotationFS>(INITIAL_CAPACITY);
      beginMap.put(type, list);
    }
    list.add(annotation);
    if (!lowMemoryProfile) {
      TypeSystem typeSystem = getCAS().getTypeSystem();
      Type parent = typeSystem.getParent(type);
      if (parent != null) {
        addBegin(annotation, parent);
      }
    }
  }

  public void addEnd(AnnotationFS annotation, Type type) {
    Set<AnnotationFS> list = endMap.get(type);
    if (list == null) {
      list = new HashSet<AnnotationFS>(INITIAL_CAPACITY);
      endMap.put(type, list);
    }
    list.add(annotation);
    if (!lowMemoryProfile) {
      TypeSystem typeSystem = getCAS().getTypeSystem();
      Type parent = typeSystem.getParent(type);
      if (parent != null) {
        addEnd(annotation, parent);
      }
    }
  }

  public void removeBegin(AnnotationFS annotation, Type type) {
    Set<AnnotationFS> list = beginMap.get(type);
    if (list != null) {
      list.remove(annotation);
      if (list.isEmpty()) {
        beginMap.remove(annotation.getType());
      }
    }
    if (!lowMemoryProfile) {
      TypeSystem typeSystem = getCAS().getTypeSystem();
      Type parent = typeSystem.getParent(type);
      if (parent != null) {
        removeBegin(annotation, parent);
      }
    }
  }

  public void removeEnd(AnnotationFS annotation, Type type) {
    Set<AnnotationFS> list = endMap.get(type);
    if (list != null) {
      list.remove(annotation);
      if (list.isEmpty()) {
        endMap.remove(annotation.getType());
      }
    }
    if (!lowMemoryProfile) {
      TypeSystem typeSystem = getCAS().getTypeSystem();
      Type parent = typeSystem.getParent(type);
      if (parent != null) {
        removeEnd(annotation, parent);
      }
    }
  }

  public Map<Type, Set<AnnotationFS>> getBeginMap() {
    return beginMap;
  }

  /**
   * @generated
   * @ordered
   */
  public final static int typeIndexID = JCasRegistry.register(RutaBasic.class);

  /**
   * @generated
   * @ordered
   */
  public final static int type = typeIndexID;

  /** @generated */
  @Override
  public int getTypeIndexID() {
    return typeIndexID;
  }

  /**
   * Never called. Disable default constructor
   * 
   * @generated
   */
  protected RutaBasic() {/* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   * 
   * @generated
   */
  public RutaBasic(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }

  /** @generated */
  public RutaBasic(JCas jcas) {
    super(jcas);
    readObject();
  }

  /** @generated */
  public RutaBasic(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }

  /**
   * <!-- begin-user-doc --> Write your own initialization here <!-- end-user-doc -->
   * 
   * @generated modifiable
   */
  private void readObject() {
  }

  // *--------------*
  // * Feature: Replacement

  /**
   * getter for Replacement - gets
   * 
   * @generated
   */
  public String getReplacement() {
    if (RutaBasic_Type.featOkTst && ((RutaBasic_Type) jcasType).casFeat_replacement == null)
      jcasType.jcas.throwFeatMissing("replacement", "org.apache.uima.ruta.type.RutaBasic");
    return jcasType.ll_cas.ll_getStringValue(addr,
            ((RutaBasic_Type) jcasType).casFeatCode_replacement);
  }

  /**
   * setter for Replacement - sets
   * 
   * @generated
   */
  public void setReplacement(String v) {
    if (RutaBasic_Type.featOkTst && ((RutaBasic_Type) jcasType).casFeat_replacement == null)
      jcasType.jcas.throwFeatMissing("replacement", "org.apache.uima.ruta.type.RutaBasic");
    jcasType.ll_cas.ll_setStringValue(addr, ((RutaBasic_Type) jcasType).casFeatCode_replacement, v);
  }

  public Map<Type, Set<AnnotationFS>> getEndMap() {
    return endMap;
  }

}