package com.rookie.model.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("tb_comment_support")
@Getter
@Setter
public class CommentSupportTable {
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;
    private Long uid;
    private Long cid;
    private Long createTime;
}
