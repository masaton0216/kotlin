/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.common.descriptors

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.types.toKotlinType
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.constants.ConstantValue
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.types.*


abstract class DescriptorWrapper<T : IrDeclaration> : DeclarationDescriptor {

    lateinit var owner: T
    fun bind(declaration: T) { owner = declaration }
}

abstract class WrappedCallableDescriptor<T : IrDeclaration>: CallableDescriptor, DescriptorWrapper<T>() {
    override fun getOriginal() = this

    override fun substitute(substitutor: TypeSubstitutor): CallableDescriptor {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOverriddenDescriptors(): Collection<CallableDescriptor> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSource() = SourceElement.NO_SOURCE

    override fun getExtensionReceiverParameter(): ReceiverParameterDescriptor? = null

    override fun getDispatchReceiverParameter(): ReceiverParameterDescriptor?  = null

    override fun getTypeParameters(): List<TypeParameterDescriptor> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getReturnType(): KotlinType? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getValueParameters(): MutableList<ValueParameterDescriptor> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hasStableParameterNames(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hasSynthesizedParameterNames() = false

    override fun getVisibility(): Visibility {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <R : Any?, D : Any?> accept(visitor: DeclarationDescriptorVisitor<R, D>?, data: D): R {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun acceptVoid(visitor: DeclarationDescriptorVisitor<Void, Void>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val annotations: Annotations = Annotations.EMPTY
}

open class WrappedValueParameterDescriptor : ValueParameterDescriptor, WrappedCallableDescriptor<IrValueParameter>() {

    override val index get() = owner.index
    override val isCrossinline get() = owner.isCrossinline
    override val isNoinline get() = owner.isNoinline
    override val varargElementType get() = owner.varargElementType?.toKotlinType()
    override fun isConst() = false
    override fun isVar() = false

    override fun getContainingDeclaration() = (owner.parent as IrFunction).descriptor
    override fun getType() = owner.type.toKotlinType()
    override fun getName() = owner.name
    override fun declaresDefaultValue() = owner.defaultValue != null
    override fun getCompileTimeInitializer(): ConstantValue<*>? {
        TODO("")
    }

    override fun copy(newOwner: CallableDescriptor, newName: Name, newIndex: Int) = object : WrappedValueParameterDescriptor() {
        override fun getContainingDeclaration() = newOwner as FunctionDescriptor
        override fun getName() = newName
        override val index = newIndex
    }


    override fun getOverriddenDescriptors(): Collection<ValueParameterDescriptor> = emptyList()

    override fun getOriginal() = this

    override fun substitute(substitutor: TypeSubstitutor): ValueParameterDescriptor {
        TODO("")
    }

    override fun <R : Any?, D : Any?> accept(visitor: DeclarationDescriptorVisitor<R, D>?, data: D) =
        visitor!!.visitValueParameterDescriptor(this, data)

    override fun acceptVoid(visitor: DeclarationDescriptorVisitor<Void, Void>?) {
        visitor!!.visitValueParameterDescriptor(this, null)
    }
}

open class WrappedTypeParameterDescriptor: TypeParameterDescriptor, WrappedCallableDescriptor<IrTypeParameter>() {
    override fun getName() = owner.name

    override fun isReified(): Boolean {
        return false
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getVariance() = owner.variance

    override fun getUpperBounds() = owner.superTypes.map { it.toKotlinType() }

    override fun getTypeConstructor(): TypeConstructor {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOriginal() = this

    override fun getIndex() = owner.index

    override fun isCapturedFromOuterDeclaration() = false

    override fun getDefaultType(): SimpleType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContainingDeclaration() = (owner.parent as IrDeclaration).descriptor

    override fun <R, D> accept(visitor: DeclarationDescriptorVisitor<R, D>?, data: D) = visitor!!.visitTypeParameterDescriptor(this, data)

    override fun acceptVoid(visitor: DeclarationDescriptorVisitor<Void, Void>?) {
        visitor!!.visitTypeParameterDescriptor(this, null)
    }

}

open class WrappedVariableDescriptor: VariableDescriptor, WrappedCallableDescriptor<IrVariable>() {

    override fun getContainingDeclaration() = (owner.parent as IrFunction).descriptor
    override fun getType() = owner.type.toKotlinType()
    override fun getName() = owner.name
    override fun isConst() = owner.isConst
    override fun isVar() = owner.isVar
    override fun isLateInit() = owner.isLateinit

    override fun getCompileTimeInitializer(): ConstantValue<*>? {
        TODO("")
    }

    override fun getOverriddenDescriptors(): Collection<VariableDescriptor> {
        TODO("Not Implemented")
    }

    override fun getOriginal() = this

    override fun substitute(substitutor: TypeSubstitutor): VariableDescriptor {
        TODO("")
    }

    override fun <R, D> accept(visitor: DeclarationDescriptorVisitor<R, D>?, data: D) = visitor!!.visitVariableDescriptor(this, data)
    override fun acceptVoid(visitor: DeclarationDescriptorVisitor<Void, Void>?) {
        visitor!!.visitVariableDescriptor(this, null)
    }
}

open class WrappedSimpleFunctionDescriptor : SimpleFunctionDescriptor, WrappedCallableDescriptor<IrSimpleFunction>() {
    override fun getOverriddenDescriptors() = owner.overriddenSymbols.map { it.descriptor }
    override fun getContainingDeclaration() = (owner.parent as IrSymbolOwner).symbol.descriptor
    override fun getModality() = owner.modality
    override fun getName() = owner.name
    override fun getVisibility() = owner.visibility
    override fun getReturnType() = owner.returnType.toKotlinType()
    override fun getDispatchReceiverParameter() = owner.dispatchReceiverParameter?.descriptor as? ReceiverParameterDescriptor
    override fun getExtensionReceiverParameter() = owner.extensionReceiverParameter?.descriptor as? ReceiverParameterDescriptor
    override fun getTypeParameters() = owner.typeParameters.map { it.descriptor }
    override fun getValueParameters() = owner.valueParameters.mapNotNull { it.descriptor as? ValueParameterDescriptor }.toMutableList()
    override fun isExternal() = owner.isExternal
    override fun isSuspend() = owner.isSuspend
    override fun isTailrec() = owner.isTailrec
    override fun isInline() = owner.isInline

    override fun isExpect() = false
    override fun isActual() = false
    override fun isInfix() = false
    override fun isOperator() = false

    override fun getOriginal() = this
    override fun substitute(substitutor: TypeSubstitutor): SimpleFunctionDescriptor {
        TODO("")
    }
    override fun setOverriddenDescriptors(overriddenDescriptors: MutableCollection<out CallableMemberDescriptor>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getKind() = CallableMemberDescriptor.Kind.SYNTHESIZED

    override fun isHiddenToOvercomeSignatureClash(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun copy(
        newOwner: DeclarationDescriptor?,
        modality: Modality?,
        visibility: Visibility?,
        kind: CallableMemberDescriptor.Kind?,
        copyOverrides: Boolean
    ): SimpleFunctionDescriptor {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isHiddenForResolutionEverywhereBesideSupercalls(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInitialSignatureDescriptor(): FunctionDescriptor? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <V : Any?> getUserData(key: FunctionDescriptor.UserDataKey<V>?): V? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun newCopyBuilder(): FunctionDescriptor.CopyBuilder<out SimpleFunctionDescriptor> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <R, D> accept(visitor: DeclarationDescriptorVisitor<R, D>?, data: D) = visitor!!.visitFunctionDescriptor(this, data)

    override fun acceptVoid(visitor: DeclarationDescriptorVisitor<Void, Void>?) {
        visitor!!.visitFunctionDescriptor(this, null)
    }
}

open class WrappedClassConstructorDescriptor: ClassConstructorDescriptor, WrappedCallableDescriptor<IrConstructor>() {
    override fun getContainingDeclaration() = (owner.parent as IrClass).descriptor

    override fun getTypeParameters() = owner.typeParameters.map { it.descriptor }
    override fun getValueParameters() = owner.valueParameters.mapNotNull { it.descriptor as? ValueParameterDescriptor }.toMutableList()

    override fun getOriginal() = this

    override fun substitute(substitutor: TypeSubstitutor): ClassConstructorDescriptor {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun copy(
        newOwner: DeclarationDescriptor,
        modality: Modality,
        visibility: Visibility,
        kind: CallableMemberDescriptor.Kind,
        copyOverrides: Boolean
    ): ClassConstructorDescriptor {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getModality() = Modality.FINAL


    override fun setOverriddenDescriptors(overriddenDescriptors: MutableCollection<out CallableMemberDescriptor>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getKind(): CallableMemberDescriptor.Kind {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getConstructedClass() = (owner.parent as IrClass).descriptor

    override fun getName() = owner.name

    override fun getOverriddenDescriptors(): MutableCollection<out FunctionDescriptor> = mutableListOf()

    override fun getInitialSignatureDescriptor(): FunctionDescriptor? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isHiddenToOvercomeSignatureClash(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isOperator() = false

    override fun isInline() = owner.isInline

    override fun isHiddenForResolutionEverywhereBesideSupercalls(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getReturnType() = owner.returnType.toKotlinType()

    override fun isPrimary() = owner.isPrimary

    override fun isExpect() = false

    override fun isTailrec() = false

    override fun isActual() = false

    override fun isInfix() = false

    override fun isSuspend() = false

    override fun <V : Any?> getUserData(key: FunctionDescriptor.UserDataKey<V>?): V? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isExternal() = owner.isExternal

    override fun newCopyBuilder(): FunctionDescriptor.CopyBuilder<out FunctionDescriptor> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <R, D> accept(visitor: DeclarationDescriptorVisitor<R, D>?, data: D) = visitor!!.visitConstructorDescriptor(this, data)

    override fun acceptVoid(visitor: DeclarationDescriptorVisitor<Void, Void>?) {
        visitor!!.visitConstructorDescriptor(this, null)
    }
}

open class WrappedClassDescriptor: ClassDescriptor, DescriptorWrapper<IrClass>() {
    override fun getName() = owner.name

    override fun getMemberScope(typeArguments: MutableList<out TypeProjection>): MemberScope {
        TODO("not implemented")
    }

    override fun getMemberScope(typeSubstitution: TypeSubstitution): MemberScope {
        TODO("not implemented")
    }

    override fun getUnsubstitutedMemberScope(): MemberScope {
        TODO("not implemented")
    }

    override fun getUnsubstitutedInnerClassesScope(): MemberScope {
        TODO("not implemented")
    }

    override fun getStaticScope(): MemberScope {
        TODO("not implemented")
    }

    override fun getConstructors() = owner.declarations.filterIsInstance<IrConstructor>().map { it.descriptor }

    override fun getContainingDeclaration() = (owner.parent as IrSymbolOwner).symbol.descriptor

    override fun getDefaultType() = owner.defaultType.toKotlinType() as SimpleType

    override fun getKind() = owner.kind

    override fun getModality() = owner.modality

    override fun getSource() = SourceElement.NO_SOURCE

    override fun getCompanionObjectDescriptor() = owner.declarations.filterIsInstance<IrClass>().firstOrNull { it.isCompanion }?.descriptor

    override fun getVisibility() = owner.visibility

    override fun isCompanionObject() = owner.isCompanion

    override fun isData() = owner.isData

    override fun isInline(): Boolean {
        TODO("not implemented")
    }

    override fun getThisAsReceiverParameter() = owner.thisReceiver?.descriptor as ReceiverParameterDescriptor

    override fun getUnsubstitutedPrimaryConstructor(): ClassConstructorDescriptor? {
        TODO("not implemented")
    }

    override fun getDeclaredTypeParameters(): List<TypeParameterDescriptor> {
        TODO("not implemented")
    }

    override fun getSealedSubclasses(): Collection<ClassDescriptor> {
        TODO("not implemented")
    }

    override fun getOriginal() = this

    override fun isExpect() = false

    override fun substitute(substitutor: TypeSubstitutor): ClassifierDescriptorWithTypeParameters {
        TODO("not implemented")
    }

    override fun isActual() = false

    override fun getTypeConstructor(): TypeConstructor {
        TODO("not implemented")
    }

    override fun isInner() = owner.isInner

    override fun isExternal() = owner.isExternal

    override fun <R : Any?, D : Any?> accept(visitor: DeclarationDescriptorVisitor<R, D>?, data: D) =
        visitor!!.visitClassDescriptor(this, data)

    override fun acceptVoid(visitor: DeclarationDescriptorVisitor<Void, Void>?) {
        visitor!!.visitClassDescriptor(this, null)
    }

    override val annotations = Annotations.EMPTY

}


open class WrappedPropertyDescriptor: PropertyDescriptor, DescriptorWrapper<IrProperty>() {
    override fun getModality() = owner.modality

    override fun setOverriddenDescriptors(overriddenDescriptors: MutableCollection<out CallableMemberDescriptor>) {
        TODO("not implemented")
    }

    override fun getKind() = CallableMemberDescriptor.Kind.SYNTHESIZED

    override fun getName() = owner.name

    override fun getSource() = SourceElement.NO_SOURCE

    override fun hasSynthesizedParameterNames(): Boolean {
        TODO("not implemented")
    }

    override fun getOverriddenDescriptors(): MutableCollection<out PropertyDescriptor> {
        TODO("not implemented")
    }

    override fun copy(
        newOwner: DeclarationDescriptor?,
        modality: Modality?,
        visibility: Visibility?,
        kind: CallableMemberDescriptor.Kind?,
        copyOverrides: Boolean
    ): CallableMemberDescriptor {
        TODO("not implemented")
    }

    override fun getValueParameters(): MutableList<ValueParameterDescriptor> = mutableListOf()

    override fun getCompileTimeInitializer(): ConstantValue<*>? {
        TODO("not implemented")
    }

    override fun isSetterProjectedOut(): Boolean {
        TODO("not implemented")
    }

    override fun getAccessors(): MutableList<PropertyAccessorDescriptor> {
        val result = mutableListOf<PropertyAccessorDescriptor>()
        owner.getter?.let { result += it.descriptor as PropertyAccessorDescriptor }
        owner.setter?.let { result += it.descriptor as PropertyAccessorDescriptor }
        return result
    }

    override fun getTypeParameters() = emptyList()

    override fun getVisibility() = owner.visibility

    override val setter: PropertySetterDescriptor? get() = owner.setter?.descriptor as PropertySetterDescriptor?

    override fun getOriginal() = this

    override fun isExpect() = false

    override fun substitute(substitutor: TypeSubstitutor): PropertyDescriptor {
        TODO("not implemented")
    }

    override fun isActual() = false

    override fun getReturnType() = owner.getter?.returnType?.toKotlinType() ?: owner.backingField?.type?.toKotlinType()

    override fun hasStableParameterNames(): Boolean {
        TODO("not implemented")
    }

    override fun getType(): KotlinType = owner.getter?.returnType?.toKotlinType() ?: owner.backingField?.type?.toKotlinType()!!

    override fun isVar() = owner.isVar

    override fun getDispatchReceiverParameter(): ReceiverParameterDescriptor? {
        TODO("not implemented")
    }

    override fun isConst() = owner.isConst

    override fun getContainingDeclaration() = (owner.parent as IrSymbolOwner).symbol.descriptor

    override fun isLateInit() = owner.isLateinit

    override fun getExtensionReceiverParameter(): ReceiverParameterDescriptor? {
        TODO("not implemented")
    }

    override fun isExternal() = owner.isExternal

    override fun <R : Any?, D : Any?> accept(visitor: DeclarationDescriptorVisitor<R, D>?, data: D) =
        visitor!!.visitPropertyDescriptor(this, data)

    override fun acceptVoid(visitor: DeclarationDescriptorVisitor<Void, Void>?) {
        visitor!!.visitPropertyDescriptor(this, null)
    }

    override val getter: PropertyGetterDescriptor? get() = owner.getter?.descriptor as PropertyGetterDescriptor?

    override fun newCopyBuilder(): CallableMemberDescriptor.CopyBuilder<out PropertyDescriptor> {
        TODO("not implemented")
    }

    override val isDelegated get() = owner.isDelegated
    override val annotations get() = Annotations.EMPTY

}