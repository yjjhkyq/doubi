package com.x.provider.customer.model.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("region")
public class Region extends BaseEntity {

  @TableId(type = IdType.INPUT)
  private Integer id;
  private Integer parentId;
  private Integer level;
  private String name;
  private Double lng;
  private Double lat;
  private Integer countryId;
}
