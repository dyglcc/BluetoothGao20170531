package com.xiaobailong.bean;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "STUDENT".
*/
public class StudentDao extends AbstractDao<Student, Long> {

    public static final String TABLENAME = "STUDENT";

    /**
     * Properties of entity Student.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Year_ = new Property(0, Long.class, "year_", false, "YEAR_");
        public final static Property Id = new Property(1, Long.class, "id", true, "_id");
        public final static Property Xuehao = new Property(2, String.class, "xuehao", false, "XUEHAO");
        public final static Property Username = new Property(3, String.class, "username", false, "USERNAME");
        public final static Property Classes = new Property(4, long.class, "classes", false, "CLASSES");
        public final static Property Mobile = new Property(5, String.class, "mobile", false, "MOBILE");
        public final static Property Ids = new Property(6, String.class, "ids", false, "IDS");
        public final static Property Sex = new Property(7, String.class, "sex", false, "SEX");
    }


    public StudentDao(DaoConfig config) {
        super(config);
    }
    
    public StudentDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"STUDENT\" (" + //
                "\"YEAR_\" INTEGER," + // 0: year_
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 1: id
                "\"XUEHAO\" TEXT NOT NULL ," + // 2: xuehao
                "\"USERNAME\" TEXT NOT NULL ," + // 3: username
                "\"CLASSES\" INTEGER NOT NULL ," + // 4: classes
                "\"MOBILE\" TEXT," + // 5: mobile
                "\"IDS\" TEXT," + // 6: ids
                "\"SEX\" TEXT);"); // 7: sex
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"STUDENT\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Student entity) {
        stmt.clearBindings();
 
        Long year_ = entity.getYear_();
        if (year_ != null) {
            stmt.bindLong(1, year_);
        }
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(2, id);
        }
        stmt.bindString(3, entity.getXuehao());
        stmt.bindString(4, entity.getUsername());
        stmt.bindLong(5, entity.getClasses());
 
        String mobile = entity.getMobile();
        if (mobile != null) {
            stmt.bindString(6, mobile);
        }
 
        String ids = entity.getIds();
        if (ids != null) {
            stmt.bindString(7, ids);
        }
 
        String sex = entity.getSex();
        if (sex != null) {
            stmt.bindString(8, sex);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Student entity) {
        stmt.clearBindings();
 
        Long year_ = entity.getYear_();
        if (year_ != null) {
            stmt.bindLong(1, year_);
        }
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(2, id);
        }
        stmt.bindString(3, entity.getXuehao());
        stmt.bindString(4, entity.getUsername());
        stmt.bindLong(5, entity.getClasses());
 
        String mobile = entity.getMobile();
        if (mobile != null) {
            stmt.bindString(6, mobile);
        }
 
        String ids = entity.getIds();
        if (ids != null) {
            stmt.bindString(7, ids);
        }
 
        String sex = entity.getSex();
        if (sex != null) {
            stmt.bindString(8, sex);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1);
    }    

    @Override
    public Student readEntity(Cursor cursor, int offset) {
        Student entity = new Student( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // year_
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // id
            cursor.getString(offset + 2), // xuehao
            cursor.getString(offset + 3), // username
            cursor.getLong(offset + 4), // classes
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // mobile
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // ids
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // sex
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Student entity, int offset) {
        entity.setYear_(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setXuehao(cursor.getString(offset + 2));
        entity.setUsername(cursor.getString(offset + 3));
        entity.setClasses(cursor.getLong(offset + 4));
        entity.setMobile(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setIds(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setSex(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Student entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Student entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Student entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}