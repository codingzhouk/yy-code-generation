package com.sqltool.common;

import java.io.File;
import java.sql.ResultSetMetaData;

import javax.lang.model.element.Modifier;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public class MakeMapperUtils {

	private String packageName;
	private String className;
	private String filePath;
	private ResultSetMetaData metaData;

	public MakeMapperUtils(String packageName, String className, String filePath, ResultSetMetaData metaData) {
		super();
		this.packageName = packageName;
		this.className = className;
		this.filePath = filePath;
		this.metaData = metaData;
	}

	public void makeMapperClass() throws Exception {
		
		//����provider
		makeSQLProviderClass();
		
		ClassName providerClass = ClassName.get(packageName+".mapper", className + "SqlProvider");
		ClassName model = ClassName.get(packageName+".model", className);
		
		/*----------findById--------*/
		MethodSpec findById = MethodSpec.methodBuilder("findById")
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addAnnotation(AnnotationSpec.builder(Select.class)
						.addMember("value", "$S", MetaDataSQL.selectById(metaData))
				        .build())
				.addParameter(ParameterSpec.builder(Long.class, "id").addAnnotation(
								AnnotationSpec.builder(Param.class)
								.addMember("value", "\"id\"")
								.build())
								.build())
				.returns(model)
				.build();
		
		/*----------findOne--------*/
		MethodSpec findOne = MethodSpec.methodBuilder("findOne")
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addAnnotation(AnnotationSpec.builder(SelectProvider.class)
						.addMember("method", "$S", "findByExample")
						.addMember("type", "$T.class" , providerClass)
				        .build())
				.addParameter(model,"example")
				.returns(model)
				.build();
		
		/*----------findPage--------*/
		ClassName list = ClassName.get("java.util", "List");
		TypeName returnList = ParameterizedTypeName.get(list, model);
		MethodSpec findPage = MethodSpec.methodBuilder("findPage")
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addAnnotation(AnnotationSpec.builder(SelectProvider.class)
						.addMember("method", "$S", "findPage")
						.addMember("type", "$T.class" , providerClass)
				        .build())
				.addParameter(model,"example")
				.returns(returnList)
				.build();
		
		/*----------count--------*/
		MethodSpec count = MethodSpec.methodBuilder("count")
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addAnnotation(AnnotationSpec.builder(SelectProvider.class)
						.addMember("method", "$S", "count")
						.addMember("type", "$T.class" , providerClass)
						.build())
				.addParameter(model,"example")
				.returns(Integer.class)
				.build();
		
		/*----------insert--------*/
		MethodSpec insert = MethodSpec.methodBuilder("insert")
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addAnnotation(AnnotationSpec.builder(Insert.class)
						.addMember("value", "$S", MetaDataSQL.insertSQL(metaData))
				        .build())
				.addParameter(model,"example")
				.returns(Integer.class)
				.build();
		
		/*----------update--------*/
		MethodSpec update = MethodSpec.methodBuilder("update")
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addAnnotation(AnnotationSpec.builder(UpdateProvider.class)
						.addMember("method", "$S", "update")
						.addMember("type", "$T.class" , providerClass)
				        .build())
				.addParameter(model,"example")
				.returns(Integer.class)
				.build();
		
		/*----------delete--------*/
		MethodSpec delete = MethodSpec.methodBuilder("delete")
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addAnnotation(AnnotationSpec.builder(Delete.class)
						.addMember("value", "$S", MetaDataSQL.deleteById(metaData))
				        .build())
				.addParameter(ParameterSpec.builder(Long.class, "id").addAnnotation(
								AnnotationSpec.builder(Param.class)
								.addMember("value", "\"id\"")
								.build())
								.build())
				.returns(Integer.class)
				.build();

		TypeSpec typeSpec = TypeSpec.interfaceBuilder(className + "Mapper")
				.addModifiers(Modifier.PUBLIC)
				.addJavadoc("@author yangyu\n")
				.addMethod(findById)
				.addMethod(findOne)
				.addMethod(findPage)
				.addMethod(count)
				.addMethod(insert)
				.addMethod(update)
				.addMethod(delete)
				.build();

		JavaFile javaFile = JavaFile.builder(packageName+".mapper", typeSpec).build();
		javaFile.writeTo(new File(filePath));
		
	}
	
	private void makeSQLProviderClass() throws Exception {
		
		ClassName model = ClassName.get(packageName+".model", className);
		
		MethodSpec whereBuilder = MethodSpec.methodBuilder("whereBuilder")
				.addModifiers(Modifier.PRIVATE)
				.addParameter(model,"example")
				.returns(String.class)
				.addStatement(MetaDataSQL.whereBuilder(metaData))
				.build();
		
		MethodSpec findByExample = MethodSpec.methodBuilder("findByExample")
				.addModifiers(Modifier.PUBLIC)
				.addParameter(model,"example")
				.returns(String.class)
				.addStatement(MetaDataSQL.colBuilder(metaData))
				.build();
		
		MethodSpec findPage = MethodSpec.methodBuilder("findPage")
				.addModifiers(Modifier.PUBLIC)
				.addParameter(model,"example")
				.returns(String.class)
				.addStatement(MetaDataSQL.colBuilderPage(metaData))
				.build();
		
		MethodSpec count = MethodSpec.methodBuilder("count")
				.addModifiers(Modifier.PUBLIC)
				.addParameter(model,"example")
				.returns(String.class)
				.addStatement(MetaDataSQL.colBuilderPageCount(metaData))
				.build();
		
		MethodSpec update = MethodSpec.methodBuilder("update")
				.addModifiers(Modifier.PUBLIC)
				.addParameter(model,"example")
				.returns(String.class)
				.addStatement(MetaDataSQL.updateBuilder(metaData))
				.build();

		TypeSpec typeSpec = TypeSpec.classBuilder(className + "SqlProvider")
				.addJavadoc("@author yangyu\n")
				.addModifiers(Modifier.PUBLIC)
				.addMethod(findByExample)
				.addMethod(findPage)
				.addMethod(count)
				.addMethod(update)
				.addMethod(whereBuilder)
				.build();

		JavaFile javaFile = JavaFile.builder(packageName+".mapper", typeSpec).build();
		javaFile.writeTo(new File(filePath));
	}

}
