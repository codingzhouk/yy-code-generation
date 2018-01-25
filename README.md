# yy-code-generation
##### 代码生成工具：controller-service(impl)-mapper-SqlProvider
##### 配置数据库连接信息，并执行 com.sqltool.common.MakeAll.java中的main()方法
###### 利用javapoet，根据数据库表结构,快速构建应用项目，工程代码结构简单，生成文件如下：

## Example

controller层

```Java
package com.website.controller;

import com.website.model.Company;
import com.website.service.CompanyService;
import java.lang.Integer;
import java.lang.Long;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangyu
 */
@RestController
@RequestMapping("/company")
public class CompanyController {
	@Autowired
	private CompanyService companyService;

	@RequestMapping("/findById/{id}")
	public Company findById(@PathVariable Long id) {
		return companyService.findById(id);
	}

	@RequestMapping("/findOne")
	public Company findOne(Company example) {
		return companyService.findOne(example);
	}

	@RequestMapping("/findPage")
	public List<Company> findPage(Company example) {
		return companyService.findPage(example);
	}

	@RequestMapping("/count")
	public Integer count(Company example) {
		return companyService.count(example);
	}

	@RequestMapping("/insert")
	public Integer insert(Company example) {
		return companyService.insert(example);
	}

	@RequestMapping("/update")
	public Integer update(Company example) {
		return companyService.update(example);
	}

	@RequestMapping("/delete/{id}")
	public Integer delete(@PathVariable Long id) {
		return companyService.delete(id);
	}
}
```
```Java
package com.website.service;

import com.website.model.Company;
import java.lang.Integer;
import java.lang.Long;
import java.util.List;

/**
 * @author yangyu
 */
public interface CompanyService {
	/**
	 * 根据主键查询对象信息
	 * 
	 * @param id
	 * @return Company
	 */
	Company findById(Long id);

	/**
	 * 根据对象查询对象信息
	 * 
	 * @param example
	 * @return Company
	 */
	Company findOne(Company example);

	/**
	 * 根据对象查询对象信息集合
	 * 
	 * @param example
	 * @return List<Company>
	 */
	List<Company> findPage(Company example);

	/**
	 * 根据对象查询对象信息数量
	 * 
	 * @param example
	 * @return Integer
	 */
	Integer count(Company example);

	/**
	 * 根据对象插入对象信息
	 * 
	 * @param example
	 * @return Integer
	 */
	Integer insert(Company example);

	/**
	 * 根据对象更新对象信息
	 * 
	 * @param example
	 * @return Integer
	 */
	Integer update(Company example);

	/**
	 * 根据主键删除对象信息
	 * 
	 * @param id
	 * @return Integer
	 */
	Integer delete(Long id);
}
```
```Java
package com.website.service.impl;

import com.website.mapper.CompanyMapper;
import com.website.model.Company;
import com.website.service.CompanyService;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yangyu
 */
@Service
public class CompanyServiceImpl implements CompanyService {
	@Autowired
	private CompanyMapper companyMapper;

	@Override
	public Company findById(Long id) {
		return companyMapper.findById(id);
	}

	@Override
	public Company findOne(Company example) {
		return companyMapper.findOne(example);
	}

	@Override
	public List<Company> findPage(Company example) {
		return companyMapper.findPage(example);
	}

	@Override
	public Integer count(Company example) {
		return companyMapper.count(example);
	}

	@Override
	public Integer insert(Company example) {
		return companyMapper.insert(example);
	}

	@Override
	public Integer update(Company example) {
		return companyMapper.update(example);
	}

	@Override
	public Integer delete(Long id) {
		return companyMapper.delete(id);
	}
}
```
```Java
package com.website.mapper;

import com.website.model.Company;
import java.lang.Integer;
import java.lang.Long;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * @author yangyu
 */
public interface CompanyMapper {
	@Select("SELECT id,company_no,company_name,company_addr,create_time FROM company WHERE id = #{id}")
	Company findById(@Param("id") Long id);

	@SelectProvider(method = "findByExample", type = CompanySqlProvider.class)
	Company findOne(Company example);

	@SelectProvider(method = "findPage", type = CompanySqlProvider.class)
	List<Company> findPage(Company example);

	@SelectProvider(method = "count", type = CompanySqlProvider.class)
	Integer count(Company example);

	@Insert("INSERT INTO company (company_no,company_name,company_addr,create_time) VALUES (#{companyNo},#{companyName},#{companyAddr},#{createTime})")
	Integer insert(Company example);

	@UpdateProvider(method = "update", type = CompanySqlProvider.class)
	Integer update(Company example);

	@Delete("DELETE FROM company WHERE id = #{id} ")
	Integer delete(@Param("id") Long id);
}
```
```Java
package com.website.mapper;

import com.website.model.Company;
import java.lang.String;

/**
 * @author yangyu
 */
public class CompanySqlProvider {
	public String findByExample(Company example) {

		StringBuffer colBuilder = new StringBuffer();
		colBuilder.append(" SELECT id,company_no,company_name,company_addr,create_time FROM company WHERE 1=1 ");
		colBuilder.append(whereBuilder(example));
		return colBuilder.toString();
	}

	public String findPage(Company example) {

		StringBuffer colBuilder = new StringBuffer();
		colBuilder.append(" SELECT id,company_no,company_name,company_addr,create_time FROM company WHERE 1=1 ");
		colBuilder.append(whereBuilder(example));
		colBuilder.append(" limit ");
		colBuilder.append((example.getSkip() - 1) * example.getLimit());
		colBuilder.append(",");
		colBuilder.append(example.getLimit());
		return colBuilder.toString();
	}

	public String count(Company example) {

		StringBuffer colBuilder = new StringBuffer();
		colBuilder.append(" SELECT COUNT(*) FROM company WHERE 1=1 ");
		colBuilder.append(whereBuilder(example));
		return colBuilder.toString();
	}

	public String update(Company example) {

		StringBuffer updateBuilder = new StringBuffer();
		updateBuilder.append("UPDATE company SET ");
		if (example.getCompanyNo() != null) {
			updateBuilder.append("company_no=#{companyNo}, ");
		}

		if (example.getCompanyName() != null) {
			updateBuilder.append("company_name=#{companyName}, ");
		}

		if (example.getCompanyAddr() != null) {
			updateBuilder.append("company_addr=#{companyAddr}, ");
		}

		if (example.getCreateTime() != null) {
			updateBuilder.append("create_time=#{createTime}, ");
		}
		updateBuilder.deleteCharAt(updateBuilder.lastIndexOf(","));
		updateBuilder.append(" WHERE id = #{id}");
		return updateBuilder.toString();
	}

	private String whereBuilder(Company example) {

		StringBuffer whereBuilder = new StringBuffer();

		if (example.getId() != null) {
			whereBuilder.append(" and id=#{id}");
		}

		if (example.getCompanyNo() != null) {
			whereBuilder.append(" and company_no=#{companyNo}");
		}

		if (example.getCompanyName() != null) {
			whereBuilder.append(" and company_name=#{companyName}");
		}

		if (example.getCompanyAddr() != null) {
			whereBuilder.append(" and company_addr=#{companyAddr}");
		}

		if (example.getCreateTime() != null) {
			whereBuilder.append(" and create_time=#{createTime}");
		}
		return whereBuilder.toString();
	}
}
```

