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

package org.apache.uima.ruta.ide.parser.ast;

import org.antlr.runtime.Token;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.expressions.Expression;

public class FeatureMatchExpression extends Expression {
  
  private final Token feature;
  private final Expression value;

  public FeatureMatchExpression(int sourceStart, int sourceEnd, Token feature, Expression value) {
    super(sourceStart, sourceEnd);
    this.feature = feature;
    this.value = value;
  }


  @Override
  public void traverse(ASTVisitor visitor) throws Exception {
    if (visitor.visit(this)) {
      if (value != null) {
        value.traverse(visitor);
      }
      visitor.endvisit(this);
    }
  }


  @Override
  public int getKind() {
    return 0;
  }


  public Token getFeature() {
    return feature;
  }


  public Expression getValue() {
    return value;
  }

}
