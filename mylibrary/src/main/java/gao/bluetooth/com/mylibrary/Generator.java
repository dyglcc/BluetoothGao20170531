package gao.bluetooth.com.mylibrary;


import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;


/**
 * Created by dongyuangui on 2017/5/31.
 */

public class Generator {

    public static void main(String[] args) throws Exception {

        int version = 20;

        String defaultPackage = "com.xiaobailong.bean";

//创建模式对象，指定版本号和自动生成的bean对象的包名

        Schema schema = new Schema(version, defaultPackage);

//指定自动生成的dao对象的包名,不指定则都DAO类生成在"test.greenDAO.bean"包中schema.setDefaultJavaPackageDao("test.greenDAO.dao");

//添加实体

        addEntity(schema, "Years");
        addEntity(schema, "Classes");
        addEntity(schema, "Devices");
        addEntity(schema, "Time_");
        addStudentEntity(schema, "Student");
        addExaminationEntity(schema, "Examination");
        addScores(schema, "Scores");

//自动生成的bean和dao存放的java-gen路径，注意要改成自己的

        String outDir = "/Users/dongyuangui/GITHUB/BluetoothGao20170531/app/src/main/java-gen/";


//调用DaoGenerator().generateAll方法自动生成代码到之前创建的java-gen目录下

        new DaoGenerator().generateAll(schema, outDir);

    }

    private static void addScores(Schema schema, String name) {

//        成绩表需要id,成绩,日期,设备,学号，姓名，班级，年级,考试时间
        Entity entity = schema.addEntity(name);
        entity.implementsSerializable();
        entity.addIdProperty().autoincrement();
        entity.addIntProperty("scores");
        entity.addLongProperty("date_");
        entity.addStringProperty("devices");
        entity.addStringProperty("xuehao");
        entity.addStringProperty("name");
        entity.addLongProperty("class_");
        entity.addLongProperty("year_");
        entity.addStringProperty("consume_time");

    }


    private static void addEntity(Schema schema, String name) {

//添加一个实体，则会自动生成实体Entity类

        Entity entity = schema.addEntity(name);
        entity.implementsSerializable();
//指定表名，如不指定，表名则为Entity（即实体类名）
//        entity.setDbName("student");
//给实体类中添加属性（即给test表中添加字段）
        entity.addIdProperty().autoincrement();//添加Id,自增长
        entity.addLongProperty("parent");
        entity.addStringProperty("path");
        entity.addStringProperty("filename").notNull();//添加String类型的name,不能为空

    }

    private static void addStudentEntity(Schema schema, String name) {

//添加一个实体，则会自动生成实体Entity类

        Entity entity = schema.addEntity(name);
        entity.implementsSerializable();
//指定表名，如不指定，表名则为Entity（即实体类名）
//        entity.setDbName("student");
//给实体类中添加属性（即给test表中添加字段）
        entity.addLongProperty("year_");
        entity.addIdProperty().autoincrement();//添加Id,自增长
        entity.addStringProperty("xuehao").notNull();
        entity.addStringProperty("username").notNull();
//        entity.addStringProperty("semester");
        entity.addLongProperty("classes").notNull();
//        entity.addIntProperty("results");
        entity.addStringProperty("mobile");//添加String类型的name,不能为空
        entity.addStringProperty("ids");//添加String类型的name,不能为空
        entity.addStringProperty("sex");
//        entity.addStringProperty("devices");
//        entity.addStringProperty("consume_time");
    }

    private static void addExaminationEntity(Schema schema, String name) {
        Entity entity = schema.addEntity(name);
        entity.implementsSerializable();
        entity.addIdProperty().autoincrement();//添加Id,自增长
        entity.addBooleanProperty("expired").notNull();
        entity.addStringProperty("break_");
        entity.addStringProperty("false_");
        entity.addStringProperty("short_");
        entity.addIntProperty("minutes");
        entity.addStringProperty("devices");
        // 用来保存设备名称的文件的文件内容的字段
        entity.addStringProperty("deviceFileDatas");

    }

}