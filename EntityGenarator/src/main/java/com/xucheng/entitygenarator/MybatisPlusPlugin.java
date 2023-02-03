package com.xucheng.entitygenarator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;


//自动生成mysql的PO类。
public class MybatisPlusPlugin {
        public static void main(String[] args) {
            //1. 全局配置 3.5.x以上已弃用GlobalConfig...
            GlobalConfig config = new GlobalConfig();
            // 是否支持AR模式
            config.setActiveRecord(true)
                    // 作者
                    .setAuthor("joker")
                    // 生成路径，最好使用绝对路径
                    .setOutputDir("E:\\joker\\ideaItem\\xuecheng\\EntityGenarator\\src\\main\\java\\com\\xuecheng\\")
                    // 文件覆盖
                    .setFileOverride(true)
                    // 主键策略
                    .setIdType(IdType.AUTO)

                    .setDateType(DateType.ONLY_DATE)
                    // 设置生成的service接口的名字的首字母是否为I，默认Service是以I开头的
//                    .setServiceName("%sService")
                    //实体类结尾名称
//                    .setEntityName("")

                    //生成基本的resultMap
                    .setBaseResultMap(false)

                    //不使用AR模式
                    .setActiveRecord(false)

                    //生成基本的SQL片段
                    .setBaseColumnList(false)
                    ;

            //2. 数据源配置
            DataSourceConfig dsConfig = new DataSourceConfig();
            // 设置数据库类型
            dsConfig.setDbType(DbType.MYSQL)
                    .setDriverName("com.mysql.cj.jdbc.Driver")
                    .setUrl("jdbc:mysql://localhost:3306/xuecheng_pay?serverTimezone=UTC")
                    .setUsername("root")
                    .setPassword("dx1234love");

            //3. 策略配置globalConfiguration中
            StrategyConfig stConfig = new StrategyConfig();

            //全局大写命名
            stConfig.setCapitalMode(true)
                    // 数据库表映射到实体的命名策略
                    .setNaming(NamingStrategy.underline_to_camel)
                    .setControllerMappingHyphenStyle(false)
                    //使用lombok
                    .setEntityLombokModel(true)
                    //使用restcontroller注解
                    .setRestControllerStyle(false)

                    // 生成的表, 支持多表一起生成，以数组形式填写
                    .setInclude("xc_orders","xc_orders_goods","xc_pay_record","mq_message","mq_message_history");
//                .setInclude(scanner("表名，多个英文逗号分割").split(","));

            //4. 包名策略配置
            PackageConfig pkConfig = new PackageConfig();
            pkConfig.setParent("")
                    .setMapper("mapper")
                    .setEntity("domain");

            //5. 整合配置
            AutoGenerator ag = new AutoGenerator();
            ag.setGlobalConfig(config)
                    .setDataSource(dsConfig)
                    .setStrategy(stConfig)
                    .setPackageInfo(pkConfig);

            //6. 执行操作
            ag.execute();
            System.out.println("======= 相关代码生成完毕  ========");
        }

 }

