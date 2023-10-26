package com.server.track_project.user.Mapper;

import com.server.track_project.user.Object.userVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;

@Mapper
public interface userMapper {

    @Insert("INSERT INTO track_project.user(id, password, email, auth) VALUES(#{userId}, #{userPw}, #{userEmail}, #{userAuth});")
    void saveUser(userVO uservo);

    @Select("SELECT * FROM track_project.user WHERE id = #{userId};")
    userVO getUserAccount(String userId);
}

