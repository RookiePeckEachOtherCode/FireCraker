package com.rookie.model.entity;

import com.mybatisflex.annotation.ColumnMask;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import com.mybatisflex.core.mask.Masks;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("tb_user")
public class UserTable {
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;
    private String name;
    @ColumnMask(Masks.FIXED_PHONE)
    private String phone;
    private String password;
    private String signature;
    private String avatar;
    private Long createTime;
    private Long updateTime;
    @ColumnMask(Masks.EMAIL)
    private String email;
    private boolean showCollection;

    public static UserTable ID(Long id) {
        return UserTable.builder().id(id).build();
    }
}
