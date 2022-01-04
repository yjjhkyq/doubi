package com.x.provider.api.general.model.event;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import com.x.provider.api.general.model.dto.CommentDTO;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StarEvent{
  private Long id;
  private String itemId;
  private Long starCustomerId;
  private boolean star;
  private Integer itemType;
  private boolean firstStar;

  private CommentDTO comment;
}
