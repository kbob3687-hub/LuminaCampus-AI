package cn.bugstack.domain.user.model.aggregate;

import cn.bugstack.domain.user.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAggregate {

    private UserEntity userEntity;

}
