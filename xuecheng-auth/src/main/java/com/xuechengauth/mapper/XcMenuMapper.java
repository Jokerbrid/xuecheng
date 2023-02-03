package com.xuechengauth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuechengauth.domain.XcMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author joker
 * @since 2023-01-31
 */
@Mapper
public interface XcMenuMapper extends BaseMapper<XcMenu> {

    @Select("SELECT * \n" +
            "FROM\n" +
            "\txc_menu \n" +
            "WHERE\n" +
            "\tid IN (\n" +
            "\tSELECT\n" +
            "\t\tmenu_id \n" +
            "\tFROM\n" +
            "\t\txc_permission \n" +
            "WHERE\n" +
            "\trole_id IN ( SELECT role_id FROM xc_user_role WHERE user_id =#{userId} ));")
    List<XcMenu> selectPermissionsByUserId(String userId);
}
