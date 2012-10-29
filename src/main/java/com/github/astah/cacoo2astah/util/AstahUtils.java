package com.github.astah.cacoo2astah.util;

import java.util.ArrayList;
import java.util.List;

import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IClassifierTemplateParameter;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.IGeneralization;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.IParameter;
import com.change_vision.jude.api.inf.model.IRealization;

public class AstahUtils {

	/**
	 * 対象が所有しているクラスを取得します。
	 * 
	 * @param owner
	 *            クラス または パッケージ
	 * @return クラスの場合はネストしているクラス群 パッケージの場合はパッケージにあるクラス群
	 */
	public static IClass[] getOwnedClasses(Object owner) {
		if (owner instanceof IClass) {
			return ((IClass) owner).getNestedClasses();
		} else if (owner instanceof IPackage) {
			List<IClass> classes = new ArrayList<IClass>();
			for (INamedElement element : ((IPackage) owner).getOwnedElements()) {
				if (element instanceof IClass) {
					classes.add((IClass) element);
				}
			}
			return classes.toArray(new IClass[classes.size()]);
		} else {
			return new IClass[0];
		}
	}

	/**
	 * 対象が所有しているクラスのうち、名前を指定して取得します。
	 * 
	 * @param owner
	 *            クラス または パッケージ
	 * @param name
	 *            名前
	 * @return クラスの場合はネストしているクラスのうち、同名のもの パッケージの場合はパッケージにあるクラスのうち、同名のもの
	 */
	public static IClass getOwnedClass(Object owner, String name) {
		if (owner instanceof IClass) {
			return getNestedClass((IClass) owner, name);
		} else if (owner instanceof IPackage) {
			return getOwnedElement((IPackage) owner, name, IClass.class);
		} else {
			return null;
		}
	}

	/**
	 * 対象のパッケージが所有しているINamedElementから、名前を指定して取得します。
	 * 
	 * @param owner
	 *            パッケージ
	 * @param name
	 *            名前
	 * @return 該当するINamedElement
	 */
	public static INamedElement getOwnedElement(IPackage owner, String name) {
		return getOwnedElement(owner, name, INamedElement.class);
	}

	/**
	 * 対象のパッケージが所有しているオブジェクトから、名前と型を指定して取得します。
	 * 
	 * @param owner
	 *            パッケージ
	 * @param name
	 *            名前
	 * @param elementType
	 *            INamedElementの子クラス型
	 * @return 該当するINamedElementの子クラス型
	 */
	public static <T extends INamedElement> T getOwnedElement(IPackage owner,
			String name, Class<T> elementType) {
		for (INamedElement element : owner.getOwnedElements()) {
			if (name.equals(element.getName())) {
				if (elementType.isInstance(element)) {
					return elementType.cast(element);
				}
			}
		}
		return null;
	}

	/**
	 * 対象のクラスが所有しているネストクラスから、名前を指定して取得します。
	 * 
	 * @param owner
	 *            クラス
	 * @param name
	 *            名前
	 * @return 該当するネストクラス
	 */
	public static IClass getNestedClass(IClass owner, String name) {
		for (IClass element : owner.getNestedClasses()) {
			if (name.equals(element.getName())) {
				return element;
			}
		}
		return null;
	}

	/**
	 * 対象のクラスと指定した親クラスの汎化を取得します。
	 * 
	 * @param owner
	 *            クラス
	 * @param superType
	 *            親クラス
	 * @return 汎化オブジェクト ただし、指定した親クラスが対象のクラスの親でない場合は、nullを返す
	 */
	public static IGeneralization getGeneralization(IClass owner,
			IClass superType) {
		for (IGeneralization generalization : owner.getGeneralizations()) {
			if (superType.equals(generalization.getSuperType())) {
				return generalization;
			}
		}
		return null;
	}

	/**
	 * 対象のクラスと指定した子クラスの汎化を取得します。
	 * 
	 * @param owner
	 *            クラス
	 * @param subType
	 *            子クラス
	 * @return 汎化オブジェクト ただし、指定した子クラスが対象のクラスの子でない場合は、nullを返す
	 */
	public static IGeneralization getSpecialization(IClass owner, IClass subType) {
		for (IGeneralization generalization : owner.getSpecializations()) {
			if (subType.equals(generalization.getSubType())) {
				return generalization;
			}
		}
		return null;
	}

	/**
	 * 対象のクラスと指定した実現先クラスの実現を取得します。
	 * 
	 * @param owner
	 *            対象のクラス
	 * @param supplier
	 *            実現先
	 * 
	 * @return 実現 ただし、指定した実現先クラスが対象のクラスの実現先でない場合は、nullを返す
	 */
	public static IRealization getClientRealization(IClass owner,
			IClass supplier) {
		for (IRealization realization : owner.getClientRealizations()) {
			if (supplier.equals(realization.getSupplier())) {
				return realization;
			}
		}
		return null;
	}

	/**
	 * 対象のクラスと指定した実現元クラスの実現を取得します。
	 * 
	 * @param owner
	 *            対象のクラス
	 * @param client
	 *            実現元
	 * 
	 * @return 実現 ただし、指定した実現元クラスが対象のクラスの実現元でない場合は、nullを返す
	 */
	public static IRealization getSupplierRealization(IClass owner,
			IClass client) {
		for (IRealization realization : owner.getSupplierRealizations()) {
			if (client.equals(realization.getClient())) {
				return realization;
			}
		}
		return null;
	}

	/**
	 * 対象クラスから指定した名前のテンプレートパラメータを取得します。
	 * 
	 * @param owner
	 *            対象のクラス
	 * @param name
	 *            パラメータ名
	 * @return テンプレートパラメータ 該当の名前のテンプレートパラメータが存在しない場合はnullが返ります。
	 */
	public static IClassifierTemplateParameter getTemplateParameter(
			IClass owner, String name) {
		for (IClassifierTemplateParameter templateParameter : owner
				.getTemplateParameters()) {
			if (name.equals(templateParameter.getName())) {
				return templateParameter;
			}
		}
		return null;
	}

	/**
	 * 対象のクラスから指定した名前の属性を取得します。
	 * 
	 * @param owner
	 *            対象のクラス
	 * @param name
	 *            属性名
	 * @return 属性 該当の名前の属性が存在しない場合はnullが返ります。
	 */
	public static IAttribute getAttribute(IClass owner, String name) {
		for (IAttribute attribute : owner.getAttributes()) {
			if (name.equals(attribute.getName())) {
				return attribute;
			}
		}
		return null;
	}

	/**
	 * 対象のクラスから指定した名前の操作を取得します。
	 * 
	 * @param owner
	 *            対象のクラス
	 * @param name
	 *            操作名
	 * @return 操作 該当の名前の操作が存在しない場合はnullが返ります。
	 */
	public static IOperation getOperation(IClass owner, String name,
			Object[] parameterTypes) {
		for (IOperation operation : owner.getOperations()) {
			if (name.equals(operation.getName())) {
				IParameter[] parameters = operation.getParameters();
				if (matches(parameters, parameterTypes)) {
					return operation;
				}
			}
		}
		return null;
	}

	/**
	 * 対象のクラスにインタフェースであるかどうかを設定します。
	 * 
	 * @param type
	 *            対象のクラス
	 * @param isInterfase
	 *            インタフェースであるかどうか
	 */
	public static void setInterface(IClass type, boolean isInterface)
			throws InvalidEditingException {
		if (isInterface) {
			addStereotype(type, "interface");
		} else {
			type.removeStereotype("interface");
		}
	}

	/**
	 * 対象の要素にステレオタイプを追加します。
	 * 
	 * @param element
	 *            対象の要素
	 * @param stereotype
	 *            追加するステレオタイプ
	 * @return 追加できた場合はtrue 既に該当するステレオタイプがある場合はfalse
	 */
	public static boolean addStereotype(IElement element, String stereotype)
			throws InvalidEditingException {
		for (String exists : element.getStereotypes()) {
			if (stereotype.equals(exists)) {
				return false;
			}
		}
		element.addStereotype(stereotype);
		return true;
	}

	static boolean matches(IParameter[] parameters, Object[] parameterTypes) {
		if (parameterTypes.length != parameters.length) {
			return false;
		}
		for (int i = 0; i < parameterTypes.length; i++) {
			Object type = parameterTypes[i];
			if (type instanceof IClass) {
				if (!type.equals(parameters[i].getType())) {
					return false;
				}
			} else {
				if (!type.equals(parameters[i].getTypeExpression())) {
					return false;
				}
			}
		}
		return true;
	}

}