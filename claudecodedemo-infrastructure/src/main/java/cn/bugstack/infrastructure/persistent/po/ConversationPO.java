package cn.bugstack.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationPO {

    private Long id;
    private Long userId;
    private String subject;
    private String question;
    private String answer;
    private Date createTime;

}
