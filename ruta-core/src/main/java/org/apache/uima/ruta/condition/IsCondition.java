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

package org.apache.uima.ruta.condition;

import java.util.Collection;
import java.util.List;

import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.ruta.RutaStream;
import org.apache.uima.ruta.expression.type.AbstractTypeListExpression;
import org.apache.uima.ruta.expression.type.ITypeExpression;
import org.apache.uima.ruta.rule.EvaluatedCondition;
import org.apache.uima.ruta.rule.MatchContext;
import org.apache.uima.ruta.type.RutaBasic;
import org.apache.uima.ruta.visitor.InferenceCrowd;

public class IsCondition extends TypeSentiveCondition {

  public IsCondition(ITypeExpression type) {
    super(type);
  }

  public IsCondition(AbstractTypeListExpression list) {
    super(list);
  }

  @Override
  public EvaluatedCondition eval(MatchContext context, RutaStream stream, InferenceCrowd crowd) {
    AnnotationFS annotation = context.getAnnotation();

    if (annotation == null) {
      return new EvaluatedCondition(this, false);
    }

    RutaBasic beginAnchor = stream.getBeginAnchor(annotation.getBegin());
    if (!isWorkingOnList()) {
      Type t = type.getType(context, stream);
      boolean result = false;
      if (t != null) {
        Collection<AnnotationFS> beginAnchors = beginAnchor.getBeginAnchors(t);
        if (beginAnchors != null) {
          for (AnnotationFS annotationFS : beginAnchors) {
            result |= check(annotation, annotationFS);
            if (result == true) {
              break;
            }
          }
        }
      }
      return new EvaluatedCondition(this, result);
    } else {
      boolean result = false;
      List<Type> types = getList().getList(context, stream);
      for (Type type : types) {
        Collection<AnnotationFS> beginAnchors = beginAnchor.getBeginAnchors(type);
        if (beginAnchors != null) {
          for (AnnotationFS annotationFS : beginAnchors) {
            result |= check(annotation, annotationFS);
            if (result == true) {
              break;
            }
          }
        }
      }
      return new EvaluatedCondition(this, result);
    }
  }

  private boolean check(AnnotationFS a1, AnnotationFS a2) {
    boolean result = false;
    if (a1 != null && a2 != null && a1.getBegin() == a2.getBegin() && a1.getEnd() == a2.getEnd()) {
      result = true;
    }
    return result;
  }

}
