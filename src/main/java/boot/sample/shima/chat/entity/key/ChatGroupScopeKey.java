package boot.sample.shima.chat.entity.key;

public enum ChatGroupScopeKey {
    OPEN,
    CLOSED,
    ;

    public ChatGroupScopeKey of(String name) {
        for (ChatGroupScopeKey key : values()) {
            if (key.name().equals(name)) {
                return key;
            }
        }
        return null;
    }
}
