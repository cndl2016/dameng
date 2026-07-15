package com.dm.cn.system.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dameng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTreeVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String label;
    private String type;
    private String phone;
    private Integer level;
    private List<UserTreeVO> children;

    public UserTreeVO(Long id, String label, String phone, String type, Integer level){
        this.id = id;
        this.label = label;
        this.phone = phone;
        this.type = type;
        this.level = level;
        this.children = new ArrayList<>();
    }
}
