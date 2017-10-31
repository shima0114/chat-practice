package boot.sample.shima.chat.entity.key;

public enum ChannelScopeKey {
    ALL("all", "ALL", "全体公開"),
    GROUP("group", "GROUP", "指定グループ"),
    USER("user", "USER", "指定ユーザー"),
    ;

    private String id;
    private String name;
    private String label;

    private ChannelScopeKey(String id, String name, String label) {
        this.id = id;
        this.name = name;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public String getName() { return name; }

    public String getLabel() {
        return label;
    }
}
