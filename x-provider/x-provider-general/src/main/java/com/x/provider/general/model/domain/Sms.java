package com.x.provider.general.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("sms")
public class Sms extends BaseEntity {
  @TableId
  private Long id;
  private String phoneNumberSet;
  private String templateId;
  private String templateParamSet;
}
