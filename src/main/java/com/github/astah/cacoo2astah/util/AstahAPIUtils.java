package com.github.astah.cacoo2astah.util;

import javax.swing.JFrame;

import com.change_vision.jude.api.inf.editor.BasicModelEditor;
import com.change_vision.jude.api.inf.editor.ClassDiagramEditor;
import com.change_vision.jude.api.inf.editor.IDiagramEditorFactory;
import com.change_vision.jude.api.inf.editor.IModelEditorFactory;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.view.IDiagramViewManager;
import com.change_vision.jude.api.inf.view.IViewManager;
import com.github.astah.cacoo2astah.APIException;

/**
 * astah* APIを扱いやすいように包んだユーティリティクラスです。 利用時にインスタンスを作成してください。
 */
public class AstahAPIUtils {

	/**
	 * 図の表示領域を管理するマネージャを返却します。
	 */
	public IDiagramViewManager getDiagramViewManager() {
		IViewManager viewManager = getViewManager();
		IDiagramViewManager diagramViewManager = viewManager
				.getDiagramViewManager();
		return diagramViewManager;
	}

	/**
	 * クラス図上にあるモデルを編集するためのエディタを取得します。
	 */
	public ClassDiagramEditor getClassDiagramEditor() {
		try {
			return getDiagramEditorFactory().getClassDiagramEditor();
		} catch (InvalidUsingException e) {
			throw new APIException(e);
		}
	}

	/**
	 * 各図に共通する基本的なモデルを編集するためのエディタを取得します。
	 * 
	 * @return BasicModelEditor
	 */
	public BasicModelEditor getBasicModelEditor() {
		try {
			return getModelEditorFactory().getBasicModelEditor();
		} catch (InvalidEditingException e) {
			throw new APIException(e);
		}
	}

	/**
	 * astah*のプロジェクトを扱うためのProjectAccessorを取得します。
	 */
	public ProjectAccessor getProjectAccessor() {
		ProjectAccessor projectAccessor = null;
		try {
			projectAccessor = ProjectAccessorFactory.getProjectAccessor();
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
		if (projectAccessor == null)
			throw new IllegalStateException("projectAccessor is null.");
		return projectAccessor;
	}

	/**
	 * astah*本体のメインウィンドウに対応するJFrameを取得します。
	 * 
	 * @return JFrame
	 */
	public JFrame getMainFrame() {
		try {
			return getProjectAccessor().getViewManager().getMainFrame();
		} catch (InvalidUsingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 現在実行しているastah*のエディションを取得します。
	 */
	public String getEdition() {
		return getProjectAccessor().getAstahEdition();
	}

	private IViewManager getViewManager() {
		ProjectAccessor projectAccessor = getProjectAccessor();
		IViewManager viewManager;
		try {
			viewManager = projectAccessor.getViewManager();
		} catch (InvalidUsingException e) {
			throw new IllegalStateException("ViewManager is null.");
		}
		
		if (viewManager == null)
			throw new IllegalStateException("ViewManager is null.");
		return viewManager;
	}

	private IModelEditorFactory getModelEditorFactory() {
		ProjectAccessor projectAccessor = getProjectAccessor();
		IModelEditorFactory modelEditorFactory = projectAccessor
				.getModelEditorFactory();
		if (modelEditorFactory == null)
			throw new IllegalStateException("modelEditorFactory is null.");
		return modelEditorFactory;
	}

	private IDiagramEditorFactory getDiagramEditorFactory() {
		ProjectAccessor projectAccessor = getProjectAccessor();
		IDiagramEditorFactory diagramEditorFactory = projectAccessor
				.getDiagramEditorFactory();
		if (diagramEditorFactory == null)
			throw new IllegalStateException("diagramEditorFactory is null.");
		return diagramEditorFactory;
	}

}