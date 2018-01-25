package com.sqltool.common;

import java.io.File;
import java.io.IOException;

import javax.lang.model.element.Modifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public class MakeServiceUtils {

	private String packageName;
	private String className;
	private String filePath;

	public MakeServiceUtils(String packageName, String className, String filePath) {
		super();
		this.packageName = packageName;
		this.className = className;
		this.filePath = filePath;
	}
	public void makeServiceClass() throws IOException {
		
		ClassName model = ClassName.get(packageName + ".model", className);
		
		/*----------findById--------*/
		MethodSpec findById = MethodSpec.methodBuilder("findById")
				.addJavadoc("根据主键查询对象信息\n@param id \n@return " + className + "\n")
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addParameter(Long.class, "id")
				.returns(model)
				.build();
		
		/*----------findOne--------*/
		MethodSpec findOne = MethodSpec.methodBuilder("findOne")
				.addJavadoc("根据对象查询对象信息\n@param example \n@return " + className + "\n")
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addParameter(model, "example")
				.returns(model)
				.build();
		
		/*----------findPage--------*/
		ClassName list = ClassName.get("java.util", "List");
		TypeName returnList = ParameterizedTypeName.get(list, model);
		MethodSpec findPage = MethodSpec.methodBuilder("findPage")
				.addJavadoc("根据对象查询对象信息集合\n@param example \n@return List<" + className + ">\n")
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addParameter(model,"example")
				.returns(returnList)
				.build();
		
		/*----------count--------*/
		MethodSpec count = MethodSpec.methodBuilder("count")
				.addJavadoc("根据对象查询对象信息数量\n@param example \n@return Integer\n")
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addParameter(model,"example")
				.returns(Integer.class)
				.build();
		
		/*----------insert--------*/
		MethodSpec insert = MethodSpec.methodBuilder("insert")
				.addJavadoc("根据对象插入对象信息\n@param example \n@return Integer\n")
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addParameter(model,"example")
				.returns(Integer.class)
				.build();
		
		/*----------update--------*/
		MethodSpec update = MethodSpec.methodBuilder("update")
				.addJavadoc("根据对象更新对象信息\n@param example \n@return Integer\n")
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addParameter(model,"example")
				.returns(Integer.class)
				.build();
		
		/*----------delete--------*/
		MethodSpec delete = MethodSpec.methodBuilder("delete")
				.addJavadoc("根据主键删除对象信息\n@param id \n@return Integer\n")
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addParameter(Long.class,"id")
				.returns(Integer.class)
				.build();

		TypeSpec typeSpec = TypeSpec.interfaceBuilder(className + "Service")
				.addJavadoc("@author yangyu\n")
				.addModifiers(Modifier.PUBLIC)
				.addMethod(findById)
				.addMethod(findOne)
				.addMethod(findPage)
				.addMethod(count)
				.addMethod(insert)
				.addMethod(update)
				.addMethod(delete)
				.build();

		JavaFile javaFile = JavaFile.builder(packageName+".service", typeSpec).build();
		javaFile.writeTo(new File(filePath));
		
		//生成service实现类
		makeImplClass();
	}
	
	private void makeImplClass() throws IOException {
		
		ClassName mapper = ClassName.get(packageName + ".mapper", className + "Mapper");
		
		FieldSpec mapperField = FieldSpec.builder(mapper, CommonUtils.lowerName(className) + "Mapper")
			    .addModifiers(Modifier.PRIVATE)
			    .addAnnotation(Autowired.class)
			    .build();
		
		ClassName model = ClassName.get(packageName+".model", className);
		ClassName superinterface = ClassName.bestGuess(packageName+".service." + className + "Service");
		
		/*----------findById--------*/
		MethodSpec findById = MethodSpec.methodBuilder("findById")
				.addModifiers(Modifier.PUBLIC)
				.addAnnotation(Override.class)
				.addParameter(Long.class, "id")
				.returns(model)
				.addStatement("return " + CommonUtils.lowerName(className) + "Mapper.findById(id)")
				.build();
		
		/*----------findOne--------*/
		MethodSpec findOne = MethodSpec.methodBuilder("findOne")
				.addModifiers(Modifier.PUBLIC)
				.addAnnotation(Override.class)
				.addParameter(model, "example")
				.returns(model)
				.addStatement("return " + CommonUtils.lowerName(className) + "Mapper.findOne(example)")
				.build();
		
		/*----------findPage--------*/
		ClassName list = ClassName.get("java.util", "List");
		TypeName returnList = ParameterizedTypeName.get(list, model);
		MethodSpec findPage = MethodSpec.methodBuilder("findPage")
				.addModifiers(Modifier.PUBLIC)
				.addAnnotation(Override.class)
				.addParameter(model,"example")
				.returns(returnList)
				.addStatement("return " + CommonUtils.lowerName(className) + "Mapper.findPage(example)")
				.build();
		
		/*----------count--------*/
		MethodSpec count = MethodSpec.methodBuilder("count")
				.addModifiers(Modifier.PUBLIC)
				.addAnnotation(Override.class)
				.addParameter(model,"example")
				.returns(Integer.class)
				.addStatement("return " + CommonUtils.lowerName(className) + "Mapper.count(example)")
				.build();
		
		/*----------insert--------*/
		MethodSpec insert = MethodSpec.methodBuilder("insert")
				.addModifiers(Modifier.PUBLIC)
				.addAnnotation(Override.class)
				.addParameter(model,"example")
				.returns(Integer.class)
				.addStatement("return " + CommonUtils.lowerName(className) + "Mapper.insert(example)")
				.build();
		
		/*----------insert--------*/
		MethodSpec update = MethodSpec.methodBuilder("update")
				.addModifiers(Modifier.PUBLIC)
				.addAnnotation(Override.class)
				.addParameter(model,"example")
				.returns(Integer.class)
				.addStatement("return " + CommonUtils.lowerName(className) + "Mapper.update(example)")
				.build();
		
		/*----------insert--------*/
		MethodSpec delete = MethodSpec.methodBuilder("delete")
				.addModifiers(Modifier.PUBLIC)
				.addAnnotation(Override.class)
				.addParameter(Long.class,"id")
				.returns(Integer.class)
				.addStatement("return " + CommonUtils.lowerName(className) + "Mapper.delete(id)")
				.build();
		
		TypeSpec typeSpec = TypeSpec.classBuilder(className + "ServiceImpl")
				.addJavadoc("@author yangyu\n")
				.addAnnotation(Service.class)
				.addModifiers(Modifier.PUBLIC)
				.addSuperinterface(superinterface)
				.addField(mapperField)
				.addMethod(findById)
				.addMethod(findOne)
				.addMethod(findPage)
				.addMethod(count)
				.addMethod(insert)
				.addMethod(update)
				.addMethod(delete)
				.build();

		JavaFile javaFile = JavaFile.builder(packageName+".service.impl", typeSpec).build();
		javaFile.writeTo(new File(filePath));
	}

}
