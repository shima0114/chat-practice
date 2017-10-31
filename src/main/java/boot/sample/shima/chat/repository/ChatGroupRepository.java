package boot.sample.shima.chat.repository;

import boot.sample.shima.chat.entity.ChatGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatGroupRepository extends JpaRepository<ChatGroup, String> {

    @Query("select g from ChatGroup g where not exists (select 1 from ChatGroupState s where g.id = s.groupId and s.entryUserId = :userId) and g.scope = 'OPEN' order by g.id")
    List<ChatGroup> getOpenGroupsWithoutJoiningGroups(@Param("userId") String userId);
}
