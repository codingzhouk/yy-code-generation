package com.sqltool.common;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

/**
 * @author yangyu
 */

public class MakeModelUtils {

	private String packageName;
	private String className;
	private String filePath;
	private ResultSetMetaData metaData;
	private List<KeyVal> prosList = new LinkedList<KeyVal>();
	private List<MethodSpec> methodList = new LinkedList<MethodSpec>();

	public MakeModelUtils(String packageName, String className, String filePath, ResultSetMetaData metaData) {
		super();
		this.packageName = packageName;
		this.className = className;
		this.filePath = filePath;
		this.metaData = metaData;
	}

	/**
	 * @param metaData
	 * @throws Exception
	 */
	public void makeModelClass() throws Exception{
		
		for (int i = 1; i < metaData.getColumnCount()+1; i++) {
			String columnName = metaData.getColumnName(i);
			String columnTypeName = metaData.getColumnTypeName(i);
			makeSetGet(columnName,CommonUtils.changeType(columnTypeName));
		}
		makeSetGet("skip",CommonUtils.changeType("INT"));
		makeSetGet("limit",CommonUtils.changeType("INT"));
		
		makeClass();
	}

	public void makeSetGet(String pros, Object type) throws IOException {

		pros = CommonUtils.camelCaseName(pros);

		MethodSpec getMethod = MethodSpec.methodBuilder(CommonUtils.makeGetMechod(pros))
				.addModifiers(Modifier.PUBLIC)
				.addStatement("return " + pros)
				.returns(type.getClass()).build();

		MethodSpec setMethod = MethodSpec.methodBuilder(CommonUtils.makeSetMechod(pros))
				.addModifiers(Modifier.PUBLIC)
				.addParameter(type.getClass(), pros)
				.addStatement("this." + pros + " = " + pros)
				.build();
		methodList.add(getMethod);
		methodList.add(setMethod);

		prosList.add(new KeyVal(pros, type.getClass()));
	}

	public void makeClass() throws IOException {

		Builder classBuilder = TypeSpec.classBuilder(className);

		for (KeyVal kv : prosList) {
			if("skip".equals(kv.getKey())) {
				FieldSpec skip = FieldSpec.builder(kv.getVal(), kv.getKey())
					    .addModifiers(Modifier.PRIVATE)
					    .initializer("$L", 1)
					    .build();
				classBuilder.addField(skip);
			} else if("limit".equals(kv.getKey())) {
				FieldSpec limit = FieldSpec.builder(kv.getVal(), kv.getKey())
					    .addModifiers(Modifier.PRIVATE)
					    .initializer("$L", 30)
					    .build();
				classBuilder.addField(limit);
			} else {
				classBuilder.addField(kv.getVal(), kv.getKey(), Modifier.PRIVATE);
			}
		}
		
		classBuilder.addModifiers(Modifier.PUBLIC).addMethods(methodList);
		classBuilder.addMethod(toStringMechtod(className));
		
		JavaFile javaFile = JavaFile.builder(packageName+".model", classBuilder.build()).build();
		javaFile.writeTo(new File(filePath));
	}
	
	private MethodSpec toStringMechtod(String className) {
		StringBuilder sb = new StringBuilder();
		sb.append("\"");
		sb.append(className);
		sb.append(" [");
		for (KeyVal kv : prosList) {
			String key = kv.getKey();
			sb.append(key).append(" = \" + ").append(key).append(" +\",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("]\"");
		return MethodSpec.methodBuilder("toString")
				.addAnnotation(Override.class)
				.returns(String.class)
				.addModifiers(Modifier.PUBLIC)
				.addStatement("return " + sb.toString())
				.build();
	}

	class KeyVal {
		private String key;
		private Class<?> val;

		public KeyVal() {
			super();
		}

		public KeyVal(String key, Class<?> val) {
			super();
			this.key = key;
			this.val = val;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public Class<?> getVal() {
			return val;
		}

		public void setVal(Class<?> val) {
			this.val = val;
		}

	}

}
