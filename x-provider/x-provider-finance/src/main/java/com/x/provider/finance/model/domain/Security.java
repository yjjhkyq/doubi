package com.x.provider.finance.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("security")
public class Security extends BaseEntity {
  @TableId
  private long id;
  /**
   * 股票编码
   */
  private String code;
  /**
   * 股票代码
   */
  private String symbol;
  /**
   * 股票中文简称
   */
  private String name;
  /**
   * 股票全面
   */
  private String fullName;
  /**
   * 股票英文名
   */
  private String enName;
  /**
   * 股票拼音
   */
  private String cnSpell;
  /**
   * 交易所代码 SSE 上交所 SZSE深交所
   */
  private String exchange;
  private String type;
  private String parentCode;
}
