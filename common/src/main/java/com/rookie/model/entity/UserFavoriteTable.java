package com.rookie.model.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("tb_user_favorite")
@Getter
@Setter
public class UserFavoriteTable {
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;
    private Long uid;
    private Long favUid;
    private Long createTime;
}
