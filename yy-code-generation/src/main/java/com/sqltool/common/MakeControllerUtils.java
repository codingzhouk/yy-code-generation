package com.sqltool.common;

import java.io.File;
import java.io.IOException;

import javax.lang.model.element.Modifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public class MakeControllerUtils {

	private String packageName;
	private String className;
	private String filePath;

	public MakeControllerUtils(String packageName, String className, String filePath) {
		super();
		this.packageName = packageName;
		this.className = className;
		this.filePath = filePath;
	}
	
	public void makeControllerClass() throws IOException {
		
		ClassName service = ClassName.get(packageName + ".service", className + "Service");
		
		FieldSpec serviceField = FieldSpec.builder(service, CommonUtils.lowerName(className) + "Service")
			    .addModifiers(Modifier.PRIVATE)
			    .addAnnotation(Autowired.class)
			    .build();
		
		ClassName model = ClassName.get(packageName+".model", className);
		
		/*----------findById--------*/
		MethodSpec findById = MethodSpec.methodBuilder("findById")
				.addAnnotation(AnnotationSpec.builder(RequestMapping.class)
						.addMember("value", "$S", "/findById/{id}").build())
				.addModifiers(Modifier.PUBLIC)
				.addParameter(ParameterSpec.builder(Long.class, "id").addAnnotation(
						AnnotationSpec.builder(PathVariable.class)
						.build())
						.build())
				.returns(model)
				.addStatement("return " + CommonUtils.lowerName(className) + "Service.findById(id)")
				.build();
		
		/*----------findOne--------*/
		MethodSpec findOne = MethodSpec.methodBuilder("findOne")
				.addAnnotation(AnnotationSpec.builder(RequestMapping.class)
						.addMember("value", "$S", "/findOne").build())
				.addModifiers(Modifier.PUBLIC)
				.addParameter(model, "example")
				.returns(model)
				.addStatement("return " + CommonUtils.lowerName(className) + "Service.findOne(example)")
				.build();
		
		/*----------findPage--------*/
		ClassName list = ClassName.get("java.util", "List");
		TypeName returnList = ParameterizedTypeName.get(list, model);
		MethodSpec findPage = MethodSpec.methodBuilder("findPage")
				.addAnnotation(AnnotationSpec.builder(RequestMapping.class)
						.addMember("value", "$S", "/findPage").build())
				.addModifiers(Modifier.PUBLIC)
				.addParameter(model,"example")
				.returns(returnList)
				.addStatement("return " + CommonUtils.lowerName(className) + "Service.findPage(example)")
				.build();
		
		/*----------count--------*/
		MethodSpec count = MethodSpec.methodBuilder("count")
				.addAnnotation(AnnotationSpec.builder(RequestMapping.class)
						.addMember("value", "$S", "/count").build())
				.addModifiers(Modifier.PUBLIC)
				.addParameter(model,"example")
				.returns(Integer.class)
				.addStatement("return " + CommonUtils.lowerName(className) + "Service.count(example)")
				.build();
		
		/*----------insert--------*/
		MethodSpec insert = MethodSpec.methodBuilder("insert")
				.addAnnotation(AnnotationSpec.builder(RequestMapping.class)
						.addMember("value", "$S", "/insert").build())
				.addModifiers(Modifier.PUBLIC)
				.addParameter(model,"example")
				.returns(Integer.class)
				.addStatement("return " + CommonUtils.lowerName(className) + "Service.insert(example)")
				.build();
		
		/*----------insert--------*/
		MethodSpec update = MethodSpec.methodBuilder("update")
				.addAnnotation(AnnotationSpec.builder(RequestMapping.class)
						.addMember("value", "$S", "/update").build())
				.addModifiers(Modifier.PUBLIC)
				.addParameter(model,"example")
				.returns(Integer.class)
				.addStatement("return " + CommonUtils.lowerName(className) + "Service.update(example)")
				.build();
		
		/*----------insert--------*/
		MethodSpec delete = MethodSpec.methodBuilder("delete")
				.addAnnotation(AnnotationSpec.builder(RequestMapping.class)
						.addMember("value", "$S", "/delete/{id}").build())
				.addModifiers(Modifier.PUBLIC)
				.addParameter(ParameterSpec.builder(Long.class, "id").addAnnotation(
						AnnotationSpec.builder(PathVariable.class)
						.build())
						.build())
				.returns(Integer.class)
				.addStatement("return " + CommonUtils.lowerName(className) + "Service.delete(id)")
				.build();
		
		TypeSpec typeSpec = TypeSpec.classBuilder(className + "Controller")
				.addJavadoc("@author yangyu\n")
				.addAnnotation(RestController.class)
				.addAnnotation(AnnotationSpec.builder(RequestMapping.class)
						.addMember("value", "$S", "/" + CommonUtils.lowerName(className)).build())
				.addModifiers(Modifier.PUBLIC)
				.addField(serviceField)
				.addMethod(findById)
				.addMethod(findOne)
				.addMethod(findPage)
				.addMethod(count)
				.addMethod(insert)
				.addMethod(update)
				.addMethod(delete)
				.build();

		JavaFile javaFile = JavaFile.builder(packageName+".controller", typeSpec).build();
		javaFile.writeTo(new File(filePath));
	}

}
