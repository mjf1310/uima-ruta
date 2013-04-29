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

package org.apache.uima.ruta.explain.apply;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.caseditor.editor.AnnotationEditor;
import org.apache.uima.caseditor.editor.ICasDocument;
import org.apache.uima.caseditor.editor.ICasDocumentListener;
import org.apache.uima.caseditor.editor.ICasEditorInputListener;
import org.apache.uima.ruta.addons.RutaAddonsPlugin;
import org.apache.uima.ruta.engine.RutaEngine;
import org.apache.uima.ruta.explain.ExplainConstants;
import org.apache.uima.ruta.explain.ExplainUtils;
import org.apache.uima.ruta.explain.tree.ExplainTree;
import org.apache.uima.ruta.explain.tree.RuleApplyNode;
import org.apache.uima.ruta.ide.core.builder.RutaProjectUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;

public class ApplyViewPage extends Page implements ISelectionListener, ICasEditorInputListener,
        IDoubleClickListener, ICasDocumentListener {

  protected TreeViewer viewer;

  protected int current = 0;

  protected Map<String, Image> images;

  protected AnnotationEditor editor;

  protected ICasDocument document;

  public ApplyViewPage(AnnotationEditor editor) {
    super();
    this.editor = editor;
    this.document = editor.getDocument();
  }

  private void initImages() {
    images = new HashMap<String, Image>();
    ImageDescriptor desc;
    Image image;
    String name;

    desc = RutaAddonsPlugin.getImageDescriptor("/icons/arrow_refresh.png");
    image = desc.createImage();
    name = ExplainConstants.BLOCK_APPLY_TYPE;
    images.put(name, image);

    desc = RutaAddonsPlugin.getImageDescriptor("/icons/arrow_right.png");
    image = desc.createImage();
    name = ExplainConstants.RULE_APPLY_TYPE;
    images.put(name, image);

    desc = RutaAddonsPlugin.getImageDescriptor("/icons/arrow_branch.png");
    image = desc.createImage();
    name = ExplainConstants.RULE_APPLY_TYPE + "Delegate";
    images.put(name, image);
  }

  public Image getImage(String name) {
    if (images == null) {
      initImages();
    }
    return images.get(name);
  }

  @Override
  public Control getControl() {
    return viewer.getControl();
  }

  @Override
  public void setFocus() {
    viewer.getControl().setFocus();
  }

  @Override
  public void init(IPageSite pageSite) {
    super.init(pageSite);
  }

  public TreeViewer getTreeViewer() {
    return viewer;
  }

  @Override
  public void createControl(Composite parent) {
    viewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.setContentProvider(new ApplyTreeContentProvider());
    viewer.setLabelProvider(new ApplyTreeLabelProvider(this));

    document.addChangeListener(this);
    
    ExplainTree tree = new ExplainTree(document.getCAS());
    viewer.setInput(tree.getRoot());
    viewer.addDoubleClickListener(this);
    getSite().setSelectionProvider(viewer);
    getSite().getPage().addSelectionListener(this);
    editor.addCasEditorInputListener(this);
    viewer.refresh();
  }

  public void doubleClick(DoubleClickEvent event) {
    ISelection selection = event.getSelection();
    if (!selection.isEmpty() && selection instanceof TreeSelection) {
      TreeSelection s = (TreeSelection) selection;
      Object firstElement = s.getFirstElement();
      if (firstElement instanceof RuleApplyNode) {
        RuleApplyNode node = (RuleApplyNode) firstElement;
        FeatureStructure fs = node.getFeatureStructure();
        if (fs.getType().getName().equals(ExplainConstants.RULE_APPLY_TYPE)) {
          Type t = fs.getType();
          Feature featureId = t.getFeatureByBaseName(ExplainConstants.ID);
          Feature featureScript = t.getFeatureByBaseName(ExplainConstants.SCRIPT);
          int id = fs.getIntValue(featureId);
          String script = fs.getStringValue(featureScript);
          IEditorInput editorInput = editor.getEditorInput();
          if (editorInput instanceof FileEditorInput) {
            FileEditorInput fei = (FileEditorInput) editorInput;
            IPath path = fei.getPath();
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IWorkspaceRoot workspaceRoot = workspace.getRoot();
            IFile iFile = workspaceRoot.getFileForLocation(path);
            IProject project = iFile.getProject();
            IScriptProject scriptProject = DLTKCore.create(project);
            List<IFolder> allScriptFolders;
            try {
              allScriptFolders = RutaProjectUtils.getAllScriptFolders(scriptProject);
              List<String> folders = RutaProjectUtils.getFolderLocations(allScriptFolders);
              String locate = RutaEngine
                      .locate(script, folders.toArray(new String[0]), ".tm");
              IPath locatedPath = new Path(locate);
              ExplainUtils.openInRutaEditor(locatedPath, id);
            } catch (CoreException e) {
              RutaAddonsPlugin.error(e);
            }
          }
        }
      }
    }
  }

  @Override
  public void dispose() {
    super.dispose();
    getSite().getPage().removeSelectionListener(this);
    editor.removeCasEditorInputListener(this);
    if (images != null) {
      for (Image each : images.values()) {
        each.dispose();
      }
    }
  }

  public void mouseDown(final MouseEvent event) {

  }

  public void mouseUp(final MouseEvent event) {

  }

  public void mouseDoubleClick(final MouseEvent event) {

  }

  public void selectionChanged(IWorkbenchPart part, ISelection selection) {

  }

  public void casDocumentChanged(IEditorInput oldInput, ICasDocument oldDocument,
          IEditorInput newInput, ICasDocument newDocument) {
    // editor.removeCasEditorInputListener(this);
    // document = newDocument;
    // editor.addCasEditorInputListener(this);
    // ExplainTree tree = new ExplainTree(document.getCAS());
    // viewer.setInput(tree.getRoot());
    // viewer.refresh();

  }

  public void added(FeatureStructure newFeatureStructure) {
    
  }

  public void added(Collection<FeatureStructure> newFeatureStructure) {
    
  }

  public void removed(FeatureStructure deletedFeatureStructure) {
    
  }

  public void removed(Collection<FeatureStructure> deletedFeatureStructure) {
    
  }

  public void updated(FeatureStructure featureStructure) {
    
  }

  public void updated(Collection<FeatureStructure> featureStructure) {
    
  }

  public void changed() {
    Display.getDefault().syncExec(new Runnable() {
      public void run() {
        reloadTree();
      }

    });
  }

  private void reloadTree() {
    ExplainTree tree = new ExplainTree(document.getCAS());
    viewer.setInput(tree.getRoot());
  }
  public void viewChanged(String oldViewName, String newViewName) {
    changed();
  }
}
