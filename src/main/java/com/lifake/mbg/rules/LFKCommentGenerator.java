package com.lifake.mbg.rules;

import com.alibaba.fastjson.JSON;
import com.lifake.base.utils.NamingFormatUtils;
import com.lifake.base.utils.ValidateUtils;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.kotlin.KotlinFile;
import org.mybatis.generator.api.dom.kotlin.KotlinFunction;
import org.mybatis.generator.api.dom.kotlin.KotlinProperty;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.List;
import java.util.Properties;
import java.util.Set;

public class LFKCommentGenerator implements CommentGenerator {

    private Properties properties = new Properties();

    public void addConfigurationProperties(Properties properties) {
        System.out.println("获取properties配置：" + JSON.toJSONString(properties));
        this.properties.putAll(properties);
    }

    /**
     * 为类成员写入注释
     *
     * @param field              成员信息
     * @param introspectedTable  表信息
     * @param introspectedColumn 列信息
     */
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        String colName = introspectedColumn.getActualColumnName();
        String remark = introspectedColumn.getRemarks();
        String type = introspectedColumn.getJdbcTypeName().toLowerCase();
        int length = introspectedColumn.getLength();
        int scale = introspectedColumn.getScale();
        boolean nullable = introspectedColumn.isNullable();
        String defaultValue = introspectedColumn.getDefaultValue();

        // 拼接列名和列备注
        System.out.println("正在写入类成员注释：" + field.getName());
        field.addJavaDocLine("/**");
        field.addJavaDocLine(" * " + colName + " " + (remark == null ? "" : remark));
        field.addJavaDocLine(" */");

        // 拼接注解
        System.out.println("正在写入类成员注解：" + field.getName() + " @LFKColumn");
        field.addJavaDocLine("@LFKColumn(");
        field.addJavaDocLine("        columnName = \"" + colName + "\",");
        field.addJavaDocLine("        type = \"" + type + "\",");
        field.addJavaDocLine("        length = " + length + ",");
        if (scale > 0) {
            field.addJavaDocLine("        scale = " + scale + ",");
        }
        if (!nullable) {
            field.addJavaDocLine("        nullable = false,");
        }
        if (ValidateUtils.isNotEmptyString(defaultValue)) {
            field.addJavaDocLine("        defaultValue = \"" + defaultValue + "\",");
        }
        field.addJavaDocLine("        comment = \"" + remark + "\"");
        field.addJavaDocLine(")");
    }

    /**
     * 为类成员写入注释
     *
     * @param field             成员信息
     * @param introspectedTable 表信息
     */
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {

        System.out.println("正在写入类成员注释：" + field.getName());
        field.addJavaDocLine("/**");

        // 拼接成员名
        String name = field.getName();
        if (name.equals("orderByClause")) {
            field.addJavaDocLine(" * 排序");
        } else if (name.equals("distinct")) {
            field.addJavaDocLine(" * 是否去重");
        } else if (name.equals("oredCriteria")) {
            field.addJavaDocLine(" * 子语句");
        }

        field.addJavaDocLine(" */");
    }

    /**
     * 为模态类写入注释
     *
     * @param topLevelClass     模态类
     * @param introspectedTable 表信息
     */
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        // 拼接导入类
        System.out.println("正在写入模态类导入类：" + topLevelClass.getType().getFullyQualifiedName());
        topLevelClass.addImportedType("com.lifake.base.annotation.LFKColumn");
        topLevelClass.addImportedType("com.lifake.base.annotation.LFKTable");

        String name = introspectedTable.getFullyQualifiedTable().toString();
        String remarks = introspectedTable.getRemarks();
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();

        // 拼接表名和表备注
        System.out.println("正在写入模态类注释：" + topLevelClass.getType().getFullyQualifiedName());
        topLevelClass.addJavaDocLine("/**");
        if (StringUtility.stringHasValue(remarks)) {
            remarks = remarks.replace(System.getProperty("line.separator"), " ");
            topLevelClass.addJavaDocLine(" * " + remarks + " " + name);
        }
        topLevelClass.addJavaDocLine(" */");

        // 拼接注解
        System.out.println("正在写入模态类注释：" + topLevelClass.getType().getFullyQualifiedName() + " @LFKTable");
        topLevelClass.addAnnotation("@LFKTable(");
        topLevelClass.addAnnotation("        tableName = \"" + name + "\",");
        if (ValidateUtils.isNotEmptyList(primaryKeyColumns)) {
            StringBuilder primaryKey = new StringBuilder("        primaryKey = {");
            for (int i = 0; i < primaryKeyColumns.size(); i++) {
                if (i>0){
                    primaryKey.append(',');
                }
                primaryKey.append('\"').append(primaryKeyColumns.get(i).getActualColumnName()).append('\"');
            }
            primaryKey.append("},");
            topLevelClass.addAnnotation(primaryKey.toString());
        }
        // TODO 唯一键的配置
        topLevelClass.addAnnotation("        comment = \"" + remarks + "\"");
        topLevelClass.addAnnotation(")");

    }

    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
        System.out.println("addClassComment：" + innerClass.getType().getFullyQualifiedName());
    }

    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean b) {
        System.out.println("addClassComment2：" + innerClass.getType().getFullyQualifiedName());
    }

    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
    }

    /**
     * 为Getter写入注释
     *
     * @param method             方法信息
     * @param introspectedTable  表信息
     * @param introspectedColumn 列信息
     */
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        System.out.println("正在写入Getter注释：" + method.getName());
        method.addJavaDocLine("/**");

        // 拼接列名和列备注
        String colName = introspectedColumn.getActualColumnName();
        String remark = introspectedColumn.getRemarks();
        method.addJavaDocLine(" * " + colName + " " + (remark == null ? "" : remark) + "的Getter");
        method.addJavaDocLine(" *");
        method.addJavaDocLine(" * @return " + colName + "的值");

        method.addJavaDocLine(" */");
    }

    /**
     * 为Setter写入注释
     *
     * @param method             方法信息
     * @param introspectedTable  表信息
     * @param introspectedColumn 列信息
     */
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {

        System.out.println("正在写入Setter注释：" + method.getName());
        method.addJavaDocLine("/**");

        // 拼接列名和列备注
        String colName = introspectedColumn.getActualColumnName();
        String remark = introspectedColumn.getRemarks();
        method.addJavaDocLine(" * " + colName + " " + (remark == null ? "" : remark) + "的Setter");
        method.addJavaDocLine(" *");
        method.addJavaDocLine(" * @param " + method.getParameters().get(0).getName() + " " + colName + "的值");

        method.addJavaDocLine(" */");
    }

    /**
     * 为方法写入注释
     *
     * @param method            方法信息
     * @param introspectedTable 表信息
     */
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        String name = method.getName();
        if (name == null) {
            return;
        }

        System.out.println("正在写入方法注释：" + name);
        method.addJavaDocLine("/**");

        // 拼接方法用途
        if (method.isConstructor()) {
            method.addJavaDocLine(" * 构造方法");
        } else if (name.startsWith("set")) {
            String bean = name.substring(3);
            bean = NamingFormatUtils.transform(bean, NamingFormatUtils.CAMEL_CASE_WITH_UPPER_FIRST, NamingFormatUtils.CAMEL_CASE_WITH_LOWER_FIRST);
            method.addJavaDocLine(" * " + bean + "的Setter");
            method.addJavaDocLine(" *");
            method.addJavaDocLine(" * @param " + bean + " " + bean + "的值");
        } else if (name.startsWith("get") || name.startsWith("is")) {
            String bean = name.startsWith("is") ? name.substring(2) : name.substring(3);
            bean = NamingFormatUtils.transform(bean, NamingFormatUtils.CAMEL_CASE_WITH_UPPER_FIRST, NamingFormatUtils.CAMEL_CASE_WITH_LOWER_FIRST);
            method.addJavaDocLine(" * " + bean + "的Getter");
            method.addJavaDocLine(" *");
            method.addJavaDocLine(" * @return " + bean + "的值");
        } else if (name.equals("or")) {
            method.addJavaDocLine(" * 或者");
        } else if (name.startsWith("createCriteria")) {
            method.addJavaDocLine(" * 开启一条语句");
        } else if (name.equals("clear")) {
            method.addJavaDocLine(" * 清空语句");
        } else if (name.equals("selectByExample")) {
            method.addJavaDocLine(" * 查询方法：根据Example");
        } else if (name.equals("selectByPrimaryKey")) {
            method.addJavaDocLine(" * 查询方法：根据主键");
        } else if (name.equals("countByExample")) {
            method.addJavaDocLine(" * 查询方法：根据Example查询数量");
        } else if (name.equals("deleteByPrimaryKey")) {
            method.addJavaDocLine(" * 删除方法：根据主键");
        } else if (name.equals("deleteByExample")) {
            method.addJavaDocLine(" * 删除方法：根据Example");
        } else if (name.equals("insert")) {
            method.addJavaDocLine(" * 新增方法：全部字段");
        } else if (name.equals("insertSelective")) {
            method.addJavaDocLine(" * 新增方法：非NULL字段");
        } else if (name.equals("updateByExampleSelective")) {
            method.addJavaDocLine(" * 修改方法：根据Example修改非NULL字段");
        } else if (name.equals("updateByExample")) {
            method.addJavaDocLine(" * 修改方法：根据Example修改全部字段");
        } else if (name.equals("updateByPrimaryKeySelective")) {
            method.addJavaDocLine(" * 修改方法：根据主键修改非NULL字段");
        } else if (name.equals("updateByPrimaryKey")) {
            method.addJavaDocLine(" * 修改方法：根据主键修改全部字段");
        }

        method.addJavaDocLine(" */");
    }

    /**
     * 为JAVA类写入注释
     *
     * @param compilationUnit JAVA类信息
     */
    public void addJavaFileComment(CompilationUnit compilationUnit) {

        System.out.println("正在写入JAVA文件注释：" + compilationUnit.getType().getFullyQualifiedName());
        compilationUnit.addFileCommentLine("/**");

        // 拼接类名和用途
        String name = compilationUnit.getType().toString();
        String modelName = compilationUnit.getType().getShortName();
        if (name.endsWith("Mapper")) {
            modelName = modelName.substring(0, modelName.length() - 6);
            compilationUnit.addFileCommentLine(" * " + modelName + "数据接口类");
        } else if (name.endsWith("Example")) {
            modelName = modelName.substring(0, modelName.length() - 7);
            compilationUnit.addFileCommentLine(" * " + modelName + "条件拼接类");
        } else {
            compilationUnit.addFileCommentLine(" * " + modelName + "模态类");
        }

        compilationUnit.addFileCommentLine(" */");
    }

    /**
     * 为xmlMapper写入注释
     *
     * @param xmlElement xml节点
     */
    public void addComment(XmlElement xmlElement) {

        xmlElement.addElement(new TextElement("<!--"));

        // 获取节点的名字与id属性
        String name = xmlElement.getName();
        String id = null;
        for (Attribute attr : xmlElement.getAttributes()) {
            if ("id".equals(attr.getName())) {
                id = attr.getValue();
                break;
            }
        }

        System.out.println("正在写入xml注释：<" + name + " id=\"" + id + "\">");
        // 输出节点注释
        if ("resultMap".equals(name)) {
            xmlElement.addElement(new TextElement("  字段映射"));
        } else if ("sql".equals(name)) {
            if ("Example_Where_Clause".equals(id)) {
                xmlElement.addElement(new TextElement("  WHERE语句"));
            } else if ("Update_By_Example_Where_Clause".equals(id)) {
                xmlElement.addElement(new TextElement("  WHERE语句"));
            } else if ("Base_Column_List".equals(id)) {
                xmlElement.addElement(new TextElement("  字段列表语句"));
            }
        } else if ("select".equals(name)) {
            if ("selectByExample".equals(id)) {
                xmlElement.addElement(new TextElement("  查询方法：根据Example"));
            } else if ("selectByPrimaryKey".equals(id)) {
                xmlElement.addElement(new TextElement("  查询方法：根据主键"));
            } else if ("countByExample".equals(id)) {
                xmlElement.addElement(new TextElement("  查询方法：根据Example查询数量"));
            }
        } else if ("delete".equals(name)) {
            if ("deleteByPrimaryKey".equals(id)) {
                xmlElement.addElement(new TextElement("  删除方法：根据主键"));
            } else if ("deleteByExample".equals(id)) {
                xmlElement.addElement(new TextElement("  删除方法：根据Example"));
            }
        } else if ("insert".equals(name)) {
            if ("insert".equals(id)) {
                xmlElement.addElement(new TextElement("  新增方法：全部字段"));
            } else if ("insertSelective".equals(id)) {
                xmlElement.addElement(new TextElement("  新增方法：非NULL字段"));
            }
        } else if ("update".equals(name)) {
            if ("updateByExampleSelective".equals(id)) {
                xmlElement.addElement(new TextElement("  修改方法：根据Example修改非NULL字段"));
            } else if ("updateByExample".equals(id)) {
                xmlElement.addElement(new TextElement("  修改方法：根据Example修改全部字段"));
            } else if ("updateByPrimaryKeySelective".equals(id)) {
                xmlElement.addElement(new TextElement("  修改方法：根据主键修改非NULL字段"));
            } else if ("updateByPrimaryKey".equals(id)) {
                xmlElement.addElement(new TextElement("  修改方法：根据主键修改全部字段"));
            }
        }

        xmlElement.addElement(new TextElement("-->"));
    }

    public void addRootComment(XmlElement xmlElement) {

        // 获取表名
        String domainName = null;
        for (Attribute attr : xmlElement.getAttributes()) {
            if (attr.getName().equals("namespace")) {
                String mapper = attr.getValue();
                String[] mapperLocation = mapper.split("\\.");
                if (mapperLocation.length > 0) {
                    mapper = mapperLocation[mapperLocation.length - 1];
                    domainName = mapper.substring(0, mapper.length() - 6);
                }
                break;
            }
        }

        System.out.println("正在写入xml根节点注释：" + domainName);

        if (StringUtility.stringHasValue(domainName)) {
            xmlElement.addElement(new TextElement("<!--"));

            // 拼接表名
            xmlElement.addElement(new TextElement("  " + domainName + "的XMLMAPPER"));

            xmlElement.addElement(new TextElement("-->"));
        }

    }

    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {
        System.out.println("addGeneralMethodAnnotation");
    }

    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> set) {
        System.out.println("addGeneralMethodAnnotation");
    }

    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {
        System.out.println("addFieldAnnotation");
    }

    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> set) {
        System.out.println("addFieldAnnotation");
    }

    public void addClassAnnotation(InnerClass innerClass, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {
        System.out.println("addClassAnnotation");
    }

    public void addFileComment(KotlinFile kotlinFile) {
        System.out.println("addFileComment");
    }

    public void addGeneralFunctionComment(KotlinFunction kf, IntrospectedTable introspectedTable, Set<String> imports) {
        System.out.println("addGeneralFunctionComment");
    }

    public void addGeneralPropertyComment(KotlinProperty property, IntrospectedTable introspectedTable, Set<String> imports) {
        System.out.println("addGeneralPropertyComment");
    }

}