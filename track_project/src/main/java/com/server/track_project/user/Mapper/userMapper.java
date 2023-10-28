package com.server.track_project.user.Mapper;

import com.server.track_project.user.Object.userVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface userMapper {

    @Insert("INSERT INTO track_project.user(id, password, email, auth) VALUES(#{id}, #{password}, #{email}, #{auth});")
    void saveUser(userVO uservo);

    @Select("SELECT * FROM user WHERE id = #{id};")
    userVO getUserAccount(@Param("id") String id);
}

