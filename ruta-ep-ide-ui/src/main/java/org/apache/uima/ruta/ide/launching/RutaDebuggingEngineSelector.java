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

package org.apache.uima.ruta.ide.launching;

import org.apache.uima.ruta.ide.RutaIdeUIPlugin;
import org.apache.uima.ruta.ide.debug.RutaDebugConstants;
import org.eclipse.dltk.core.DLTKIdContributionSelector;
import org.eclipse.dltk.core.PreferencesLookupDelegate;

public class RutaDebuggingEngineSelector extends DLTKIdContributionSelector {
  @Override
  protected String getSavedContributionId(PreferencesLookupDelegate delegate) {
    return delegate.getString(RutaIdeUIPlugin.PLUGIN_ID, RutaDebugConstants.DEBUGGING_ENGINE_ID_KEY);
  }

}
